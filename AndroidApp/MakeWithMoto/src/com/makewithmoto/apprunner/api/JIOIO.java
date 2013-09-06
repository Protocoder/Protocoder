package com.makewithmoto.apprunner.api;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.hardware.HardwareCallback;
import com.makewithmoto.hardware.IOIOBoard;
import com.makewithmoto.sensors.WhatIsRunning;

public class JIOIO extends JInterface implements HardwareCallback {

	private String TAG = "JIOIO";

	private IOIOBoard board;

	boolean isStarted = false;

	public JIOIO(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void start() {

		if (!isStarted) {
			this.board = new IOIOBoard(a.get(), this);
			board.powerOn();
			WhatIsRunning.getInstance().add(board);

			isStarted = true;
		}
	}

	@JavascriptInterface
	@APIMethod(description = "clean up and poweroff makr board", example = "makr.stop();")
	public void stop() {
		if (isStarted) {
			isStarted = false;
			board.powerOff();
			board = null;
		}
	}

	@JavascriptInterface
	@APIMethod(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public void prueba() throws ConnectionLostException {
		//DigitalOutput led = ioio.openDigitalOutput(0, true); // start with the on board LED off
		//led.write(true);

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
		// TODO Auto-generated method stub

	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub

	}

}