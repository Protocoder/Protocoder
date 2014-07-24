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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.protocoderrunner.R;
import org.protocoderrunner.utils.MLog;

public class PWindow extends RelativeLayout implements PViewInterface {

    private static final String TAG = "PWindow";
    private final int currentColor;
	private final int viewCount = 0;
	private final Context c;
    private final RelativeLayout mBar;
    private final LinearLayout mMainContainer;
    private final PWindow mWindow;
    private Button mBtnClose;
	private TextView mTitle;

	public PWindow(Context context) {
		super(context);
		c = context;
		currentColor = Color.argb(255, 255, 255, 255);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.pwidget_window, this, true);

        mWindow = this;
        mBar = (RelativeLayout) findViewById(R.id.pWidgetWindowBar);
		mTitle = (TextView) findViewById(R.id.pWidgetWindowTitle);
		mBtnClose = (Button) findViewById(R.id.pWidgetWindowClose);
        mMainContainer = (LinearLayout) findViewById(R.id.pWidgetWindowMainContainer);

        //setOnTouchListener(this);

        mBar.setOnTouchListener(onMoveListener);

	}

	public PWindow addWidget(View v) {
		v.setAlpha(0);
		v.animate().alpha(1).setDuration(500).setStartDelay(100 * (1 + viewCount));

		mMainContainer.addView(v);

        return mWindow;
	}

    public PWindow showBar(boolean b) {
        if (b) {
            mBar.setVisibility(View.VISIBLE);
        } else {
            mBar.setVisibility(View.INVISIBLE);
        }

        return mWindow;

    }

	public PWindow setTitle(String text) {
		mTitle.setText(text);

        return mWindow;

    }

	public PWindow setTitleColor(String color) {
		mTitle.setTextColor(Color.parseColor(color));

        return mWindow;
    }

	public PWindow setBarBackgroundColor(String color) {
		mBar.setBackgroundColor(Color.parseColor(color));

        return mWindow;
    }
    public PWindow setWindowBackgroundColor(String color) {
		mWindow.setBackgroundColor(Color.parseColor(color));

        return mWindow;
    }


    OnTouchListener onMoveListener = new OnTouchListener() {

        public int x_init;
        public int y_init;

        @Override
        public boolean onTouch(View v, MotionEvent e) {

            //FixedLayout.LayoutParams layoutParams = (Rel.LayoutParams) v.getLayoutParams();

            int action = e.getActionMasked();

            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    //DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    //v.startDrag(null, shadowBuilder, v, 0);
                    //v.setVisibility(View.INVISIBLE);
                    x_init = (int) e.getRawX() - (int) mWindow.getX();
                    MLog.network(getContext(), TAG, "" + x_init + " " + (int) e.getRawX() + " " + (int) mWindow.getX() + " " + (int) mWindow.getLeft());
                    y_init = (int) e.getRawY() - (int) mWindow.getY();

                    break;

                case MotionEvent.ACTION_MOVE:

                    int x_cord = (int) e.getRawX();
                    int y_cord = (int) e.getRawY();

                    mWindow.setX(x_cord - x_init);
                    mWindow.setY(y_cord - y_init);

                    break;
            }

            return true;
        }

    };

}
