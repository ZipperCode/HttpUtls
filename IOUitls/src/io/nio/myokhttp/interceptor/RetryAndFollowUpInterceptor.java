package io.nio.myokhttp.interceptor;

import io.nio.myokhttp.HttpClient;
import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;

import java.io.IOException;

public class RetryAndFollowUpInterceptor implements Interceptor {

    private final HttpClient client;

    private final int maxTryTimes;

    private int retryTimes;

    public RetryAndFollowUpInterceptor(HttpClient client, int maxTryTimes) {
        this.client = client;
        this.maxTryTimes = maxTryTimes;
        this.retryTimes = 0;
    }

    @Override
    public Response intercept(Chain chain) throws Exception {
        Request request = chain.request();
        do {
            try {
                Response response = chain.proceed(request);
                return response;
            } catch (Exception e) {
                retryTimes++;
            }
        } while (retryTimes < request.retryTimes());
        throw new IOException("try times over three, please try!");
    }
}
