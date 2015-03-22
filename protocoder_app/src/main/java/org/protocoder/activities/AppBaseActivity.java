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
