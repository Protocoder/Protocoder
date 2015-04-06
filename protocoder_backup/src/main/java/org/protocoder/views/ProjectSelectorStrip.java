package org.protocoder.views;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain mContext copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.support.v4.view.PagerTitleStrip;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ProjectSelectorStrip extends PagerTitleStrip {
    private static final String TAG = "PagerTabStrip";
    private boolean mIgnoreTap;
    private float mInitialMotionY;
    private float mInitialMotionX;
    private float mTouchSlop;
    private View mCurrText;

    public ProjectSelectorStrip(Context context) {
        this(context, null);
    }

    public ProjectSelectorStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        // Any tap within touch slop to either side of the current item
        // will scroll to prev/next.
        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - mInitialMotionX) > mTouchSlop || Math.abs(y - mInitialMotionY) > mTouchSlop) {
                    mIgnoreTap = true;
                }
                break;

        }

        return true;
    }

}
