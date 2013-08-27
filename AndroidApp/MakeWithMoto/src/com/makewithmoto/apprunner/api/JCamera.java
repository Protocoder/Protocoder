package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;

public class JCamera extends JInterface {

	public JCamera(Activity a) {
		super(a);
	}

	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.startCamera();")
	public void startCamera(String file) {
		

	}	
	
}
