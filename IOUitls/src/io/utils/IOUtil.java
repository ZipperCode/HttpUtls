package io.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtil {

    public static void close(Closeable...closes){
        if(closes == null){
            return;
        }
        try {
            for (Closeable close : closes) {
                close.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String byteHexToString(byte [] data){
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        for (byte b : data){
            stringBuilder.append(String.format("%02X ",b));
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        byte[] b1 = new byte[10];
        b1[0] = 1;
        b1[1] = 10;
        b1[2] = 11;
        b1[3] = 15;
        System.out.println(byteHexToString(b1));
    }
}
