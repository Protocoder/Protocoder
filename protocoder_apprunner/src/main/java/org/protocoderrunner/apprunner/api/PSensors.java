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

package org.protocoderrunner.apprunner.api;

import android.content.Context;

import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.sensors.PAccelerometer;
import org.protocoderrunner.apprunner.api.sensors.PGPS;
import org.protocoderrunner.apprunner.api.sensors.PGyroscope;
import org.protocoderrunner.apprunner.api.sensors.PLightIntensity;
import org.protocoderrunner.apprunner.api.sensors.PMagnetic;
import org.protocoderrunner.apprunner.api.sensors.PNfc;
import org.protocoderrunner.apprunner.api.sensors.POrientation;
import org.protocoderrunner.apprunner.api.sensors.PPressure;
import org.protocoderrunner.apprunner.api.sensors.PProximity;
import org.protocoderrunner.apprunner.api.sensors.PStep;

public class PSensors extends PInterface {


    public final PAccelerometer accelerometer;
    public final PGyroscope gyroscope;
    public final PGPS gps;
    public final PLightIntensity lightIntensity;
    public final PMagnetic magnetic;
    public final POrientation orientation;
    public final PPressure pressure;
    public final PProximity proximity;
    public final PStep stepDetector;
    public PNfc nfc;

    public PSensors(Context context) {
        super(context);

        accelerometer = new PAccelerometer(context);
        gyroscope = new PGyroscope(context);
        gps = new PGPS(context);
        lightIntensity = new PLightIntensity(context);
        magnetic = new PMagnetic(context);

        orientation = new POrientation(context);
        pressure = new PPressure(context);
        proximity = new PProximity(context);
        stepDetector = new PStep(context);
    }

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        nfc = new PNfc(getContext());
        nfc.initForParentFragment(getFragment());
    }

    @Override
    public void destroy() {

    }

}
