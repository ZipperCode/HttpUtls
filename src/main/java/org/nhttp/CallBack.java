package org.nhttp;

/**
 * @author : zzp
 * @date : 2020/8/7
 **/
public interface CallBack {
    /**
     * 请求回调成功 responseCode >= 200 && responseCode < 300
     * @param response 响应的内容
     */
    void success(ResponseWrapper response) throws Exception;

    /**
     * 请求失败的回调
     * @param message 失败的消息
     */
    void failure(String message);

    /**
     * 请求错误回调
     * @param e 异常
     */
    void onError(NetworkException e);
}
