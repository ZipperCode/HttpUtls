package org.http.callback;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.http.util.HttpUtil;
import org.http.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.http.util.HttpUtil.EXCEPTION_CODE;

public abstract class StringResponseHandler implements Callback,RequestCallback<String> {
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        onFailure(HttpUtil.EXCEPTION_CODE,e.getMessage());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try{
            if(response.isSuccessful()){
                onSuccess(response.body().string());
            }else{
                onFailure(response.code(),response.message());
            }
        }catch (Exception exception){
            onFailure(EXCEPTION_CODE,exception.getMessage());
        }
    }

    @Override
    public abstract void onSuccess(String result);

    @Override
    public abstract void onFailure(int code, String message);
}
