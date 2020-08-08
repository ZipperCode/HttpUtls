package org.nhttp;

import java.util.HashMap;
import java.util.Map;

public class HttpFactoty {

    public static Map<String,String> builderHeader(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("Accept-Encoding","gzip, deflate, br");
        headers.put("Accept-Language","zh-CN,zh;q=0.9");
        return headers;
    }
}
