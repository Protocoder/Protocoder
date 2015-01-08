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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.widget.SeekBar;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

public class PSlider extends SeekBar implements PViewInterface {

    private float mMin = 0.0f;
    private float mMax = 100f;
    private float mCurrentValue = 0.0f;
    private int MAX_VALUE = 999999999;

	public PSlider(Context context) {
		super(context);
        super.setMax(MAX_VALUE);
		// setProgressDrawable(getResources().getDrawable(R.drawable.ui_seekbar_progress));
	}


    @ProtocoderScript
    @APIMethod(description = "Changes slider value", example = "")
    @APIParam(params = { "value" })
    public void setValue(float value) {
        mCurrentValue = value;
        int valueInt = (int) ((value - mMin) / (mMax - mMin) * MAX_VALUE);
        setProgress(valueInt);
    }

    public float valueToFloat(int valueInt) {
        float valueFloat = (float) (valueInt * (mMax - mMin) / MAX_VALUE) + mMin;

        return valueFloat;
    }

    @ProtocoderScript
    @APIMethod(description = "Gets the slider value", example = "")
    @APIParam(params = { "" })
    public float getValue() {
        return mCurrentValue;
    }

    @ProtocoderScript
    @APIMethod(description = "Sets the minimum slider value", example = "")
    @APIParam(params = { "" })
    public void setMin(float min) {
        mMin = min;
    }

    @ProtocoderScript
    @APIMethod(description = "Sets the maximum slider value", example = "")
    @APIParam(params = { "" })
    public void setMax(float max) {
        mMax = max;
    }

    @ProtocoderScript
    @APIMethod(description = "Gets the minimum  slider value", example = "")
    @APIParam(params = { "" })
    public float getMinVal() {
        return mMax;
    }

    @ProtocoderScript
    @APIMethod(description = "Gets the maximum slider value", example = "")
    @APIParam(params = { "" })
    public float getMaxVal() {
        return mMax;
    }

}
