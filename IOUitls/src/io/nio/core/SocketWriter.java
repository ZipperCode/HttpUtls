package io.nio.core;

import com.io.utils.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketWriter extends Thread implements Closeable {

    private final Selector selector;
    private final IOHandler ioHandler;

    /**
     * 是否可读
     */
    private AtomicBoolean isCanedRead = new AtomicBoolean(false);

    public SocketWriter(Selector selector,IOHandler ioHandler){
        this.selector = selector;
        this.ioHandler = ioHandler;
    }

    @Override
    public void close() throws IOException {
        isCanedRead.set(false);
        selector.wakeup();
        IOUtil.close(selector);
    }
}
