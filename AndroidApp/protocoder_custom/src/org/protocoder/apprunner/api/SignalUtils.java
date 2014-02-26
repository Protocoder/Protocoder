/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
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

package org.protocoder.apprunner.api;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.JavascriptInterface;

import android.app.Activity;

public class SignalUtils extends JInterface {

    public SignalUtils(Activity a) {
	super(a);

    }

    public LowPass lowpass() {
	return null;
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    @APIParam(params = { "function()" })
    public void fft(boolean visible) {

	// FFT fft = new FFT(10);
	// fft.fft(re, im);
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