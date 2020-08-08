package org.nhttp;

import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author : zzp
 * @date : 2020/8/7
 **/
public class ResponseWrapper {

    private String contentType;

    private Builder builder;
    /**
     * 实体内容
     */
    private Object data;
    /**
     * 下载进度回调
     */
    private ProgressCallback progressCallback;

    public ResponseWrapper(Builder builder) {
        this.builder = builder;
        this.contentType = builder.contentType;
        if(ContentType.JSON.equals(this.contentType)){
            data = new Gson().fromJson(builder.responseText, builder.responseType);
        }else if(ContentType.TEXT.equals(this.contentType)){
            data = builder.responseText;
        }
    }

    public <T> T data(){
        return (T) data;
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public ProgressCallback getProgressCallback() {
        return progressCallback;
    }

    public static class Builder{
        Type responseType;

        String responseText;

        private String contentType;

        public Builder() {
            responseType = String.class;
            contentType = ContentType.TEXT;
        }

        public Builder responseType(Type responseType){
            this.responseType = responseType;
//            if(responseType != null){
//                if(responseType instanceof ParameterizedType){
//                    aClass = (Class<?>) ((ParameterizedType)responseType).getRawType();
//                }else if(responseType instanceof TypeVariable){
//                    System.out.println(((TypeVariable) responseType).getGenericDeclaration());
//                }else{
//                    aClass = (Class<?>)responseType;
//                }
//            }
            return this;
        }

        public Builder responseText(String responseText){
            this.responseText = responseText;
            return this;
        }

        public Builder contentType(String contentType){
            this.contentType = contentType;
            return this;
        }


        public ResponseWrapper build(){
            return new ResponseWrapper(this);
        }
    }

    public static void main(String[] args) {
        ResponseWrapper responseWrapper = new Builder()
                .responseText("{\n" +
                        "  \"list\": [\n" +
                        "    {\n" +
                        "      \"address\": \"\",\n" +
                        "      \"appeui\": \"1616161616161616\",\n" +
                        "      \"area\": \"\",\n" +
                        "      \"city\": \"\",\n" +
                        "      \"createdAt\": \"2017-02-26T09:29:05Z\",\n" +
                        "      \"deveui\": \"4797c534001e0036\",\n" +
                        "      \"lastact\": 1488102408,\n" +
                        "      \"lat\": \"\",\n" +
                        "      \"lng\": \"\",\n" +
                        "      \"name\": \"\",\n" +
                        "      \"type\": \"\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"address\": \"\",\n" +
                        "      \"appeui\": \"1616161616161616\",\n" +
                        "      \"area\": \"\",\n" +
                        "      \"city\": \"\",\n" +
                        "      \"createdAt\": \"2017-02-26T09:56:44Z\",\n" +
                        "      \"deveui\": \"4797c534001e0038\",\n" +
                        "      \"lastact\": 1488103464,\n" +
                        "      \"lat\": \"\",\n" +
                        "      \"lng\": \"\",\n" +
                        "      \"name\": \"\",\n" +
                        "      \"type\": \"\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"more\": false\n" +
                        "}")
                .responseType(InputStream.class)
                .build();
    }
}
