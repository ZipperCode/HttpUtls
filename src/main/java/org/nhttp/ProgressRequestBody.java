package org.nhttp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @author : zzp
 * @date : 2020/8/7
 **/
public class ProgressRequestBody extends RequestBody {

    private MediaType contentType;

    private File file;

    private ProgressCallback progressCallback;

    private long contentLength;

    private long progress;

    public ProgressRequestBody(MediaType contentType, File file, ProgressCallback progressCallback) {
        this.contentType = contentType;
        this.file = file;
        this.progressCallback = progressCallback;
        this.contentLength = file.length();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long totalBytesRead = 0;
            for (long readCount; (readCount = source.read(bufferedSink.getBuffer(), 8192)) != -1; ) {
                totalBytesRead += readCount;
                int percent = (int)(contentLength * 1.0f / totalBytesRead) * 100;
                progress = percent;
                progressCallback.uploadProgress(percent);
            }
        } finally {
            if(source != null){
                source.close();
            }
        }
    }


}
