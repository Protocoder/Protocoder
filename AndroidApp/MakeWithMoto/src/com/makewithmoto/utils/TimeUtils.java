package com.makewithmoto.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtils {

	static public String getCurrentTime() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		return sdf.format(cal.getTime());

	} 
	

}
