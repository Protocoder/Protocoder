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

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerSettings;

public class PToolbar implements PViewInterface {

    private final AppRunnerActivity mContext;
    private final ActionBar mToolbar;
    private int currentColor;
    private Paint paint;

    public PToolbar(AppRunnerActivity context) {
        mContext = context;
        mToolbar = mContext.getSupportActionBar();
    }


    @ProtoMethod(description = "Set toolbar title name", example = "")
    @ProtoMethodParam(params = {"titleName"})
    public PToolbar title(String title) {
        mToolbar.setTitle(title);
        return this;
    }


    @ProtoMethod(description = "Sets toolbar secondary title", example = "")
    @ProtoMethodParam(params = {"subtitleName"})
    public PToolbar subtitle(String subtitle) {
        mToolbar.setSubtitle(subtitle);
        return this;
    }


    @ProtoMethod(description = "Show/Hide title bar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PToolbar show(Boolean b) {
        if (b) {
            mToolbar.show();
        } else {
            mToolbar.hide();
        }
        return this;
    }


    @ProtoMethod(description = "Changes the title bar color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PToolbar bgColor(int r, int g, int b, int alpha) {
        int c = Color.argb(alpha, r, g, b);

        ColorDrawable d = new ColorDrawable();
        d.setColor(c);
        mToolbar.setBackgroundDrawable(d);

        return this;
    }


//    @ProtoMethod(description = "Changes the title text color", example = "")
//    @ProtoMethodParam(params = {"r", "g", "b", "mContext"})
//    public PToolbar textColor(int r, int g, int b, int alpha) {
//        int c = Color.argb(alpha, r, g, b);
//
//        //int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//        TextView textTitleView = (TextView) mContext.findViewById(titleId);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mContext.getWindow().setStatusBarColor(c);
//        } else {
//            textTitleView.setTextColor(c);
//        }
//
//        return this;
//    }


    @ProtoMethod(description = "Sets an image rather than text as toolbar title", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PToolbar imageIcon(String imagePath) {
        Bitmap myBitmap = BitmapFactory.decodeFile(AppRunnerSettings.get().project.getStoragePath() + imagePath);
        Drawable icon = new BitmapDrawable(mContext.getResources(), myBitmap);

        mToolbar.setIcon(icon);

        return this;
    }

}
