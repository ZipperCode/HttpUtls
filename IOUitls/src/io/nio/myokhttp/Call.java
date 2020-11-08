package io.nio.myokhttp;

import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;

import java.io.Closeable;
import java.io.IOException;

public interface Call extends Closeable {
    /**
     * 获取请求信息
     * @return 请求信息
     */
    Request request();

    /**
     * 同步执行任务
     * @return 响应
     * @throws IOException 异常
     */
    Response execute() throws IOException;

    /**
     * 任务被异步执行
     * @param callback 回调函数
     */
    void enqueue(Callback callback);

    /**
     * 取消某一项任务
     */
    void cancel();

    /**
     * 任务是否被执行了
     * @return true 任务被执行了
     */
    boolean isExecuted();

    /**
     * 该任务是否被取消
     * @return true 被取消
     */
    boolean isCancelled();

}
