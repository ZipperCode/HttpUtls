package io.nio.myokhttp;

public enum ContentType {

    HTML("text/html; charset=utf-8"),
    TEXT("text/plain; charset=utf-8"),
    XML("text/xml; charset=utf-8"),
    IMAGE("image/jpeg"),
    PNG("image/png"),
    JSON("application/json; charset=utf-8"),
    URL_DECODE("application/x-www-form-urlencoded"),
    FORM_DATA("multipart/form-data"),
    STREAM("application/octet-stream");
    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return contentType;
    }

}
