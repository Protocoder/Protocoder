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

package org.protocoder.views;

/*
 * use vectors 
 * add values to it 
 * 
 */
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PlotView extends View {
	private static final String TAG = "PlotView";
	// paint
	private Paint mPaint = new Paint();
	private Canvas mCanvas = new Canvas();
	private Bitmap bitmap; // Cache

	Vector<Plot> plots = new Vector<PlotView.Plot>();

	// util data
	private float mLastX;
	private float mMaxValue;
	private float mMinValue;

	// widget size
	private float mWidth;
	private float mHeight;
	private int mNumPoints;
	private int mCurrentPosition = 0;
	private int mDefinition = 5;
	private float mMinBoundary;
	private float mMaxBoundary;
	private boolean mReady = false;
	private float thickness = 2;

	public PlotView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		init();
	}

	public PlotView(Context context) {
		super(context);
		init();
	}

	public void init() {
		mDefinition = 5;

		mPaint.setStrokeWidth(1.0f);
		// mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

		if (isInEditMode()) {
			loadDemoValues();
		}

	}

	public void loadDemoValues() {
		Plot plot1 = new Plot(Color.RED);
		addPlot(plot1);
		mNumPoints = 1000; // (int)(getWidth() / mDefinition);
		for (int i = 0; i < mNumPoints; i++) {
			plot1.plotValues.add(((float) Math.random() * 220));
		}
	}

	public void addPlot(Plot plot) {
		plots.add(plot);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;

		mNumPoints = (int) (mWidth / mDefinition);

		// zeroed the plot
		for (Plot p : plots) {
			p.zero();
		}

		if (mWidth < mHeight) {
			mMaxValue = w;
		} else {
			mMaxValue = w - 50;
		}

		mLastX = mMaxValue;

		// create a bitmap for caching what was drawn
		if (bitmap != null) {
			bitmap.recycle();
		}
		mCanvas = new Canvas();
		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(bitmap);
		mPaint.setColor(0x88000000);
		mPaint.setStyle(Style.FILL);

		// round rect mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight),
		// 12, 12, mPaint);
		mCanvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);

		// draw grid
		mPaint.setColor(0x55FFFFFF);
		for (int i = 0; i < mWidth; i = i + (int) (mWidth / 9)) {
			mCanvas.drawLine(i, 0, i, mHeight, mPaint);
		}
		for (int j = 0; j < mHeight; j = j + (int) (mHeight / 5)) {
			mCanvas.drawLine(0, j, mWidth, j, mPaint);

		}
		// plotValues = new Vector<Float>();
		// setLimits(0, 1);
		mReady = true;

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		synchronized (this) {

			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
					new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

			for (Plot p : plots) {

				// canvas.save(Canvas.MATRIX_SAVE_FLAG);
				mPaint.setStyle(Style.STROKE);
				mPaint.setColor(p.color);
				mPaint.setStyle(Paint.Style.FILL);

				// Uncomment to try
				// canvas.drawText(
				// "hola " + mNumPoints + " " + p.plotValues.size() + " " +
				// p.plotValues.get(p.plotValues.size() - 1), 100,
				// 100, mPaint);

				int i = 0;

				float y = 0;
				for (int x = 0; x < mWidth; x += mDefinition) {

					y = CanvasUtils.map((float) p.plotValues.get(i), (float) mMinBoundary, (float) mMaxBoundary,
							(float) mHeight, 0f);

					// Log.d(TAG, mMinBoundary + " " + mMaxBoundary + " " + y);
					// canvas.drawPoint(x, y, mPaint);

					canvas.drawCircle(x, y, thickness, mPaint);

					if (i < mNumPoints - 1) {
						i++;
					}

				}

				// show value
				// mPaint.setColor(Color.BLACK);
				float lastVal = p.plotValues.get(plots.get(0).plotValues.size() - 1);
				canvas.drawText("" + lastVal, mWidth - 20, y, mPaint);

			}

			// draw border
			// mPaint.setColor(0xFF000000);
			// mPaint.setStyle(Style.STROKE);
			// mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 0, 0,
			// mPaint);

		}
	}

	public void setValue(Plot p, float v1) {

		if (mReady) {
			// shift points
			p.plotValues.remove(0);
			p.plotValues.add(v1);

			// check if is min or max
			if (v1 >= mMaxValue)
				mMaxValue = v1;
			if (v1 < mMinValue)
				mMinValue = v1;

			invalidate();
		}

	}

	public void setLimits(float min, float max) {
		mMinBoundary = min;
		mMaxBoundary = max;
	}

	public void setDefinition(int definition) {
		mDefinition = definition;
	}

	public void destroy() {
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

	public class Plot {

		// values to plot
		public Vector<Float> plotValues;
		int color;

		public Plot(int color) {
			plotValues = new Vector<Float>();
			this.color = color;

			zero();
		}

		public void zero() {
			plotValues.clear();
			for (int i = 0; i < mNumPoints; i++) {
				plotValues.add(0f);
			}
		}

	}

	public void setThickness(float r) {
		thickness = r;

	}

}