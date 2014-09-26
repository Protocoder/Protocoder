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
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

public class PTextView extends TextView implements PViewInterface, PViewMethodsInterface {

    PViewMethods vM;
	public PTextView(Context context) {
		super(context);
        vM = new PViewMethods();
	}

    @ProtocoderScript
    @APIMethod(description = "Sets the text color", example = "")
    @APIParam(params = { "colorHex" })
    public PTextView color(String c) {
        this.setTextColor(Color.parseColor(c));
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Sets the background color", example = "")
    @APIParam(params = { "colorHex" })
    public PTextView background(String c) {
        this.setBackgroundColor(Color.parseColor(c));
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Sets the text size", example = "")
    @APIParam(params = { "size" })
    public PTextView textSize(int size) {
        this.setTextSize(size);
        return this;
    }


    @ProtocoderScript
    @APIMethod(description = "Enables/disables the scroll in the text view", example = "")
    @APIParam(params = { "size" })
    public PTextView scrollable(boolean b) {
        if (b) {
            this.setMovementMethod(new ScrollingMovementMethod());
        } else {
            this.setMovementMethod(null);
        }
        return this;
    }


    @ProtocoderScript
    @APIMethod(description = "Changes the text to the given text", example = "")
    @APIParam(params = { "text" })
	public PTextView text(String text) {
        this.setText(text);
        return this;
    }


    @ProtocoderScript
    @APIMethod(description = "Changes the text to the given html text", example = "")
    @APIParam(params = { "htmlText" })
	public PTextView html(String text) {
        this.setText(Html.fromHtml(text));
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Appends text to the text view", example = "")
    @APIParam(params = { "text" })
	public PTextView append(String text) {
        this.setText(getText() + text);
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Clears the text", example = "")
    @APIParam(params = { "text" })
	public PTextView clear(String text) {
        this.clear("");
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Changes the box size of the text", example = "")
    @APIParam(params = { "w", "h" })
	public PTextView boxsize(int w, int h) {
        this.setWidth(w);
        this.setHeight(h);
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Sets a new position for the text", example = "")
    @APIParam(params = { "x", "y" })
	public PTextView pos(int x, int y) {
        this.setX(x);
        this.setY(y);
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Specifies a shadow for the text", example = "")
    @APIParam(params = { "x", "y", "radius", "colorHex" })
    public PTextView shadow(int x, int y, int r, String c) {
        this.setShadowLayer(r, x, y, Color.parseColor(c));
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Changes the font", example = "")
    @APIParam(params = { "Typeface" })
    public PTextView font(Typeface f) {
        this.setTypeface(f);
        return this;
    }

  @ProtocoderScript
    @APIMethod(description = "Centers the text inside the textview", example = "")
    @APIParam(params = { "Typeface" })
    public PTextView center(String centering) {
        this.setGravity(Gravity.CENTER_VERTICAL);
        return this;
    }


}
