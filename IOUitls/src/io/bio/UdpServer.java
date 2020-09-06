package io.bio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

public class UdpServer implements Runnable {

    public static void main(String[] args) {
        new Thread(new UdpServer()).start();
    }

    private DatagramSocket datagramSocket;

    private byte[] buffer = new byte[1024];

    public UdpServer() {
        try {
            this.datagramSocket = new DatagramSocket(1234);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length );
        Scanner scanner = new Scanner(System.in);
        try{
            while (!Thread.interrupted()){
                datagramSocket.receive(datagramPacket);
                System.out.println(new String(buffer));
                String s = scanner.nextLine();
                DatagramPacket datagramPacket1 = new DatagramPacket(s.getBytes(),s.length() );
                datagramSocket.send(datagramPacket1);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
