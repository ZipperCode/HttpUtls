package io;

public class Main {

    public static void main(String[] args) {
        Object object = new Object();
        System.out.println("wait before");
        try {
            object.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("wait after");
    }
}
