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

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.protocoderrunner.R;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

public class PCard extends LinearLayout implements PViewInterface {

    private final int currentColor;
    private final int viewCount = 0;
    private final Context c;
    LinearLayout cardLl;
    TextView title;

    public PCard(Context context) {
        super(context);
        c = context;
        currentColor = Color.argb(255, 255, 255, 255);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pwidget_card, this, true);

        title = (TextView) findViewById(R.id.cardTitle);
        cardLl = (LinearLayout) findViewById(R.id.cardWidgets);

    }

    @Override

    @ProtoMethod(description = "Adds a new view", example = "")
    @ProtoMethodParam(params = {"view"})
    public void addView(View v) {
        v.setAlpha(0);
        v.animate().alpha(1).setDuration(500).setStartDelay(100 * (1 + viewCount));

        // v.setPadding(0, 0, 0, AndroidUtils.pixelsToDp(c, 3));
        cardLl.addView(v);
    }


    @ProtoMethod(description = "Add a row of n columns", example = "")
    @ProtoMethodParam(params = {"columnNumber"})
    public PRow addRow(int n) {
        PRow row = new PRow(c, cardLl, n);

        return row;
    }


    @ProtoMethod(description = "Set the title of the card", example = "")
    @ProtoMethodParam(params = {"text"})
    public void setTitle(String text) {
        if (text.isEmpty() == false) {
            title.setVisibility(View.VISIBLE);
            title.setText(text);
        }
    }


    @ProtoMethod(description = "Changes the title color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void setTitleColor(String color) {
        title.setBackgroundColor(Color.parseColor(color));
    }


    @ProtoMethod(description = "Card with horizontal views", example = "")
    @ProtoMethodParam(params = {""})
    public void setHorizontal() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.cardWidgets);
        ll.setOrientation(LinearLayout.HORIZONTAL);
    }


    @ProtoMethod(description = "Card with vertical views", example = "")
    @ProtoMethodParam(params = {""})
    public void setVertical() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.cardWidgets);
        ll.setOrientation(LinearLayout.VERTICAL);
    }

}
