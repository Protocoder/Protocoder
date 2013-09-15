package com.makewithmoto.apprunner.api;

import java.io.File;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.fragments.CameraFragment;
import com.makewithmoto.fragments.CameraFragment.CameraListener;

public class JCamera extends JInterface {

	private CameraFragment cameraFragment;


	public JCamera(Activity a, CameraFragment cameraFragment) {
		super(a);
		
		this.cameraFragment = cameraFragment;
	}

	
	@JavascriptInterface
	@APIMethod(description = "", example = "camera.takePicture();")
	public void takePicture(String file, final String callbackfn) {
		cameraFragment.takePic(((AppRunnerActivity) a.get()).getCurrentDir() + File.separator + file);
		cameraFragment.addListener(new CameraListener() {
			
			@Override
			public void onVideoRecorded() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPicTaken() {
				callback(callbackfn);
				cameraFragment.removeListener(this);
			}
		});
	}	
	
	
	
	
}
