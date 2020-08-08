package org.http.request;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.http.request.RequestParam;
import org.http.util.HeadersUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    public static final String URL_CODER = "UTF-8";

    private String url;

    private Map<String, Object> param = new HashMap<>(10);

    private Map<String, String> header = new HashMap<>(10);

    private Object tag;

    private HttpMethod httpMethod = HttpMethod.GET;


    public RequestBuilder(){

    }

    public RequestBuilder url(String url){
        this.url = url;
        return this;
    }

    public RequestBuilder tag(Object tag){
        this.tag = tag;
        return this;
    }

    public RequestBuilder addParam(String key,Object value){
        this.param.put(key,value);
        return this;
    }

    public RequestBuilder method(HttpMethod httpMethod){
        this.httpMethod = httpMethod;
        return this;
    }

    public RequestBuilder addHeader(String key,String value){
        header.put(key,value);
        return this;
    }

    public RequestBuilder addHeaders(Map<String,String> headers){
        header.putAll(headers);
        return this;
    }

    public Request build(){
        Request.Builder builder = new Request.Builder();
        switch (httpMethod){
            case POST:
                builder.post(postBodyBuild()).url(url);
                break;
            case GET:
            default:
                builder.get().url(getBuild());
                break;
        }
        return builder
                .tag(tag)
                .build();
    }

    private RequestBody postBodyBuild(){
        FormBody.Builder builder = new FormBody.Builder();
        if(param != null){
            for (Map.Entry<String,Object> entry : param.entrySet()){
                builder.addEncoded(entry.getKey(),entry.getValue().toString());
            }
        }
        return builder.build();
    }

    public String getBuild() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url).append("?");
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getValue() instanceof String) {
                try {
                    stringBuilder.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode((String)entry.getValue(), URL_CODER))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }
        return stringBuilder
                .delete(stringBuilder.length() - 1, stringBuilder.length())
                .toString();
    }

    public static Request buildGetRequest(String url){
        return new Request.Builder()
                .url(url)
                .get()
                .headers(HeadersUtil.getDefaultHeaders())
                .build();
    }


}
