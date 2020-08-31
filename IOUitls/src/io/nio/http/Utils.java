package io.nio.http;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utils {

	public static byte[] gzip(byte[] plain){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = null;
		byte[] result = new byte[0];
		try{
			gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
			gzipOutputStream.write(plain);
			gzipOutputStream.finish();
			result = byteArrayOutputStream.toByteArray();
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			try {
				if(gzipOutputStream != null){
					gzipOutputStream.close();
				}
				byteArrayOutputStream.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	public static byte[] unGzip(byte[] gzipString){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream byteArrayInputStream = null;
		GZIPInputStream gzipInputStream = null;
		byte[] result = new byte[0];
		try{
			byteArrayInputStream = new ByteArrayInputStream(gzipString);
			gzipInputStream = new GZIPInputStream(byteArrayInputStream);
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = gzipInputStream.read(buff)) > 0){
				byteArrayOutputStream.write(buff,0,len);
			}
			byteArrayOutputStream.flush();
			result = byteArrayOutputStream.toByteArray();
		} catch (IOException |UncheckedIOException e){
			e.printStackTrace();
		} finally {
			try{
				if(byteArrayInputStream != null){
					byteArrayInputStream.close();
				}
				byteArrayOutputStream.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "hello world";
		byte[] b = gzip(s.getBytes("UTF-8"));
		String s1 = new String(b);
		System.out.println(s1);
		System.out.println(new String(unGzip(b),"UTF-8"));
	}
}
