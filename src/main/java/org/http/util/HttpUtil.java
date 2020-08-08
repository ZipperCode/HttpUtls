package org.http.util;

import com.sun.xml.internal.ws.api.message.Header;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.http.interceptor.HeaderInterceptor;
import org.http.interceptor.LogInterceptor;
import org.http.interceptor.RetryInterceptor;
import org.http.request.RequestBuilder;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    private static final int CALL_TIMEOUT = 10;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_WRITE_TIMEOUT = 10;
    public static final int EXCEPTION_CODE = 0;

    public static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                // 调用超时
                .callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
                // 连接超时
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
                // 读写超时
                .writeTimeout(READ_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_WRITE_TIMEOUT,TimeUnit.SECONDS)
                // 添加Https支持
                .hostnameVerifier(getDefaultVerifier())
//                .sslSocketFactory()
                // 去除默认重试
                .retryOnConnectionFailure(false)
                // 请求头补充
//                .addInterceptor(new HeaderInterceptor())
                // 日志记录
                .addInterceptor(new LogInterceptor())
                // 自动以重试
                .addInterceptor(new RetryInterceptor())
                .build();
    }

    public static HostnameVerifier getDefaultVerifier(){
        return (s, sslSession) -> true;
    }

    public static SocketFactory getDefaultSocketFactory(){
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    public static TrustManager getDefaultTrustManager(){
        TrustManager trustManager = null;

        return trustManager;
    }


    public static void cancelAll(){
        client.dispatcher().cancelAll();
    }


    public static String get(Request request){
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "网络加载失败";
    }

    public static String get(String url){
        return get(RequestBuilder.buildGetRequest(url));
    }

    public static InputStream getStream(Request request){
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    public static InputStream getStream(String url){
        try {
            Response response = client.newCall(RequestBuilder.buildGetRequest(url)).execute();
            if(response.isSuccessful()){
                return response.body().byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
    }

    public static void asyncGet(String url, Callback callback){
        client.newCall(RequestBuilder.buildGetRequest(url)).enqueue(callback);
    }

    public static void asyncGet(Request request, Callback callback){
        client.newCall(request).enqueue(callback);
    }


    public static String post(Request request){
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return "网络加载失败";
    }


    public static void asyncPost(Request request,Callback callback){
        client.newCall(request).enqueue(callback);
    }


}
