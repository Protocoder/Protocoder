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

/*
 * use vectors 
 * add values to it 
 * 
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

import java.util.HashMap;
import java.util.Map;

public class PPadView extends View {
	private static final String TAG = "TouchAreaView";
    private static final String TYPE_PROG = "prog";
    private static final String TYPE_FINGER = "finger";
    // paint
	private final Paint mPaint = new Paint();
	private Canvas mCanvas = new Canvas();
	private Bitmap bitmap; // Cache

	// widget size
	private float mWidth;
	private float mHeight;
	private boolean lastTouch = false;
	private OnTouchAreaListener mOnTouchAreaListener;
	HashMap<Integer, TouchEvent> t = new HashMap<Integer, PPadView.TouchEvent>();
    private int mStrokeColor;
    private int mBackgroundColor = 0x00FFFFFF;
    private int mPadsColorStroke = 0x0000FF;
    private int mPadsColorBg = 0x880000FF;

    public PPadView(Context context) {
		super(context);
		init();
	}

	public void init() {
		mPaint.setStrokeWidth(1.0f);
		mPaint.setAntiAlias(true);
		// mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

		if (isInEditMode()) {
			loadDemoValues();
		}

	}

	public void loadDemoValues() {

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w - 1;
		mHeight = h - 1;

		// create mContext bitmap for caching what was drawn
		if (bitmap != null) {
			bitmap.recycle();
		}

		mCanvas = new Canvas();
		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(bitmap);

       // AndroidUtils.setViewGenericShadow(this, w, h);

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		synchronized (this) {

			// saved
			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
					new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

            //clear
            mCanvas.drawColor(0, Mode.CLEAR);

            // background
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(mBackgroundColor);
			mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 5, 5, mPaint);


			mPaint.setStyle(Paint.Style.STROKE);
			mCanvas.drawColor(mStrokeColor);
			mPaint.setColor(mStrokeColor);

            mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 5, 5, mPaint);

			for (Map.Entry<Integer, PPadView.TouchEvent> t1 : t.entrySet()) {
				int key = t1.getKey();
				TouchEvent value = t1.getValue();
                
				mPaint.setColor(mPadsColorBg);
				mPaint.setStyle(Paint.Style.FILL);

				mCanvas.drawCircle(value.x, value.y, 50, mPaint);

				mPaint.setColor(mPadsColorStroke);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(3);

				mCanvas.drawCircle(value.x, value.y, 50, mPaint);
			}
		}

		t.clear();

		if (lastTouch) {
			lastTouch = false;
			invalidate();
		}

		/*
		 * Runnable task = new Runnable() {
		 * 
		 * @Override public void run() { // handler.postDelayed(this, duration);
		 * handler.removeCallbacks(this); rl.remove(this); } };
		 * handler.postDelayed(task, 200);
		 */

	}

	public class TouchEvent {
		public String type;
		public int id;
		public String action;
		public int x;
		public int y;

		public TouchEvent(String type, int pointerId, String string, int x, int y) {
			this.type = type;
			this.id = pointerId;
			this.action = string;
			this.x = x;
			this.y = y;
		}

	}

    public TouchEvent newTouch(int id, int x, int y) {
        TouchEvent t = new TouchEvent(TYPE_PROG, id, "move", x, y);

        return t;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// AndroidUtils.dumpMotionEvent(event);

		int action = event.getAction();
		int actionCode = event.getActionMasked();

		// get positions per finger
		int numPoints = event.getPointerCount();
		for (int i = 0; i < numPoints; i++) {
			int id = event.getPointerId(i);
			TouchEvent o = new TouchEvent(TYPE_FINGER, id, "move", (int) event.getX(i), (int) event.getY(i));
			t.put(id, o);
		}

		// check finger if down or up
		int p = action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		TouchEvent te = t.get(p);
		if (te != null) {
			if (actionCode == MotionEvent.ACTION_POINTER_DOWN) {
				te.action = "down";
			} else if (actionCode == MotionEvent.ACTION_POINTER_UP) {
				te.action = "up";
				if (numPoints == 1) {
					lastTouch = true;
				}
			}

			// if last finger up clear array
			if (actionCode == MotionEvent.ACTION_UP) {
				// t.get(0).action = "up";
				lastTouch = true;
			}

			mOnTouchAreaListener.onGenericTouch(t);
		}

		invalidate();

		return true;
	}

	public void destroy() {
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

	public void setTouchAreaListener(OnTouchAreaListener l) {
		mOnTouchAreaListener = l;
	}

	public interface OnTouchAreaListener {
		public abstract void onGenericTouch(HashMap<Integer, TouchEvent> t);
	}

	void printSamples(MotionEvent ev) {
		final int historySize = ev.getHistorySize();
		final int pointerCount = ev.getPointerCount();
		for (int h = 0; h < historySize; h++) {
			System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
			for (int p = 0; p < pointerCount; p++) {
				System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getHistoricalX(p, h),
						ev.getHistoricalY(p, h));
			}
		}
		System.out.printf("At time %d:", ev.getEventTime());
		for (int p = 0; p < pointerCount; p++) {
			System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getX(p), ev.getY(p));
		}
	}

    @ProtocoderScript
    @APIMethod(description = "Change the pad color", example = "")
    @APIParam(params = { "colorHex" })
    public PPadView padsColor(String c) {
        mPadsColorStroke = Color.parseColor(c);
        int r = Color.red(mPadsColorStroke);
        int g = Color.green(mPadsColorStroke);
        int b = Color.blue(mPadsColorStroke);
        
        mPadsColorBg = Color.argb(125, r, g, b);
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Change the strokeColor", example = "")
    @APIParam(params = { "colorHex" })
    public PPadView strokeColor(String c) {
        mBackgroundColor = Color.parseColor(c);
        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Change the background color", example = "")
    @APIParam(params = { "colorHex" })
    public PPadView backgroundColor(String c) {
        mBackgroundColor = Color.parseColor(c);
        return this;
    }

}