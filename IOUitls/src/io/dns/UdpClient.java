package io.dns;

import io.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
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
            byteBuffer = ByteBuffer.allocate(128);
            selector = Selector.open();
            datagramChannel = DatagramChannel.open();
            SocketAddress sa = new InetSocketAddress("8.8.8.8", 53);
            datagramChannel.configureBlocking(false);
            datagramChannel.socket().connect(sa);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            datagramChannel.register(selector, SelectionKey.OP_WRITE);
            while (!Thread.interrupted()) {
                if (selector.select() > 0) {
                    // 获取以选择的键的集合
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            onRead(key);
                        } else if (key.isWritable()) {
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
        /**
         * 53 6A 00 00
         *
         *
         * 53 6A 80 01
         * 80 01 ==> 1000 0000 0000 00001 qr = 1, opcode = 0,... rCode = 1
         */
        DnsPacket dnsPacket = new DnsPacket();
        DnsHeader dnsHeader = new DnsHeader();
        DnsFlags dnsFlags = new DnsFlags();
        dnsFlags.mQr = false;
        dnsFlags.mOpCode = 0;
        dnsFlags.mAa = false;
        dnsFlags.mRd = false;
        // 0 000 0 1 0 0 0000 0000
        // 0000 0100 0000 0000
        System.out.println("dnsFlag = " + dnsFlags);
        dnsHeader.mTransactionId = ((short) 2354);
        dnsHeader.mDnsFlags = dnsFlags;
        dnsHeader.mQuestionCount = ((short) 1);
        Question question = new Question();
        question.mQueryDomain = "www.baidu.com";
        question.mQueryType = 1;
        question.mQueryClass = 1;
        dnsPacket.mHeader = dnsHeader;
        dnsPacket.mQuestions = new Question[]{question};
        byteBuffer.clear();
        dnsPacket.toBytes(byteBuffer);
        System.out.println(IOUtil.byteHexToString(byteBuffer.array()));
        byteBuffer.flip();
        channel.write(byteBuffer);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void onRead(SelectionKey key) throws IOException {
        DatagramChannel channel = (DatagramChannel) key.channel();
        byteBuffer.clear();
        channel.read(byteBuffer);
        System.out.println("收到服务端消息：");
        byteBuffer.limit(128);
        DnsPacket dnsPacket = DnsPacket.fromBytes(byteBuffer);
        System.out.println(dnsPacket);
        System.out.println(IOUtil.byteHexToString(byteBuffer.array()));
        channel.register(selector, SelectionKey.OP_WRITE);
    }
}
