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

package org.protocoder.projectlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.protocoder.R;
import org.protocoderrunner.utils.MLog;

import java.lang.ref.WeakReference;

public class ProjectItem extends LinearLayout {

    private static final String TAG = "ProjectItem";
    private final Drawable bg;
    private WeakReference<View> v;
	// private Context c;
	private final WeakReference<Context> c;
	private String t;
    private boolean highlighted = false;

    public ProjectItem(Context context, boolean listMode) {
		super(context);
		this.c = new WeakReference<Context>(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (listMode) {
			this.v = new WeakReference<View>(inflater.inflate(R.layout.view_project_item_list, this, true));
		} else {
			this.v = new WeakReference<View>(inflater.inflate(R.layout.view_project_item, this, true));
		}

        FrameLayout fl = (FrameLayout) findViewById(R.id.viewProjectItemBackground);
        bg = fl.getBackground();
        setMenu();
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

    public void reInit(String text, boolean selected) {
        setText(text);
        setHighlighted(selected);
        MLog.d(TAG, "reInit " + t + " " + highlighted);
    }

	public void drawText(ImageView imageView, String t2) {

		// ImageView myImageView =
		Bitmap myBitmap = Bitmap.createBitmap(100, 100, Config.RGB_565);
		Paint myPaint = new Paint();
		myPaint.setColor(Color.BLUE);
		myPaint.setAntiAlias(true);
		myPaint.setTextSize(80);

		int x1 = 10;
		int y1 = 80;
		int x2 = 20;
		int y2 = 20;

		// Create a new image bitmap and attach a brand new canvas to it
		Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
		Canvas tempCanvas = new Canvas(tempBitmap);

		// Draw the image bitmap into the cavas
		tempCanvas.drawBitmap(myBitmap, 0, 0, null);

		// Draw everything else you want into the canvas, in this example a
		// rectangle with rounded edges
		tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myPaint);
		tempCanvas.drawText(t2.substring(0, 1).toUpperCase(), x1, y1, myPaint);

		// Attach the canvas to the ImageView
		imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

	}

    public void setMenu() {
        ImageView imageView = (ImageView) findViewById(R.id.card_menu_button);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showContextMenu();
            }
        });
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showContextMenu();
                return true;
            }
        });
    }

    public Drawable getBg() {
        return bg;
    }

    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            getBg().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        } else {
            getBg().clearColorFilter();
        }
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return highlighted;
    }
}
