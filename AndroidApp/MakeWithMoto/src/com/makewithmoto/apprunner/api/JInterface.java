package com.makewithmoto.apprunner.api;

import java.lang.ref.WeakReference;

import android.app.Activity;

import com.makewithmoto.apidoc.APIManager;
import com.makewithmoto.apprunner.AppRunnerActivity;


public class JInterface {
	
	protected static final String TAG = "JSInterface";
	public WeakReference<AppRunnerActivity> c;
	

	public JInterface(Activity appActivity) {
		super();
		this.c = new WeakReference<AppRunnerActivity>((AppRunnerActivity) appActivity); 
		APIManager.getInstance().addClass(this.getClass());

	}
	
	public void destroy() {
	}

}
