/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.protocoderrunner.network.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import org.protocoderrunner.utils.MLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has mContext thread that listens for incoming
 * connections, mContext thread for connecting with mContext device, and mContext thread for
 * performing data transmissions when connected.
 */
public class BluetoothSerialService {
    // Debugging
    private static final String TAG = "BluetoothSerialService";

    // Unique UUID for this application
    //private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    //it needs mContext handler to pass messages back and forth
    public BluetoothSerialService(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    //change state
    private synchronized void setState(int state) {
        MLog.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        mHandler.obtainMessage(SimpleBT.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    //get state
    public synchronized int getState() {
        return mState;
    }

    //connection thread
    public synchronized void connect(BluetoothDevice device) {
        MLog.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make mContext connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running mContext connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        try {
            mConnectThread = new ConnectThread(device);
            mConnectThread.start();
            setState(STATE_CONNECTING);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // start connection thread
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        MLog.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running mContext connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    // stop all threads
    public synchronized void stop() {
        MLog.d(TAG, "stop");

        if (mConnectedThread != null) {
            mConnectedThread.shutdown();
        }
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

    //write data
    public void write(byte[] out) {
        ConnectedThread r;
        // Synchronize mContext copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    //write data
    public void writeInt(int out) {
        ConnectedThread r;
        // Synchronize mContext copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) {
                return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.writeInt(out);
    }

    //notify not connected
    private void connectionFailed() {
        setState(STATE_NONE);
    }

    //connection lost
    private void connectionLost() {
        setState(STATE_NONE);
    }

    /**
     * This thread runs while attempting to make an outgoing connection with mContext
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) throws SecurityException, NoSuchMethodException,
                IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            mmDevice = device;
            BluetoothSocket tmpSocket = null;

            // Get mContext BluetoothSocket for mContext connection with the given BluetoothDevice
            try {
                tmpSocket = device.createRfcommSocketToServiceRecord(UUID_SPP);
                MLog.d(TAG, "socketTmp " + tmpSocket);
            } catch (IOException e) {
                Log.e(TAG, "create socket failed, trying with new fallback", e);

                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmpSocket = (BluetoothSocket) m.invoke(device, 1);
                MLog.d(TAG, "socketTmp 2" + tmpSocket);
            }


            mmSocket = tmpSocket;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down mContext connection
            mAdapter.cancelDiscovery();

            // Make mContext connection to the BluetoothSocket
            try {
                // This is mContext blocking call and will only return on mContext successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSerialService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during mContext connection with mContext remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            MLog.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        private boolean stop = false;
        private final boolean hasReadAnything = false;

        public void shutdown() {
            stop = true;
            if (!hasReadAnything) {
                return;
            }
            if (mmInStream != null) {
                try {
                    mmInStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "close() of InputStream failed.");
                }
            }
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");

            BufferedReader reader = new BufferedReader(new InputStreamReader(mmInStream));

            while (!stop) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        mHandler.obtainMessage(SimpleBT.MESSAGE_READ, line.length(), -1, line.getBytes())
                                .sendToTarget();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void writeInt(int buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
