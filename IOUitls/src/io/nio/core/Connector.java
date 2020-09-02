package io.nio.core;

import io.utils.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

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
//        this.socketReader.register(socketChannel, this);
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
    public void onProduceException(Exception e) {
        IOUtil.close(this);
    }

    @Override
    public byte[] consume() {
        System.out.print("请输入您要发送到客户端的字符：");
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        return text.getBytes();
    }

    @Override
    public void onCustomException(Exception e) {
        IOUtil.close(this);
    }


    public interface OnClientStateChange{
        void onChange(Connector connector);
    }
}
