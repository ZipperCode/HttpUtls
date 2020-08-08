package org.http.request;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RequestParam implements Iterable<Map.Entry<String,Object>>{
    private Map<String, Object> param = new HashMap<>(10);

    public RequestParam(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            param.put(entry.getKey(), entry.getValue());
        }
    }

    public RequestParam add(String key, Object value) {
        param.put(key, value);
        return this;
    }

    public RequestParam remove(String key) {
        param.remove(key);
        return this;
    }

    public String buildUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (entry.getValue() instanceof String) {
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        return stringBuilder
                .delete(stringBuilder.length() - 1, stringBuilder.length())
                .toString();
    }


    @NotNull
    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return param.entrySet().iterator();
    }

}
