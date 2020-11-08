package io.nio.myokhttp.body;

import io.nio.myokhttp.ContentType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FormBody extends RequestBody {

    public static final String URL_ENCODE_CHARSET = "UTF-8";

    /**
     * 请求参数
     */
    Map<String, String> params;

    public FormBody(Builder builder) {
        super();
        this.params = builder.params;
    }

    @Override
    public ContentType contentType() {
        return ContentType.URL_DECODE;
    }

    @Override
    public long contentLength() {
        long contentLength = 0L;
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            contentLength += (entry.getKey().getBytes().length + entry.getValue().getBytes().length);
        }
        return contentLength;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {

    }


    public final static class Builder {

        Map<String, String> params;

        public Builder() {
            this.params = new HashMap<>();
        }

        public Builder param(String key, String value) {
            try {
                key = URLEncoder.encode(key, URL_ENCODE_CHARSET);
                value = URLEncoder.encode(value, URL_ENCODE_CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            this.params.put(key, value);
            return this;
        }

        public Builder params(Map<String, String> params) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    this.params.put(URLEncoder.encode(entry.getKey(), URL_ENCODE_CHARSET),
                            URLEncoder.encode(entry.getValue(), URL_ENCODE_CHARSET));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        public FormBody build() {
            return new FormBody(this);
        }
    }
}
