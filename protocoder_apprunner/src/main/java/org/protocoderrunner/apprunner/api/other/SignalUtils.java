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