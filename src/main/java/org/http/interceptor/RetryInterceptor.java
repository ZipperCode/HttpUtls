package org.http.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RetryInterceptor implements Interceptor {
    public static final int MAX_RETRY_TIMES = 3;
    private int currentRetryTimes = 1;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && currentRetryTimes <= MAX_RETRY_TIMES){
            response = chain.proceed(request);
        }
        return response;
    }
}
