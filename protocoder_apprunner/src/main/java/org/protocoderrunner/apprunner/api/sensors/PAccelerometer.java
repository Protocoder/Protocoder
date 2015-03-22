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

package org.protocoderrunner.apprunner.api.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.other.WhatIsRunningInterface;

public class PAccelerometer extends CustomSensorManager implements WhatIsRunningInterface {

    private AccelerometerChangeCB mCallbackAccelerometerChange;
    private AccelerometerForceCB mCallbackAccelerometerForce;

    interface AccelerometerChangeCB {
        void event(float x, float y, float z);
    }

    interface AccelerometerForceCB {
        void event(float force);
    }

    private final static String TAG = "PAccelerometer";


    public PAccelerometer(Context c) {
        super(c);

        type = Sensor.TYPE_ACCELEROMETER;
    }

    public void start() {
        if (running) {
            return;
        }
        super.start();

        listener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mCallbackAccelerometerChange != null) {
                    mCallbackAccelerometerChange.event(event.values[0], event.values[1], event.values[2]);
                }

                if (mCallbackAccelerometerForce != null) {
                    float force = (float) Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2)
                            + Math.pow(event.values[2], 2));

                    mCallbackAccelerometerForce.event(force);
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

        isSupported = sensormanager.registerListener(listener, sensor, speed);
    }


    @ProtoMethod(description = "Start the accelerometer. Returns x, y, z", example = "")
    @ProtoMethodParam(params = {"function(x, y, z)"})
    public void onChange(final AccelerometerChangeCB callbackfn) {
        mCallbackAccelerometerChange = callbackfn;

        start();
    }

    @ProtoMethod(description = "Start the accelerometer. Returns x, y, z", example = "")
    @ProtoMethodParam(params = {"function(x, y, z)"})
    public void onForce(final AccelerometerChangeCB callbackfn) {
        mCallbackAccelerometerChange = callbackfn;

        start();
    }

}
