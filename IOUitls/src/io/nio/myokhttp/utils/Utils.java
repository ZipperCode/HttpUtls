package io.nio.myokhttp.utils;

public class Utils {

    public static String parserHost(String host, int port){
        if(port == 80 || port == 443){
            return host;
        }
        return host + ":" + port;
    }
}
