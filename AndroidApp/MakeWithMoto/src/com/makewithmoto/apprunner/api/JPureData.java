package com.makewithmoto.apprunner.api;

import org.puredata.core.PdBase;

public class JPureData {


	public void sendMessage(String message, String value) {
		if (value.isEmpty()) {
			PdBase.sendBang(message);
		} else if(value.matches("[0-9]+")) {
			PdBase.sendFloat(message, Float.parseFloat(value));
		} else { 
			PdBase.sendSymbol(message, value);
		}
	}
	
	public void sendBang(String name) {
		PdBase.sendBang(name);
	}

	public void sendFloat(String name, int value) {
		PdBase.sendFloat(name, value); 

	}

}
