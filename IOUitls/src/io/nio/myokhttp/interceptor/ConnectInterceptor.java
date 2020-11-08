package io.nio.myokhttp.interceptor;

import io.nio.myokhttp.*;
import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;

public class ConnectInterceptor implements Interceptor {

    private final HttpClient client;

    public ConnectInterceptor(HttpClient client) {
        this.client = client;
    }

    @Override
    public Response intercept(Chain chain) throws  Exception {
        RealInterceptorChain realChain = (RealInterceptorChain) chain;
        Request request = realChain.request();
        HttpUrl url = request.url();
        HttpURLConnection httpURLConnection = null;
        HttpConnection httpConnection = client.connectionPool().getConnection(url.urlString());
        if(httpConnection == null){
            if ("https".equalsIgnoreCase(url.scheme())) {
                SSLContext ssl = SSLContext.getInstance("TLS");
                ssl.init(null, new TrustManager[]{client.x509TrustManager()}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(client.hostnameVerifier());
                httpURLConnection = (HttpsURLConnection) url.url().openConnection();
            } else {
                httpURLConnection = (HttpURLConnection) url.url().openConnection();
            }
            String connHeader = request.header("Connection");
            if (connHeader != null && connHeader.equalsIgnoreCase("keep-alive")) {
                httpConnection = new HttpConnection(client.connectionPool(), url.urlString(), httpURLConnection);
                client.connectionPool().addConnection(url.urlString(), httpConnection);
            }
        }

        assert httpURLConnection != null;
        httpURLConnection.setConnectTimeout(chain.connectTimeoutMillis());
        httpURLConnection.setReadTimeout(chain.readTimeoutMillis());
        // 设置输入输出
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        // 是否使用缓存,没有实现缓存拦截器，默认使用原生缓存
        httpURLConnection.setUseCaches(false);
        // 是否允许重定向
        httpURLConnection.setInstanceFollowRedirects(true);
        Set<Map.Entry<String, String>> entries = request.headers().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        // 设置请求方法
        httpURLConnection.setRequestMethod(request.method());

        Response response = realChain.proceed(request,httpConnection);
        return response;
    }
}
