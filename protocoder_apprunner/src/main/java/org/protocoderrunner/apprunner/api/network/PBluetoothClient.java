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

    @ProtoMethod(description = "Connects to mContext bluetooth device using mContext popup", example = "")
    @ProtoMethodParam(params = {"function(name, macAddress, strength)"})
    public void connectSerial(final CallbackConnected callbackfn) {
        start();

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

    private void start() {
        simpleBT = new SimpleBT(mFragment.getActivity());
        simpleBT.start();

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
