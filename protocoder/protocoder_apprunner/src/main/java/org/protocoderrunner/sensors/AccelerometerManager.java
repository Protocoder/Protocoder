/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoderrunner.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.apprunner.api.other.WhatIsRunningInterface;

public class AccelerometerManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface AccelerometerListener extends CustomSensorListener {

		public void onAccelerometerChanged(float x, float y, float z);

		public void onShake(float force);

	}

	private final static String TAG = "Accel";

	private float currentValueX;
	private float previousValueX;
	private float currentValueY;
	private float previousValueY;
	private float currentValueZ;
	private float previousValueZ;

	public AccelerometerManager(Context c) {
		super(c);

		// register
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		listener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				// listener
				for (CustomSensorListener l : listeners) {
					((AccelerometerListener) l).onAccelerometerChanged(event.values[0], event.values[1],
							event.values[2]);

					float force = (float) Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2)
							+ Math.pow(event.values[2], 2));

					((AccelerometerListener) l).onShake(force);
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

	public float total() {

		return currentValueX + currentValueY + currentValueZ;
	}

	public float totalDiff() {

		return Math.abs((currentValueX + currentValueY + currentValueZ)
				- (currentValueX + currentValueY + currentValueZ));
	}

	public float diffX() {
		return Math.abs(currentValueX - previousValueX);
	}

	public float diffY() {
		return Math.abs(currentValueY - previousValueY);
	}

	public float diffZ() {
		return Math.abs(currentValueZ - previousValueZ);
	}

}
