package io.nio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NioClient extends Thread {

    public static void main(String[] args) {
        new NioClient("localhost",10000).start();
    }

    private Selector selector;

    private SocketChannel clientSocketChannel;

    private ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

    private ByteBuffer byteReadBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

    private ByteBuffer byteWriteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

    private WriteHandler writeHandler = new WriteHandler();

    public NioClient(String remoteAddress, int port) {
        try {
            selector = Selector.open();
            clientSocketChannel = SocketChannel.open();
            clientSocketChannel.configureBlocking(false);
            if (clientSocketChannel.connect(new InetSocketAddress(remoteAddress, port))) {
//				// 当前是阻塞模式
                clientSocketChannel.register(selector, SelectionKey.OP_READ);
            } else {
//				// 当前是非阻塞模式
                clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
            System.out.println("客户端启动成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
			writeHandler.start();
            while (!Thread.interrupted()) {
                if (selector.select() == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
					clientSocketChannel = (SocketChannel) key.channel();
                    if (key.isConnectable() && clientSocketChannel.finishConnect()) {
                        System.out.println("[Client] isConnectable ");
						clientSocketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        clientSocketChannel.read(byteReadBuffer);
                        System.out.println("[Client] isReadable ==> " + (new String(byteReadBuffer.array())));
                        clientSocketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isWritable()) {
                        System.out.println("[Client] isWritable");
                        System.out.println("key.readyOps() & ~SelectionKey.OP_WRITE ====> " + (key.readyOps() & ~SelectionKey.OP_WRITE));
                        key.interestOps(key.readyOps() & ~SelectionKey.OP_WRITE);
                        clientSocketChannel.register(selector, SelectionKey.OP_READ);
						clientSocketChannel.write(byteWriteBuffer);
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocketChannel != null) {
                    clientSocketChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	public void write(String msg) {
		try {
			byteWriteBuffer.clear();
			byteWriteBuffer.put(msg.getBytes("UTF-8"));
			byteWriteBuffer.flip();
			clientSocketChannel.register(selector,SelectionKey.OP_WRITE);
			selector.wakeup();
		} catch (ClosedChannelException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	public class WriteHandler extends Thread{
		@Override
		public void run() {
			super.run();
			Scanner scanner = new Scanner(System.in);
			System.out.println("请输入要发送服务端消息：");
			while (!Thread.interrupted()){
				String text = scanner.nextLine();
				write(text);
			}
		}
	}
}
