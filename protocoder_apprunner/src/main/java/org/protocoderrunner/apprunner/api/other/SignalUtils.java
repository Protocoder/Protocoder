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

package org.protocoderrunner.apprunner.api.other;

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.utils.FFT;

public class SignalUtils extends PInterface {

    FFT fft;
    double im[];

    public SignalUtils(Context a, int n) {
        super(a);
        fft = new FFT(n);
        im = new double[n];

        for (int i = 0; i < n; i++) {
            im[i] = 0;
        }
    }

    public LowPass lowpass() {
        return null;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function()"})
    public double[] fft(double[] re) {
        fft.fft(re, im.clone());

        return re;
    }

    class LowPass {
        int n;
        float[] vals;
        float sum = 0.0f;

        public LowPass(int n) {
            this.n = n;
            vals = new float[n];
        }

        public float smooth(float newVal) {

            for (int i = 0; i < vals.length; i++) {
                sum = +vals[i];

                // shift to the left
                if (i < vals.length - 1) {
                    vals[i] = vals[i + 1];
                } else {
                    vals[i] = newVal;
                }
            }
            return sum / n;
        }

    }

}