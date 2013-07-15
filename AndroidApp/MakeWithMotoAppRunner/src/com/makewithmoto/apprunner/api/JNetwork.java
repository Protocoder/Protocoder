package com.makewithmoto.apprunner.api;

import android.support.v4.app.FragmentActivity;
import android.webkit.JavascriptInterface;

public class JNetwork extends JInterface {
	
	
	public JNetwork(FragmentActivity fragmentActivity) {
		super(fragmentActivity);
	}

	@JavascriptInterface
	public void createOSCServer(String port) {
	}
	
	@JavascriptInterface
	public void connectOSC(String address, String port) {
	}
	
	@JavascriptInterface
	public void sendOSC(String json) {
	}
	
	
}
