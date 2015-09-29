package com.mrbu.androidxmlparser.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class IOUtils {

	private static final String UTF_8 = "utf-8";

	public static String stream2String(InputStream in) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int len=0;
		try {
			byte[] buffer=new byte[1024];
			while((len=in.read(buffer))!=-1){
				baos.write(buffer, 0, len);
			}
			byte[] byteArray = baos.toByteArray();
			return new String(byteArray,UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			close(in,baos);
		}
		return "";
	}
	/**
	 * 关闭流
	 */
	public static boolean close(Closeable...io) {
		for(Closeable i:io){
			if (i != null) {
				try {
					i.close();
				} catch (IOException e) {
				}
			}
		}
		return true;
	}
	
}
