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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;

public class PInput extends EditText implements PViewInterface {

    private EditText mInput;

    public PInput(Context context) {
        super(context);

        mInput = this;
    }

    public void text(String... txt) {
        String joinedText = "";
        for (int i = 0; i < txt.length; i++) {
            joinedText += txt[i];
        }
        this.setText(joinedText);
    }

    public String text() {
        return this.getText().toString();
    }

    public void onChange(final ReturnInterface callbackfn) {

        if (callbackfn != null) {
            // On focus lost, we need to call the callback function
            mInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ReturnObject r = new ReturnObject(PInput.this);
                    r.put("text", mInput.getText().toString());
                    callbackfn.event(r);
                }
            });

        }
    }

    public void onFocusLost(final ReturnInterface callbackfn) {
        mInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ReturnObject r = new ReturnObject(PInput.this);
                r.put("focused", hasFocus);
                callbackfn.event(r);
            }
        });
    }
}
