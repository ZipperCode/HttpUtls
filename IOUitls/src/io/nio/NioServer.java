package io.nio;

import io.utils.IOUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NioServer implements Runnable {

	public static void main(String[] args) {
		NioServer nioServer = new NioServer(10000);
		new Thread(nioServer).start();
	}

	private final int mPort;
	private Selector mSelector;
	private ByteBuffer mReadByteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);
	private ByteBuffer mWriteByteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

	private WriteHandler writeHandler = new WriteHandler();

	private List<SocketChannel> socketChannelList = new ArrayList<>();

	public NioServer(int port) {
		this.mPort = port;
		init();
	}

	public void init() {
		try {
			// 打开选择器
			mSelector = Selector.open();
			// 打开服务端通道
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			// 绑定一个服务器端口号
			serverSocketChannel.socket().bind(new InetSocketAddress(mPort));
			// 设置非阻塞状态
			serverSocketChannel.configureBlocking(false);
			// 通道注册连接事件到选择器上
			serverSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
			System.out.println("服务端启动成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
//			writeHandler.start();
			while (!Thread.interrupted()){
				if(mSelector.select() > 0){
					Iterator<SelectionKey> selectionKeyIterator = mSelector.selectedKeys().iterator();
					while (selectionKeyIterator.hasNext()){
						SelectionKey selectionKey = selectionKeyIterator.next();
						System.out.println("[Server] == > interestOps = " + selectionKey.interestOps());
						try{
							if(selectionKey.isAcceptable()){
								System.out.println("[Server] isAcceptable " + selectionKey);
								accept(selectionKey);
							}else if(selectionKey.isReadable()){
								System.out.println("[Server] isReadable " + selectionKey);
								read(selectionKey);
							}else if(selectionKey.isWritable()){
								System.out.println("[Server] isWritable " + selectionKey);
								write(selectionKey);
							}
							selectionKeyIterator.remove();
						}catch (IOException e){
							selectionKey.cancel();
							throw e;
						}
					}
				}
				TimeUnit.SECONDS.sleep(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(mSelector);
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
		// 注册读取事件
		socketChannel.register(mSelector,SelectionKey.OP_READ);
		socketChannelList.add(socketChannel);
	}

	private void read(SelectionKey key) throws IOException{
		// 清除缓冲区数据
		mReadByteBuffer.clear();
		SocketChannel channel = (SocketChannel) key.channel();
		// 读取数据到缓冲区
		int size = channel.read(mReadByteBuffer);
		// 缓冲区游标归0
		mReadByteBuffer.flip();
		System.out.println("position = " + mReadByteBuffer.position());
		System.out.println("limit = " + mReadByteBuffer.limit());
		System.out.println("remaining = " + mReadByteBuffer.remaining());
		channel.configureBlocking(false);
		// 将缓冲区数据写出显示
		String printString = new String(mReadByteBuffer.array(),"UTF-8");
		System.out.println("收到来自：" +channel.getRemoteAddress() + " 的数据：" +printString);
		// 收到数据后注册写事件
		channel.register(mSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}

	private void write(SelectionKey key) throws IOException{
		// 清除写缓冲区
		mWriteByteBuffer.clear();
		SocketChannel channel = (SocketChannel) key.channel();
		Scanner scanner = new Scanner(System.in);
		String text = scanner.nextLine();
		mWriteByteBuffer.put(text.getBytes());
		mWriteByteBuffer.flip();
		// 写数据
		channel.write(mWriteByteBuffer);
		// 注册读事件
		channel.register(mSelector,SelectionKey.OP_WRITE | SelectionKey.OP_READ);
	}

	private void write(String msg){
		try {
			mWriteByteBuffer.clear();
			mWriteByteBuffer.put(msg.getBytes("UTF-8"));
			mWriteByteBuffer.flip();
			for (SocketChannel socketChannel : socketChannelList){
				System.out.println("客户端socket注册写 " + socketChannel.keyFor(mSelector));
				socketChannel.register(mSelector,SelectionKey.OP_WRITE);
			}
		} catch (ClosedChannelException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public class WriteHandler extends Thread{
		@Override
		public void run() {
			super.run();
			Scanner scanner = new Scanner(System.in);
			while (!Thread.interrupted()){
				System.out.print("请输入要发送到客户端的字符：");
				String text = scanner.nextLine();
				write(text);
			}
		}
	}
}
