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

import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.sensors.WhatIsRunningInterface;

import java.util.Set;
import java.util.Vector;

public class SimpleBT implements WhatIsRunningInterface {
	private static final String TAG = "BT";

	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
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

	Vector<SimpleBTListener> listeners = new Vector<SimpleBT.SimpleBTListener>();

	private final Activity ac;

    public interface SimpleBTListener {

		public void onConnected();
		public void onMessageReceived(String data);
		public void onRawDataReceived(final byte[] buffer, final int size);

	}

	public SimpleBT(Activity ac) {
		this.ac = ac;
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
					break;
				case BluetoothSerialService.STATE_CONNECTING:
					break;
				case BluetoothSerialService.STATE_NONE:
					connected = false;
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
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				// here is where we get the BT data
				Log.d(TAG, "received " + readMessage);
				for (int i = 0; i < listeners.size(); i++) {
					SimpleBTListener l = listeners.get(0);
					l.onMessageReceived(readMessage);
				}

				break;
			case MESSAGE_DEVICE_NAME:
                for (int i = 0; i < listeners.size(); i++) {
                    SimpleBTListener l = listeners.get(0);
                    l.onConnected();
                }
				//Toast.makeText(ac.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void start() {

		// If BT is not on, request that it be enabled.
		// setupUserInterface() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			ac.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the Bluetooth session
		} else {
			if (mBluetoothService == null) {
				startBtService();
			}
		}
	}

	private PNetwork.onBluetoothListener onBluetoothListener;

	public void scanBluetooth(PNetwork.onBluetoothListener onBluetoothListener2) {
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
		ac.registerReceiver(mReceiver, filter);
	}

	public void destroy() {

		if (mBluetoothService != null) {
			mBluetoothService.stop();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				//String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				//BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				// Attempt to connect to the device
				//mBluetoothService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a Bluetooth session
				startBtService();
			} else {
				// User did not enable Bluetooth or an error occurred
				//Log.d(TAG, "BT not enabled");
				Toast.makeText(ac.getApplicationContext(), "BT not enabled :(", Toast.LENGTH_SHORT).show();

				// TODO show error
				// finish();
			}
		}
	}

	public void addListener(SimpleBTListener simpleBTListener) {
		listeners.add(simpleBTListener);
	}

	public void removeListener(SimpleBTListener simpleBTListener) {
		listeners.remove(simpleBTListener);
	}

	public void send(String string) {
		mBluetoothService.write(string.getBytes());

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
