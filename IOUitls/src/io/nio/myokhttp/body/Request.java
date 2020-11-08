package io.nio.myokhttp.body;

import io.nio.myokhttp.HttpMethod;
import io.nio.myokhttp.HttpUrl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class Request {

    Builder builder;
    /**
     * 标签，用于请求取消等操作
     */
    String tag;
    /**
     * 请求的url
     */
    HttpUrl url;
    /**
     * 请求方法
     */
    HttpMethod method;

    /**
     * 请求头
     */
    Map<String, String> headers;
    /**
     * 是否支持序列化
     */
    boolean isSerialize;
    /**
     * 用于json数据提交
     */
    String json;
    /**
     * 响应的类型，用于反序列化
     */
    Type responseType;

    int retryTimes;

    RequestBody requestBody;

    public Request(Builder builder) {
        this.builder = builder;
        this.responseType = builder.responseType;
        this.method = builder.method;
        this.url = new HttpUrl(builder.url);
        this.tag = builder.tag;
        this.headers = builder.headers;
        this.isSerialize = builder.isSerialize;
        this.json = builder.json;
        this.retryTimes = builder.retryTimes;
    }

    public HttpUrl url(){
        return url;
    }

    public int retryTimes() {
        return retryTimes;
    }

    public  RequestBody body(){
        return requestBody;
    }

    public String header(String key){
        return headers.get(key);
    }

    public Map<String, String> headers(){
        return headers;
    }

    public Builder newBuilder() {
        return builder;
    }

    public String method() {
        return method.name();
    }

    public static final class Builder {

        /**
         * 标签，用于请求取消等操作
         */
        String tag;
        /**
         * 请求的url
         */
        String url;
        /**
         * 请求方法
         */
        HttpMethod method;
        /**
         * 请求头
         */
        Map<String, String> headers;
        /**
         * 是否支持序列化
         */
        boolean isSerialize;
        /**
         * 用于json数据提交
         */
        String json;
        /**
         * 响应的类型，用于反序列化
         */
        Type responseType;

        /**
         * 重试次数，重试和重定向拦截器使用
         */
        int retryTimes;

        RequestBody requestBody;

        public Builder() {
            this.method = HttpMethod.GET;
            this.headers = new HashMap<>();
            this.retryTimes = 3;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder addHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder addHeader(String key, String values) {
            this.headers.put(key, values);
            return this;
        }



        public Builder serialize(boolean isSerialize) {
            this.isSerialize = isSerialize;
            return this;
        }

        public Builder json(String json) {
            this.json = json;
            return this;
        }

        public Builder responseType(Type responseType) {
            this.responseType = responseType;
            return this;
        }

        public Builder responseType(Class<?> classes) {
            this.responseType = classes;//getSuperclassTypeParameter(classes);
            return this;
        }

        public Builder get() {
            method = HttpMethod.GET;
            return this;
        }

        public Builder post() {
            method = HttpMethod.POST;
            return this;
        }

        public Builder retryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public Builder removeHeader(String key) {
            this.headers.remove(key);
            return this;
        }

        public Builder body(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Request build() {
            Request requestWrapper = new Request(this);
            return requestWrapper;
        }


    }

}
