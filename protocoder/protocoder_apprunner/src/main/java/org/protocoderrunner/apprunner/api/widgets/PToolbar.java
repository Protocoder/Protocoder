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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.ProtocoderScript;

public class PToolbar implements PViewInterface {

    private final AppRunnerActivity mContext;
    private final ActionBar mToolbar;
    private int currentColor;
    private Paint paint;

    public PToolbar(AppRunnerActivity context) {
        mContext = context;
        mToolbar = mContext.getSupportActionBar();
	}

    @ProtocoderScript
    @APIMethod(description = "Set toolbar title name", example = "")
    @APIParam(params = { "titleName" })
    public PToolbar title(String title) {
        mToolbar.setTitle(title);
        return this;
    }


    @ProtocoderScript
    @APIMethod(description = "Sets toolbar secondary title", example = "")
    @APIParam(params = { "subtitleName" })
    public PToolbar subtitle(String subtitle) {
        mToolbar.setSubtitle(subtitle);
        return this;
    }


    @ProtocoderScript
    @APIMethod(description = "Show/Hide title bar", example = "")
    @APIParam(params = { "boolean" })
    public PToolbar show(Boolean b) {
        if (b) {
            mToolbar.show();
        } else {
            mToolbar.hide();
        }
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Changes the title bar color", example = "")
    @APIParam(params = { "r", "g", "b", "alpha" })
    public PToolbar bgColor(int r, int g, int b, int alpha) {
        int c = Color.argb(alpha, r, g, b);

        ColorDrawable d = new ColorDrawable();
        d.setColor(c);
        mToolbar.setBackgroundDrawable(d);

        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Changes the title text color", example = "")
    @APIParam(params = { "r", "g", "b", "mContext" })
    public PToolbar textColor(int r, int g, int b, int alpha) {
        int c = Color.argb(alpha, r, g, b);

        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView textTitleView = (TextView) mContext.findViewById(titleId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContext.getWindow().setStatusBarColor(Color.BLUE);
        } else {
           textTitleView.setTextColor(c);
        }

        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Sets an image rather than text as toolbar title", example = "")
    @APIParam(params = { "imageName" })
    public PToolbar imageIcon(String imagePath) {
        Bitmap myBitmap = BitmapFactory.decodeFile(AppRunnerSettings.get().project.getStoragePath() + imagePath);
        Drawable icon = new BitmapDrawable(mContext.getResources(), myBitmap);

        mToolbar.setIcon(icon);

        return this;
    }

}
