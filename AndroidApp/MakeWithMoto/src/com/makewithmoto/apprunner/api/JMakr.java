package com.makewithmoto.apprunner.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android_serialport_api.SerialPort;

import com.makewithmoto.apidoc.APIAnnotation;

public class JMakr extends JInterface {
	
	private boolean isStarted = false;
	private static final String TAG = "JMakr";
	private static final String MAKR_ENABLE = "/sys/class/makr/makr/5v_enable";
	
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String callbackfn;
	private String receivedData;

	public JMakr(Activity a) {
		super(a);
	}
	
	@JavascriptInterface
	@APIAnnotation(description = "makes the phone vibrate", example = "android.vibrate(500);")	
	public void start() {
		
		try {
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			
		

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			// DisplayError(R.string.error_security);
		} catch (IOException e) {
			// DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			// DisplayError(R.string.error_configuration);
		}

		
		
		initializeMAKR();
		isStarted = true;
	}
	
	@JavascriptInterface
	@APIAnnotation(description = "makes the phone vibrate", example = "android.vibrate(500);")
	public void stop() {
		if (mReadThread != null)
			mReadThread.interrupt();
		closeSerialPort();
		mSerialPort = null;
	}

	
	private class ReadThread extends Thread {
		

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				final int size;
				try {
					final byte[] buffer = new byte[64];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						
						c.get().runOnUiThread(new Runnable() {
							public void run() {

								receivedData = new String(buffer, 0, size);
								callback("OnSerialRead("+receivedData+");");    
							}
						});

						/*
						for (final MAKrListener l : listeners) {
							l.onRawDataReceived(buffer, size);

							c.runOnUiThread(new Runnable() {
								public void run() {

									receivedData = new String(buffer, 0, size);
									String[] data = receivedData.split("::");
									
									l.onMessageReceived(data[0], data[1]);
								}
							});

						}
						
						*/
						
						
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	
	
	public boolean isEnabled() {
		boolean status = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(MAKR_ENABLE));
			String rv = br.readLine();
			if (rv.matches("on")) {
				return true;
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return status;
	}

	/*
	 * Turn on or off the device
	 */
	public void enable(boolean value) {
		BufferedWriter writer = null;
		try {
			FileOutputStream fos = new FileOutputStream(MAKR_ENABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			writer = new BufferedWriter(osw);
			if (value)
				writer.write("on\n");
			else
				writer.write("off\n");
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void initializeMAKR() {

		// enable the device
		if (!isEnabled()) {
			enable(true);
		}
	}

	@JavascriptInterface
	@APIAnnotation(description = "makes the phone vibrate", example = "android.vibrate(500);")
	public void writeSerial(String cmd) {

		try {
			cmd = cmd + "\n";
			mOutputStream.write(cmd.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	
	/* Serial port stuff */ 
	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			String path = "/dev/ttyHSL2";
			int baudrate = 9600;

			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null; 
		}
	}
		
	
	public void resume() {
		if (isStarted ) { 
			start();		
		}
	}
	
	public void pause() {
		if (isStarted ) { 
			stop();		
		}
	}

}