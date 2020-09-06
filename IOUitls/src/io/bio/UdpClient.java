package io.bio;

import io.utils.IOUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.Selector;
import java.util.Scanner;

public class UdpClient implements Runnable {

    public static void main(String[] args) {
        new Thread(new UdpClient()).start();
    }

    private DatagramSocket datagramSocket;

    private byte[] buffer = new byte[1024];

    public UdpClient() {
        try {
            this.datagramSocket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
       try{
           InetAddress inetAddress = InetAddress.getByName("localhost");
           while (!Thread.interrupted()){
               String s = scanner.nextLine();
               DatagramPacket datagramPacket = new DatagramPacket(s.getBytes(), 5,s.length() - 5,inetAddress,1234);
               datagramSocket.send(datagramPacket);

               DatagramPacket recPacket = new DatagramPacket(buffer, buffer.length);
               datagramSocket.receive(recPacket);
               System.out.println(new String(buffer));
           }
       }catch (IOException e){
           e.printStackTrace();
       }finally {
           IOUtil.close(datagramSocket);
       }
    }
}
