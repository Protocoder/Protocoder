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
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.other.WhatIsRunningInterface;

public class PProximity extends CustomSensorManager implements WhatIsRunningInterface {


    public interface ProximityListener {
        public void event(float distance);
    }

    private final static String TAG = "Proximity";
    private ProximityListener mCallbackProximityChange;


    public PProximity(Context c) {
        super(c);

        type = Sensor.TYPE_PROXIMITY;
    }


    @ProtoMethod(description = "Start the proximity sensor. Returns a proximty value. It might differ per device", example = "")
    @ProtoMethodParam(params = {"function(proximity)"})
    public void start() {
        if (running) {
            return;
        }
        super.start();

        listener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                mCallbackProximityChange.event(event.values[0]);
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
    public void onChange(final ProximityListener callbackfn) {
        mCallbackProximityChange = callbackfn;

        start();
    }

}
