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

package org.protocoder.views;

/*
 * use vectors 
 * add values to it 
 * 
 */
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class PadView extends View {
	private static final String TAG = "TouchAreaView";
	// paint
	private final Paint mPaint = new Paint();
	private Canvas mCanvas = new Canvas();
	private Bitmap bitmap; // Cache

	// widget size
	private float mWidth;
	private float mHeight;
	private boolean lastTouch = false;
	private OnTouchAreaListener mOnTouchAreaListener;
	HashMap<Integer, TouchEvent> t = new HashMap<Integer, PadView.TouchEvent>();

	public PadView(Context context) {
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

		// create a bitmap for caching what was drawn
		if (bitmap != null) {
			bitmap.recycle();
		}

		mCanvas = new Canvas();
		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(bitmap);

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		synchronized (this) {

			// saved
			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
					new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

			// paint
			// mPaint.setStyle(Style.STROKE);

			mPaint.setColor(0x88FFFFFF);
			mPaint.setStyle(Style.STROKE);

			mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 5, 5, mPaint);

			mPaint.setStyle(Paint.Style.STROKE);
			mCanvas.drawColor(0, Mode.CLEAR);
			mPaint.setColor(0x88000000);
			mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 5, 5, mPaint);

			for (Map.Entry<Integer, PadView.TouchEvent> t1 : t.entrySet()) {
				int key = t1.getKey();
				TouchEvent value = t1.getValue();

				mPaint.setColor(0x550000FF);
				mPaint.setStyle(Paint.Style.FILL);

				mCanvas.drawCircle(value.x, value.y, 50, mPaint);

				mPaint.setColor(0xFF0000FF);
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// AndroidUtils.dumpMotionEvent(event);

		int action = event.getAction();
		int actionCode = event.getActionMasked();

		// get positions per finger
		int numPoints = event.getPointerCount();
		for (int i = 0; i < numPoints; i++) {
			int id = event.getPointerId(i);
			TouchEvent o = new TouchEvent("finger", id, "move", (int) event.getX(i), (int) event.getY(i));
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

}