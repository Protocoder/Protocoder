package com.makewithmoto.app.utils;

import java.util.UUID;

public class Utils {
	
	public static String generateRandomString() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

}
