package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.APIAnnotation;

public class JVideo extends JInterface {

	public JVideo(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIAnnotation(description = "plays a video", example = "media.playVieo(fileName);")
	public void playVideo(String file) {
		

	}	
	
}
