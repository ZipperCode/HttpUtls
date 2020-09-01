package io.nio.core;


import io.utils.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketReader extends Thread implements Closeable {

    /**
     * 选择器
     */
    private Selector selector;
    /**
     * 是否被取消
     */
    private final AtomicBoolean isCanceled = new AtomicBoolean(false);
    /**
     * 是否处于等待
     */
    private final AtomicBoolean isWaiting = new AtomicBoolean(true);
    /**
     * 没一个连接对应他们的IO回调处理器
     */
    private Map<SelectionKey, IProducer> map = new HashMap<>();

    private final IOHandler ioHandler;

    public SocketReader(Selector selector, IOHandler ioHandler) {
        this.ioHandler = ioHandler;
        this.selector = selector;
        // 设置当前线程为最大优先级
//        setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
        try {
            System.out.println("SocketReader 开始监听读事件");
            while (!isCanceled.get()) {
//                System.out.println("SocketReader 开始轮询是否有通道被选择");
                // 注册之后当前线程被阻塞
                boolean b = selector.select() == 0;
                System.out.println("b = " + b);
                if (b) {
                    System.out.println("SocketReader Selector被唤醒，没有任何通道可用");
                    ioHandler.onNone();
//                    waiting();
                    continue;
                }
                Iterator<SelectionKey> selectKeys = selector.selectedKeys().iterator();
                while (selectKeys.hasNext()) {
                    SelectionKey selectionKey = selectKeys.next();
                    selectKeys.remove();
                    if (selectionKey.isReadable()) {
                        System.out.println("SocketReader 包含读的通道");
//                        System.out.println("监听到客户端数据可读，开始提交IO处理");
                        // 取消事件注册
                        selectionKey.interestOps(selectionKey.readyOps() & ~ SelectionKey.OP_READ);
                        IProducer iProducer = map.get(selectionKey);
                        System.out.println("iProducer = " + iProducer);
                        if (iProducer != null) {
                            ioHandler.onTask(new ReadHandler(selectionKey, iProducer));
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtil.close(this);
        }
    }

    public void register(SocketChannel socketChannel, IProducer producer) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " : 开始注册读通道事件");
        // 获取isWaiting的锁
        synchronized (isWaiting) {
            // 唤醒Selector
            selector.wakeup();
            // 注册通道事件
            try {
                SelectionKey selectionKey = null;
                socketChannel.configureBlocking(false);
                if (socketChannel.isRegistered()) {
                    // 查询是否已经注册过
                    System.out.println(threadName + " : 查询是否已经注册过");
                    selectionKey = socketChannel.keyFor(selector);
                    if (selectionKey != null) {
                        selectionKey.interestOps(0);
                        selectionKey.channel().register(selector,SelectionKey.OP_READ);
                    }
                }
                if(selectionKey == null){
                    System.out.println(threadName + " : selectionKey 为空，注册读事件 添加到map中");
                    selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                    map.put(selectionKey, producer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 唤醒Selector
                selector.wakeup();
            }
        }
    }

    public void unRegister(SocketChannel socketChannel) {
        if (socketChannel.isRegistered()) {
            SelectionKey selectionKey = socketChannel.keyFor(selector);
            if (selectionKey != null) {
                selectionKey.cancel();
                map.remove(selectionKey);
                selector.wakeup();
            }
        }
    }

    public void waiting() {
        synchronized (isWaiting) {
            try {
                System.out.println("isWaiting 加锁");
                isWaiting.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void starting() {
        synchronized (isWaiting) {
            System.out.println("释放isWaiting锁");
            isWaiting.notifyAll();
        }
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
                channel.register(selector,SelectionKey.OP_READ);
                // 唤醒selector
                selector.wakeup();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {

    }

}
