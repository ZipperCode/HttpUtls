package io.nio.udp;

import io.utils.IOUtil;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

public class UdpClient implements Runnable {

    public static void main(String[] args) {
        new Thread(new UdpClient()).start();
    }

    private Selector selector;
    private DatagramChannel datagramChannel;
    private ByteBuffer byteBuffer;

    private Scanner scanner;
    public UdpClient() {
        scanner = new Scanner(System.in);
        try {
            byteBuffer = ByteBuffer.allocate(1024);
            selector = Selector.open();
            datagramChannel = DatagramChannel.open();
            SocketAddress sa = new InetSocketAddress("localhost", 9080);
            datagramChannel.configureBlocking(false);
            datagramChannel.socket().connect(sa);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            datagramChannel.register(selector,SelectionKey.OP_WRITE);
            while (!Thread.interrupted()) {
                if (selector.select() > 0) {
                    // 获取以选择的键的集合
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if(key.isReadable()){
                            onRead(key);
                        }else if(key.isWritable()){
                            onWrite(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onWrite(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        System.out.println("请输入发送给服务端的消息：");
        String s = scanner.nextLine();
        byteBuffer.clear();
        byteBuffer.put(s.getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
        channel.register(selector,SelectionKey.OP_READ);
    }

    private void onRead(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        byteBuffer.clear();
        channel.read(byteBuffer);
        System.out.println("收到服务端消息：");
        System.out.println(new String(byteBuffer.array()));
        channel.register(selector,SelectionKey.OP_WRITE);
    }
}
