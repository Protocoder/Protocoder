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

package org.protocoderrunner.apprunner.api.boards;

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.hardware.HardwareCallback;
import org.protocoderrunner.hardware.IOIOBoard;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

public class PIOIO extends PInterface implements HardwareCallback {

	private final String TAG = "PIOIO";

	private IOIOBoard board;
	boolean mIoioStarted = false;
	private IOIO mIoio;
	private startCB mIoioCallbackfn;

	public PIOIO(Context a) {
		super(a);
	}

	// --------- getRequest ---------//
	public interface startCB {
		void event();
	}


    @ProtocoderScript
    @APIMethod(description = "initializes ioio board", example = "ioio.start();")
    @APIParam(params = { "" })
    public void start() {
        if (!mIoioStarted) {
            this.board = new IOIOBoard(mContext, this);
            board.powerOn();
            WhatIsRunning.getInstance().add(board);
        }
    }

	@ProtocoderScript
	@APIMethod(description = "initializes ioio board", example = "ioio.start();")
    @APIParam(params = { "function()" })
    public void start(startCB callbackfn) {
		mIoioCallbackfn = callbackfn;
		if (!mIoioStarted) {
			this.board = new IOIOBoard(mContext, this);
			board.powerOn();
			WhatIsRunning.getInstance().add(board);

		}
	}

	public IOIO get() {
		return mIoio;
	}

	@ProtocoderScript
	@APIMethod(description = "stops the ioio board", example = "ioio.stop();")
	public void stop() {
		mIoioStarted = false;
		board.powerOff();
		board = null;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
    @APIParam(params = { "pinNumber" })
    public DigitalOutput openDigitalOutput(int pinNum) throws ConnectionLostException {
		return mIoio.openDigitalOutput(pinNum, false); // start with the on board

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
    @APIParam(params = { "pinNumber" })
    public DigitalInput openDigitalInput(int pinNum) throws ConnectionLostException {
		return mIoio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
    @APIParam(params = { "pinNumber" })
    public AnalogInput openAnalogInput(int pinNum) throws ConnectionLostException {
		return mIoio.openAnalogInput(pinNum);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
    @APIParam(params = { "pinNumber", "frequency" })
    public PwmOutput openPWMOutput(int pinNum, int freq) throws ConnectionLostException {
        return mIoio.openPwmOutput(pinNum, freq);
	}


	public void resume() {
	}

	public void pause() {
	}


    @ProtocoderScript
    @APIMethod(description = "returns true is the ioio board is connected", example = "")
    public boolean isStarted() {
        return mIoioStarted;
    }


    @Override
	public void onConnect(Object obj) {
		this.mIoio = (IOIO) obj;
		MLog.d(TAG, "MOIO Connected");

        if (mIoioCallbackfn != null) {
            mIoioCallbackfn.event();
        }

		mIoioStarted = true;
		mHandler.post(new Runnable() {

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

	}

}