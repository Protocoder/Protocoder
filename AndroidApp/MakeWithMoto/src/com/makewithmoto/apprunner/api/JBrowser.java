package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.makewithmoto.apidoc.APIAnnotation;

public class JBrowser extends JInterface {
	
	private WebView webview;

	public JBrowser(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIAnnotation(description = "displays an html page", example = "browser.loadData(\"<html><head></head><body><h1>Hello World!!</h1></body></html>\")")
	public void loadData(String content) {
		webview = new WebView(c.get());
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.addJavascriptInterface(c.get(), "activity");
	    c.get().setContentView(webview);    
	    
	    
	    webview.loadData(content, "text/html", "utf-8");	
	}
	
	@JavascriptInterface
	@APIAnnotation(description = "displays an html url", example = "browser.loadUrl(\"http://www.google.com\")")
	public void loadUrl(String url) {
		webview = new WebView(c.get());
	    webview.getSettings().setJavaScriptEnabled(true);
	    webview.addJavascriptInterface(c.get(), "activity");
	    c.get().setContentView(webview);    
	    
	    
	    webview.loadUrl(url);	
	}
			
}