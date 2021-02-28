package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static final int MB = 1024 * 1024;

	public static List<byte[]> split(File file, int size) throws IOException {
		List<byte[]> files = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		byte[] tmp = new byte[size * MB];
		@SuppressWarnings("resource")
		FileInputStream fileInputStream =  new FileInputStream(file);
		while(fileInputStream.read(tmp)!=-1) {
			files.add(tmp);
			tmp = new byte[size * MB];
		}		
		return files;
	}

}
