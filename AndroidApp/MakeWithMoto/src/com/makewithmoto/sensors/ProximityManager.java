package com.makewithmoto.sensors;

import java.util.Vector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximityManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface ProximityListener {

		public void onDistanceChanged(float distance); 
		
	}

	
	private final static String TAG = "Proximity";

	/** indicates whether or not Accelerometer Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Accelerometer Sensor is running */
	private static boolean running = false;
	
	Vector<ProximityListener> listeners; 


	boolean proximitySupported;

	SensorManager sensormanager;
	SensorEventListener proximityListener;

	Sensor proximity;

	public ProximityManager(Context c) {
		super(c);
		listeners = new Vector<ProximityListener>();

		// register
		sensormanager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		proximity = sensormanager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		proximityListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (ProximityListener l : listeners) {
					l.onDistanceChanged(event.values[0]);
				}
				
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				switch (accuracy) {
				case SensorManager.SENSOR_STATUS_UNRELIABLE:
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
					break;
				case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
					break;
				}
			}

		};

	}

	@Override
	public boolean isListening() {
		return false;
	}

	@Override
	public void start() {
		running = true;
		proximitySupported = sensormanager.registerListener(proximityListener, proximity, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void stop() {
		running = false;
		sensormanager.unregisterListener(proximityListener);
	}

	public void addListener(ProximityListener distanceListener) {
		listeners.add(distanceListener);
	}

	public void removeListener(ProximityListener distanceListener) {
		listeners.remove(distanceListener);
	}


}
