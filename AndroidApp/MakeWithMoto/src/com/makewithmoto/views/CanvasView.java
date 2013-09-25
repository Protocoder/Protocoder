package com.makewithmoto.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.View;

/**
 * View that has "pluggable" handlers for callbacks. Reduces the need for
 * subclassing View.
 * 
 * @author Mikael Kindborg Email: mikael.kindborg@gmail.com Blog:
 *         divineprogrammer@blogspot.com Twitter: @divineprog Copyright (c)
 *         Mikael Kindborg 2010 Source code license: MIT
 */
public class CanvasView extends View {
	OnDrawListener onDrawListener;
	OnMeasureListener onMeasureListener;
	OnSizeChangedListener onSizeChangedListener;

	Bitmap myCanvasBitmap = null;
	Canvas myCanvas = null;

	Matrix identityMatrix;

	public CanvasView(Context context) {
		super(context);
	}

	public CanvasView setOnDrawListener(OnDrawListener listener) {
		this.onDrawListener = listener;
		return this;
	}

	public CanvasView setOnMeasureListener(OnMeasureListener listener) {
		this.onMeasureListener = listener;
		return this;
	}

	public CanvasView setOnSizeChangedListener(OnSizeChangedListener listener) {
		this.onSizeChangedListener = listener;
		return this;
	}

	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (null != onDrawListener) {
			onDrawListener.onDraw(canvas);
		}

		canvas.drawBitmap(myCanvasBitmap, identityMatrix, null);

	}

	@SuppressLint("WrongCall")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);

		myCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		myCanvas = new Canvas();
		myCanvas.setBitmap(myCanvasBitmap);

		identityMatrix = new Matrix();

		setMeasuredDimension(w, h);
	}

	public Canvas getCanvas() {
		return myCanvas;
	}
	
	public Bitmap getCanvasBitmap() {
		return myCanvasBitmap;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (null != onSizeChangedListener) {
			onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
		}
	}

	public interface OnDrawListener {
		void onDraw(Canvas canvas);
	}

	public interface OnMeasureListener {
		Point onMeasure(int widthMeasureSpec, int heightMeasureSpec);
	}

	public interface OnSizeChangedListener {
		void onSizeChanged(int w, int h, int oldw, int oldh);
	}
}
