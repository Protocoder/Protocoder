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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.api.other.WhatIsRunningInterface;

public class PLightIntensity extends CustomSensorManager implements WhatIsRunningInterface {

    private final static String TAG = PLightIntensity.class.getSimpleName();

    private ReturnInterface mCallbackLightChange;


    public PLightIntensity(AppRunner appRunner) {
        super(appRunner);

        type = Sensor.TYPE_LIGHT;
    }


    @ProtoMethod(description = "Start the light sensor. Returns the intensity. The value per device might vary", example = "")
    @ProtoMethodParam(params = {"function(intensity)"})
    public void start() {
        super.start();

        mListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                ReturnObject r = new ReturnObject();
                r.put("intensity", event.values[0]);
                mCallbackLightChange.event(r);
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

        isEnabled = mSensormanager.registerListener(mListener, sensor, speed);
    }

    @Override
    public String units() {
        return "lux";
    }


    @ProtoMethod(description = "Start the light sensor. Returns x, y, z", example = "")
    @ProtoMethodParam(params = {"function(x, y, z)"})
    public PLightIntensity onChange(final ReturnInterface callbackfn) {
        mCallbackLightChange = callbackfn;

        return this;
    }

}
