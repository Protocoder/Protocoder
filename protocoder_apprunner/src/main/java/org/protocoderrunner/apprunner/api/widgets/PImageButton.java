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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

import org.protocoderrunner.utils.MLog;

public class PImageButton extends PImageView implements PViewInterface {

    private String TAG = "PImageButton";

    private final PImageButton img;
    Context c;
    int mColor;
    int mColorReset;
    public boolean hideBackground = false;

    public PImageButton(Context context) {
        super(context);
        this.img = this;

        /*
        this.mColor = Color.parseColor(color);
        this.mColorReset = Color.parseColor("#FFFFFFFF");

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN) {
                    img.setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);

                } else if (action == MotionEvent.ACTION_UP) {
                    img.setColorFilter(mColor, PorterDuff.Mode.MULTIPLY);

                }

                return false;
            }
        });
        */
    }

    /**
     * Adds an image with the option to hide the default background
     */

    public// --------- getRequest ---------//
    interface addImageButtonCB {
        void event();
    }

    public PImageButton onClick(final addImageButtonCB callbackfn) {
        // Set on click behavior
        img.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MLog.d(TAG, "" + event.getAction());
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    MLog.d(TAG, "down");
                    if (hideBackground) {
                        img.getDrawable().setColorFilter(0xDD00CCFC, PorterDuff.Mode.MULTIPLY);

                    }
                    callbackfn.event();

                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    MLog.d(TAG, "up");
                    if (hideBackground) {
                        img.getDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);

                    }
                }

                return true;
            }
        });

        return img;
    }

}
