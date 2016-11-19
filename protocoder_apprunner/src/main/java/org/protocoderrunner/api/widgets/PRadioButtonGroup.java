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

import android.content.Context;
import android.widget.RadioGroup;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;

public class PRadioButtonGroup extends RadioGroup {

    public PRadioButtonGroup(Context context) {
        super(context);
        orientation("vertical");
    }

    public void orientation(String orientation) {
        int o = RadioGroup.HORIZONTAL;
        switch (orientation) {
            case "horizontal":
                o = RadioGroup.VERTICAL;
                break;
            case "vertical":
                o = RadioGroup.VERTICAL;
                break;
        }
        setOrientation(o);
    }

    public PRadioButton add(String text) {
        PRadioButton rb = new PRadioButton(getContext());
        rb.selected(false);
        rb.text(text);
        addView(rb);
        return rb;
    }

    public void clear() {
        clearCheck();
    }

    public void onSelected(final ReturnInterface cb) {
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ReturnObject r = new ReturnObject(PRadioButtonGroup.this);
                PRadioButton rb = (PRadioButton) findViewById(checkedId);
                r.put("selected", rb.getText());
                cb.event(r);
            }
        });
    }
}
