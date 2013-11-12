/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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

package org.protocoder.apprunner.api.widgets;

import org.protocoder.AppSettings;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.JavascriptInterface;
import org.protocoder.views.PlotView;
import org.protocoder.views.PlotView.Plot;

import android.app.Activity;
import android.graphics.Color;

public class JPlotView extends JInterface implements JViewInterface {

    private static final String TAG = "JPlotView";
    String name;
    private Plot plot;
    private PlotView plotView;

    public JPlotView(Activity a, PlotView plotView, float min, float max) {
	super(a);
	this.plotView = plotView;
	plot = plotView.new Plot(Color.RED);
	plotView.addPlot(plot);
	plotView.setLimits(min, max);

    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void setThickness(float r) {
	plotView.setThickness(r);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void setLimits(float min, float max) {
	plotView.setLimits(min, max);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void update(float value) {
	plotView.setValue(plot, value);
    }

    @Override
    public void move(float x, float y) {
	plotView.animate().x(x).setDuration(AppSettings.animSpeed);
	plotView.animate().y(y).setDuration(AppSettings.animSpeed);

    }

    @Override
    public void rotate(float deg) {
	plotView.animate().rotation(deg).setDuration(AppSettings.animSpeed);
    }

}
