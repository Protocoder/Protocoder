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

package org.protocoderrunner.api.widgets;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.Image;
import org.protocoderrunner.base.utils.MLog;

public class PImageButton extends ImageButton implements PViewInterface {

    private String TAG = PImageButton.class.getSimpleName();

    private AppRunner mAppRunner;
    private boolean hideBackground = false;
    private Bitmap mImageOff;
    private Bitmap mImageOn;

    public PImageButton(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        setScaleType(ScaleType.FIT_XY);
    }

    public PImageButton image(String imagePath) {
        mImageOff = loadImage(imagePath);
        setImageBitmap(mImageOff);

        return this;
    }


    public PImageButton pressed(String imagePath) {
        mImageOn = loadImage(imagePath);

        return this;
    }

    public PImageButton noBackground() {
        this.setBackgroundResource(0);
        hideBackground = true;

        return this;
    }

    /**
     * Adds an image with the option to hide the default background
     */
    public PImageButton onClick(final ReturnInterface callbackfn) {
        // Set on click behavior
        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MLog.d(TAG, "" + event.getAction());
                int action = event.getAction();

                ReturnObject r = new ReturnObject(PImageButton.this);

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        r.put("action", "down");
                        on();

                        break;

                    case MotionEvent.ACTION_UP:
                        r.put("action", "up");
                        off();

                        break;

                    case MotionEvent.ACTION_CANCEL:
                        r.put("action", "cancel");
                        off();

                        break;
                }
                callbackfn.event(r);

                return true;
            }
        });

        return this;
    }

    private void on() {
        if (hideBackground) PImageButton.this.getDrawable().setColorFilter(0xDD00CCFC, PorterDuff.Mode.MULTIPLY);
        if (mImageOn != null) setImageBitmap(mImageOn);
    }

    private void off() {
        if (hideBackground) PImageButton.this.getDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
        if (mImageOff != null) setImageBitmap(mImageOff);
    }

    private Bitmap loadImage(String imagePath) {
        Bitmap bmp = Image.loadBitmap(mAppRunner.getProject().getFullPathForFile(imagePath));

        return bmp;
    }

}
