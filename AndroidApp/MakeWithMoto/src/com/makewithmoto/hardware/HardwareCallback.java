package com.makewithmoto.hardware;

public interface HardwareCallback {
	public void onConnect(Object obj);
	public void setup();
	public void loop();
	public void onComplete();
}
