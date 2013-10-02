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

package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.makewithmoto.views.PlotView;
import com.makewithmoto.views.PlotView.Plot;

public class JPlotView extends JInterface {


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
	
	public void setLimits(float min, float max) { 
		plotView.setLimits(min, max);		
	}
	
	public void update(float value) { 
		plotView.setValue(plot, value);
	} 
	


}
