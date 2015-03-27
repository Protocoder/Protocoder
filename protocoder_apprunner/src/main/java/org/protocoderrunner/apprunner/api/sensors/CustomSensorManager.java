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

package org.protocoderrunner.apprunner.api.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;

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
    @ProtoMethodParam(params = {""})
    public void stop() {
        running = false;
        if (listener != null) {
            sensormanager.unregisterListener(listener);
            listener = null;

            // WhatIsRunning.getInstance().remove(this);
        }
    }


    @ProtoMethod(description = "Set the speed of the sensor 'slow', 'fast', 'normal'", example = "")
    @ProtoMethodParam(params = {"function(x, y, z)"})
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
