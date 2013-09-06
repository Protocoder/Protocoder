package com.makewithmoto.hardware;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.makewithmoto.hardware.IOIOBoardService.IOIOServiceBinder;
import com.makewithmoto.system.SysFs;

public class IOIOBoard extends HardwareBase {
	
	private static String TAG = "IOIOBoard";

	private Activity activity_;
	private IOIOBoardService service_;
	private Intent serviceIntent_;
	private Boolean serviceBound = false;
	//protected IOIO ioio;

		
	private ServiceConnection connection_ = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IOIOServiceBinder binder = (IOIOServiceBinder) service;
			service_ = binder.getService();
			service_.setCallback(callback_);
			service_.start(serviceIntent_);
			serviceBound = true;
		}
	};
	
	public IOIOBoard(Activity activity, HardwareCallback callback) {
		super(callback);
		activity_ = activity;
	}

	
	/**
	 * Start
	 * To power on the board u can do this (/system/xbin/makr_poweron.sh):
	 * 
	 * echo 43  > /sys/class/gpio/export
	 * echo out > /sys/class/gpio/gpio43/direction
	 * echo 1   > /sys/class/gpio/gpio43/value
	 */
	@Override
	public void powerOn() {
		SysFs.write("/sys/class/gpio/export", "43");
		SysFs.write("/sys/class/gpio/gpio43/direction", "out");
		SysFs.write("/sys/class/gpio/gpio43/value", "1");
		
		Log.d(TAG, "Setting up intent");
		serviceIntent_ = new Intent(activity_, IOIOBoardService.class);
		Log.d(TAG, "Binding service...");
		activity_.bindService(serviceIntent_, connection_, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "Service bound with connection");
	}
	
	/**
	 * Power off the board
	 * To power off the board u can do this (/system/xbin/makr_poweroff.sh):
	 * 
	 * echo 43  > /sys/class/gpio/export
	 * echo out > /sys/class/gpio/gpio43/direction
	 * echo 0   > /sys/class/gpio/gpio43/value
	 */
	@Override
	public void powerOff() {
		if (serviceBound) {
			Log.d(TAG, "Aborting thread...");
			service_.stopSelf();
			activity_.unbindService(connection_);
			serviceBound = false;
			service_ = null;
		}
		SysFs.write("/sys/class/gpio/export", "43");
		SysFs.write("/sys/class/gpio/gpio43/direction", "out");
		SysFs.write("/sys/class/gpio/gpio43/value", "0");
	}
	
	public void stop() {
		Log.d(TAG, "IOIOBoard stop called");
		if (serviceIntent_ != null) {
			activity_.stopService(serviceIntent_);
		}
		powerOff();
	}
	
	

}
