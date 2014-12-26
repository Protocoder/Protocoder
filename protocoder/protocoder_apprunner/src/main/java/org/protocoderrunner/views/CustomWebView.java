/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoderrunner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;
import java.lang.ref.WeakReference;

/*
 * http://stackoverflow.com/questions/13257990/android-webview-inside-scrollview-scrolls-only-scrollview
 */

public class CustomWebView extends WebView {

	private Context mContext;

	public CustomWebView(Context context) {
		super(context);
		this.mContext = context;
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
		String path = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;
		loadUrl("file://" + path);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(event);
	}
}
