package com.makewithmoto.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
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
		webView.addJavascriptInterface(new MyJavaScriptInterface(getActivity()), "android");

		webView.setWebChromeClient(new WebChromeClient() {
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}
		});

		webView.getSettings().setGeolocationDatabasePath("/data/data/customwebview");
		
		if (mUrl != null) {
			webView.loadUrl(mUrl);
		}
	}

	public class MyJavaScriptInterface {
		Context mContext;

		MyJavaScriptInterface(Context c) {
			mContext = c;
		}

		@JavascriptInterface
		public void vibrate() {
			Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(1000);
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
