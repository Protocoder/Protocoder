/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.api.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.other.WhatIsRunningInterface;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;

public abstract class CustomSensorManager implements WhatIsRunningInterface {

    private final static String TAG = CustomSensorManager.class.getSimpleName();

    AppRunner mAppRunner;

    protected Sensor sensor;
    protected SensorManager mSensormanager;
    protected SensorEventListener mListener;
    protected ReturnInterface mCallback;
    protected int speed = SensorManager.SENSOR_DELAY_FASTEST;
    protected int type = -1;
    protected boolean isEnabled = false;

    public CustomSensorManager(AppRunner appRunner) {
        mAppRunner = appRunner;
        mSensormanager = (SensorManager) mAppRunner.getAppContext().getSystemService(Context.SENSOR_SERVICE);
    }

    public boolean isListening() {
        return false;
    }

    @ProtoMethod(description = "Start the sensor", example = "")
    public void start() {
        if (isEnabled) {
            return;
        }
        mAppRunner.whatIsRunning.add(this);
        sensor = mSensormanager.getDefaultSensor(type);
    }

    @ProtoMethod(description = "Stop the sensor", example = "")
    @ProtoMethodParam(params = {""})
    public void stop() {
        isEnabled = false;
        if (mListener != null) {
            mSensormanager.unregisterListener(mListener);
            mListener = null;
        }
    }


    @ProtoMethod(description = "Set the speed of the sensor 'slow', 'fast', 'normal'", example = "")
    @ProtoMethodParam(params = {"speed=['slow', 'fast', 'normal']"})
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

    @ProtoMethod(description = "Check if the device has accelerometer", example = "")
    public boolean isAvailable() {
        return mSensormanager.getDefaultSensor(type) != null;
    }

    public Sensor info() {
        return mSensormanager.getDefaultSensor(type);
    }

    public abstract String units();

    public void __stop() {
        stop();
    }
}
