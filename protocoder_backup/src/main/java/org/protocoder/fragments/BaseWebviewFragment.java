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

package org.protocoder.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.protocoder.R;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.utils.MLog;

@SuppressLint({"NewApi", "ValidFragment"})
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

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        MLog.d("WEBVIEW", "LOADED BaseWebView");
        v = inflater.inflate(R.layout.webview, container, false);

        return v;
    }

    public WebView getWebview() {
        return webView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            this.mUrl = bundle.getString("url");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MLog.d("WEBVIEW", "onActivityCreated");
        webView = (WebView) v.findViewById(R.id.webView1);
        MLog.d("WEBVIEW", "Loaded WebView");

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
