package org.http.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import okhttp3.Headers;

public class HeadersUtil {

    public static Headers getDefaultHeaders(){
        return new Headers.Builder()
                .add("Accept", "text/plain")
                .add("Accept-Charset", "utf-8")
                .add("Accept-Encoding","gzip, deflate")
                .add("Cache-Control","no-cache")
                .add("Connection","keep-alive")
                .add("Content-Type","application/x-www-form-urlencoded")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                .build();
    }
}
