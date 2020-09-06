package io.nio.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

public class UdpServer implements Runnable {

    public static void main(String[] args) {
        new Thread(new UdpServer()).start();
    }

    private Selector selector;

    private DatagramChannel datagramChannel;

    private ByteBuffer byteBuffer;

    private Scanner scanner;

    public UdpServer() {
        try {
            byteBuffer = ByteBuffer.allocate(1024);
            scanner = new Scanner(System.in);
            selector = Selector.open();
            datagramChannel = DatagramChannel.open();
            datagramChannel.socket().bind(new InetSocketAddress(9080));
            datagramChannel.configureBlocking(false);
            datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
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
        System.out.println("请输入发送给客户端的消息：");
        String s = scanner.nextLine();
        byteBuffer.clear();
        byteBuffer.put(s.getBytes());
        byteBuffer.flip();
        // 有问题
        channel.write(byteBuffer);
        channel.register(selector,SelectionKey.OP_READ);
    }

    private void onRead(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        byteBuffer.clear();
        channel.receive(byteBuffer);
        System.out.println("收到客户端消息：");
        System.out.println(new String(byteBuffer.array()));
        channel.register(selector,SelectionKey.OP_WRITE);
    }
}
