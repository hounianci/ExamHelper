package com.utils.video;

import java.io.FileInputStream;
import java.io.IOException;

import com.utils.UnsignedNumeric;
import com.utils.Utils;
import com.utils.video.flv.FLVHeader;
import com.utils.video.flv.FLVTag;

public class VideoUtils {

	public static int FLV_SIGNATURE = (0x46<<16)+(0x4c<<8)+(0x56);
	
	public static FLVHeader readFLVHeader(FileInputStream inputStream) throws IOException {
		FLVHeader flvHeader = new FLVHeader();
		byte[] tmp = new byte[3];
		inputStream.read(tmp);
		int signature = Utils.byteToInt(tmp);
		if(FLV_SIGNATURE != signature) {
			throw new RuntimeException("file type error.");
		}
		tmp = new byte[1];
		inputStream.read(tmp);
		short version = tmp[0];
		flvHeader.setVersion(version);
		inputStream.read(tmp);
		short typeFlags = tmp[0];
		flvHeader.setTypeFlags(typeFlags);
		tmp = new byte[4];
		inputStream.read(tmp);
		long dataOffset = new UnsignedNumeric(tmp).getVal();
		flvHeader.setDataOffset(dataOffset);
		return flvHeader;
	}
	
	public static FLVTag readFLVTag(FileInputStream inputStream) throws IOException {
		FLVTag tag = new FLVTag();
		byte[] tmp = new byte[1];
		inputStream.read(tmp);
		byte fliter = (byte) (tmp[0]&(1<<1));
		byte tagType = (byte) (tmp[0]&(1<<5));
		tag.setFilter(fliter);
		tag.setTagType(tagType);
		
		tmp = new byte[3];
		inputStream.read(tmp);
		int dataSize = (int) new UnsignedNumeric(tmp).getVal();
		tag.setDataSize(dataSize);

		tmp = new byte[3];
		inputStream.read(tmp);
		int timestamp = (int) new UnsignedNumeric(tmp).getVal();
		tag.setTimestamp(timestamp);
		
		tmp = new byte[1];
		int timestampExt = (int) new UnsignedNumeric(tmp).getVal();
		tag.setTimestampExtended(timestampExt);
		
		tmp = new byte[3];
		inputStream.read(tmp);

		tmp = new byte[1];
		inputStream.read(tmp);
		readScriptTagData(tag, inputStream);
		return tag;
	}
	
	public static void readScriptTagData(FLVTag tag, FileInputStream inputStream) throws IOException {
		byte[] tmp = new byte[1];
		inputStream.read(tmp);
		int type = (int) new UnsignedNumeric(tmp).getVal();
		System.out.println(Integer.toHexString(type));
		tmp = new byte[2];
		inputStream.read(tmp);
		int len = (int) new UnsignedNumeric(tmp).getVal();
		tmp = new byte[0xA];
		inputStream.read(tmp);
		System.out.println(new String(tmp));
		
		tmp = new byte[1];
		inputStream.read(tmp);
		int type2 = (int) new UnsignedNumeric(tmp).getVal();
		tmp = new byte[4];
		inputStream.read(tmp);
		long arrayLen = new UnsignedNumeric(tmp).getVal();
		for(int i=0; i<arrayLen; i++) {
			tmp = new byte[2];
			inputStream.read(tmp);
			int eleLen = (int) new UnsignedNumeric(tmp).getVal();
			tmp = new byte[eleLen];
			inputStream.read(tmp);
			System.out.println(new String(tmp));
			tmp = new byte[8];
			inputStream.read(tmp);
			long val = new UnsignedNumeric(tmp).getVal();
			System.out.println(val);
			tmp = new byte[1];
			inputStream.read(tmp);
			int sign = (int) new UnsignedNumeric(tmp).getVal();
			System.out.println(sign);
		}
	}
	
}
