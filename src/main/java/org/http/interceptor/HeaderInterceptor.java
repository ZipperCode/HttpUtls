package org.http.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.http.util.HeadersUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 请求头拦截器
 * @author zzp
 */
public class HeaderInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .headers(HeadersUtil.getDefaultHeaders())
                .build();
        return chain.proceed(request);
    }
}
