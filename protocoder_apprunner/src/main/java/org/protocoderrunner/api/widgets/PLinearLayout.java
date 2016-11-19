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

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;

public class PLinearLayout extends LinearLayout {

    private final AppRunner mAppRunner;
    private final LayoutParams mLp;

    public PLinearLayout(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        mLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void orientation(String orientation) {
        int mode = VERTICAL;
        switch (orientation) {
            case "horizontal":
                mode = HORIZONTAL;
                break;
        }
        setOrientation(mode);

    }
    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void add(View v) {
        addView(v);
    }

    public void add(View v, float weight) {
        // lp.gravity = Gravity.CENTER;
        mLp.weight = weight;
        // setWeightSum(1.0f);
        addView(v, mLp);
    }

    public void alignViews(String horizontal, String vertical) {
        int h = Gravity.LEFT;
        switch (horizontal) {
            case "left":
                h = Gravity.LEFT;
                break;
            case "center":
                h = Gravity.CENTER_HORIZONTAL;
                break;
            case "right":
                h = Gravity.RIGHT;
                break;
        }

        int v = Gravity.TOP;
        switch (vertical) {
            case "top":
                v = Gravity.TOP;
                break;
            case "center":
                v = Gravity.CENTER_VERTICAL;
                break;
            case "bottom":
                v = Gravity.BOTTOM;
                break;
        }

        setGravity(h | v);
    }

    public void padding(float l, float t, float r, float b) {
        setPadding((int) l, (int) t, (int) r, (int) b);
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {
        removeAllViews();
    }

    public void background(int r, int g, int b) {
        setBackgroundColor(Color.rgb(r, g, b));
    }

}
