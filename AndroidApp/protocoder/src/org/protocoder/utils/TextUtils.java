/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
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

package org.protocoder.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class TextUtils {
	public static void changeFont(Context c, int id, View parentView, String fontName) {
		TextView txt = (TextView) parentView.findViewById(id);

		// Typeface font = Typeface.createFromAsset(c.getAssets(),
		// "brownproregular.otf");
		Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);
		txt.setTypeface(font);
	}

	public static void changeFont(Context c, View txt, String fontName) {

		// Typeface font = Typeface.createFromAsset(c.getAssets(),
		// "brownproregular.otf");
		Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);
		((TextView) txt).setTypeface(font);
	}

	public static void changeFont(Activity activity, int id, String fontName) {
		TextView txt = (TextView) activity.findViewById(id);

		// Typeface font = Typeface.createFromAsset(c.getAssets(),
		// "brownproregular.otf");
		Typeface font = Typeface.createFromAsset(activity.getAssets(), fontName);
		txt.setTypeface(font);
	}

	public static void changeFont(Context c, TextView txt, String fontName) {

		Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);
		txt.setTypeface(font);
	}

}