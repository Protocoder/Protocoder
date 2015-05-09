package org.protocoderrunner.network.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.api.network.PBluetooth;
import org.protocoderrunner.apprunner.api.other.WhatIsRunningInterface;
import org.protocoderrunner.utils.MLog;

import java.util.Set;

public class SimpleBT implements WhatIsRunningInterface {
    private static final String TAG = "BT";

    // Intent request codes
    public static final int REQUEST_ENABLE_BT = 2;

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;

    // State variables
    private final boolean paused = false;
    private boolean connected = false;

    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the Bluetooth services
    private BluetoothSerialService mBluetoothService = null;

   // Vector<SimpleBTListener> listeners = new Vector<SimpleBT.SimpleBTListener>();

    private final Context mContext;
    private final AppRunnerActivity mActivity;

    private SimpleBTListener mSimpleBTListener;

    public interface SimpleBTListener {
        public void onConnected(boolean connected);
        public void onMessageReceived(String data);
        public void onRawDataReceived(final byte[] buffer, final int size);
    }

    public SimpleBT(Context c, AppRunnerActivity activity) {
        this.mContext = c;
        this.mActivity = activity;
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startBtService() {
        // Initialize the BluetoothService to perform Bluetooth connections
        mBluetoothService = new BluetoothSerialService(mHandler);
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        private String mConnectedDeviceName;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_CONNECTED:
                            connected = true;
                                if (mSimpleBTListener != null) mSimpleBTListener.onConnected(connected);
                            break;
                        case BluetoothSerialService.STATE_CONNECTING:
                            break;
                        case BluetoothSerialService.STATE_NONE:
                            connected = false;
                            if (mSimpleBTListener != null) mSimpleBTListener.onConnected(connected);

                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "written = '" + writeMessage + "'");
                    break;
                case MESSAGE_READ:
                    if (paused) {
                        break;
                    }
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct mContext string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    // here is where we get the BT data
                    Log.d(TAG, "received " + readMessage);

                    if (mSimpleBTListener != null) mSimpleBTListener.onMessageReceived(readMessage);

                    break;
            }
        }
    };


    public void start() {
        //try to start the bluetooth if not enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (mActivity != null) {
                mActivity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                MLog.d(TAG, "you must enabled bluetooth before");
            }
            // Otherwise, setup the Bluetooth session
        } else {
            if (mBluetoothService == null) {
                startBtService();
            }
        }
    }

    private PBluetooth.onBluetoothListener onBluetoothListener;

    public void scanBluetooth(PBluetooth.onBluetoothListener onBluetoothListener2) {
        onBluetoothListener = onBluetoothListener2;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothAdapter.startDiscovery();
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    onBluetoothListener.onDeviceFound(device.getName(), device.getAddress(), rssi);
                    //Log.d(TAG, device.getName() + "\n" + device.getAddress() + " " + rssi);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void destroy() {

        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up the Bluetooth session
                    startBtService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    //Log.d(TAG, "BT not enabled");
                    Toast.makeText(mContext.getApplicationContext(), "BT not enabled :(", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void addListener(SimpleBTListener simpleBTListener) {
        mSimpleBTListener = simpleBTListener;
    }

    public void removeListener(SimpleBTListener simpleBTListener) {
        //listeners.remove(simpleBTListener);
        mSimpleBTListener = null;
    }

    public void send(String string) {
        mBluetoothService.write(string.getBytes());
    }

    public void sendInt(int num) {
        mBluetoothService.writeInt(num);
    }

    public void disconnect() {
        mBluetoothService.stop();
    }

    public void disable() {
        mBluetoothAdapter.disable();
    }

    public void connectByMac(String mac) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
        mBluetoothService.connect(device);
    }

    public void connectByName(String name) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(name)) {
                    device = device;
                    mBluetoothService.connect(device);

                    break;
                }
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public Set<BluetoothDevice> listBondedDevices() {
        return mBluetoothAdapter.getBondedDevices();
    }

    public BluetoothAdapter getAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothSerialService getSerialService() {
        return mBluetoothService;
    }

    @Override
    public void stop() {
        disconnect();
    }

}
