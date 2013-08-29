package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;

public class JFileIO extends JInterface {

	String TAG = "JFileIO";

	public JFileIO(Activity a) {
        super(a); 
	}

	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void createDir(String name) {
		
	}
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void remove(String name) {
		
	}
	
	

}
