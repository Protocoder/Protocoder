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

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.views.PlotView;

import android.content.Context;
import android.graphics.Color;

public class PPlotView extends PlotView implements PViewInterface {

	private static final String TAG = "JPlotView";

	public PPlotView(Context context) {
		super(context);
	}

    @ProtocoderScript
    @APIMethod(description = "", example = "")
    public PlotView setBackground(String c) {
        super.setBackgroundColor(Color.parseColor(c));

        return this;
    }

    @Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PlotView setThickness(float r) {
		return super.setThickness(r);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PlotView setDefinition(int definition) {
		return super.setDefinition(definition);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PlotView setColor(String plotName, String rgb) {
		return super.setColor(plotName, rgb);
	}

	@Override
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PlotView setLimits(float min, float max) {
        return super.setLimits(min, max);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void update(float value) {
		super.setValue("default", value);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void update(String plotName, float value) {
		super.setValue(plotName, value);
	}

}
