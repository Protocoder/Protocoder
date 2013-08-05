package com.makewithmoto.apprunner.api;

import ioio.lib.api.IOIO;
import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.APIAnnotation;
import com.makewithmoto.hardware.HardwareCallback;
import com.makewithmoto.hardware.IOIOBoard;
import com.makewithmoto.sensors.WhatIsRunning;

public class JIOIO extends JInterface implements HardwareCallback {

	private String TAG = "JIOIO";

	private IOIOBoard board;
	protected IOIO ioio;

	boolean isStarted = false;

	public JIOIO(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIAnnotation(description = "initializes makr board", example = "makr.start();")
	public void start() {

		if (!isStarted) {
			this.board = new IOIOBoard(a.get(), this);
			board.powerOn();
			WhatIsRunning.getInstance().add(board);

			isStarted = true;
		}
	}

	@JavascriptInterface
	@APIAnnotation(description = "clean up and poweroff makr board", example = "makr.stop();")
	public void stop() {
		if (isStarted) {
			isStarted = false;
			board.powerOff();
			board = null;
		}
	}

	@JavascriptInterface
	@APIAnnotation(description = "sends commands to makr board", example = "makr.writeSerial(\"LEDON\");")
	public void writeSerial(String cmd) {
		if (isStarted) {
			// makr.writeSerial(cmd);

		}
	}

	@JavascriptInterface
	@APIAnnotation(description = "resumes makr activity", example = "makr.resume();")
	public void resume() {
		// makr.resume();
	}

	@JavascriptInterface
	@APIAnnotation(description = "pause makr activity", example = "makr.pause();")
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