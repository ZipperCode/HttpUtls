package io.nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NioHttpServer implements Runnable {

	public static void main(String[] args) {
		NioHttpServer nioServer = new NioHttpServer(9999);
		new Thread(nioServer).start();
	}

	private final int mPort;
	private Selector mSelector;
	private ByteBuffer mReadByteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);
	private ByteBuffer mWriteByteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

	public NioHttpServer(int port) {
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
			while (true){
				if(mSelector.select(5) > 0){
					Iterator<SelectionKey> selectionKeyIterator = mSelector.selectedKeys().iterator();
					while (selectionKeyIterator.hasNext()){
						SelectionKey selectionKey = selectionKeyIterator.next();
						selectionKeyIterator.remove();
						try{
							if(selectionKey.isAcceptable()){
								accept(selectionKey);
							}else if(selectionKey.isReadable()){
								read(selectionKey);
							}else if(selectionKey.isWritable()){
								write(selectionKey);
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
			try {
				if (mSelector != null) {
					mSelector.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

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
	}

	private void read(SelectionKey key) throws IOException{
		// 清除缓冲区数据
		mReadByteBuffer.clear();
		SocketChannel channel = (SocketChannel) key.channel();
		// 读取数据到缓冲区
		channel.read(mReadByteBuffer);
		// 缓冲区游标归0
		mReadByteBuffer.flip();
		// 将缓冲区数据写出显示
		String printString = new String(mReadByteBuffer.array(),"UTF-8");
		System.out.println("收到来自：" +channel.getRemoteAddress() + " 的数据：" +printString);
		// 收到数据后注册写事件
		channel.register(mSelector,SelectionKey.OP_WRITE);
	}

	private void write(SelectionKey key) throws IOException{
		// 清除写缓冲区
		mWriteByteBuffer.clear();
		SocketChannel channel = (SocketChannel) key.channel();
		Scanner scanner = new Scanner(System.in);
//		String inputString = scanner.next();
		String inputString = "hahahahhahahah";
		inputString = buildResponse(inputString.length()) + inputString;
		mWriteByteBuffer.put(inputString.getBytes("UTF-8"));
		// 游标归0
		mWriteByteBuffer.flip();
		// 写数据
		channel.write(mWriteByteBuffer);
		// 注册读事件
		channel.register(mSelector,SelectionKey.OP_READ);
	}

	public String buildResponse(int contentSize){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("content-encoding: gzip").append("\r\n")
				.append("content-length:").append(contentSize).append("\r\n")
				.append("content-type:text/plain; charset=UTF-8").append("\r\n")
				.append("status:200").append("\r\n")
				.append("\r\n");
		return stringBuilder.toString();
	}
}
