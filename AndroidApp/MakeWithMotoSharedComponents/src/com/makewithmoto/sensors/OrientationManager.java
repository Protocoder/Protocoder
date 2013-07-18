package com.makewithmoto.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class OrientationManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface OrientationListener extends CustomSensorListener {

		public void onOrientation(float pitch, float roll, float z);
	}

	public OrientationManager(Context c) {
		super();
		
	
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		listener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {

				for (CustomSensorListener l : listeners) {
					((OrientationListener) l).onOrientation(event.values[0], event.values[1],
							event.values[2]);

				

				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};
	}


}
