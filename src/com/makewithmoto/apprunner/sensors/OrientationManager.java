package com.makewithmoto.apprunner.sensors;

import java.util.Vector;

import com.makewithmoto.appruner.webrunner.WhatIsRunningInterface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface OrientationListener {

		public void onOrientation(float pitch, float roll, float z);
	}

	SensorManager sensormanager;
	SensorEventListener orientationListener;
	Vector<OrientationListener> listeners;
	private Context c;
	Sensor orientation;
	private boolean orientationSupported;

	public OrientationManager(Context c) {
		this.c = c;
		listeners = new Vector<OrientationListener>();
		// register
		sensormanager = (SensorManager) c
				.getSystemService(Context.SENSOR_SERVICE);
		orientation = sensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		orientationListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {

				// Log.d("qq", event.values[0] + " " + event.values[1] + " " +
				// event.values[2]);

				for (OrientationListener l : listeners) {
					l.onOrientation(event.values[0], event.values[1],
							event.values[2]);

					// listener.onOrientationChange(event.values[2],
					// event.values[1], event.values[0]);
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		};
	}

	public void start() {
		orientationSupported = sensormanager.registerListener(
				orientationListener, orientation,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void stop() {
		sensormanager.unregisterListener(orientationListener);
	}

	public void addListener(OrientationListener orientationListener) {
		listeners.add(orientationListener);
	}

	public void removeListener(OrientationListener orientationListener) {
		listeners.remove(orientationListener);
	}

}
