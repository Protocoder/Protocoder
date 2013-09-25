package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.WebView;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.APIParam;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;



public class JBrowser extends JInterface {
	
	private WebView webview;

	public JBrowser(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIMethod(description = "displays an html page", example = "browser.loadData(\"<html><head></head><body><h1>Hello World!!</h1></body></html>\")")
	public void loadData(@APIParam String content) {
		webview = new WebView(a.get());
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.addJavascriptInterface(a.get(), "activity");
	    a.get().setContentView(webview);    
	    
	    webview.loadData(content, "text/html", "utf-8");	
	}
	
	@JavascriptInterface
	@APIMethod(description = "displays an html url", example = "browser.loadUrl(\"http://www.google.com\")")
	public void loadUrl(String url) {
		webview = new WebView(a.get());
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.addJavascriptInterface(a.get(), "activity");
	    a.get().setContentView(webview);    
	    
	    
	    webview.loadUrl(url);	
	}
			
}