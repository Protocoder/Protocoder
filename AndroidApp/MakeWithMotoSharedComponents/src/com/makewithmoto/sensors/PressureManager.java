package com.makewithmoto.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PressureManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface PressureListener extends CustomSensorListener {

		public void onPressureChanged(float f); 
		
	}

	private final static String TAG = "Pressure";
	

	public PressureManager(Context c) { 
		super(); 
		
		// register
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		listener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (CustomSensorListener l : listeners) {
					((PressureListener)l).onPressureChanged(event.values[0]);
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
