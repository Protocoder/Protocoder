/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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

package org.protocoder.fragments;

import java.io.IOException;

import org.protocoder.utils.FileIO;
import org.protocoder.utils.MLog;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HelpFragment extends BaseWebviewFragment {
	private View v;
	private static final int MENU_CLOSE = 101;

	private final String TAG = "ApplicationWebView";

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
		// setVisible(false);
	}

	@Override
	public void loadViewFromFile(String filename) {
		filename = "file://" + filename;
		MLog.d(TAG, "loading " + filename);
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
