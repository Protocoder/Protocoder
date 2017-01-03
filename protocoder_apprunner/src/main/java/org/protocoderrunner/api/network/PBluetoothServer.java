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

package org.protocoderrunner.api.network;


import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import org.protocoderrunner.api.ProtoBase;
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.MLog;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PBluetoothServer extends ProtoBase {

    private final PBluetooth mPBluetooth;
    private final String name;

    final Handler handler = new Handler();

    BluetoothServerSocket mBluetoothServer;
    ArrayList<ConnectedDevice> mServerConnections = new ArrayList<>();
    boolean mServerStarted = true;

    // server
    private ServerListenNewConnectionsThread mServerListenNewConnectionsThread;

    private ReturnInterface mCallbackOnNewConnection;
    private ReturnInterface mCallbackOnNewData;

    public PBluetoothServer(PBluetooth pBluetooth, AppRunner appRunner, String name) {
        super(appRunner);
        mPBluetooth = pBluetooth;
        this.name = name;
    }

    public PBluetoothServer start() throws IOException {
        MLog.d(TAG, "Bluetooth server start");
        mBluetoothServer = mPBluetooth.getAdapter().listenUsingRfcommWithServiceRecord(name, mPBluetooth.UUID_SPP);

        mServerListenNewConnectionsThread = new ServerListenNewConnectionsThread();
        mServerListenNewConnectionsThread.start();

        return this;
    }

    public PBluetoothServer send() {

        return this;
    }

    public PBluetoothServer onNewConnection(ReturnInterface callback) {
        mCallbackOnNewConnection = callback;
        return this;
    }

    public PBluetoothServer onNewData(ReturnInterface callback) {
        mCallbackOnNewData = callback;

        return this;
    }

    public PBluetoothServer stop() {
        MLog.d(TAG, "Bluetooth Closed");
        __stop();

        return this;
    }

    @Override
    public void __stop() {
        mServerStarted = false;
        if (mServerListenNewConnectionsThread != null) {
            mServerListenNewConnectionsThread.cancel();
            mServerListenNewConnectionsThread = null;
         }

        for (ConnectedDevice connectedDevice : mServerConnections) {
            connectedDevice.stop();
        }
    }


    /***************************************************************
     * IMPL
     */

    private class ServerListenNewConnectionsThread extends Thread {

        public ServerListenNewConnectionsThread() { }

        public void run() {
            mServerStarted = true;

            while (mServerStarted) {
                try {
                    BluetoothSocket serverSocket = mBluetoothServer.accept();
                    MLog.d(TAG, "accepting connection " + serverSocket.getRemoteDevice().getAddress());
                    connectToClient(serverSocket); // since this is blocking, once we get a client we connect it
                } catch (IOException e) {
                    MLog.d(TAG, "BLUETOOTH:" + e.getMessage());
                }
            }
        }

        public void cancel() {
            mServerStarted = false;
        }
    }


    // http://stackoverflow.com/questions/13450406/how-to-receive-serial-data-using-android-bluetooth
    private void connectToClient(final BluetoothSocket btSocketClient) throws IOException {
        MLog.d(TAG, "connection to device: " + btSocketClient.getRemoteDevice().getName());

        final ConnectedDevice connectedDevice = new ConnectedDevice(btSocketClient);
        mServerConnections.add(connectedDevice);

        // callback
        if (mCallbackOnNewConnection != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ReturnObject ret = new ReturnObject();
                    ret.put("device", connectedDevice);
                    ret.put("name", btSocketClient.getRemoteDevice().getName());
                    ret.put("mac", btSocketClient.getRemoteDevice().getAddress());
                    mCallbackOnNewConnection.event(ret);
                }
            });

        }

        connectedDevice.startThread();
    }


    private class ConnectedDevice {
        private ConnectionThread connectionThread;
        BluetoothSocket bluetoothSocket;
        private InputStream inputStream;
        private OutputStream outputStream;
        String name;
        String mac;

        ConnectedDevice(BluetoothSocket bluetoothSocketClient) {
            bluetoothSocket = bluetoothSocketClient;
            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            name = bluetoothSocketClient.getRemoteDevice().getName();
            mac = bluetoothSocketClient.getRemoteDevice().getAddress();
        }

        public ConnectedDevice send(String msg) {
            try {
                outputStream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return this;
        }

        public void startThread() {
            connectionThread = new ConnectionThread(this);
            connectionThread.start();
        }

        public void stop() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionThread.cancel();
            connectionThread = null;
        }
    }

    private class ConnectionThread extends Thread {
        private final BluetoothSocket mmBtSocketClient;
        private final ConnectedDevice mmConectedDevice;
        OutputStream outputStream;
        InputStream inputStream;
        DataInputStream dataInputStream;
        private boolean stop = false;

        ConnectionThread(ConnectedDevice connectedDevice) {
            mmConectedDevice = connectedDevice;
            mmBtSocketClient = connectedDevice.bluetoothSocket;
            try {
                outputStream = mmBtSocketClient.getOutputStream();
                inputStream = mmBtSocketClient.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MLog.d(TAG, "start connection thread " + inputStream + " " + dataInputStream + " " + outputStream);
        }

        public void shutdown() {
            stop = true;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            MLog.d(TAG, "run");
            byte[] byteArray = new byte[1024];

            // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            while (!stop) {
                MLog.d(TAG, "running " + stop);

                try {
                    MLog.d(TAG, "trying to readline");
                    // final String line = reader.readLine();
                    // dataInputStream.readFully(byteArray, 0, byteArray.length);
                    inputStream.read(byteArray);

                    String line = new String(byteArray);

                    MLog.d(TAG, "trying to readline...");

                    if (line != null) {
                        MLog.d(TAG, line);

                        // get the data
                        if (mCallbackOnNewData != null) {
                            final ReturnObject ret = new ReturnObject();
                            ret.put("device", mmConectedDevice);
                            ret.put("data", line);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mCallbackOnNewData.event(ret);
                                }
                            });
                        }
                    }
                } catch (IOException ex) {
                    MLog.d(TAG, ex.toString());
                    stop = true;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            stop = true;
        }
    }


}
