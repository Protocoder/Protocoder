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

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.boards.PArduino;
import org.protocoderrunner.apprunner.api.boards.PIOIO;
import org.protocoderrunner.apprunner.api.boards.PSerial;
import org.protocoderrunner.hardware.AdkPort;
import org.protocoderrunner.utils.MLog;

public class PBoards extends PInterface {

    private final String TAG = "PBoards";

    public PBoards(Context a) {
        super(a);
    }


    @ProtoMethod(description = "initializes the ioio board", example = "")
    @ProtoMethodParam(params = {"function()"})
    public PIOIO startIOIO(PIOIO.startCB callbackfn) {
        PIOIO ioio = new PIOIO(getContext());
        ioio.start(callbackfn);

        return ioio;
    }


    @ProtoMethod(description = "initializes serial communication", example = "")
    @ProtoMethodParam(params = {"bauds", "function()"})
    public PSerial connectSerial(int baud, PSerial.OnStartCallback callbackfn) {
        PSerial serial = new PSerial(getContext());
        serial.start(baud, callbackfn);

        return serial;
    }


    @ProtoMethod(description = "initializes arduino board without callback", example = "")
    @ProtoMethodParam(params = {""})
    public PArduino connectArduino() {
        PArduino arduino = new PArduino(getContext());
        arduino.start();

        return arduino;
    }


    @ProtoMethod(description = "initializes arduino board with callback", example = "")
    @ProtoMethodParam(params = {"bauds", "function()"})
    public PArduino connectArduino(int bauds, String endline, PArduino.onReadCB callbackfn) {
        PArduino arduino = new PArduino(getContext());
        arduino.start(bauds, endline, callbackfn);

        return arduino;
    }


    @ProtoMethod(description = "initializes adk boards with callback", example = "")
    @ProtoMethodParam(params = {"bauds", "function()"})
    public AdkPort startADK(PArduino.onReadCB callbackfn) {
        final AdkPort adk = new AdkPort(getContext());


        Thread thread = new Thread(adk);
        thread.start();

        String[] list = adk.getList(getContext());
        for (int i = 0; i < list.length; i++) {
            // writeToConsole(list[i] + "\n\r");
        }

        adk.attachOnNew(new AdkPort.MessageNotifier() {
            @Override
            public void onNew() {
                int av = adk.available();
                byte[] buf = adk.readB();

                String toAdd = new String(buf, 0, av);
                MLog.d(TAG, "Received:" + toAdd);
            }
        });

        return adk;
    }

}