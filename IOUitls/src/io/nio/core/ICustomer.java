package io.nio.core;

public interface ICustomer {

    void consume(byte[] data);

    void onCustomException(Exception e);
}
