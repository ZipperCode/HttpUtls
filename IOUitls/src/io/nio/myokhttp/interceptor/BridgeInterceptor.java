package io.nio.myokhttp.interceptor;

import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.RequestBody;
import io.nio.myokhttp.body.Response;
import io.nio.myokhttp.utils.Utils;

/**
 * 桥接拦截器，用于http请求头填充
 */
public class BridgeInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws Exception {
        Request userRequest = chain.request();
        Request.Builder requestBuilder = userRequest.newBuilder();
        RequestBody body = userRequest.body();
        if(body != null){
            if (body.contentType() != null) {
                requestBuilder.addHeader("Content-Type", body.contentType().toString());

            }
            // HTTP中有无ContentLength都行，但是如果使用压缩比如gzip等，
            // 无法确定传递的是真正的消息长度还是压缩后的长度
            // 所以如果请求的长度不确定，那么可以通过设置Transfer-Encoding头，
            // 服务端可以通过Transfer-Encoding头中的方法计算真正的长度，而ContentLength请求头将被忽略
            // 所以如果有Transfer-Encoding就不需要设置ContentLength了，反之亦然
            long contentLength = body.contentLength();
            if (contentLength != -1L) {
                requestBuilder.addHeader("Content-Length", Long.toString(contentLength));
                requestBuilder.removeHeader("Transfer-Encoding");
            } else {
                requestBuilder.addHeader("Transfer-Encoding", "chunked");
                requestBuilder.removeHeader("Content-Length");
            }
        }
        if (userRequest.header("Host") == null) {
            requestBuilder.addHeader("Host", Utils.parserHost(userRequest.url().host(), userRequest.url().port()));
        }

        if (userRequest.header("Connection") == null) {
            requestBuilder.addHeader("Connection", "Keep-Alive");
        }

        if (userRequest.header("User-Agent") == null) {
            requestBuilder.addHeader("User-Agent", "okhttp/1.0");
        }

        Response response = chain.proceed(requestBuilder.build());
        return response;
    }
}
