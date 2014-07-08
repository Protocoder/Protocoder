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

package org.protocoder.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepManager extends CustomSensorManager implements WhatIsRunningInterface {

	public interface StepListener extends CustomSensorListener {

		public void onStepMade();

	}

	private final static String TAG = "Step";

	public StepManager(Context c) {
		super(c);

		// register
		sensor = sensormanager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

		listener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				// listener
				for (CustomSensorListener l : listeners) {
					((StepListener) l).onStepMade();
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

}
