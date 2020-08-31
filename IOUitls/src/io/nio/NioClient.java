package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioClient implements Runnable{


	public static void main(String[] args) {
		NioClient nioClient = new NioClient("localhost",9090);
		new Thread(nioClient).start();
	}

	private SocketChannel mClientSocketChannel;

	private ByteBuffer mByteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);

	public NioClient(String remoteAddress,int port) {
		try {
			mClientSocketChannel = SocketChannel.open();
			mClientSocketChannel.connect(new InetSocketAddress(remoteAddress,port));
			System.out.println("客户端启动成功");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try{
			Scanner scanner = new Scanner(System.in);
			while (true){
				mByteBuffer.clear();
				System.out.print("请输入要发送的数据：");
				String inputString = scanner.next();
				mByteBuffer.put(inputString.getBytes("UTF-8"));
				mByteBuffer.flip();
				mClientSocketChannel.write(mByteBuffer);
				mByteBuffer.clear();
				if(mClientSocketChannel.read(mByteBuffer) < 0){
					continue;
				}
				mByteBuffer.flip();
				String receiveString = new String(mByteBuffer.array(),"UTF-8");
				System.out.println(receiveString);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try{
				if(mClientSocketChannel != null){
					mClientSocketChannel.close();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
