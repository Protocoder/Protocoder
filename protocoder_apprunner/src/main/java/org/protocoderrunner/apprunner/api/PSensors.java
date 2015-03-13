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

package org.protocoderrunner.apprunner.api;

import android.content.Context;

import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.sensors.PAccelerometer;
import org.protocoderrunner.apprunner.api.sensors.PGPS;
import org.protocoderrunner.apprunner.api.sensors.PGyroscope;
import org.protocoderrunner.apprunner.api.sensors.PLightIntensity;
import org.protocoderrunner.apprunner.api.sensors.PMagnetic;
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

	public PSensors(Context context) {
		super(context);

        accelerometer = new PAccelerometer(context);
        gyroscope = new PGyroscope(context);
        gps = new PGPS(context);
        lightIntensity = new PLightIntensity(context);
        magnetic = new PMagnetic(context);
        //nfc = new PNFC(context);
        orientation = new POrientation(context);
        pressure = new PPressure(context);
        proximity = new PProximity(context);
        stepDetector = new PStep(context);

	}

    @Override
	public void destroy() {

	}

}
