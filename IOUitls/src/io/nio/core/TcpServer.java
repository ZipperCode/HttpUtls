package io.nio.core;

import com.io.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TcpServer extends Thread implements Connector.OnClientStateChange{
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

    public TcpServer(int port){
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
            socketWriter = new SocketWriter(selector,ioHandler);
            // 设置当前线程为最大优先级
            setPriority(Thread.MAX_PRIORITY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socketReader.start();
            socketWriter.start();
            ioHandler.start();
            System.out.println("开始监听每一个连接");
            while (!isClosed){
                if(selector.select() > 0){
                    Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
                    while (selectionKeyIterator.hasNext()){
                        SelectionKey selectionKey = selectionKeyIterator.next();
//                        System.out.println("是否有可选的通道" + (selectionKey.readyOps() == SelectionKey.OP_READ));
                        selectionKeyIterator.remove();
                        try{
                            // 当前线程只处理连接
                            if(selectionKey.isAcceptable()){
                                accept(selectionKey);
                            }
                        }catch (IOException e){
                            selectionKey.cancel();
                            throw e;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(serverSocketChannel,selector);
        }
    }

    private void accept(SelectionKey key) throws IOException{
        // 获取服务端注册的通道
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        // 阻塞直到接受连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("客户端：" + socketChannel.getRemoteAddress() + " 连接成功");
        // 设置非阻塞模式
        socketChannel.configureBlocking(false);
//        socketChannel.register(selector,SelectionKey.OP_READ);
        // 每当有一个客户端连接后，创建一个客户端连接类
        clients.add(new Connector(socketReader,socketWriter,socketChannel,this));
    }

    @Override
    public void onChange(Connector connector) {
        System.out.println(Thread.currentThread().getName() + " : 客户端 ： " + connector + "断开连接");
        clients.remove(connector);
        System.out.println(clients);
    }

    public static void main(String[] args) {
        new TcpServer(9090).start();
    }


}
