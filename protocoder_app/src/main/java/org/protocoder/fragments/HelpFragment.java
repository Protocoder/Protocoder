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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

import java.io.IOException;

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
