package org.http.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtil {

    private static Gson GSON = new Gson();

    public static <T> String obj2Str(T object){
        return GSON.toJson(object);
    }

    public static <T> T str2Obj(String json, Class<T> objClass){
        return GSON.fromJson(json,objClass);
    }

    public static <T> T str2Obj(String json, Type objType){
        return GSON.fromJson(json,objType);
    }

    public static String trim(String json){
        if(json == null){
            return "null";
        }
        return json.replaceAll("[\\r\\t\\n\\s]","");
    }
}
