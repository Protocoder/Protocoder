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

    @ProtoMethod(description = "")
    @ProtoMethodParam(params = {""})
    public PBluetoothClient connectSerial(String mac, PBluetoothClient.CallbackConnected callback) {
        start();

        PBluetoothClient pBluetoothClient = new PBluetoothClient(this, getContext(), getFragment());
        pBluetoothClient.connectSerial(mac, callback);

        return pBluetoothClient;
    }

    @ProtoMethod(description = "Connect to a bluetooth serial device", example = "")
    @ProtoMethodParam(params = {})
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
        simpleBT = new SimpleBT(getContext(), getActivity());
        simpleBT.start();

        if (getActivity() != null) {
            MLog.d(TAG, "starting bluetooth in a activity");

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
        } else {
            MLog.d(TAG, "starting bluetooth as service");
        }

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
