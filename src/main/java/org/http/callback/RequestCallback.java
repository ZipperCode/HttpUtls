package org.http.callback;

import okhttp3.Callback;

import java.io.IOException;

public interface RequestCallback<T>  {
    void onSuccess(T result);

    void onFailure(int code,String message);
}
