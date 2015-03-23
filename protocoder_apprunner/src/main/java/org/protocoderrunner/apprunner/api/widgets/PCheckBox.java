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
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class PCheckBox extends CheckBox implements PViewInterface {

    public PCheckBox(Context context) {
        super(context);
    }

    // --------- checkbox ---------//
    public interface addGenericCheckboxCB {
        void event(boolean isChecked);
    }

    public PCheckBox onChange(final addGenericCheckboxCB callbackfn) {

        // Add the click callback
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callbackfn.event(isChecked);
            }
        });

        return this;
    }

}
