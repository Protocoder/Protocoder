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

public class PBluetooth extends PInterface {
    public PBluetooth(Context context) {
        super(context);
    }


    //--------- Bluetooth ---------//
    //methods
    //scanNetworks
    //connectBluetoothSerialByUi
    //connectBluetoothSerialByMac
    //connectBluetoothSerialByName
    //send
    //disconnect
    //enable
    //isConnected

    private scanBTNetworksCB onBluetoothfn;
    private SimpleBT simpleBT;
    private boolean mBtStarted = false;


    public interface onBluetoothListener {
        public void onDeviceFound(String name, String macAddress, float strength);
        public void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    interface scanBTNetworksCB {
        void event(String name, String macAddress, float strength);
    }


    @ProtoMethod(description = "Scan bluetooth networks. Gives back the name, mac and signal strength", example = "")
    @ProtoMethodParam(params = { "function(name, macAddress, strength)" })
    public void scanNetworks(final scanBTNetworksCB callbackfn) {
        start();
        onBluetoothfn = callbackfn;
        simpleBT.scanBluetooth(new onBluetoothListener() {

            @Override
            public void onDeviceFound(String name, String macAddress, float strength) {
                onBluetoothfn.event(name, macAddress, strength);
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {

            }
        });

    }

    @ProtoMethod(description = "Start the bluetooth adapter", example = "")
    @ProtoMethodParam(params = { "" })
    public SimpleBT start() {
        if (mBtStarted) {
            return simpleBT;
        }
        simpleBT = new SimpleBT(getActivity());
        simpleBT.start();
        getActivity().addBluetoothListener(new onBluetoothListener() {

            @Override
            public void onDeviceFound(String name, String macAddress, float strength) {
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                simpleBT.onActivityResult(requestCode, resultCode, data);

                switch (requestCode) {
                    case SimpleBT.REQUEST_ENABLE_BT:
                        // When the request to enable Bluetooth returns
                        if (resultCode == Activity.RESULT_OK) {
                            //MLog.d(TAG, "enabling BT");
                            // Bluetooth is now enabled, so set up mContext Bluetooth session
                            mBtStarted = true;
                            simpleBT.startBtService();

                            // User did not enable Bluetooth or an error occurred
                        } else {
                            //	MLog.d(TAG, "BT not enabled");
                            Toast.makeText(getActivity().getApplicationContext(), "BT not enabled :(", Toast.LENGTH_SHORT)
                                    .show();

                        }
                }
            }
        });

        WhatIsRunning.getInstance().add(simpleBT);
        return simpleBT;
    }

    // --------- connectBluetooth ---------//
    interface connectBluetoothCB {
        void event(String what, String data);
    }

    //TODO removed new impl needed

    @ProtoMethod(description = "Connects to mContext bluetooth device using mContext popup", example = "")
    @ProtoMethodParam(params = { "function(name, macAddress, strength)" })
    public void connectSerialByUi(final connectBluetoothCB callbackfn) {
        start();
        NativeArray nativeArray = getBondedDevices();
        String[] arrayStrings = new String[(int) nativeArray.size()];
        for (int i = 0; i < nativeArray.size(); i++) {
            arrayStrings[i] = (String) nativeArray.get(i, null);
        }

        getFragment().pUi.popupChoice("Connect to device", arrayStrings, new PUI.choiceDialogCB() {
            @Override
            public void event(String string) {
                connectSerialByMac(string.split(" ")[1], callbackfn);
            }
        });
        //simpleBT.startDeviceListActivity();
    }


    @ProtoMethod(description = "Connect to mContext bluetooth device using the mac address", example = "")
    @ProtoMethodParam(params = { "mac", "function(data)" })
    public void connectSerialByMac(String mac, final connectBluetoothCB callbackfn) {
        start();
        simpleBT.connectByMac(mac);
        addBTConnectionListener(callbackfn);

    }


    @ProtoMethod(description = "Connect to mContext bluetooth device using mContext name", example = "")
    @ProtoMethodParam(params = { "name, function(data)" })
    public void connectSerialByName(String name, final connectBluetoothCB callbackfn) {
        start();
        simpleBT.connectByName(name);
        addBTConnectionListener(callbackfn);
    }

    private void addBTConnectionListener(final connectBluetoothCB callbackfn) {
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
                            callbackfn.event("data", data);
                        }
                    });
                }
            }

            @Override
            public void onConnected() {
                callbackfn.event("connected", null);
            }
        });
    }



    @ProtoMethod(description = "Send bluetooth serial message", example = "")
    @ProtoMethodParam(params = { "string" })
    public NativeArray getBondedDevices() {
        start();

        Set<BluetoothDevice> listDevices = simpleBT.listBondedDevices();
        MLog.d(TAG, "listDevices " + listDevices);
        int listSize = listDevices.size();
        ProtocoderNativeArray array = new ProtocoderNativeArray(listSize);
        MLog.d(TAG, "array " + array);


        int counter = 0;
        for (BluetoothDevice b : listDevices) {
            MLog.d(TAG, "b " + b);

            String s = b.getName() + " " + b.getAddress();
            array.addPE(counter++, s);
        }

        return array;
    }


    @ProtoMethod(description = "Send bluetooth serial message", example = "")
    @ProtoMethodParam(params = { "string" })
    public void send(String string) {
        if (simpleBT.isConnected()) {
            simpleBT.send(string);
        }
    }


    @ProtoMethod(description = "Disconnect the bluetooth", example = "")
    @ProtoMethodParam(params = { "" })
    public void disconnect() {
        if (simpleBT.isConnected()) {
            simpleBT.disconnect();
        }
    }


    @ProtoMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = { "boolean" })
    public void enable(boolean b) {
        if (b) {
            simpleBT.start();
        } else {
            simpleBT.disable();
        }
    }


    @ProtoMethod(description = "Enable/Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = { "boolean" })
    public boolean isConnected() {
        return simpleBT.isConnected();
    }

}
