package io.nio.myokhttp.body;

import java.util.HashMap;
import java.util.Map;

public final class Response {
    /**
     * 请求
     */
    final Request request;
    /**
     * 响应码
     */
    final int code;
    /**
     * 消息体
     */
    final String message;
    /**
     * 响应头
     */
    final Map<String, String> headers;
    /**
     * 响应体
     */
    final ResponseBody body;
    /**
     * 请求发送时间戳
     */
    final long sentRequestAtMillis;
    /**
     * 收到响应时间戳
     */
    final long receivedResponseAtMillis;


    public Response(Builder builder) {
        this.request = builder.request;
        this.code = builder.code;
        this.message = builder.message;
        this.headers = builder.headers;
        this.body = builder.body;
        this.sentRequestAtMillis = builder.sentRequestAtMillis;
        this.receivedResponseAtMillis = builder.receivedResponseAtMillis;
    }


    public ResponseBody body(){
        return body;
    }

    public static final class Builder {
        /**
         * 请求
         */
        Request request;
        /**
         * 响应码
         */
        int code;
        /**
         * 消息体
         */
        String message;
        /**
         * 响应头
         */
        Map<String, String> headers;
        /**
         * 响应体
         */
        ResponseBody body;
        /**
         * 请求发送时间戳
         */
        long sentRequestAtMillis;
        /**
         * 收到响应时间戳
         */
        long receivedResponseAtMillis;

        public Builder() {
            this.headers = new HashMap<>();
        }

        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder responseBody(ResponseBody responseBody) {
            this.body = responseBody;
            return this;
        }

        public Builder sentRequestAtMillis(long sentRequestAtMillis) {
            this.sentRequestAtMillis = sentRequestAtMillis;
            return this;
        }

        public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
            this.receivedResponseAtMillis = receivedResponseAtMillis;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }


    enum Protocol {

    }
}
