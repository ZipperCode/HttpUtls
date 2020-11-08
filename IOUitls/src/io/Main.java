package io;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        Class<?> aClass = Class.forName("java.lang.Class");
        Class<?> cClass = Class.forName("io.Main");
        System.out.println("Main - main => " + Main.class.getClassLoader());
        System.out.println("Main - main => " + Class.class.getClassLoader());
        Method getDeclareMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
        System.out.println(getDeclareMethod);
        Method mainTest1Method = (Method) getDeclareMethod.invoke(cClass, "test1", null);
        System.out.println(mainTest1Method);
        mainTest1Method.invoke(new Main());
        System.out.println("----------------------");
        Method getDeclaredConstructor = Class.class.getDeclaredMethod("getDeclaredConstructor", Class[].class);
        System.out.println(cClass.getConstructor(String.class));
        System.out.println(getDeclaredConstructor.invoke(cClass,new Class[]{String.class}));
        System.out.println(getDeclaredConstructor);

//        System.out.println(Arrays.toString(getDeclaredMethod));
    }

    public Main(){
        System.out.println("Main is init");
    }

    public Main(String s){
        System.out.println("Main is init 2");
    }

    public void test(String name, Class<?> ... params){
        System.out.println("test");
    }

    public void test1(){
        System.out.println("test1");
        System.out.println(getClass().getClassLoader());
    }
}
