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
