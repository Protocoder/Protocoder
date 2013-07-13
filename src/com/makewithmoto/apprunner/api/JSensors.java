package com.makewithmoto.apprunner.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.RemoteException;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apprunner.MWMActivity;
import com.makewithmoto.apprunner.sensors.AccelerometerManager;
import com.makewithmoto.apprunner.sensors.AccelerometerManager.AccelerometerListener;
import com.makewithmoto.apprunner.sensors.OrientationManager;
import com.makewithmoto.apprunner.sensors.OrientationManager.OrientationListener;
import com.makewithmoto.apprunner.webrunner.WhatIsRunning;

public class JSensors extends JInterface {

	private AccelerometerManager accelerometerManager;
	private AccelerometerListener accelerometerListener;
	private OrientationManager orientationManager;
	private OrientationListener orientationListener;

	public JSensors(MWMActivity mwmActivity) {
		super(mwmActivity);
	}

	@JavascriptInterface
	public void startAccelerometer(final String callback) {
		accelerometerManager = new AccelerometerManager(c.get());
		accelerometerListener = new AccelerometerListener() {

			@Override
			public void onShake(float force) {

			}

			@Override
			public void onAccelerometerChanged(float x, float y, float z) {
				JSONObject res = new JSONObject();
				try {
					res.put("type", "sensor");
					res.put("name", "accelerometer");
					res.put("x", x);
					res.put("y", y);
					res.put("z", z);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					c.get().tryWriteToSockets("sensor", res.toString());
				} catch (RemoteException e) {
					Log.i(TAG, "Failed sending sensor to the socket");
					e.printStackTrace();
				}
//				applicationWebView.runJavascript("window['" + callback + "']('" + res.toString() + "');");
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		};
		accelerometerManager.addListener(accelerometerListener);
		accelerometerManager.start();
		WhatIsRunning.getInstance().add(accelerometerManager);
	}

	@JavascriptInterface
	public void stopAccelerometer(final String callback) {
		Log.d(TAG, "Called stopAccelerometer");
		accelerometerManager.removeListener(accelerometerListener);
		accelerometerManager.stop();
	}

	@JavascriptInterface
	public void startOrientation(final String callback) {
		orientationManager = new OrientationManager(c.get());

		orientationListener = new OrientationListener() {

			@Override
			public void onOrientation(float pitch, float roll, float z) {
				// TODO Auto-generated method stub

			}
		};
		orientationManager.addListener(orientationListener);
		orientationManager.start();
		WhatIsRunning.getInstance().add(orientationManager);

	}

	@JavascriptInterface
	public void stopOrientation() {
		orientationManager.removeListener(orientationListener);
		orientationManager.stop();
	}

	public String createCallback(String nameCallback, float x, float y, float z) {
		String m = "javascript:" + nameCallback + "('" + x + "', '" + y
				+ "', '" + z + "');";

		Log.d(TAG, m);

		return m;

	}

	@Override
	public void destroy() {
		if (accelerometerManager != null) {
			stopAccelerometer("undefinedCallback");
		}

	}

}
