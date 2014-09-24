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

package org.protocoderrunner.apprunner.api;

import android.app.Activity;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.boards.PIOIO;
import org.protocoderrunner.apprunner.api.boards.PArduino;
import org.protocoderrunner.apprunner.api.boards.PSerial;
import org.protocoderrunner.utils.MLog;

public class PBoards extends PInterface {

	private final String TAG = "PBoards";

	public PBoards(Activity a) {
		super(a);
	}

	@ProtocoderScript
	@APIMethod(description = "initializes the ioio board", example = "")
	@APIParam(params = { "function()" })
	public PIOIO startIOIO(PIOIO.startCB callbackfn) {
		PIOIO ioio = new PIOIO(a.get());
		ioio.start(callbackfn);

		return ioio;
	}

	@ProtocoderScript
	@APIMethod(description = "initializes serial communication", example = "")
	@APIParam(params = { "bauds", "function()" })
	public PSerial startSerial(int baud, PSerial.startCB callbackfn) {
		PSerial serial = new PSerial(a.get());
		serial.start(baud, callbackfn);

		return serial;
	}


    @ProtocoderScript
    @APIMethod(description = "initializes arduino board without callback", example = "")
    @APIParam(params = { "" })
    public PArduino startArduino() {
        PArduino arduino = new PArduino(a.get());
        arduino.start();

        return arduino;
    }

    @ProtocoderScript
    @APIMethod(description = "initializes arduino board with callback", example = "")
    @APIParam(params = { "bauds", "function()" })
    public PArduino startArduino(int bauds, PArduino.onReadCB callbackfn) {
        PArduino arduino = new PArduino(a.get());
        arduino.start(bauds, callbackfn);

        return arduino;
    }

}