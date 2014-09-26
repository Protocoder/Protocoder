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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

public class PButton extends Button implements PViewInterface, PViewMethodsInterface {

	private int currentColor;
    private Paint paint;

    public PButton(Context context) {
		super(context);
		currentColor = Color.argb(255, 255, 255, 255);

        init();
	}


    @ProtocoderScript
    @APIMethod(description = "Changes the font type to the button", example = "")
    @APIParam(params = { "Typeface" })
    public void setFont(Typeface f) {
        this.setTypeface(f);
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Changes the font type to the button", example = "")
    @APIParam(params = { "" })
    public PButton font(Typeface font) {
        this.setFont(font);
        return this;
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Changes the font text color", example = "")
    @APIParam(params = { "colorHex" })
    public PButton color(String c) {
        this.setTextColor(Color.parseColor(c));
        return this;
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Changes the background color", example = "")
    @APIParam(params = { "" })
    public PButton background(String c) {
        this.setBackgroundColor(Color.parseColor(c));
        return this;
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Sets html text", example = "")
    @APIParam(params = { "htmlText" })
    public PButton html(String htmlText) {
        this.setText(Html.fromHtml(htmlText));

        return this;
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Changes the button size", example = "")
    @APIParam(params = { "w", "h" })
    public PButton boxsize(int w, int h) {
        this.setWidth(w);
        this.setHeight(h);

        return this;
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Changes the text size", example = "")
    @APIParam(params = { "size" })
    public View textSize(int size) {
        this.setTextSize(size);
        return this;
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Button position", example = "")
    @APIParam(params = { "x", "y" })
    public PButton pos(int x, int y) {
        this.setX(x);
        this.setY(y);
        return this;
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.parseColor("#550000FF"));
//        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.parseColor("#FF0000FF"));
//        super.onDraw(canvas);
//        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }
}
