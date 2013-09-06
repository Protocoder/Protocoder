package com.makewithmoto.apprunner.api;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.hardware.HardwareCallback;
import com.makewithmoto.hardware.IOIOBoard;
import com.makewithmoto.sensors.WhatIsRunning;

public class JIOIO extends JInterface implements HardwareCallback {

	private String TAG = "JIOIO";

	private IOIOBoard board;

	boolean isStarted = false;

	private IOIO ioio;

	private DigitalOutput led;

	private String moiocallbackfn;

	public JIOIO(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void start(String callbackfn) {
		moiocallbackfn = callbackfn;
		if (!isStarted) {
			this.board = new IOIOBoard(a.get(), this);
			board.powerOn();
			WhatIsRunning.getInstance().add(board);

		}
	}

	@JavascriptInterface
	@APIMethod(description = "clean up and poweroff makr board", example = "makr.stop();")
	public void stop() {
		isStarted = false;
		board.powerOff();
		board = null;
	}

	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public DigitalOutput openDigitalOutput(int pinNum)
			throws ConnectionLostException {
		return ioio.openDigitalOutput(pinNum, true); // start with the on board
														// LED off

	}

	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public DigitalInput openDigitalInput(int pinNum)
			throws ConnectionLostException {
		return ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);

	}

	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public AnalogInput openAnalogInput(int pinNum)
			throws ConnectionLostException {
		return ioio.openAnalogInput(pinNum);

	}

	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public PwmOutput openPWMOutput(int pinNum, int freq)
			throws ConnectionLostException {
		return ioio.openPwmOutput(pinNum, freq);
	}

	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public void setDigitalPin(int num, boolean status)
			throws ConnectionLostException {
		led.write(status);

	}

	@JavascriptInterface
	@APIMethod(description = "resumes makr activity", example = "makr.resume();")
	public void resume() {
		// makr.resume();
	}

	@JavascriptInterface
	@APIMethod(description = "pause makr activity", example = "makr.pause();")
	public void pause() {
		// makr.pause();
	}

	@Override
	public void onConnect(Object obj) {
		this.ioio = (IOIO) obj;
		Log.d(TAG, "MOIO Connected");
		callback(moiocallbackfn); 
		
		isStarted = true;
		this.a.get().runOnUiThread(new Runnable() {

			@Override
			public void run() {
			}
		});
	}

	@Override
	public void setup() {
	}

	@Override
	public void loop() {
	}

	@Override
	public void onComplete() {
		this.a.get().finish();
	}

}