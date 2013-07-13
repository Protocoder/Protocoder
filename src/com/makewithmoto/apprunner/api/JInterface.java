package com.makewithmoto.apprunner.api;

import java.lang.ref.WeakReference;

import android.support.v4.app.FragmentActivity;

import com.makewithmoto.apprunner.MWMActivity;
import com.makewithmoto.apprunner.webrunner.ApplicationWebView;

public class JInterface {
	
	protected static final String TAG = "JSInterface";
	public WeakReference<MWMActivity> c;

	protected ApplicationWebView applicationWebView;

	public JInterface(FragmentActivity mwmActivity) {
		super();
		this.c = new WeakReference<MWMActivity>((MWMActivity) mwmActivity); 
//		this.c = (MWMActivity) mwmActivity;
		this.applicationWebView = c.get().getApplicationWebView();
	}
	
	public void destroy() {
	}

}
