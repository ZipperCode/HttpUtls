package io.nio.core;

import java.io.IOException;

public interface IProducer{
    void produce(byte[] data) throws IOException;

    void onProduceException(Exception e);
}
