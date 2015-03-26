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

package org.protocoderrunner.apprunner.api.network;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.other.ProtocoderNativeArray;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.network.bt.SimpleBT;
import org.protocoderrunner.utils.MLog;

import java.util.Set;

public class PBluetoothServer extends PInterface {

    private SimpleBT simpleBT;
    private boolean mBtStarted = false;

    public PBluetoothServer(Context context) {
        super(context);

        WhatIsRunning.getInstance().add(this);
    }





    @ProtoMethod(description = "Send bluetooth serial message", example = "")
    @ProtoMethodParam(params = {"string"})
    public void send(String string) {
        if (simpleBT.isConnected()) {
            simpleBT.send(string);
        }
    }


    @ProtoMethod(description = "Disconnect the bluetooth", example = "")
    @ProtoMethodParam(params = {""})
    public void disconnect() {
        if (simpleBT.isConnected()) {
            simpleBT.disconnect();
        }
    }


    @ProtoMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void enable(boolean b) {
        if (b) {
            simpleBT.start();
        } else {
            simpleBT.disable();
        }
    }


    @ProtoMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public boolean isConnected() {
        return simpleBT.isConnected();
    }

}
