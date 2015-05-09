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


import android.content.Context;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.network.bt.SimpleBT;

public class PBluetoothClient extends PInterface {


    private final PBluetooth mPBluetooth;
    private SimpleBT simpleBT;
    private boolean mBtStarted = false;
    private CallbackConnected mCallbackConnected;
    private CallbackNewData mCallbackData;
    private AppRunnerFragment mFragment;

    public PBluetoothClient(PBluetooth pBluetooth, Context context, AppRunnerFragment fragment) {
        super(context);
        mPBluetooth = pBluetooth;
        mFragment = fragment;

        simpleBT = new SimpleBT(getContext(), null);
        simpleBT.start();

        WhatIsRunning.getInstance().add(this);
    }


    // --------- connectBluetooth ---------//
    interface CallbackConnected {
        void event(boolean connected);
    }

    // --------- connectBluetooth ---------//
    interface CallbackNewData {
        void event(String data);
    }

    @ProtoMethod(description = "Connects to mContext bluetooth device using the popup", example = "")
    @ProtoMethodParam(params = {"function(name, macAddress, strength)"})
    public void connectSerial(final CallbackConnected callbackfn) {
        NativeArray nativeArray = mPBluetooth.getBondedDevices();
        String[] arrayStrings = new String[(int) nativeArray.size()];
        for (int i = 0; i < nativeArray.size(); i++) {
            arrayStrings[i] = (String) nativeArray.get(i, null);
        }

        mFragment.pUi.popupChoice("Connect to device", arrayStrings, new PUI.choiceDialogCB() {
            @Override
            public void event(String string) {
                connectSerial(string.split(" ")[1], callbackfn);
            }
        });
        listen();
    }

    @ProtoMethod(description = "Connect to mContext bluetooth device using the mac address", example = "")
    @ProtoMethodParam(params = {"mac", "function(data)"})
    public void connectSerial(String mac, final CallbackConnected callbackfn) {
        simpleBT.connectByMac(mac);
        mCallbackConnected = callbackfn;
        listen();

    }

    @ProtoMethod(description = "Connect to mContext bluetooth device using mContext name", example = "")
    @ProtoMethodParam(params = {"name, function(data)"})
    public void connectSerialByName(String name, final CallbackConnected callbackfn) {
        simpleBT.connectByName(name);
        mCallbackConnected = callbackfn;

        listen();
    }

    public void onNewData(CallbackNewData callbackfn) {
        mCallbackData = callbackfn;
    }

    private void listen() {
        simpleBT.addListener(new SimpleBT.SimpleBTListener() {

            @Override
            public void onRawDataReceived(byte[] buffer, int size) {
                //MLog.network(mContext, "Bluetooth", "1. got " + buffer.toString());
            }

            @Override
            public void onMessageReceived(final String data) {
                //MLog.network(mContext, "Bluetooth", "2. got " + data);

                if (data != "") {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //MLog.d(TAG, "Got data: " + data);
                            if (mCallbackData != null) mCallbackData.event(data);
                        }
                    });
                }
            }

            @Override
            public void onConnected(boolean connected) {
                mCallbackConnected.event(connected);
            }
        });
    }

    @ProtoMethod(description = "Send bluetooth serial message", example = "")
    @ProtoMethodParam(params = {"string"})
    public void send(String string) {
        if (simpleBT.isConnected()) {
            simpleBT.send(string);
        }
    }

    @ProtoMethod(description = "Send bluetooth serial message", example = "")
    @ProtoMethodParam(params = {"int"})
    public void sendInt(int num) {
        if (simpleBT.isConnected()) {
            simpleBT.sendInt(num);
        }
    }

    @ProtoMethod(description = "Disconnect the bluetooth", example = "")
    @ProtoMethodParam(params = {""})
    public void disconnect() {
        if (simpleBT.isConnected()) {
            simpleBT.disconnect();
            //mCallbackConnected.event(false);
        }
    }

    @ProtoMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public boolean isConnected() {
        return simpleBT.isConnected();
    }


    public void stop() {
        if (simpleBT.isConnected()) {
            simpleBT.disconnect();
            simpleBT = null;
        }

    }
}
