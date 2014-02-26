/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.hardware;

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

import android.util.Log;
import android_serialport_api.SerialPort;

public class MAKRBoard {

	private boolean isStarted = false;
	private static final String TAG = "MAKRBoard";
	private static final String MAKR_ENABLE = "/sys/class/makr/makr/5v_enable";

	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private String receivedData;

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

	public void powerOn() {
		// enable the device
		if (!isEnabled()) {
			enable(true);
		}
	}

	public void powerOff() {
		if (isEnabled()) {
			enable(false);
		}
	}

	public void start() {

		try {
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
		} catch (SecurityException e) {
			// DisplayError(R.string.error_security);
		} catch (IOException e) {
			// DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			// DisplayError(R.string.error_configuration);
		}

		powerOn();
		isStarted = true;
	}

	public void stop() {
		closeSerialPort();
		mSerialPort = null;
		powerOff();
	}

	public String readSerial() {

		final int size;
		try {
			final byte[] buffer = new byte[64];
			if (mInputStream == null)
				return "";
			size = mInputStream.read(buffer);
			if (size > 0) {
				receivedData = new String(buffer, 0, size);
			} else {
				receivedData = "";
			}
			// Log.d(TAG,"Rx data : "+receivedData);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return receivedData;

	}

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
			if ((path.length() == 0) || (baudrate == -1)) {
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
		if (isStarted) {
			start();
		}
	}

	public void pause() {
		if (isStarted) {
			stop();
		}
	}

}