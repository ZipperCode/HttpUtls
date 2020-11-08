package io.nio.myokhttp.interceptor;

import io.nio.myokhttp.Call;
import io.nio.myokhttp.HttpConnection;
import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;

import java.io.IOException;

public interface Interceptor {

    Response intercept(Chain chain) throws Exception;


    public interface Chain {
        Request request();

        Response proceed(Request request) throws Exception;

        HttpConnection connection();

        Call call();

        int connectTimeoutMillis();

        int readTimeoutMillis();
    }
}
