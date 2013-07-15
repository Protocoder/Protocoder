package com.makewithmoto.fragments;
/**
 * Class containing some useful functions when working with graphics 
 * 
 * taken from Processing.org source code 
 * 
 */


import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;
import android.view.View;

public class Utils {

	static public int getAge(int _year, int _month, int _day) {

		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(_year, _month, _day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH))
				|| ((m == cal.get(Calendar.MONTH)) && (d < cal.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if (a < 0)
			throw new IllegalArgumentException("Age < 0");
		return a;
	}

	//TODO: activar el jpeg provider
	public void handleConnection(Socket socket /* ,JpegProvider jpegProvider */) throws Exception {
		// byte[] data = jpegProvider.getJpeg();
		OutputStream outputStream = socket.getOutputStream();
		outputStream
				.write(("HTTP/1.0 200 OK\r\n" + "Server: YourServerName\r\n"
						+ "Connection: close\r\n" + "Max-Age: 0\r\n" + "Expires: 0\r\n"
						+ "Cache-Control: no-cache, private\r\n" + "Pragma: no-cache\r\n"
						+ "Content-Type: multipart/x-mixed-replace; "
						+ "boundary=--BoundaryString\r\n\r\n").getBytes());
		while (true) {
			// data = jpegProvider.getJpeg();
			outputStream.write(("--BoundaryString\r\n" + "Content-type: image/jpg\r\n"
					+ "Content-Length: " +
					// data.length +
					"\r\n\r\n").getBytes());
			// outputStream.write(data);
			outputStream.write("\r\n\r\n".getBytes());
			outputStream.flush();
		}
	} 

	static public void rescale(View view) {
		Bitmap originalImage = null;

		originalImage = Bitmap.createScaledBitmap(originalImage, view.getWidth(), view.getHeight(),
				true);

	}

	static public String getCurrentTime() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		return sdf.format(cal.getTime());

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
