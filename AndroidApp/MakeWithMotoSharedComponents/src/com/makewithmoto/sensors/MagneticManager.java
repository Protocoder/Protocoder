package com.makewithmoto.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MagneticManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface MagneticListener extends CustomSensorListener {

		public void onMagneticChanged(float f); 
		
	}

	private final static String TAG = "Magnetic";
	

	public MagneticManager(Context c) { 
		super(c);
		
		// register
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		listener = new SensorEventListener() {

			public void onSensorChanged(SensorEvent event) { 
				//listener
				for (CustomSensorListener l : listeners) {
					((MagneticListener)l).onMagneticChanged(event.values[0]);
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
