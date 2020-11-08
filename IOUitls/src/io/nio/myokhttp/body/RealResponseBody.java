package io.nio.myokhttp.body;

import io.nio.myokhttp.ContentType;

import java.io.IOException;
import java.io.InputStream;

public class RealResponseBody extends ResponseBody {

    private final String contentType;

    private final long contentLength;

    private final InputStream inputStream;

    public RealResponseBody(String contentType, long contentLength,InputStream inputStream) {
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.inputStream = inputStream;
    }


    @Override
    public ContentType contentType() {
        return ContentType.valueOf(contentType);
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public InputStream byteStream() throws IOException {
        return inputStream;
    }


}
