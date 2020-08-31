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
}
