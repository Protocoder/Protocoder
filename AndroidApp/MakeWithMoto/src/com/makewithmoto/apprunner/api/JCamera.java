package com.makewithmoto.apprunner.api;

import java.io.File;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.fragments.CameraFragment;

public class JCamera extends JInterface {

	private CameraFragment cameraFragment;


	public JCamera(Activity a, CameraFragment cameraFragment) {
		super(a);
		
		this.cameraFragment = cameraFragment;
	}

	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public void takePicture(String file) {
		cameraFragment.takePic(((AppRunnerActivity) a.get()).getCurrentDir() + File.separator + file);
	}	
	
	
	
}
