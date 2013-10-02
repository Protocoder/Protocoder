/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package com.makewithmoto.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.makewithmoto.R;
import com.makewithmoto.base.BaseFragment;

@SuppressLint({ "NewApi", "ValidFragment" })
public class BaseWebviewFragment extends BaseFragment {

	protected WebView webView;
	final Handler myHandler = new Handler();
	protected View v;
	private String mUrl = null;
	
	public BaseWebviewFragment(String file) {
		super();
		mUrl = file;
	}
	
	public BaseWebviewFragment() {
		super();
	}

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		Log.d("WEBVIEW", "LOADED BaseWebView");
		v = inflater.inflate(R.layout.webview, container, false);
		
		return v;
	}
	
	public WebView getWebview() {
		return webView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.d("WEBVIEW", "onActivityCreated");
		webView = (WebView) v.findViewById(R.id.webView1);
		Log.d("WEBVIEW", "Loaded WebView");

		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setGeolocationEnabled(true);
		settings.setAppCacheEnabled(false);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);

		settings.setLightTouchEnabled(true);

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}
		});

		webView.getSettings().setGeolocationDatabasePath("/data/data/customwebview");
		
		if (mUrl != null) {
			webView.loadUrl(mUrl);
		}
	}


	public void setPage(String Url) {
		webView.loadUrl(Url);
	}
	
	public void loadViewFromFile(String filename) {
		filename = "file://" + filename;
		webView.loadUrl(filename);
	}

}
