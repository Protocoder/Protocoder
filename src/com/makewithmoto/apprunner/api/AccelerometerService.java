//package com.makewithmoto.apprunner.api;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.util.Log;
//
//import com.makewithmoto.appruner.webrunner.WhatIsRunning;
//import com.makewithmoto.apprunner.sensors.AccelerometerManager;
//import com.makewithmoto.apprunner.sensors.AccelerometerManager.AccelerometerListener;
//
//public class AccelerometerService extends Service implements AccelerometerListener {
//	private String callback_;
//	private AccelerometerManager accelerometerManager;
//	private AccelerometerListener accelerometerListener;
//	
//    @Override
//    public void onStart(Intent intent, int startId) {
//    	Bundle extras = intent.getExtras();
//    	if (extras == null) {
//    		Log.d("AccelerometerService", "Null extras");
//    	} else {
//    		String callback = (String) extras.get("callback");
//    		Log.d("AccelerometerService", "Setting callback: " + callback);
//    		callback_ = callback;
//    	}
//    }
//
//    
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		Log.d("AccelerometerService", "onCreate");
//		accelerometerManager = new AccelerometerManager(this.getApplicationContext());
//		accelerometerListener = new AccelerometerListener() {
//
//			@Override
//			public void onShake(float force) {
//
//			}
//
//			@Override
//			public void onAccelerometerChanged(float x, float y, float z) {
//				Log.d("AccelerometerService", "x: " + x);
//				final JSONObject res = new JSONObject();
//				try {
//					res.put("type", "sensor");
//					res.put("name", "accelerometer");
//					res.put("x", x);
//					res.put("y", y);
//					res.put("z", z);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				try {
//					c.tryWriteToSockets("sensor", res.toString());
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				c.runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						applicationWebView.runJavascript("window['" + callback_ + "']('" + res.toString() + "');");						
//					}
//				});
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//
//		accelerometerManager.addListener(accelerometerListener);
//		accelerometerManager.start();
//		WhatIsRunning.getInstance().add(accelerometerManager);
//	}
//	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		accelerometerManager.removeListener(accelerometerListener);
//		accelerometerManager.stop();
//	}
//	
//	@Override
//	public void onAccelerometerChanged(float x, float y, float z) {
//	}
//
//	@Override
//	public void onShake(float force) {
//	}
//
//	@Override
//	public IBinder onBind(Intent arg0) {
//		return null;
//	}
//	
//}