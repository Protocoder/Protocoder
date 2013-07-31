package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.APIAnnotation;

public class JCamera extends JInterface {

	public JCamera(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIAnnotation(description = "", example = "camera.startCamera();")
	public void startCamera(String file) {
		

	}	
	
}
