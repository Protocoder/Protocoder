/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoderrunner.apprunner.api.other;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.puredata.core.PdBase;

public class PPureData {

	public PPureData() {
		WhatIsRunning.getInstance().add(this);
	}


	@ProtoMethod(description = "Sends a message to PdLib", example = "")
	@ProtoMethodParam(params = { "message", "value" })
	public void sendMessage(String message, String value) {
		if (value.isEmpty()) {
			PdBase.sendBang(message);
		} else if (value.matches("[0-9]+")) {
			PdBase.sendFloat(message, Float.parseFloat(value));
		} else {
			PdBase.sendSymbol(message, value);
		}
	}


	@ProtoMethod(description = "Sends a bang to PdLib", example = "")
	@ProtoMethodParam(params = { "name" })
	public void sendBang(String name) {
		PdBase.sendBang(name);
	}


	@ProtoMethod(description = "Sends a float number to PdLib", example = "")
	@ProtoMethodParam(params = { "name", "value" })
	public void sendFloat(String name, int value) {
		PdBase.sendFloat(name, value);
	}


	@ProtoMethod(description = "Sends a note to PdLib", example = "")
	@ProtoMethodParam(params = { "channel", "pitch, velocity" })
	public void sendNoteOn(int channel, int pitch, int velocity) {
		PdBase.sendNoteOn(channel, pitch, velocity);
	}


	@ProtoMethod(description = "Sends a midibyte to PdLib", example = "")
	@ProtoMethodParam(params = { "port", "value" })
	public void sendMidiByte(int port, int value) {
		PdBase.sendMidiByte(port, value);
	}


	@ProtoMethod(description = "Gets an array from PdLib", example = "")
	@ProtoMethodParam(params = { "name", "size" })
	public float[] getArray(String source, int n) {
		// public void getArray(float[] destination, int destOffset, String
		// source, int srcOffset, int n) {
		// PdBase.readArray(destination, destOffset, source, srcOffset, n);

		float[] destination = new float[n];
		PdBase.readArray(destination, 0, source, 0, n);

		return destination;
	}


	@ProtoMethod(description = "Sends and array of floats to PdLib", example = "")
	@ProtoMethodParam(params = { "name", "array", "size" })
	public void sendArray(String destination, float[] source, int n) {
		PdBase.writeArray(destination, 0, source, 0, n);
	}

	public void stop() {
		PdBase.release();
	}
}
