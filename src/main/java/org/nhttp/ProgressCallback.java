package org.nhttp;

@FunctionalInterface
public interface ProgressCallback{
    void uploadProgress(long progress);
}