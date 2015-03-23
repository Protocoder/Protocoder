/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.apprunner.api.boards;

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.hardware.HardwareCallback;
import org.protocoderrunner.hardware.IOIOBoard;
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


    @ProtoMethod(description = "initializes ioio board", example = "ioio.start();")
    @ProtoMethodParam(params = {""})
    public void start() {
        if (!mIoioStarted) {
            this.board = new IOIOBoard(getContext(), this);
            board.powerOn();
            WhatIsRunning.getInstance().add(board);
        }
    }


    @ProtoMethod(description = "initializes ioio board", example = "ioio.start();")
    @ProtoMethodParam(params = {"function()"})
    public void start(startCB callbackfn) {
        mIoioCallbackfn = callbackfn;
        if (!mIoioStarted) {
            this.board = new IOIOBoard(getContext(), this);
            board.powerOn();
            WhatIsRunning.getInstance().add(board);

        }
    }

    public IOIO get() {
        return mIoio;
    }


    @ProtoMethod(description = "stops the ioio board", example = "ioio.stop();")
    public void stop() {
        mIoioStarted = false;
        board.powerOff();
        board = null;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"pinNumber"})
    public DigitalOutput openDigitalOutput(int pinNum) throws ConnectionLostException {
        return mIoio.openDigitalOutput(pinNum, false); // start with the on board

    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"pinNumber"})
    public DigitalInput openDigitalInput(int pinNum) throws ConnectionLostException {
        return mIoio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);

    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"pinNumber"})
    public AnalogInput openAnalogInput(int pinNum) throws ConnectionLostException {
        return mIoio.openAnalogInput(pinNum);

    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"pinNumber", "frequency"})
    public PwmOutput openPWMOutput(int pinNum, int freq) throws ConnectionLostException {
        return mIoio.openPwmOutput(pinNum, freq);
    }


    public void resume() {
    }

    public void pause() {
    }


    @ProtoMethod(description = "returns true is the ioio board is connected", example = "")
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