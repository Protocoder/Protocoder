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

import android.view.View;
import android.widget.LinearLayout;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;

public class PLinearLayout extends LinearLayout implements PViewInterface {

    private final AppRunner mAppRunner;
    private PListAdapter plistAdapter;

    public PLinearLayout(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
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

    public void add(View v, float p) {
        // LayoutParams lp = new LayoutParams()
        // addView(v, lp);
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {
        removeAllViews();
    }

}
