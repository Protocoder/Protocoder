package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.APIAnnotation;

public class JWebApp extends JInterface {

	String TAG = "JWebApp";

	public JWebApp(Activity a) {
        super(a); 
	}

	@JavascriptInterface 
    @APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public JWebAppPlot addWidget(String name, int x, int y, int w, int h) {
		
		JWebAppPlot jWebAppPlot = new JWebAppPlot(a.get());
		jWebAppPlot.add(name, x, y, w, h);
		
		return jWebAppPlot;
	}
}
