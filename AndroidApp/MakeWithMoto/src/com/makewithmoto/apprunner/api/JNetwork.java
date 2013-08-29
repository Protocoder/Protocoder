package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.hardware.MAKRBoard;

public class JNetwork extends JInterface {

	private String receivedData;
	private String TAG = "JNetwork";

	boolean isStarted = false;
	private String callbackfn;

	public JNetwork(Activity a) {
		super(a);

	}

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void startOSC(int port, final String callbackfn) {

		this.callbackfn = callbackfn;

	}

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void sendOSC(String address, int port) {

	}

	// previous callback callback("OnSerialRead("+receivedData+");");
	// callback(callbackfn, "\"" + receivedData + "\"");

}