package com.utils;

public class UnsignedNumeric {
	/**
	 * 	大端存储
	 */
	private byte[] bytes;

	public UnsignedNumeric(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public long getVal() {
		long res = 0;
		for(int i=0; i<bytes.length; i++) {
			int byteOffset = 8*(bytes.length-i-1);
			long byteVal = Byte.toUnsignedLong(bytes[i]) << byteOffset;
			res += byteVal;
		}
		return res;
	}
	
}
