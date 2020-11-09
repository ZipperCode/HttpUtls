package io.nio.myokhttp;

import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;
import io.nio.myokhttp.interceptor.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealCall implements Call {

    private final HttpClient httpClient;

    private final Request request;

    private boolean executed;

    private boolean canceled;

    public RealCall(HttpClient httpClient, Request request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response execute() throws IOException {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }
            this.executed = true;
        }

        Response response = null;
        try {
            this.httpClient.dispatcher().executed(this);
            response = getResponseWithInterceptorChain();
        } catch (Exception e) {
            throw new IOException("execute call failure !!!");
        } finally {
            this.httpClient.dispatcher().finished(this);
        }
        return response;
    }

    /**
     * 拦截器执行
     *
     * @return 结果
     */
    private Response getResponseWithInterceptorChain() throws Exception {
        List<Interceptor> interceptors = new ArrayList<>(this.httpClient.interceptorList);
        interceptors.add(new RetryAndFollowUpInterceptor(this.httpClient, request.retryTimes()));
        interceptors.add(new BridgeInterceptor());
        interceptors.add(new ConnectInterceptor(httpClient));
        interceptors.add(new CallServerInterceptor());
        Interceptor.Chain chain = new RealInterceptorChain(interceptors,
                0,
                request,
                null,
                this,
                this.httpClient.connectTimeoutMillis(),
                this.httpClient.readTimeoutMillis());
        return chain.proceed(this.request);
    }

    @Override
    public void enqueue(Callback callback) {
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already Executed");
            }

            this.executed = true;
        }

        this.httpClient.dispatcher().enqueue(new RealCall.AsyncCall(callback));
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void close() throws IOException {
        executed = true;
        canceled = true;
        this.httpClient.dispatcher().finished(this);
    }

    final class AsyncCall implements Runnable {

        private final Callback callback;

        AsyncCall(Callback callback) {
            this.callback = callback;
        }

        Request request(){
            return RealCall.this.request;
        }

        RealCall get(){
            return RealCall.this;
        }

        @Override
        public void run() {
            try {
                Response response  = getResponseWithInterceptorChain();
                if(isCancelled()){
                    this.callback.onFailure(RealCall.this, new IOException("Canceled"));
                }else{
                    this.callback.onSuccess(RealCall.this, response);
                }
            } catch (Exception e) {
                this.callback.onFailure(RealCall.this,e);
            } finally {
                RealCall.this.httpClient.dispatcher().finished(this);
            }
        }
    }
}
