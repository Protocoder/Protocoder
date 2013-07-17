package com.makewithmoto.appruner.webrunner;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.makewithmoto.app.utils.Template;
import com.makewithmoto.apprunner.MWMActivity;
import com.makewithmoto.events.Project;
import com.makewithmoto.fragments.BaseWebviewFragment;
import com.makewithmoto.utils.ALog;

@SuppressLint("ValidFragment")
public class ApplicationWebView extends BaseWebviewFragment {
	private final String TAG="ApplicationWebView";
	//private Context c;
	private WeakReference<Context> c;
			
	
	public ApplicationWebView(Context c) {
		this.c = new WeakReference<Context>(c);
	}
	
	public void setReady() {
		((MWMActivity) c.get()).setWebViewReady();
	}

	public void onResume(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setVisible(false);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getWebview().clearCache(true);
		getWebview().destroyDrawingCache();
	}
	
	public void addJavascriptInterface(final Object inter, final String name) {
		((Activity) c.get()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				getWebview().addJavascriptInterface(inter, name);
			}
		});
	}

	public void loadViewFromFile(String filename) {
		filename = "file://" + filename;
		ALog.d("qq", "loading " + filename);
		getWebview().loadUrl(filename);
	}
	
	public void loadUrl(String str) {
		getWebview().loadUrl(str);
	}
	
	public void runJavascript(final String str) {
		((Activity) c.get()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getWebview().loadUrl("javascript:(function() {" + str + "})();");
			}
		});
	}
	
	public void runBackgroundJavascript(final String str) {
		getWebview().loadUrl("javascript:(function() {" + str + "})();");
	}
	
	public void loadViewFromTemplate(String template) {
		getWebview().setWebViewClient(new CustomWebViewClient(this));
		getWebview().loadDataWithBaseURL("file://template", template, "text/html", "utf-8", null);
	}

	public void setVisible(boolean b) {
		if (b) {
			getWebview().setVisibility(View.VISIBLE);
		} else {
			getWebview().setVisibility(View.INVISIBLE);
		}
	}
	
	public WebView getWebview() {
		return webView;
	}
	
	public void launchProject(Project project) {
		ALog.d("ApplicationWebView", "Launching " + project.getName());
		String contents = Template.mergeAssetFile(c.get(), "script_template.html", project.getCode());
		loadViewFromTemplate(contents);
	}


	private class CustomWebViewClient extends WebViewClient {
		private ApplicationWebView c;
		
		public CustomWebViewClient(ApplicationWebView view) {
			super();
			this.c = view;
		}
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("moto://")) {
	            Log.d(TAG, "Link starting with moto://");
	            return true;
			}
			return false;
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished " + url);
			c.setReady();
		}
	}


	public Context getContext() {
		return c.get();
	}
}
