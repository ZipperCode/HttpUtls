package io.nio.myokhttp;

import io.nio.myokhttp.body.Response;

public interface Callback {
    void onSuccess(Call call, Response response);

    void onFailure(Call call,Exception e);
}
