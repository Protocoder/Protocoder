/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;

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
