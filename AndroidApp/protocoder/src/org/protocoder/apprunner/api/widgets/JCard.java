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

package org.protocoder.apprunner.api.widgets;

import org.protocoder.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JCard extends LinearLayout implements JViewInterface {

	private int currentColor;
	private int viewCount = 0;
	private Context c;
	LinearLayout cardLl;
	TextView title;

	public JCard(Context context) {
		super(context);
		c = context;
		currentColor = Color.argb(255, 255, 255, 255);

		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_card_view_holder, this, true);

		title = (TextView) findViewById(R.id.cardTitle);
		cardLl = (LinearLayout) findViewById(R.id.cardWidgets);

	}

	public void addWidget(View v) {
		v.setAlpha(0);
		v.animate().alpha(1).setDuration(500).setStartDelay((long) (100 * (1 + viewCount)));

		// v.setPadding(0, 0, 0, AndroidUtils.pixelsToDp(c, 3));
		cardLl.addView(v);
	}

	public JRow addRow(int n) {
		JRow row = new JRow(c, cardLl, n);

		return row;
	}

	public void setTitle(String text) {
		title.setVisibility(View.VISIBLE);
		title.setText(text);
	}

	public void setTitleColor(int color) {
		title.setBackgroundColor(color);
	}

	public void setHorizontal() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.cardWidgets);
		ll.setOrientation(LinearLayout.HORIZONTAL);
	}

	public void setVertical() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.cardWidgets);
		ll.setOrientation(LinearLayout.VERTICAL);
	}

	public void hide() {
		this.setVisibility(View.GONE);
	}

	public void show() {
		this.setVisibility(View.VISIBLE);
	}
}
