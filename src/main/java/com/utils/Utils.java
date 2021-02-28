package com.utils;

import java.util.List;

public class Utils {

	public static final int MINUTE = 60*1000;
	
	public static boolean isNullOrEmpty(List<?> list) {
		return list==null || list.size()==0;
	}
	
	/**
	 * byte转int，大端存储
	 * @param bytes
	 * @return
	 */
	public static int byteToInt(byte[] bytes) {
		int res = 0;
		for(int i=0; i<bytes.length; i++) {
			res += bytes[i]<<((bytes.length-1-i)*8);
		}
		return res;
	}
	
}
