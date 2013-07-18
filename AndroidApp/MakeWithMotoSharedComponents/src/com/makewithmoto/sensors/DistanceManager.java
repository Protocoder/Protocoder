package com.makewithmoto.sensors;

import java.util.Vector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DistanceManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface GyroscopeListener {

		public void onGyroscopeChanged(float x, float y, float z); 
		
	}

	
	private final static String TAG = "Gyroscope";

	/** indicates whether or not Accelerometer Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Accelerometer Sensor is running */
	private static boolean running = false;
	
	Vector<GyroscopeListener> listeners; 


	boolean gyroscopeSupported;

	SensorManager sensormanager;
	SensorEventListener gyroscopeListener;

	Sensor gyroscope;

	public DistanceManager(Context c) {
		super(c);
		listeners = new Vector<GyroscopeListener>();

		// register
		sensormanager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		gyroscope = sensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		gyroscopeListener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (GyroscopeListener l : listeners) {
					l.onGyroscopeChanged(event.values[0], event.values[1], event.values[2]);
				}
				
			}

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

	public boolean isListening() {
		return false;
	}

	public void start() {
		running = true;
		gyroscopeSupported = sensormanager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_GAME);
	}

	public void stop() {
		running = false;
		sensormanager.unregisterListener(gyroscopeListener);
	}

	public void addListener(GyroscopeListener gyroscopeListener) {
		listeners.add(gyroscopeListener);
	}

	public void removeListener(GyroscopeListener gyroscopeListener) {
		listeners.remove(gyroscopeListener);
	}


}
