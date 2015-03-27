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
    public PIOIO connectIOIO(PIOIO.startCB callbackfn) {
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