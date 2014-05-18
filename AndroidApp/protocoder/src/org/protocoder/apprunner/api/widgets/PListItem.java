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

package org.protocoder.apprunner.api.widgets;

import java.lang.ref.WeakReference;

import org.protocoder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PListItem extends LinearLayout {

	private final WeakReference<View> v;
	// private Context c;
	private final WeakReference<Context> c;
	private String t;

	public PListItem(Context context) {
		super(context);
		this.c = new WeakReference<Context>(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.v = new WeakReference<View>(inflater.inflate(R.layout.view_project_item_, this, true));
	}

	public void setImage(int resId) {
		ImageView imageView = (ImageView) v.get().findViewById(R.id.customViewImage);
		imageView.setImageResource(resId);

		// drawText(imageView, t);
	}

	public void setText(String text) {
		this.t = text;
		TextView textView = (TextView) v.get().findViewById(R.id.customViewText);
		// TextUtils.changeFont(c.get(), textView, Fonts.MENU_TITLE);
		textView.setText(text);
	}

	public String getName() {
		return t;
	}

}
