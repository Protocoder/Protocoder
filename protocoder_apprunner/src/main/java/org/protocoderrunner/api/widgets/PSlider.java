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

package org.protocoderrunner.api.widgets;

import android.content.Context;
import android.widget.SeekBar;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

public class PSlider extends SeekBar implements PViewInterface {

    private final PSlider mSlider;
    private float mMin = 0.0f;
    private float mMax = 100f;
    private float mCurrentValue = 0.0f;
    private int MAX_VALUE = 999999999;

    public PSlider(Context context) {
        super(context);
        super.setMax(MAX_VALUE);
        // setProgressDrawable(getResources().getDrawable(R.drawable.ui_seekbar_progress));

        mSlider = this;
    }

    @ProtoMethod(description = "Changes slider value", example = "")
    @ProtoMethodParam(params = {"value"})
    public void value(float value) {
        mCurrentValue = value;
        int valueInt = (int) ((value - mMin) / (mMax - mMin) * MAX_VALUE);
        setProgress(valueInt);
    }

    @ProtoMethod(description = "Gets the slider value", example = "")
    @ProtoMethodParam(params = {""})
    public float value() {
        return mCurrentValue;
    }

    @ProtoMethod(description = "Sets the minimum and maximum slider values", example = "")
    @ProtoMethodParam(params = {""})
    public PSlider range(float min, float max) {
        mMin = min;
        mMax = max;
        return this;
    }

    @ProtoMethod(description = "Sets the minimum slider value", example = "")
    @ProtoMethodParam(params = {""})
    public PSlider min(float min) {
        mMin = min;
        return this;
    }

    @ProtoMethod(description = "Sets the maximum slider value", example = "")
    @ProtoMethodParam(params = {""})
    public PSlider max(float max) {
        mMax = max;
        return this;
    }

    @ProtoMethod(description = "Gets the minimum  slider value", example = "")
    @ProtoMethodParam(params = {""})
    public float min() {
        return mMin;
    }

    @ProtoMethod(description = "Gets the maximum slider value", example = "")
    @ProtoMethodParam(params = {""})
    public float max() {
        return mMax;
    }

    private float valueToFloat(int valueInt) {
        float valueFloat = (float) (valueInt * (mMax - mMin) / MAX_VALUE) + mMin;
        return valueFloat;
    }

    @ProtoMethod(description = "On slider change", example = "")
    @ProtoMethodParam(params = {"function(value)"})
    public PSlider onChange(final ReturnInterface callbackfn) {
        // Add the change listener
        mSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ReturnObject r = new ReturnObject(PSlider.this);
                r.put("value", mSlider.valueToFloat(progress));
                callbackfn.event(r);
            }
        });

        return mSlider;
    }

}
