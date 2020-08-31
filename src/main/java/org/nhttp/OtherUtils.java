package org.nhttp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherUtils {
    public static String downloadRename(String srcFileName){
        String parentPath = new File(srcFileName).getParent();
        System.out.println("parentPath = " + parentPath);
        return appendFileName(srcFileName,0);
    }

//    public static String appendFileName(String srcFileName, int index){
//        File file = new File(srcFileName);
//        if(!file.exists()){
//            return srcFileName;
//        }
//        String fileName = file.getName();
//        String parentPath = Paths.get(srcFileName).getParent().toString();
//        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
//        fileName = fileName.substring(0,fileName.lastIndexOf("."));
//        if(fileName.matches("(.+)\\(\\d*\\)")){
//            fileName = fileName.replaceAll("\\(\\d*\\)","(" + (++index) + ")");
//        }else{
//            fileName = fileName + "(" +(++index)+ ")";
//        }
//        return appendFileName(parentPath + File.separator + fileName +"."+ ext,index);
//    }


    public static String appendFileName(String srcFileName, int index){
        File file = new File(srcFileName);
        if(!file.exists()){
            return srcFileName;
        }
        String fileName = file.getName();
        String parentPath = file.getParent();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = fileName.substring(0,fileName.lastIndexOf("."));
        if(fileName.matches("(.+)\\(\\d*\\)")){
            fileName = fileName.replaceAll("\\(\\d*\\)","(" + (++index) + ")");
        }else{
            fileName = fileName + "(" +(++index)+ ")";
        }
        return appendFileName(parentPath + File.separator + fileName +"."+ ext,index);
    }

    public static void main(String[] args) throws IOException {
//        System.out.println("D:\\\\1(1)".matches("(.+)\\(\\d*\\)"));
//        Pattern r = Pattern.compile("\\d*");
//
//        // 现在创建 matcher 对象
//        Matcher m = r.matcher("D:\\\\(1)");
//        System.out.println(m.find());
        String fileName = "D://1.txt";
        File file = new File(fileName);
        if(!file.exists()){
            file.createNewFile();
        }
        String newFileName1 = downloadRename(fileName);
        System.out.println(newFileName1);
        File newFile1 = new File(newFileName1);
        if(!newFile1.exists()){
            newFile1.createNewFile();
        }
        String newFileName2 = downloadRename(fileName);
        System.out.println(newFileName2);
        File newFile2 = new File(newFileName2);
        if(!newFile2.exists()){
            newFile2.createNewFile();
        }

    }
}
