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

package org.protocoder.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.protocoder.R;
import org.protocoderrunner.apprunner.api.other.PLooper;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.utils.AndroidUtils;

@SuppressLint("NewApi")
public class AppBaseActivity extends BaseActivity {

    public Toolbar mToolbar;
    private String mTitleUpperCase;
    private PLooper mLoop;
    private boolean mIsToolbarBack = false;
    private String prepend = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setToolbar() {
        // Create the action bar programmatically
        if (!AndroidUtils.isWear(this)) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }


        final int[] counter = {0};
        mTitleUpperCase = mToolbar.getTitle().toString().toUpperCase();
        mLoop = new PLooper(1000, new PLooper.LooperCB() {
            @Override
            public void event() {
                if (mIsToolbarBack) {
                    prepend = "";
                } else {
                    prepend = "> ";
                }
                if (counter[0]++ % 2 == 0) {
                    mToolbar.setTitle(prepend + mTitleUpperCase + "_");
                } else {
                    mToolbar.setTitle(prepend + mTitleUpperCase + "");
                }
            }
        });

    }

    public void setToolbarBack() {

        if (null != mToolbar) {
            mIsToolbarBack = true;
            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

//            mToolbar.setTitle(R.string.title_activity_settings);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(AppBaseActivity.this);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoop.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoop.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
