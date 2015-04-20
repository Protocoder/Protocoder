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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.utils.MLog;

public class PButton extends Button implements PViewInterface, PViewMethodsInterface {
    private static final String TAG = PButton.class.getSimpleName();

    // private int currentColor;
   // private Paint paint;

    public PButton(Context context) {
        super(context);
        //currentColor = Color.argb(255, 255, 255, 255);

        //init();
    }

    // --------- newButton ---------//
    public interface addGenericButtonCB {
        void event();
    }


    @ProtoMethod(description = "Changes the font type to the button", example = "")
    @ProtoMethodParam(params = {"Typeface"})
    public PButton onClick(final addGenericButtonCB callbackfn) {
        // Set on click behavior
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callbackfn != null) {
                    callbackfn.event();
                }
            }
        });

        return this;
    }


    @ProtoMethod(description = "Changes the font type to the button", example = "")
    @ProtoMethodParam(params = {"Typeface"})
    public PButton font(Typeface f) {
        this.setTypeface(f);

        return this;
    }


    @ProtoMethod(description = "Changes the font text color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PButton color(String c) {
        this.setTextColor(Color.parseColor(c));
        return this;
    }


    @ProtoMethod(description = "Changes the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PButton background(String c) {
        this.setBackgroundColor(Color.parseColor(c));
        return this;
    }


    @ProtoMethod(description = "Sets html text", example = "")
    @ProtoMethodParam(params = {"htmlText"})
    public PButton html(String htmlText) {
        this.setText(Html.fromHtml(htmlText));

        return this;
    }


    @ProtoMethod(description = "Changes the button size", example = "")
    @ProtoMethodParam(params = {"w", "h"})
    public PButton boxsize(int w, int h) {
        this.setWidth(w);
        this.setHeight(h);

        return this;
    }


    @ProtoMethod(description = "Changes the text size", example = "")
    @ProtoMethodParam(params = {"size"})
    public View textSize(int size) {
        this.setTextSize(size);
        return this;
    }


    @ProtoMethod(description = "Button position", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PButton pos(int x, int y) {
        this.setX(x);
        this.setY(y);
        return this;
    }

//    private void init() {
//
//        paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(4);
//
//
//        float radius = 2;
//        float[] outerR = {radius, radius, radius, radius, radius, radius, radius, radius};
//
//        ShapeDrawable pressed = new ShapeDrawable(new RoundRectShape(outerR, null, null));
//        pressed.getPaint().setColor(0xFF00FF00);
//        //pressed.getPaint().setStyle(S);
//        pressed.getPaint().setShadowLayer(2, 5, 5, 0xFF000000);
//        pressed.setPadding(15, 15, 15, 15);
//
//        GradientDrawable normal = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xFF0000FF, 0xFF0000FF});
//        //normal.getPaint().setColor(0x550000FF);
//        normal.setStroke(0, 0xFF000000);
//        normal.setShape(GradientDrawable.RECTANGLE);
//        normal.setCornerRadius(2);
//        //normal.get
//
//
//        ShapeDrawable disabled = new ShapeDrawable(new RoundRectShape(outerR, null, null));
//        disabled.getPaint().setColor(0xFF555555);
//
//
//        StateListDrawable states = new StateListDrawable();
//        states.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
//        states.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_enabled}, pressed);
//        states.addState(new int[]{android.R.attr.state_enabled}, normal);
//        states.addState(new int[]{-android.R.attr.state_enabled}, disabled);
//
//        setBackgroundDrawable(states);
//
//
//        //getBackground().setColorFilter(new PorterDuffColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY));
//        //states.
//        setPadding(15, 15, 15, 15);
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.parseColor("#550000FF"));
        //canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        //paint.setStyle(Paint.Style.STROKE);
        //paint.setColor(Color.parseColor("#FF0000FF"));
        //super.onDraw(canvas);
        //canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
//    }
}
