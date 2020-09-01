package io.bio;

import io.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO 服务端
 * 服务端使用线程池，主线程进行阻塞轮询，每一个客户端需要两个线程读写
 */
public class TcpServer{
    /**
     * 服务器端Socket
     */
    private ServerSocket serverSocket;
    /**
     * 线程池来进行客户端Socket的处理
     */
    private ExecutorService serverSocketThreadPool = Executors.newCachedThreadPool();

    public TcpServer(int port) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        System.out.println("服务端启动成功");
    }

    public static void main(String[] args) {
        try {
            new TcpServer(9999).start();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void start() {
        try{
            while (true){
                Socket accept = serverSocket.accept();
                if(accept == null){
                    continue;
                }
                System.out.println("客户端:" + accept.getInetAddress() + "连接到服务器");
                serverSocketThreadPool.execute(new ServerReadHandler(accept));
                serverSocketThreadPool.execute(new ServerWriteHandler(accept));
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            IOUtil.close(serverSocket);
        }
    }

    public void stop(){
        IOUtil.close(serverSocket);
        serverSocketThreadPool.shutdownNow();
    }

    public class ServerReadHandler implements Runnable{

        final Socket socket;

        public ServerReadHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            try{
                InputStream inputStream = socket.getInputStream();
                byte[] buff = new byte[1024];
                while (true){
                    // 添加休眠时间，防止CPU频繁轮询
                    Thread.sleep(10);
                    Arrays.fill(buff,(byte)0);;
                    int readSize = inputStream.read(buff);
                    if(readSize < 0){
                        continue;
                    }
                    System.out.println(socket.getLocalAddress() + ":" + socket.getPort() + "==>"+ (new String(buff,0,readSize)));
                }
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            } finally {
                IOUtil.close(socket);
            }
        }
    }

    public class ServerWriteHandler implements Runnable{

        final PrintStream printStream;

        public ServerWriteHandler(Socket socket) throws IOException {
            this.printStream = new PrintStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            Scanner scanner = null;
            try{
                scanner = new Scanner(System.in);
                String line = null;
                while (!"exit".equalsIgnoreCase((line = scanner.nextLine()))){
                    printStream.print(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                IOUtil.close(scanner,printStream);
            }
        }
    }
}
