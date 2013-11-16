/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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

package org.protocoder.views;

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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchAreaView extends View {
    private static final String TAG = "TouchAreaView";
    // paint
    private Paint mPaint = new Paint();
    private Canvas mCanvas = new Canvas();
    private Bitmap bitmap; // Cache

    // widget size
    private float mWidth;
    private float mHeight;
    private float xPointer;
    private float yPointer;
    private boolean touching = false;
    private OnTouchAreaListener mOnTouchAreaListener;
    private String mHexColor;

    public TouchAreaView(Context context, String hexColor) {
	super(context);

	mHexColor = hexColor;
	init();
    }

    public TouchAreaView(Context context) {
	super(context);
	init();
    }

    public void init() {

	mPaint.setStrokeWidth(1.0f);
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
	mPaint.setColor(0x88000000);
	mPaint.setStyle(Style.STROKE);

	mCanvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);

	super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

	synchronized (this) {

	    // saved
	    canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, bitmap
		    .getWidth(), bitmap.getHeight()), null);

	    // paint
	    // mPaint.setStyle(Style.STROKE);

	    if (touching) {
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0x880000FF);
		mCanvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);
	    } else { 
		mPaint.setStyle(Paint.Style.STROKE);
		mCanvas.drawColor(0, Mode.CLEAR);
		mPaint.setColor(0x88000000);
		mCanvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);
	    }
	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	// Convert coordinates to our internal coordinate system
	xPointer = event.getX();
	yPointer = event.getY();
	Log.d(TAG, xPointer + " " + yPointer);

	switch (event.getActionMasked()) {
	case MotionEvent.ACTION_CANCEL:
	    touching = false;
	    break;

	case MotionEvent.ACTION_DOWN:
	    touching = true;
	    break;

	case MotionEvent.ACTION_UP:
	    touching = false;
	    break;

	case MotionEvent.ACTION_MOVE:
	    if (xPointer > 0 && xPointer < mWidth && yPointer > 0 && yPointer < mHeight) {
		touching = true;
	    } else { 
		touching = false;
	    }
	    break;

	}

	mOnTouchAreaListener.onTouch(this, touching, xPointer, yPointer);

	invalidate();
	return touching;
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

	public abstract void onTouch(TouchAreaView touchAreaView, boolean touching, float xPointer, float yPointer);

    }

    void printSamples(MotionEvent ev) {
	final int historySize = ev.getHistorySize();
	final int pointerCount = ev.getPointerCount();
	for (int h = 0; h < historySize; h++) {
	    System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
	    for (int p = 0; p < pointerCount; p++) {
		System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getHistoricalX(p, h), ev
			.getHistoricalY(p, h));
	    }
	}
	System.out.printf("At time %d:", ev.getEventTime());
	for (int p = 0; p < pointerCount; p++) {
	    System.out.printf("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getX(p), ev.getY(p));
	}
    }

}