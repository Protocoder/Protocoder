package org.protocoder.bt;

import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SimpleBT {
	private static final String TAG = "BT";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Message types sent from the BluetoothService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

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

		public void onMessageReceived(String cmd, String data);

		public void onRawDataReceived(final byte[] buffer, final int size);

	}

	public SimpleBT(Activity ac) {
		this.ac = ac;
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	}

	private void setupUserInterface() {
		Log.d(TAG, "setupUserInterface()");

		// Initialize the BluetoothService to perform Bluetooth connections
		mBluetoothService = new BluetoothSerialService(mHandler);
	}

	// The Handler that gets information back from the BluetoothService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothSerialService.STATE_CONNECTED:
					// connected = true;
					// mTitle.setText(mConnectedDeviceName);
					break;
				case BluetoothSerialService.STATE_CONNECTING:
					// mTitle.setText("Connecting");
					break;
				case BluetoothSerialService.STATE_NONE:
					connected = false;
					// mTitle.setText("Not connected");
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				// mConversationArrayAdapter.add(">>> " + writeMessage);
				Log.d(TAG, "written = '" + writeMessage + "'");
				break;
			case MESSAGE_READ:
				if (paused) {
					break;
				}
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);

				// TODO here is where we get the BT data
				Log.d(TAG, "received " + readMessage);

				// mConversationArrayAdapter.add(readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				// mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(ac.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				// Toast.makeText(getApplicationContext(),
				// msg.getData().getString(TOAST),
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void startDeviceListActivity() {
		Intent serverIntent = new Intent(ac.getApplicationContext(), DeviceListActivity.class);
		ac.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	public void start() {

		// If BT is not on, request that it be enabled.
		// setupUserInterface() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			ac.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the Bluetooth session
		} else {
			if (mBluetoothService == null) {
				setupUserInterface();
			}
		}
	}

	public void destroy() {

		if (mBluetoothService != null) {
			mBluetoothService.stop();
		}
	}

	public void result(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				// Attempt to connect to the device
				mBluetoothService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a Bluetooth session
				setupUserInterface();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(ac.getApplicationContext(), "BT not enabled, leaving", Toast.LENGTH_SHORT).show();

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

}
