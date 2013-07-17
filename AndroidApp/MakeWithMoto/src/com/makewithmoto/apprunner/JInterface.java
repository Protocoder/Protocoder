package com.makewithmoto.apprunner;

import java.lang.ref.WeakReference;
import android.app.Activity;

public class JInterface {
	
	protected static final String TAG = "JSInterface";
	public WeakReference<AppRunnerActivity> c;


	public JInterface(Activity appActivity) {
		super();
		this.c = new WeakReference<AppRunnerActivity>((AppRunnerActivity) appActivity); 

	}
	
	public void destroy() {
	}

}
