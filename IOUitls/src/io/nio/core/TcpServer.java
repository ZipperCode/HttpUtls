package io.nio.core;

import io.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class TcpServer extends Thread implements Connector.OnClientStateChange {
    /**
     * 选择器，选择连接，读写通道
     */
    private Selector selector;
    /**
     * 服务器通道
     */
    private ServerSocketChannel serverSocketChannel;
    /**
     * 读通道处理
     */
    private SocketReader socketReader;
    /**
     * 写通道处理
     */
    private SocketWriter socketWriter;
    /**
     * IO处理
     */
    private IOHandler ioHandler;
    /**
     * 已经连接的客户端
     */
    private List<Connector> clients = new ArrayList<>();
    /**
     * 端口
     */
    private final int port;
    /**
     * 是否关闭
     */
    private boolean isClosed = false;

    private Map<SelectionKey,Connector> clientMap = new HashMap();

    public TcpServer(int port) {
        this.port = port;
        try {
            // 打开选择器
            selector = Selector.open();
            // 打开服务端通道
            serverSocketChannel = ServerSocketChannel.open();
            // 绑定一个服务器端口号
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            // 设置非阻塞状态
            serverSocketChannel.configureBlocking(false);
            // 通道注册连接事件到选择器上
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("TCP服务启动成功");
            ioHandler = new IOHandler();
            // 读通道处理
            socketReader = new SocketReader(selector, ioHandler);
            // 写处理
            socketWriter = new SocketWriter(selector, ioHandler);
            // 设置当前线程为最大优先级
            setPriority(Thread.MAX_PRIORITY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
//            socketReader.start();
//            socketWriter.start();
            ioHandler.start();
            System.out.println("开始监听每一个连接");
            while (!isClosed) {
                if (selector.select() > 0) {
                    Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                    while (selectionKeyIterator.hasNext()) {
                        SelectionKey selectionKey = selectionKeyIterator.next();
                        selectionKeyIterator.remove();
                        try {
                            if (selectionKey.isAcceptable()) {
                                System.out.println("[Server] isAcceptable");
                                accept(selectionKey);
                            } else if (selectionKey.isReadable()) {
                                System.out.println("[Server] isReadable");
                                read(selectionKey);
                            } else if (selectionKey.isWritable()) {

                            }
                        } catch (IOException e) {
                            selectionKey.cancel();
                            throw e;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(serverSocketChannel, selector);
        }
    }

    private void accept(SelectionKey key) throws IOException {
        // 获取服务端注册的通道
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        // 阻塞直到接受连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("客户端：" + socketChannel.getRemoteAddress() + " 连接成功");
        // 设置非阻塞模式
        socketChannel.configureBlocking(false);
        SelectionKey clientReadSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("clientReadSelectionKey = " + clientReadSelectionKey);
        // 同时注册读和写事件，Selector.select()会调用，isWritable()方法为true
//        socketChannel.register(selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        // 每当有一个客户端连接后，创建一个客户端连接类
        Connector connector = new Connector(socketReader, socketWriter, socketChannel, this);
        clientMap.put(clientReadSelectionKey,connector);
    }

    private void read(SelectionKey key) throws IOException{
        IProducer iProducer = clientMap.get(key);
        System.out.println("iProducer = " + iProducer);
        if (iProducer != null) {
            key.interestOps(key.readyOps() & ~SelectionKey.OP_READ);
            ioHandler.onTask(new ReadHandler(key,iProducer));
        }
    }

    private void write(SelectionKey key) throws IOException{

    }

    @Override
    public void onChange(Connector connector) {
        System.out.println(Thread.currentThread().getName() + " : 客户端 ： " + connector + "断开连接");
        clients.remove(connector);
        System.out.println(clients);
    }

    class ReadHandler implements Runnable {

        private final SelectionKey selectionKey;

        private IProducer producer;

        private final ByteBuffer byteBuffer;

        public ReadHandler(SelectionKey selectionKey, IProducer producer) {
            this.selectionKey = selectionKey;
            this.producer = producer;
            this.byteBuffer = ByteBuffer.allocate(256);
        }


        @Override
        public void run() {
            try {
                // 清除缓冲区数据
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                // 读取数据到缓冲区
                channel.read(byteBuffer);
                // 缓冲区游标归0
                byteBuffer.flip();
                // 消费事件
                producer.produce(byteBuffer.array());
                // 注册读事件
                channel.register(selector, SelectionKey.OP_READ);
                selector.wakeup();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new TcpServer(10000).start();
    }


}
