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

package org.protocoder.gui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import org.protocoder.R;
import org.protocoder.gui._components.APIWebviewFragment;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.utils.MLog;

public class AboutActivity extends BaseActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        setupActivity();

        FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentWebview);
        fl.setVisibility(View.VISIBLE);
        MLog.d(TAG, "using webide");
        APIWebviewFragment webViewFragment = new APIWebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", "http://127.0.0.1:8585");
        webViewFragment.setArguments(bundle);
        addFragment(webViewFragment, R.id.fragmentEditor, "qq");
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        enableBackOnToolbar();
    }
}
