package io.nio.core;

public interface ICustomer {

    byte[] consume();

    void onCustomException(Exception e);
}
