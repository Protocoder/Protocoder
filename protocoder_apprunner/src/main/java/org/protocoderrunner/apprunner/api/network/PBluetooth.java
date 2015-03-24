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
    private scanBTNetworksCB onBluetoothfn;
    private SimpleBT simpleBT;
    private boolean mBtStarted = false;

    public PBluetooth(Context context) {
        super(context);

        WhatIsRunning.getInstance().add(this);
    }

    @ProtoMethod(description = "")
    @ProtoMethodParam(params = {""})
    public void createSerialServer() {
        start();
    }

    public PBluetoothClient connectSerial(String mac, PBluetoothClient.CallbackConnected callback) {
        start();

        PBluetoothClient pBluetoothClient = new PBluetoothClient(this, getContext(), getFragment());
        pBluetoothClient.connectSerial(mac, callback);

        return pBluetoothClient;
    }

    public PBluetoothClient connectSerial(PBluetoothClient.CallbackConnected callback) {
        start();

        PBluetoothClient pBluetoothClient = new PBluetoothClient(this, getContext(), getFragment());
        pBluetoothClient.connectSerial(callback);

        return pBluetoothClient;
    }

    @ProtoMethod(description = "Start the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {""})
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

    public interface onBluetoothListener {
        public void onDeviceFound(String name, String macAddress, float strength);
        public void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    interface scanBTNetworksCB {
        void event(String name, String macAddress, float strength);
    }


    @ProtoMethod(description = "Scan bluetooth networks. Gives back the name, mac and signal strength", example = "")
    @ProtoMethodParam(params = {"function(name, macAddress, strength)"})
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

    @ProtoMethod(description = "Send bluetooth serial message", example = "")
    @ProtoMethodParam(params = {"string"})
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

    @ProtoMethod(description = "Enable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {})
    public void enable() {
        start();
    }


    @ProtoMethod(description = "Disable the bluetooth adapter", example = "")
    @ProtoMethodParam(params = {})
    public void disable() {
        start();
        simpleBT.disable();
    }



    public void stop() {
        if (simpleBT.isConnected()) {
            simpleBT.disconnect();
        }

    }

}
