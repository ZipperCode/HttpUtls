package io.nio.core;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SocketChannelWrapper {

    private Selector selector;

    private final ServerSocketChannel serverSocketChannel;

    public SocketChannelWrapper(ServerSocketChannel serverSocketChannel,Selector selector) throws IOException {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
        this.serverSocketChannel.configureBlocking(false);

    }
}
