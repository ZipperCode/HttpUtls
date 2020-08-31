package io.bio;

import com.io.utils.IOUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * BIO 客户端
 * 一个客户端需要三个线程：主线程、读线程、写线程
 */
public class TcpClient {

    private Socket socket;

    public TcpClient(String host,int port) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host,port));
        System.out.println("客户端启动成功");
    }

    public static void main(String[] args) {
        try {
            new TcpClient("localhost",9090).start();
            new TcpClient("localhost",9090).start();
            // 等待子线程结束
            Thread.currentThread().join();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }

    public void start() {
        try{
            new Thread(new ClientReadHandler(socket)).start();
            new Thread(new ClientWriteHandler(socket)).start();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            IOUtil.close(socket);
        }
    }

    public class ClientReadHandler implements Runnable {

        InputStream inputStream;

        public ClientReadHandler(Socket socket) throws IOException {
            this.inputStream = socket.getInputStream();
        }

        @Override
        public void run() {
            try{
                byte [] buffer = new byte[512];
                while (socket.isConnected()){
                    Arrays.fill(buffer,(byte)0);
                    int size = socket.getInputStream().read(buffer);
                    if(size > 0){
                        System.out.println("收到服务端 "+ socket.getRemoteSocketAddress() +"数据==>" + (new String(buffer,0,size)));
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                IOUtil.close(inputStream);
            }
        }
    }

    public class ClientWriteHandler implements Runnable{

        PrintStream printStream;

        public ClientWriteHandler(Socket socket) throws IOException {
            printStream = new PrintStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            InputStream inputStream = System.in;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try{
                String line = null;
                while (socket.isConnected()){
                    line = bufferedReader.readLine();
                    printStream.print(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                IOUtil.close(printStream);
            }
        }
    }
}
