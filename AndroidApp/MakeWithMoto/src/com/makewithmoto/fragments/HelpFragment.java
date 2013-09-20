package com.makewithmoto.fragments;

import java.io.IOException;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.makewithmoto.network.ALog;
import com.makewithmoto.utils.FileIO;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HelpFragment extends BaseWebviewFragment {
	private View v;
	private static final int MENU_CLOSE = 101;
	
	private final String TAG="ApplicationWebView";
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("HELPFRAGMENT", "Hereere-----");
		try {
			String contents = FileIO.readFromAssets(getActivity(), "help.html");
			loadViewFromTemplate(contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//setVisible(false);
	}
	
	@Override
	public void loadViewFromFile(String filename) {
		filename = "file://" + filename;
		ALog.d(TAG, "loading " + filename);
		getWebview().loadUrl(filename);
	}
	
	@Override
	public WebView getWebview() {
		return webView;
	}
	
	public void loadViewFromTemplate(String template) {
		getWebview().loadDataWithBaseURL("file://template", template, "text/html", "utf-8", null);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(1, MENU_CLOSE, 0, "Close").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case MENU_CLOSE:
			setVisible(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void setVisible(boolean b) {
		if (b) {
			getWebview().setVisibility(View.VISIBLE);
		} else {
			getWebview().setVisibility(View.INVISIBLE);
		}
	}

}
