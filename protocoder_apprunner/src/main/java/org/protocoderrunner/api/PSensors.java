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

package org.protocoderrunner.api;

import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.api.sensors.PAccelerometer;
import org.protocoderrunner.api.sensors.PGPS;
import org.protocoderrunner.api.sensors.PGyroscope;
import org.protocoderrunner.api.sensors.PLightIntensity;
import org.protocoderrunner.api.sensors.PMagnetic;
import org.protocoderrunner.api.sensors.POrientation;
import org.protocoderrunner.api.sensors.PPressure;
import org.protocoderrunner.api.sensors.PProximity;
import org.protocoderrunner.api.sensors.PStep;
import org.protocoderrunner.apidoc.annotation.ProtoField;
import org.protocoderrunner.apidoc.annotation.ProtoObject;
import org.protocoderrunner.apprunner.AppRunner;


@ProtoObject
public class PSensors extends ProtoBase {

    @ProtoField
    public final PAccelerometer accelerometer;
    @ProtoField
    public final PGyroscope gyroscope;
    @ProtoField
    public final PGPS gps;
    @ProtoField
    public final PLightIntensity light;
    @ProtoField
    public final PMagnetic magnetic;
    @ProtoField
    public final POrientation orientation;
    @ProtoField
    public final PPressure pressure;
    @ProtoField
    public final PProximity proximity;
    @ProtoField
    public final PStep stepDetector;

    public PSensors(AppRunner appRunner) {
        super(appRunner);

        accelerometer = new PAccelerometer(appRunner);
        gyroscope = new PGyroscope(appRunner);
        gps = new PGPS(appRunner);
        light = new PLightIntensity(appRunner);
        magnetic = new PMagnetic(appRunner);
        orientation = new POrientation(appRunner);
        pressure = new PPressure(appRunner);
        proximity = new PProximity(appRunner);
        stepDetector = new PStep(appRunner);
    }

    public ReturnObject listAvailable() {
        ReturnObject r = new ReturnObject();
        r.put("accelerometer", accelerometer.isAvailable());
        r.put("gyroscope", gyroscope.isAvailable());
        r.put("gps", gps.isAvailable());
        r.put("light", light.isAvailable());
        r.put("magnetic", magnetic.isAvailable());
        r.put("orientation", orientation.isAvailable());
        r.put("pressure", pressure.isAvailable());
        r.put("proximity", proximity.isAvailable());
        r.put("step", stepDetector.isAvailable());

        return r;
    }

    @Override
    public void __stop() {

    }
}
