package io.nio.core;

import com.io.utils.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Connector implements Closeable,IProducer,ICustomer {

    private SocketReader socketReader;

    private SocketWriter socketWriter;

    private SocketChannel socketChannel;

    private OnClientStateChange onClientStateChange;

    public Connector(SocketReader socketReader,
                     SocketWriter socketWriter,
                     SocketChannel socketChannel,
                     OnClientStateChange onClientStateChange) throws IOException {
        this.socketChannel = socketChannel;
        this.socketReader = socketReader;
        this.socketWriter = socketWriter;
        this.onClientStateChange = onClientStateChange;
        // 注册读事件
        this.socketReader.register(socketChannel, this);
    }

    @Override
    public void close() throws IOException {
        onClientStateChange.onChange(this);
        IOUtil.close(socketChannel);
    }

    @Override
    public void produce(byte[] data) throws IOException {
        // 将缓冲区数据写出显示
        String printString = new String(data, "UTF-8");
        System.out.println(Thread.currentThread().getName() + " : 收到来自：" + socketChannel.getRemoteAddress() + " 的数据：" + printString);
    }

    @Override
    public void consume(byte[] data) {

    }


    public interface OnClientStateChange{
        void onChange(Connector connector);
    }
}
