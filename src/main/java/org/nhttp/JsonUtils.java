package org.nhttp;

import com.google.gson.Gson;

public class JsonUtils {

    public static final Gson GSON = new Gson();

    public static String obj2Json(Object object){
        return GSON.toJson(object);
    }

    public static <T> T json2Obj(String json,Class<T> tClass){
        return GSON.fromJson(json,tClass);
    }
}
