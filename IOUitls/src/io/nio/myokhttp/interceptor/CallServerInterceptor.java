package io.nio.myokhttp.interceptor;

import io.nio.myokhttp.body.RealResponseBody;
import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.RequestBody;
import io.nio.myokhttp.body.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class CallServerInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        RealInterceptorChain realChain = (RealInterceptorChain)chain;
        HttpURLConnection httpURLConnection = realChain.connection().connection();
        Request request = chain.request();
        Response.Builder responseBuilder = new Response.Builder().request(request).sentRequestAtMillis(System.currentTimeMillis());
        // 写入参数
        OutputStream outputStream = httpURLConnection.getOutputStream();
        RequestBody body = request.body();
        if(body != null){
            request.body().writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        }

        httpURLConnection.connect();

        int code = httpURLConnection.getResponseCode();
        String message = httpURLConnection.getResponseMessage();
        responseBuilder.code(code).message(message);
        // 获取响应头
        Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            StringBuilder values = new StringBuilder();
            for (String value : entry.getValue()) {
                values.append(value);
            }
            responseBuilder.header(entry.getKey(),values.toString());
        }
        // 获取响应实体
        String contentType = httpURLConnection.getHeaderField("Content-Type");
        int contentLength = 0;
        try{
            contentLength = Integer.parseInt(httpURLConnection.getHeaderField("Content-Length"));
        }catch (Throwable e){
            e.printStackTrace();
        }

        InputStream inputStream = httpURLConnection.getInputStream();
        RealResponseBody responseBody = new RealResponseBody(contentType,contentLength,inputStream);
        long receivedResponseAtMillis = System.currentTimeMillis();
        Response response = responseBuilder.responseBody(responseBody).receivedResponseAtMillis(receivedResponseAtMillis).build();
        return response;
    }

}
