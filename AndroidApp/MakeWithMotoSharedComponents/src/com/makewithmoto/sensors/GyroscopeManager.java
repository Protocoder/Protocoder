package com.makewithmoto.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GyroscopeManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface GyroscopeListener extends CustomSensorListener {

		public void onGyroscopeChanged(float x, float y, float z); 
		
	}

	
	private final static String TAG = "Gyroscope";


	public GyroscopeManager(Context c) {
		super(c);
		
		// register
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		listener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (CustomSensorListener l : listeners) {
					((GyroscopeListener)l ).onGyroscopeChanged(event.values[0], event.values[1], event.values[2]);
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



}
