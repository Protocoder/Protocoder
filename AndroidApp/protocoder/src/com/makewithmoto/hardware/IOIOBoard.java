/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

package com.makewithmoto.hardware;

import ioio.lib.api.IOIO;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.makewithmoto.hardware.IOIOBoardService.IOIOServiceBinder;
import com.makewithmoto.utils.SysFs;

public class IOIOBoard extends HardwareBase {
	
	private static String TAG = "IOIOBoard";

	private Activity activity_;
	private IOIOBoardService service_;
	private Intent serviceIntent_;
	private Boolean serviceBound = false;
	protected IOIO ioio;

		
	private ServiceConnection connection_ = new ServiceConnection() {
		
		@Override 
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
			Log.d(TAG, "onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			IOIOServiceBinder binder = (IOIOServiceBinder) service;
			service_ = binder.getService();
			service_.setCallback(callback_);
			service_.start(serviceIntent_);
			serviceBound = true;
			Log.d(TAG, "onServiceConnected");
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
		powerOff();
		if (serviceIntent_ != null) {
			activity_.stopService(serviceIntent_);
		}
	}



}
