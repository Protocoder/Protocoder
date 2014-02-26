/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.sensors;

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
				// listener
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
		proximitySupported = sensormanager.registerListener(proximityListener, proximity,
				SensorManager.SENSOR_DELAY_GAME);
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
