/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices
*
* Copyright (C) 2013 Victor Suarez suarez.garcia.victor@gmail.com
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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * This class control The Connection With Bluetooth Low Energy(4.0).
 *
 * @author Victor Suarez
 * @version 1.0
 */
@SuppressLint("NewApi")
public class PBluetoothLe extends PInterface {

    private List<BluetoothDevice> mDevices;
    private BluetoothDevice currentDevice;
    private BluetoothAdapter mAdapter;
    private static Long SCAN_TIMEOUT = 10000L;
    private Boolean connected = false;
    private BluetoothGatt mGatt;
    private Context context;
    private callBackNewData mcallBackNewData;
    private callBackconnected mCallBackconnected;

    //Callback that control the Functionality of scan devices.
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mDevices.add(device);
        }
    };

    //Runnable that stop the Scan Mode
    private Runnable SearchDevices = new Runnable() {
        @Override
        public void run() {
            mAdapter.stopLeScan(mScanCallback);
        }
    };
    //Callback that controls Connection State and Characteristic State
    private BluetoothGattCallback mbluetoothListener = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mCallBackconnected != null) {
                connected = (status == 0);
                mGatt.discoverServices();
                MLog.i(TAG, "CONNECTED");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (mcallBackNewData != null) {
                byte[] data = characteristic.getValue();

                boolean mFirstPacket = (data[0] & 0x80) == 0x80;
                int mMessageCount = (((data[0] & 0x60) >> 5));
                int mPendingCount = (data[0] & 0x1f);
                byte[] mPacket = data;
                ByteBuffer buffer = ByteBuffer.allocate(2);
                buffer.put(data[5]);
                buffer.put(data[6]);
                byte[] data2 = buffer.array();
                MLog.i(TAG, "CHANGING..." + new String(data2));
                mcallBackNewData.event(new String(data2));

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            MLog.i(TAG, "Service Discovered");
            mCallBackconnected.event(status == 0);
        }
    };

    //Interface that controls the new data event
    public interface callBackNewData {
        public void event(String data);
    }

    //Interfaz that control the Conection
    public interface callBackconnected {
        public void event(boolean connected);
    }

    public PBluetoothLe(Context context) {
        super(context);
        this.mDevices = new ArrayList<BluetoothDevice>();
        this.mAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        MLog.d(TAG, "Initializated");
        this.context = context;
        WhatIsRunning.getInstance().add(this);
    }

    @ProtoMethod(description = "Scan Bluetooth Devices During 10 Seconds")
    @ProtoMethodParam(params = {""})
    public List<BluetoothDevice> scan() {
        return scan(SCAN_TIMEOUT);
    }

    @ProtoMethod(description = "Scan Bluetooth Devices for the especific miliseconds")
    @ProtoMethodParam(params = {"milis"})
    public List<BluetoothDevice> scan(Long milis) {
        MLog.d(TAG, "Scanning Devices");
        Handler handler = new Handler();
        mAdapter.startLeScan(this.mScanCallback);
        handler.postDelayed(SearchDevices, milis);

        return mDevices;
    }

    @ProtoMethod(description = "Connect to a new Device")
    @ProtoMethodParam(params = {"address", "callback"})
    public void connect(String address, final callBackconnected callback) {
        currentDevice = mAdapter.getRemoteDevice(address);
        mGatt = currentDevice.connectGatt(context, true, mbluetoothListener);
        this.mCallBackconnected = callback;
        MLog.i(TAG, "CONECTING...");

        connected = true;
    }

    @ProtoMethod(description = "Show if is connected")
    @ProtoMethodParam(params = {""})
    public Boolean isConnected() {
        return connected;
    }

    @ProtoMethod(description = "Disconnect from the current device")
    @ProtoMethodParam(params = {""})
    public void disconnect() {
        this.mGatt.close();
        this.connected = false;
    }

    @ProtoMethod(description = "get the Current Services")
    @ProtoMethodParam(params = {""})
    public List<BluetoothGattService> getServices() throws Exception {
        return mGatt.getServices();
    }

    @ProtoMethod(description = "initialice a new Listener from a especific Characteristic")
    @ProtoMethodParam(params = {"uuidService", "uuidCharacteristic", "callBackNewData"})
    public void listenCharacteristic(String uuidService, String uuidCharaceristic, callBackNewData callback) {
        BluetoothGattService service = mGatt.getService(UUID.fromString(uuidService));

        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(uuidCharaceristic));
        mGatt.setCharacteristicNotification(characteristic, true);
        for (BluetoothGattDescriptor bluetoothGattDescriptor : characteristic.getDescriptors()) {
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mGatt.writeDescriptor(bluetoothGattDescriptor);
        }
        this.mcallBackNewData = callback;
        MLog.i(TAG, "LISTENING...");
    }

    public BluetoothDevice getCurrentDevice() {
        return this.currentDevice;
    }

    public void stop() {
        if (connected) {
            mGatt.close();
        }
    }

}
