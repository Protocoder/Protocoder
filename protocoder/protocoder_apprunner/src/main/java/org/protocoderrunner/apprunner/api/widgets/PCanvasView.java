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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.util.HashMap;
import java.util.Queue;
import java.util.Vector;


public class PCanvasView extends View implements PViewInterface {

    private static final String TAG = "PCanvasView";

    public PorterDuff.Mode FILTER_ADD = PorterDuff.Mode.ADD;
    public PorterDuff.Mode FILTER_XOR = PorterDuff.Mode.XOR;
    public PorterDuff.Mode FILTER_CLEAR = PorterDuff.Mode.CLEAR;
    public PorterDuff.Mode FILTER_LIGHTEN = PorterDuff.Mode.LIGHTEN;
    public PorterDuff.Mode FILTER_MULTIPLY = PorterDuff.Mode.MULTIPLY;
    public PorterDuff.Mode FILTER_SCREEN = PorterDuff.Mode.SCREEN;
    public PorterDuff.Mode FILTER_OVERLAY = PorterDuff.Mode.OVERLAY;
    public PorterDuff.Mode FILTER_DARKEN = PorterDuff.Mode.DARKEN;
    public PorterDuff.Mode FILTER_DST = PorterDuff.Mode.DST;
    public PorterDuff.Mode FILTER_DST_ATOP = PorterDuff.Mode.DST_ATOP;
    public PorterDuff.Mode FILTER_DST_IN = PorterDuff.Mode.DST_IN;
    public PorterDuff.Mode FILTER_DST_OUT = PorterDuff.Mode.DST_OUT;
    public PorterDuff.Mode FILTER_DST_OVER = PorterDuff.Mode.DST_OVER;
    public PorterDuff.Mode FILTER_SRC = PorterDuff.Mode.SRC;
    public PorterDuff.Mode FILTER_SRC_ATOP = PorterDuff.Mode.SRC_ATOP;
    public PorterDuff.Mode FILTER_SRC_IN = PorterDuff.Mode.SRC_IN;
    public PorterDuff.Mode FILTER_SRC_OUT = PorterDuff.Mode.SRC_OUT;
    public PorterDuff.Mode FILTER_SRC_OVER = PorterDuff.Mode.SRC_OVER;

    public boolean MODE_CORNER = true;
    public boolean MODE_CENTER = false;

    public Paint.Align ALIGN_CENTER = Paint.Align.CENTER;
    public Paint.Align ALIGN_LEFT = Paint.Align.LEFT;
    public Paint.Align ALIGN_RIGHT = Paint.Align.RIGHT;

    public Paint.Cap CAP_ROUND = Paint.Cap.ROUND;
    public Paint.Cap CAP_BUTT = Paint.Cap.BUTT;
    public Paint.Cap CAP_SQUARE = Paint.Cap.SQUARE;

    private final Context context;
    private PUtil.Looper loop;
    private int mWidth;
    private int mHeight;
    private Paint mPaintFill;
    private Paint mPaintStroke;
    private Paint mPaintBackground;

    private boolean mAutoDraw;
    private boolean mModeCorner = MODE_CORNER;


    public interface PCanvasInterfaceDraw {
        void onDraw(Canvas c);
    }

    public interface PCanvasInterfaceTouch {
        void onTouch(float x, float y);
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Canvas mCanvas;
    private Bitmap mCurrentBmp;
    private final Vector<Layer> mLayerFifo;
    private PCanvasInterfaceDraw pCanvasInterfaceDraw;
    private PCanvasInterfaceTouch pCanvasInterfaceTouch;
    private int currentLayer = -1;


    //on create
    public PCanvasView(Context context, int w, int h, PCanvasInterfaceDraw pCanvasInterfaceDraw, PCanvasInterfaceTouch pCanvasInterfaceTouch) {
        super(context);
        this.context = context;
        WhatIsRunning.getInstance().add(this);
        this.pCanvasInterfaceDraw = pCanvasInterfaceDraw;
        this.pCanvasInterfaceTouch = pCanvasInterfaceTouch;

        mWidth = w;
        mHeight = h;

        mLayerFifo = new Vector<Layer>();

        Layer layer = createNewLayer();
        layer.visible = true;
        mCurrentBmp = layer.bmp;
        mLayerFifo.add(++currentLayer, layer);

        mCanvas = new Canvas(mCurrentBmp);

        draw(mCanvas);
        mPaintFill = new Paint();
        mPaintStroke = new Paint();
        mPaintFill.setAntiAlias(true);
        mPaintStroke.setAntiAlias(true);

        mPaintBackground = new Paint();
    }

    public PCanvasView(Context context, int w, int h) {
        this(context, w, h, null, null);
    }

    //on draw
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        //draw all the layers
        for (Layer layer : mLayerFifo) {
           if(layer.visible) {
               MLog.network(context, TAG, "visible " + layer.visible);
               c.drawBitmap(layer.bmp, 0, 0, null);
           }
        }

    }

    //on touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pCanvasInterfaceTouch != null) {
            pCanvasInterfaceTouch.onTouch(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void draw(PCanvasInterfaceDraw pCanvasInterface) {
        pCanvasInterface.onDraw(mCanvas);
    }

    public void onTouch(PCanvasInterfaceTouch pCanvasInterfaceTouch) {
        this.pCanvasInterfaceTouch = pCanvasInterfaceTouch;
    }

    public void loopDraw(int ms, final PCanvasInterfaceDraw pCanvasInterfaceDraw) {
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

        mAutoDraw = true;
    }

    public void refresh() {
        if (!mAutoDraw) {
            invalidate();
        }
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

    //TODO drawPaint o drawARGB
    public PCanvasView background(int r, int g, int b, int alpha) {
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setARGB(alpha, r, g, b);
        mCanvas.drawRect(0, 0, mWidth, mHeight, mPaintBackground);
        refresh();

        return this;
    }

    public PCanvasView point(float x, float y) {
        mCanvas.drawPoint(x, y, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    public PCanvasView points(float[] points) {
        mCanvas.drawPoints(points, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    public PCanvasView points(float[] points, int offset, int count) {
        mCanvas.drawPoints(points, offset, count, mPaintStroke);
        refresh();
        return this;
    }


    public PCanvasView line(float x1, float y1, float x2, float y2) {
        mCanvas.drawLine(x1, y1, x2, y2, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    public PCanvasView path(float x1, float y1, float x2, float y2) {
        Path path = new Path();
        path.lineTo(x1, y1);
        mCanvas.drawPath(path, mPaintStroke);
        refresh();
        return this;
    }

    public PCanvasView ellipse(float x1, float y1, float width, float height) {
        mCanvas.drawOval(place(x1, y1, width, height), mPaintFill);
        mCanvas.drawOval(place(x1, y1, width, height), mPaintStroke);
        refresh();

        return this;
    }

    public PCanvasView rect(float x1, float y1, float width, float height) {
        mCanvas.drawRect(place(x1, y1, width, height), mPaintFill);
        mCanvas.drawRect(place(x1, y1, width, height), mPaintStroke);
        refresh();

        return this;
    }

    public PCanvasView rect(float x1, float y1, float width, float height, float rx, float ry) {
        mCanvas.drawRoundRect(place(x1, y1, width, height), rx, ry, mPaintFill);
        mCanvas.drawRoundRect(place(x1, y1, width, height), rx, ry, mPaintStroke);

        refresh();

        return this;
    }

    public PCanvasView arc(float x1, float y1, float x2, float y2, float initAngle, float sweepAngle, boolean center) {
        mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintFill);
        mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintStroke);
        refresh();

        return this;
    }

    public PCanvasView text(String text, float x, float y) {
        mCanvas.drawText(text, x, y, mPaintFill);
        mCanvas.drawText(text, x, y, mPaintStroke);
        refresh();

        return this;
    }

    public void clear() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        refresh();
    }

    public PCanvasView fill(int r, int g, int b, int alpha) {
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setARGB(alpha, r, g, b);

        return this;
    }

    public PCanvasView fill(int r, int g, int b) {
        fill(r, g, b, 255);

        return this;
    }


    public PCanvasView stroke(int r, int g, int b, int alpha) {
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setARGB(alpha, r, g, b);

        return this;
    }

    public PCanvasView stroke(int r, int g, int b) {
        stroke(r, g, b, 255);

        return this;
    }

    public PCanvasView strokeWidth(float w) {
        mPaintStroke.setStrokeWidth(w);

        return this;
    }


    public PCanvasView strokeCap(Paint.Cap cap) {
        mPaintStroke.setStrokeCap(cap);

        return this;
    }

    public PCanvasView font(Typeface typeface) {
        mPaintFill.setTypeface(typeface);
        mPaintStroke.setTypeface(typeface);

        return this;
    }

    public PCanvasView textSize(int size) {
        mPaintFill.setTextSize(size);
        mPaintStroke.setTextSize(size);

        return this;
    }

    public PCanvasView antiAlias(boolean b) {
        mPaintFill.setAntiAlias(b);
        mPaintStroke.setAntiAlias(b);

        return this;
    }

    public PCanvasView shadow(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintFill.setShadowLayer(radius, x, y, c);
        mPaintStroke.setShadowLayer(radius, x, y, c);

        return this;
    }


    public PCanvasView filter(PorterDuff.Mode mode) {
        mPaintFill.setXfermode(new PorterDuffXfermode(mode));
        mPaintStroke.setXfermode(new PorterDuffXfermode(mode));

        return this;
    }

    public PCanvasView save() {
        mCanvas.save();

        return this;
    }
    public PCanvasView rotate(float degrees) {
        mCanvas.rotate(degrees);

        return this;
    }

    public PCanvasView translate(float x, float y) {
        mCanvas.translate(x, y);

        return this;
    }

    public PCanvasView skew(float x, float y) {
        mCanvas.skew(x, y);

        return this;
    }

    public PCanvasView scale(float x, float y) {
        mCanvas.scale(x, y);

        return this;
    }


    public PCanvasView restore() {
        mCanvas.restore();
        return this;
    }

    public int newLayer() {
        //create a new bitmap
        Layer layer = createNewLayer();
        layer.visible = true;
        mLayerFifo.add(++currentLayer, layer);
        mCurrentBmp = layer.bmp;
        mCanvas.setBitmap(mCurrentBmp);

        return currentLayer;
    }

    public void deleteLayer(int pos) {
        mLayerFifo.remove(pos);
    }

    public PCanvasView setLayer(int pos, boolean hideAll) {
        //all layers off
       if (hideAll) {
           for (Layer layer : mLayerFifo) {
               layer.visible = false;
           }
           ;
       }

        //desired layer on
        Layer layer = mLayerFifo.get(pos);
        layer.visible = true;
        mCurrentBmp = layer.bmp;
        mCanvas.setBitmap(mCurrentBmp);

        refresh();

        return this;
    }

    public PCanvasView enableLayer(int pos, boolean b) {
        Layer layer = mLayerFifo.get(pos);
        layer.visible = b;
        mCurrentBmp = layer.bmp;

        refresh();

        return this;
    }

    public PCanvasView mode(boolean mode) {
        mModeCorner = mode;

        return this;
    }


    private RectF place(float x, float y, float width, float height) {
        RectF rectf = null;
        if (mModeCorner) {
            rectf = new RectF(x, y, x + width, y + height);
        } else {
            rectf = new RectF(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        }

        return rectf;
    }

    private Layer createNewLayer() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap _bmp = Bitmap.createBitmap(mWidth, mHeight, conf);
        Layer layer = new Layer(_bmp);

        return layer;
    }


    class Layer {
        public boolean visible = false;
        Bitmap bmp;

        Layer(Bitmap bmp) {
            this.bmp = bmp;
        }
    }


}