package com.makewithmoto.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class TimeUtils {
	
	public static String generateRandomString() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}
	

	static public String getCurrentTime() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		return sdf.format(cal.getTime());

	} 
	

}
