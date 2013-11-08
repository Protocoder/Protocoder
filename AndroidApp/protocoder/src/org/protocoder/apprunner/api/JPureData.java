/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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

package org.protocoder.apprunner.api;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.JavascriptInterface;
import org.puredata.core.PdBase;



public class JPureData {


	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"message", "value"} )
	public void sendMessage(String message, String value) {
		if (value.isEmpty()) {
			PdBase.sendBang(message);
		} else if(value.matches("[0-9]+")) {
			PdBase.sendFloat(message, Float.parseFloat(value));
		} else { 
			PdBase.sendSymbol(message, value);
		}
	}
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"name"} )
	public void sendBang(String name) {
		PdBase.sendBang(name);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"name", "value"} )
	public void sendFloat(String name, int value) {
		PdBase.sendFloat(name, value); 
	}
	
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"name", "pitch, velocity"} )
	public void sendNoteOn(int channel, int pitch, int velocity) {
		PdBase.sendNoteOn(channel, pitch, velocity); 
	}
	
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"port", "value"} )
	public void sendFloat(int port, int value) {
		PdBase.sendMidiByte(port, value); 
	}
	
	
}
