package com.makewithmoto.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface LightListener extends CustomSensorListener {

		public void onLightChanged(float f); 
		
	}

	private final static String TAG = "Light";
	

	public LightManager(Context c) { 
		super(); 
		
		// register
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_LIGHT);

		listener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (CustomSensorListener l : listeners) {
					((LightListener)l).onLightChanged(event.values[0]);
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
