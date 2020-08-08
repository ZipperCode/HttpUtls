package org.http.interceptor;

import okhttp3.*;
import okio.Okio;
import org.http.util.DateUtil;
import org.http.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.DeflaterOutputStream;

public class LogInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("[Request] : ")
                .append(DateUtil.getNow2String(DateUtil.DATETIME_PATTERN_DETAIL)).append("\r\n")
                .append("Method : ").append(request.method()).append("\r\n")
                .append("url : ").append(request.url()).append("\r\n");

        if(request.method().equalsIgnoreCase("post")){
            RequestBody body = request.body();
            if(body instanceof FormBody){
                FormBody formBody = (FormBody)body;
                stringBuilder.append("body : ").append(JsonUtil.obj2Str(formBody)).append("\r\n");
            }else{
                stringBuilder.append(body);
            }
        }
        Response response = chain.proceed(request);
        stringBuilder
                .append("[Response] : ")
                .append(DateUtil.getNow2String(DateUtil.DATETIME_PATTERN_DETAIL)).append("\r\n")
                .append("status : ").append(response.code()).append("\r\n")
                .append("message : ").append(response.message()).append("\r\n");
        if (response.isSuccessful()) {
            stringBuilder.append("body : ").append(JsonUtil.trim(response.body().string()));
        }
        System.out.println(stringBuilder.toString());
        return response;
    }

    public void writeFile(){

    }
}
