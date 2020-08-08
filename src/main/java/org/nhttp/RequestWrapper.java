package org.nhttp;

import com.google.gson.internal.$Gson$Types;
import okhttp3.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author : zzp
 * @date : 2020/8/6
 **/
public class RequestWrapper {

    /**
     * ContentType json
     */
    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json;charset=utf-8");

    public static final MediaType MEDIA_TYPE_TEXT
            = MediaType.parse("text/plain; charset=utf-8");

    public static final MediaType MEDIA_TYPE_STREAM
            = MediaType.parse("application/octet-stream");

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String DEFAULT_DOWNLOAD_DIR = Paths.get("D:","OkHttpDownload")
            .normalize().toString();

    /**
     * 封装的OkHttp Request
     */
    private Request request;

    private Builder builder;

    /**
     * 响应类型
     */
    private Type responseType;
    /**
     * 上传回调进度
     */
    private ProgressCallback progressCallback;
    /**
     * 下载的目录
     */
    private String downloadDir;
    /**
     * 下载文件的文件名，下载文件作为本地存储的文件名
     */
    private String downloadFileName;
    /**
     * 是否在主线程回调
     */
    private boolean isRunMainThread;

    public RequestWrapper(Builder builder) {
        this.builder = builder;
        this.responseType = builder.responseType;
        this.progressCallback = builder.progressCallback;
        this.downloadDir = builder.downloadDir;
        this.downloadFileName = builder.downloadFileName == null || "".equals(downloadFileName) ?
                builder.url.substring(builder.url.lastIndexOf("/") +1) :
                builder.downloadFileName;
        Request.Builder requestBuilder = new Request.Builder()
                .url(builder.url);
        for(Map.Entry<String, String> entry : builder.headers.entrySet()){
            requestBuilder.addHeader(entry.getKey(),entry.getValue());
        }
        if (builder.method == HttpMethod.GET) {
            requestBuilder.get();
        } else if (builder.method == HttpMethod.POST) {
            RequestBody requestBody = null;
            if (builder.postType == PostType.URL_DECODED) {
                FormBody.Builder requestBodyBuilder = new FormBody.Builder();
                for (Map.Entry<String, Object> entry : builder.params.entrySet()) {
                    try {
                        requestBodyBuilder.add(URLEncoder.encode(entry.getKey(), CHARSET_UTF8),
                                URLEncoder.encode(entry.getValue().toString(), CHARSET_UTF8));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                requestBody = requestBodyBuilder.build();
            } else if (builder.postType == PostType.FORM_DATA) {
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
                for (Map.Entry<String, Object> entry : builder.params.entrySet()) {
                    if(entry.getValue() instanceof String){
                        multipartBodyBuilder.addFormDataPart(entry.getKey(),(String)entry.getValue());
                    }else if(entry.getValue() instanceof File){
                        File file = (File) entry.getValue();
                        if(file != null){
                            multipartBodyBuilder.addFormDataPart(entry.getKey(),
                                    file.getName(),
                                    RequestBody.create(MEDIA_TYPE_STREAM,file));
                        }
                    }
                }
                requestBody = multipartBodyBuilder.build();
            } else if (builder.postType == PostType.JSON) {
                requestBody = FormBody.create(MEDIA_TYPE_JSON, builder.json);
            } else if(builder.postType == PostType.UPLOAD){


            }
            if (requestBody == null) {
                requestBody = RequestBody.create(MEDIA_TYPE_TEXT, "");
            }
            requestBuilder.post(requestBody);
        }
        request = requestBuilder.build();
    }

    public Type responseType() {
        return responseType;
    }

    public Request request(){
        return request;
    }

    public ProgressCallback progressCallback(){
        return this.progressCallback;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public boolean isRunMainThread() {
        return isRunMainThread;
    }

    public static class Builder {
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
         * 请求参数
         */
        Map<String, Object> params;
        /**
         * 请求头
         */
        Map<String, String> headers;
        /**
         * 是否支持序列化
         */
        boolean isSerialize;
        /**
         * {@link PostType#URL_DECODED}
         * {@link PostType#FORM_DATA}
         * {@link PostType#JSON}
         * {@link PostType#UPLOAD}
         * post请求的类型
         */
        PostType postType;
        /**
         * 用于json数据提交
         */
        String json;
        /**
         * 上传进度回调
         */
        ProgressCallback progressCallback;
        /**
         * 响应的类型，用于反序列化
         */
        Type responseType;
        /**
         * 下载文件使用的目录
         */
        String downloadDir;
        /**
         * 下载文件的文件名
         */
        String downloadFileName;
        /**
         * 是否在主线程回调
         */
        boolean isRunMainThread;

        public Builder() {
            method = HttpMethod.GET;
            postType = PostType.URL_DECODED;
            downloadDir = DEFAULT_DOWNLOAD_DIR;
            params = new HashMap<>();
            headers = new HashMap<>();
            headers.putAll(HttpFactoty.builderHeader());
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

        public Builder addParam(String key,Object value){
            this.params.put(key,value);
            return this;
        }

        public Builder addParams(Map<String,Object> params){
            this.params.putAll(params);
            return this;
        }

        public Builder serialize(boolean isSerialize) {
            this.isSerialize = isSerialize;
            return this;
        }

        public Builder postType(PostType postType) {
            this.postType = postType;
            return this;
        }

        public Builder json(String json) {
            this.json = json;
            return this;
        }

        public Builder upload(File file, ProgressCallback progressCallback){
            this.params.put("file",file);
            this.progressCallback = progressCallback;
            this.postType = PostType.UPLOAD;
            return this;
        }

        public Builder progressCallback(ProgressCallback progressCallback){
            this.progressCallback = progressCallback;
            return this;
        }

        public RequestWrapper build(){
            RequestWrapper requestWrapper =  new RequestWrapper(this);
            return requestWrapper;
        }

        public Builder responseType(Type responseType){
            this.responseType = responseType;
            return this;
        }

        public Builder responseType(Class<?> classes){
            this.responseType = classes;//getSuperclassTypeParameter(classes);
            return this;
        }

        private static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                return null;
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public Builder get(){
            method = HttpMethod.GET;
            return this;
        }

        public Builder post(){
            method = HttpMethod.POST;
            return this;
        }

        public Builder downloadDir(String downloadDir){
            this.downloadDir = downloadDir;
            return this;
        }

        public Builder runMain(){
            this.isRunMainThread = true;
            return this;
        }

        public Builder runSub(){
            this.isRunMainThread = false;
            return this;
        }
    }


    enum PostType {
        URL_DECODED,
        FORM_DATA,
        JSON,
        UPLOAD
    }
}
