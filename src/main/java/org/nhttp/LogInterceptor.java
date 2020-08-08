package org.nhttp;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sun.rmi.runtime.Log;

import java.io.IOException;

/**
 * 请求日志拦截器
 */
public class LogInterceptor implements Interceptor {
    private static final String TAG = LogInterceptor.class.getSimpleName();

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        StringBuilder stringBuilder = new StringBuilder();
        String reqMethod = request.method();
        stringBuilder.append("DateUtils.getTime()").append("\t")
                .append("请求方式：")
                .append(reqMethod).append(",")
                .append("请求URL").append("===>")
                .append(request.url().toString());
        System.out.println(stringBuilder.toString());
//        if(reqMethod.equalsIgnoreCase("POST")){
//            stringBuilder.delete(0,stringBuilder.length());
//            stringBuilder.append("DateUtils.getTime()").append("\t")
//                    .append("请求参数 : [")
//                    .append("]");
//            RequestBody body = request.body();
//        }

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        stringBuilder.deleteCharAt(0).append("\r\n")
                .append("DateUtils.getTime()").append("\t")
                .append("响应内容").append("===>")
                .append("response code = ").append(response.code()).append("\t")
                .append("response message = ").append(response.message())
                .append(responseBody);
        System.out.println(stringBuilder.toString());
        return response;
    }
}
