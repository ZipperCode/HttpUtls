package org.nhttp;


public class NetworkException extends Exception {

    private int code;

    private String msg;

    public NetworkException(ExceptionCode exceptionCode) {
        this(exceptionCode.code,exceptionCode.message);
    }

    public NetworkException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    enum ExceptionCode{
        EXCEPTION_40X(404,"站点请求失败，请网络或者URL是否正确"),
        EXCEPTION_50X(500,"访问出现异常，请联系后台管理员"),
        EXCEPTION_DOWNLOAD(100,"网络下载异常"),
        EXCEPTION(0,"本地网络异常，请检查网络");

        public final int code;
        public final String message;

        ExceptionCode(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
