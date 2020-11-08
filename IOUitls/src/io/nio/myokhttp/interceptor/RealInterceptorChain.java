package io.nio.myokhttp.interceptor;

import io.nio.myokhttp.Call;
import io.nio.myokhttp.HttpConnection;
import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;

import java.io.IOException;
import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private final int index;
    private final Request request;
    private final HttpConnection connection;
    private final Call call;
    private final int connectTimeout;
    private final int readTimeout;


    public RealInterceptorChain(List<Interceptor> interceptors,
                                int index,
                                Request request,
                                HttpConnection connection,
                                Call call,
                                int connectTimeout,
                                int readTimeout) {
        this.interceptors = interceptors;
        this.connection = connection;
        this.index = index;
        this.request = request;
        this.call = call;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) throws Exception {
        return proceed(request,this.connection);
    }

    public Response proceed(Request request, HttpConnection connection) throws Exception{
        if (this.index >= this.interceptors.size()) {
            throw new AssertionError();
        }
        RealInterceptorChain next = new RealInterceptorChain(this.interceptors,
                this.index + 1, request, connection, this.call, this.connectTimeout,
                this.readTimeout);
        Interceptor interceptor = this.interceptors.get(this.index);
        Response response  = interceptor.intercept(next);
        return response;
    }

    @Override
    public HttpConnection connection() {
        return connection;
    }

    @Override
    public Call call() {
        return call;
    }

    @Override
    public int connectTimeoutMillis() {
        return connectTimeout;
    }

    @Override
    public int readTimeoutMillis() {
        return readTimeout;
    }
}
