package com.makewithmoto.utils;

import java.util.UUID;

public class StrUtils {

	public static String generateRandomString() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return uuid;
	}
	
	
	public static String bytetostring(byte[] b) {
		int j = 0, in = 0;
		String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
				"F" };
		String out = "";

		for (int i = 0; i < b.length; ++i) {
			in = (int) b[i] & 0x0f;
			j = (in >> 4) & 0x0f;
			out += hex[j];
			j = in & 0xf;
			out += hex[j];

		}

		return out;
	} 



}
