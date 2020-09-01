package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NioClient implements Runnable{


	public static void main(String[] args) {
		NioClient nioClient = new NioClient("localhost",9999);
		new Thread(nioClient).start();
	}

	private Selector selector;

	private SocketChannel clientSocketChannel;

	private ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

	public NioClient(String remoteAddress,int port) {
		try {
			selector = Selector.open();
			clientSocketChannel = SocketChannel.open();
			clientSocketChannel.configureBlocking(false);
			if(clientSocketChannel.connect(new InetSocketAddress(remoteAddress,port))){
//				// 当前是阻塞模式
				clientSocketChannel.register(selector, SelectionKey.OP_READ);
			}else{
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
		try{
			Scanner scanner = new Scanner(System.in);
			while (!Thread.interrupted()){
				if (selector.select() > 0){
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while (iterator.hasNext()){
						SelectionKey key = iterator.next();
						iterator.remove();
						if(key.isValid()){
							SocketChannel socketChannel = (SocketChannel) key.channel();
							if(key.isConnectable() && socketChannel.finishConnect()){
								System.out.println("[Client] isConnectable ");
//								socketChannel.register(selector,SelectionKey.OP_WRITE);
								socketChannel.register(selector,SelectionKey.OP_READ | SelectionKey.OP_WRITE);
							}else if(key.isReadable()){
								System.out.println("[Client] isReadable ");
								byteBuffer.clear();
								int read = socketChannel.read(byteBuffer);
								System.out.println("收到服务端数据 ： "+new String(byteBuffer.array()));
//								socketChannel.register(selector,SelectionKey.OP_READ);
							} else if(key.isWritable()){
								System.out.println("[Client] isWritable");
								System.out.print("请输入要发送到服务端的字符：");
								String text = scanner.nextLine();
								byteBuffer.clear();
								byteBuffer.put(text.getBytes());
								byteBuffer.flip();
								socketChannel.write(byteBuffer);
//								socketChannel.register(selector,SelectionKey.OP_WRITE);

							}
						}
					}
				}
//				byteBuffer.clear();
//				System.out.print("请输入要发送的数据：");
//				String inputString = scanner.next();
//				byteBuffer.put(inputString.getBytes("UTF-8"));
//				byteBuffer.flip();
//				clientSocketChannel.write(byteBuffer);
//				byteBuffer.clear();
//				if(clientSocketChannel.read(byteBuffer) < 0){
//					continue;
//				}
//				byteBuffer.flip();
//				String receiveString = new String(byteBuffer.array(),"UTF-8");
//				System.out.println(receiveString);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try{
				if(clientSocketChannel != null){
					clientSocketChannel.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

	private void doWrite(SocketChannel socketChannel) throws IOException {
		byteBuffer.clear();
		byteBuffer.put("hello".getBytes());
		byteBuffer.flip();
		clientSocketChannel.write(byteBuffer);
	}
}
