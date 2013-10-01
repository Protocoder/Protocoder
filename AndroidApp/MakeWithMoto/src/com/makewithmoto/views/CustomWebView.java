package com.makewithmoto.views;

import java.io.File;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.apprunner.AppRunnerSettings;

/*
 * http://stackoverflow.com/questions/13257990/android-webview-inside-scrollview-scrolls-only-scrollview
 */

public class CustomWebView extends WebView {

    private WeakReference<AppRunnerActivity> a;

	public CustomWebView(Activity appActivity) {
        super(appActivity);
        this.a = new WeakReference<AppRunnerActivity>(
				(AppRunnerActivity) appActivity);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void loadData(String content) { 
	    this.loadData(content, "text/html", "utf-8");	

    }
    
    public void loadHTMLFile(String fileName) { 
    	String path = AppRunnerSettings.get().project.getUrl()   + File.separator + fileName;
    	loadUrl("file://"+path);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }          
}
