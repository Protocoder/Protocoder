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

package org.protocoderrunner.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.sensors.WhatIsRunning;

import processing.core.PApplet;

public class CanvasView extends View {
    private final Context context;
    private PUtil.Looper loop;
    private int mWidth;
    private int mHeight;
    //private SurfaceHolder sh;


    public interface PCanvasInterfaceDraw {
        void onDraw(Canvas c);
    }

    public interface PCanvasInterfaceTouch {
        void onTouch(float x, float y);
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Canvas mCanvas;
	private Bitmap bmp;
    private PCanvasInterfaceDraw pCanvasInterfaceDraw;
    private PCanvasInterfaceTouch pCanvasInterfaceTouch;



    //on create
	public CanvasView(Context context, int w, int h, PCanvasInterfaceDraw pCanvasInterfaceDraw, PCanvasInterfaceTouch pCanvasInterfaceTouch) {
		super(context);
        this.context = context;
        WhatIsRunning.getInstance().add(this);
        this.pCanvasInterfaceDraw = pCanvasInterfaceDraw;
        this.pCanvasInterfaceTouch = pCanvasInterfaceTouch;

		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(w, h, conf);
		mCanvas = new Canvas(bmp);
		draw(mCanvas);

	}

    public CanvasView(Context context, int w, int h) {
        this(context, w, h, null, null);
    }
    //on draw
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		c.drawBitmap(bmp, 0, 0, null);
	}

    //on touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pCanvasInterfaceTouch != null) {
            pCanvasInterfaceTouch.onTouch(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void clear() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    public void draw(PCanvasInterfaceDraw pCanvasInterface) {
        pCanvasInterface.onDraw(mCanvas);
    }

    public void onTouch(PCanvasInterfaceTouch pCanvasInterfaceTouch) {
        this.pCanvasInterfaceTouch = pCanvasInterfaceTouch;
    }

    public void autoDraw(int ms, final PCanvasInterfaceDraw pCanvasInterfaceDraw) {
        if (loop != null) {
            loop.stop();
            loop = null;
        }


        PUtil util = new PUtil((Activity) context);
        loop = util.loop(ms, new PUtil.LooperCB() {
            @Override
            public void event() {
                pCanvasInterfaceDraw.onDraw(mCanvas);
                invalidate();
            }
        });

    }

    public Canvas getCanvas() {

		return mCanvas;
	}

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    public void stop() {
        if (loop != null) {
            loop.stop();
        }
    }
}