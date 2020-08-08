package org.http.callback;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.http.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.http.util.HttpUtil.EXCEPTION_CODE;


public abstract class JsonResponseHandler<T> implements Callback,RequestCallback<T> {

    private Type objectType;

    public JsonResponseHandler() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if(genericSuperclass instanceof Class){
            throw new RuntimeException("not found parameterizedType");
        }
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        this.objectType  = actualTypeArguments[0];
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        onFailure(EXCEPTION_CODE,e.getMessage());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
            if(response.isSuccessful()){
                onSuccess(JsonUtil.str2Obj(response.body().string(),objectType));
            }else{
                onFailure(response.code(),response.message());
            }
        }catch (Exception exception){
            onFailure(EXCEPTION_CODE,exception.getMessage());
        }
    }

    @Override
    public abstract void onSuccess(T result);

    @Override
    public abstract void onFailure(int code,String message);
}
