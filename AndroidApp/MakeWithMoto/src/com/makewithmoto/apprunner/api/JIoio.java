package com.makewithmoto.apprunner.api;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;
import ioio.lib.api.Uart.Parity;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.hardware.HardwareCallback;
import com.makewithmoto.hardware.IOIOBoard;
import com.makewithmoto.network.ALog;
import com.makewithmoto.sensors.WhatIsRunning;
import com.makewithmoto.utils.Utils;

public class JIoio extends JInterface implements HardwareCallback {

	private IOIOBoard board;
	protected IOIO ioio;
	HashMap<String, JSONObject> mObjectsFromJS = new HashMap<String, JSONObject>();
	protected HashMap<String, Object> components;
	
	public JIoio(FragmentActivity fragmentActivity) {
		super(fragmentActivity);
		
		this.board = new IOIOBoard(fragmentActivity, this);
	//	this.components = c.get().getComponents();
		
		Log.d("jIOIO", "Starting board");
		board.powerOn();
		WhatIsRunning.getInstance().add(board);
	}
	
	public void setReady() {
		//applicationWebView.runJavascript("window['ioio']._setReady();");
		//TODO
	}
	
	@Override
	public void destroy() {
		super.destroy();
		Log.d("jIOIO", "Destroying service");
		board.powerOff();
		board = null;
	}

	// /// COMPONENTS
	@JavascriptInterface
	public void createDigitalOutput(String pin, final String asName,
			String callbackfn) {
		int pinNum = Integer.parseInt(pin);
		String randomName = Utils.generateRandomString();

		try {
			ioio.lib.api.DigitalOutput dio = this.ioio.openDigitalOutput(pinNum);
			components.put(randomName, dio);
			Log.d(TAG, "Calling callback: " + callbackfn + "('" + asName + "', '" + randomName + "');");
		//	applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void writeDigitalOutput(String name, String state) {
		DigitalOutput comp = (DigitalOutput) components.get(name);
		try {
			comp.write(Boolean.parseBoolean(state));
		} catch (ConnectionLostException e) {
			e.printStackTrace();
		}
	}

	@JavascriptInterface
	public void createDigitalInput(String pin, final String asName,
			String callbackfn) {
		ALog.i("Creating digital input on pin " + pin + " as " + asName);
		int pinNum = Integer.parseInt(pin);
		String randomName = Utils.generateRandomString();
		try {

			DigitalInput dio = ioio.openDigitalInput(pinNum);
			components.put(randomName, dio);
			//applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void readDigitalInput(String name, final String callbackfn) {
		DigitalInput comp = (DigitalInput) components.get(name);
		boolean v;
		try {
			v = comp.read();
		//	applicationWebView.runJavascript("window['" + callback + "']('" + v + "');");
			callback(callbackfn);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void blockReadDigitalInput(String name, final String untilState,
			final String callbackfn) {
		ALog.i("Creating digital blocking read on pin for " + name + " until "
				+ untilState);
		final DigitalInput comp = (DigitalInput) components.get(name);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				try {
					comp.waitForValue(Boolean.valueOf(untilState));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ConnectionLostException e) {
					e.printStackTrace();
				}
			//	applicationWebView.runJavascript("window['" + callback + "']();");
				callback(callbackfn);
			}
		};
		(new Thread(r)).start();
	}

	// / ANALOG
	@JavascriptInterface
	public void createAnalogInput(final String pin, final String asName,
			final String callbackfn) {
		ALog.i("Creating analog input on pin " + pin + " as " + asName);
		int pinNum = Integer.parseInt(pin);
		String randomName = Utils.generateRandomString();

		AnalogInput dio;
		try {
			dio = ioio.openAnalogInput(pinNum);
			components.put(randomName, dio);
//			applicationWebView.runJavascript("setAnalogInput('" + asName
//					+ "', '" + randomName + "','" + callback + "');");
		//	applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void readAnalogInput(String name, final String callbackfn) {
		AnalogInput comp = (AnalogInput) components.get(name);
		float v;
		try {
			v = comp.read();
			//applicationWebView.runJavascript("window['" + callback + "']('" + v
			//		+ "');");
			callback(callbackfn);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void createPwmOutput(String pin, String freq, final String asName,
			final String callbackfn) {
		ALog.i("Creating pwm output on pin " + pin + " as " + asName);
		int pinNum = Integer.parseInt(pin);
		int freqHz = Integer.parseInt(freq);
		String randomName = Utils.generateRandomString();

		PwmOutput pwm;
		try {
			pwm = ioio.openPwmOutput(pinNum, freqHz);
			components.put(randomName, pwm);
		//	applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void setDutyCycle(String name, String num) {
		PwmOutput comp = (PwmOutput) components.get(name);
		try {
			comp.setDutyCycle(Float.parseFloat(num));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void setPulseWidth(String name, String num) {
		PwmOutput comp = (PwmOutput) components.get(name);
		try {
			comp.setPulseWidth(Float.parseFloat(num));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	// ////////////////////////////////////
	// PulseInput
	// ////////////////////////////////////
	@JavascriptInterface
	public void createPulseInput(String pin, final String asName,
			final String callbackfn) {
		ALog.i("Creating pwm output on pin " + pin + " as " + asName);
		int pinNum = Integer.parseInt(pin);
		String randomName = Utils.generateRandomString();

		PulseInput pulse;
		try {
			pulse = ioio.openPulseInput(pinNum, PulseMode.POSITIVE);

			components.put(randomName, pulse);
			//applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void getPulseInputDuration(final String name, final String callbackfn) {
		PulseInput pulse = (PulseInput) components.get(name);
		float v;
		try {
			v = pulse.getDuration();
			callback(callbackfn);
		//	applicationWebView.runJavascript("window['" + callback + "']('" + v
		//			+ "');");
		} catch (InterruptedException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void getPulseFrequency(final String name, final String callbackfn) {
		PulseInput pulse = (PulseInput) components.get(name);
		float v;
		try {
			v = pulse.getFrequency();
	//		applicationWebView.runJavascript("window['" + callback + "']('" + v
	//				+ "');");
			callback(callbackfn);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	// ////////////////////////
	// UART
	// ////////////////////////
	@JavascriptInterface
	public void createUart(String rpin, String tpin, String strBaud,
			final String asName, final String callbackfn) {
		ALog.i("Creating uart on pin " + rpin + " and " + tpin + " as "
				+ asName);
		int rPinNum = Integer.parseInt(rpin);
		int tPinNum = Integer.parseInt(tpin);
		int baud = Integer.parseInt(strBaud);
		String randomName = Utils.generateRandomString();

		Uart uart;
		try {
			uart = ioio.openUart(rPinNum, tPinNum, baud, Parity.NONE,
					Uart.StopBits.ONE);

			components.put(randomName, uart);
//			applicationWebView.runJavascript("setUart('" + asName + "', '"
//					+ randomName + "','" + callback + "');");
			//applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void readUart(final String name, final String callbackfn) {
		Uart uart = (Uart) components.get(name);
		InputStream in = uart.getInputStream();
		StringBuilder sb = new StringBuilder();
		String output;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UT8-F"));
			while ((output = reader.readLine()) != null) {
				sb.append(output).append("\n");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			sb.append(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			sb.append(e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				sb.append(e.getMessage());
			}
		}
		
		callback(callbackfn);
	//	applicationWebView.runJavascript("window['" + callback + "']('"
	//			+ sb.toString() + "');");
	}

	@JavascriptInterface
	public void writeUart(String name, String toWrite, final String callbackfn) {
		Uart uart = (Uart) components.get(name);
		OutputStream out = uart.getOutputStream();
		Boolean successful = false;

		try {
			byte[] bytes = toWrite.getBytes(Charset.forName("UT8-F"));
			out.write(bytes);
			successful = true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

	//	applicationWebView.runJavascript("window['" + callback + "']('"
	//			+ successful.toString() + "');");
		
		callback(callbackfn);
	}

	// /////////////////////////////
	// Serial Peripheral Interface
	// /////////////////////////////
	@JavascriptInterface
	public void createSpi(String misoPin, String mosiPin, String clkPin,
			String sspins, final String asName, final String callbackfn) {
		ALog.i("Creating SPI on pin " + misoPin + ", " + mosiPin + " (clock: "
				+ clkPin + ") with slaves " + sspins);
		int misoPinInt = Integer.parseInt(misoPin);
		int mosiPinInt = Integer.parseInt(mosiPin);
		int clockPinInt = Integer.parseInt(clkPin);
		List<String> slavePins1 = Arrays.asList(sspins.split("\\s*,\\s*"));
		int[] slavePins = new int[slavePins1.size()];
		for (int i = 0; i < slavePins1.size(); i++) {
			slavePins[i] = Integer.parseInt(slavePins1.get(i));
		}

		String randomName = Utils.generateRandomString();

		SpiMaster spi;
		try {
			spi = ioio.openSpiMaster(misoPinInt, mosiPinInt, clockPinInt,
					slavePins, SpiMaster.Rate.RATE_1M);

			components.put(randomName, spi);
		//	applicationWebView.runJavascript("window['" + callback + "']('" + asName + "', '" + randomName + "');");
			callback(callbackfn);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void writeReadSPI(String name, String slaveNum, String data,
			final String callbackfn) {
		// byte[] request = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };
		// byte[] response = new byte[4];
		// spi.writeRead(0, request, request.length, 7, response,
		// response.length);
		SpiMaster spi = (SpiMaster) components.get(name);

		List<String> dataList = Arrays.asList(data.split("\\s*,\\s*"));
		byte[] request = new byte[dataList.size()];

		for (int i = 0; i < dataList.size(); i++) {
			request[i] = Byte.parseByte(dataList.get(i));
		}
		byte[] response = new byte[4];
		try {
			spi.writeRead(0, request, request.length, 7, response,
					response.length);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
		callback(callbackfn);
	//	applicationWebView.runJavascript("window['" + callback + "']('"
	//			+ response.toString() + "');");
	}

	// /////////////////////////////
	// Serial Peripheral Interface
	// /////////////////////////////
	@JavascriptInterface
	public void createTwi(String twiPin, final String asName,
			final String callbackfn) {
		ALog.i("Creating two wire interface on pin " + twiPin);
		int pinNum = Integer.parseInt(twiPin);

		String randomName = Utils.generateRandomString();

		TwiMaster twi;
		try {
			twi = ioio.openTwiMaster(pinNum, TwiMaster.Rate.RATE_100KHz, false);

			components.put(randomName, twi);
			
			callback(callbackfn);
		//	applicationWebView.runJavascript("setTwi('" + asName + "', '"
		//			+ randomName + "','" + callback + "');");
			
			
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
	}

	@JavascriptInterface
	public void writeReadTWI(String name, String address, String dataToWrite,
			String bytes, final String callbackfn) {
		TwiMaster twi = (TwiMaster) components.get(name);

		List<String> dataList = Arrays.asList(dataToWrite.split("\\s*,\\s*"));
		byte[] request = new byte[dataList.size()];

		for (int i = 0; i < dataList.size(); i++) {
			request[i] = Byte.parseByte(dataList.get(i));
		}
		byte[] response = new byte[Integer.parseInt(bytes)];
		try {
			twi.writeRead(Integer.parseInt(address), false, request,
					request.length, response, response.length);
		} catch (ConnectionLostException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			ALog.i(e.getMessage());
		}
		
		callback(callbackfn);
		//applicationWebView.runJavascript("window['" + callback + "']('"
		//		+ response.toString() + "');");
	}

	@Override
	public void onConnect(Object obj) {
		this.ioio = (IOIO) obj;
		ALog.i("Connected just fine... saved ioio connection");
		this.c.get().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				setReady();
			}
		});
	}

	@Override
	public void setup() {}

	@Override
	public void loop() {}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		this.c.get().finish();
	}
}