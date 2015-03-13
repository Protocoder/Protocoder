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
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.util.Vector;

public class CustomSensorManager {

	private final static String TAG = "CustomSensor";

	Context c;

	public boolean running = false;
	boolean isSupported;

    int speed = SensorManager.SENSOR_DELAY_FASTEST;

    Sensor sensor;
	SensorManager sensormanager;
	SensorEventListener listener;
    protected int type = -1;

	public CustomSensorManager(Context c) {
		sensormanager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);

	}

	public boolean isListening() {
		return false;
	}

    @ProtoMethod(description = "Start the sensor", example = "")
    public void start() {
	    running = true;
        WhatIsRunning.getInstance().add(this);
        sensor = sensormanager.getDefaultSensor(type);
    }

    @ProtoMethod(description = "Stop the sensor", example = "")
    @ProtoMethodParam(params = { "" })
	public void stop() {
		running = false;
        if (listener != null) {
            sensormanager.unregisterListener(listener);
            listener = null;

           // WhatIsRunning.getInstance().remove(this);
        }
	}


    @ProtoMethod(description = "Set the speed of the sensor 'slow', 'fast', 'normal'", example = "")
    @ProtoMethodParam(params = { "function(x, y, z)" })
    public void sensorsSpeed(String speed) {
        if (speed.equals("slow")) {
            this.speed = SensorManager.SENSOR_DELAY_UI;
        } else if (speed.equals("fast")) {
            this.speed = SensorManager.SENSOR_DELAY_FASTEST;
        } else {
            this.speed = SensorManager.SENSOR_DELAY_NORMAL;
        }

    }


    public float maximum() {
        return sensor.getMaximumRange();
    }

    public float power() {
        return sensor.getPower();
    }

    public float resolution() {
        return sensor.getResolution();
    }



}
