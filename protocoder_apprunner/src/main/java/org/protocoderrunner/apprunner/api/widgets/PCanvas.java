/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
*
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.apprunner.api.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.apprunner.api.other.PLooper;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.Image;
import org.protocoderrunner.utils.MLog;

import java.io.File;
import java.util.Vector;

import static android.graphics.Shader.TileMode;


public class PCanvas extends View implements PViewInterface {

    private static final String TAG = PCanvas.class.getSimpleName();

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

    public TileMode MODE_CLAMP = Shader.TileMode.CLAMP;
    public TileMode MODE_MIRROR = TileMode.MIRROR;
    public TileMode MODE_REPEAT = TileMode.REPEAT;

    public boolean MODE_CORNER = true;
    public boolean MODE_CENTER = false;

    public Paint.Align ALIGN_CENTER = Paint.Align.CENTER;
    public Paint.Align ALIGN_LEFT = Paint.Align.LEFT;
    public Paint.Align ALIGN_RIGHT = Paint.Align.RIGHT;

    public Paint.Cap CAP_ROUND = Paint.Cap.ROUND;
    public Paint.Cap CAP_BUTT = Paint.Cap.BUTT;
    public Paint.Cap CAP_SQUARE = Paint.Cap.SQUARE;

    private final Context context;
    private PLooper loop;
    private Paint mPaintFill;
    private Paint mPaintStroke;
    private Paint mPaintBackground;

    private boolean mAutoDraw = false;
    private boolean mModeCorner = MODE_CORNER;
    private boolean strokeOn = false;
    private boolean fillOn = true;
    private boolean viewIsInit = false;
    private int mWidth;
    private int mHeight;

    private final Rect textBounds = new Rect();


    public interface PCanvasInterfaceDraw {
        void onDraw(Canvas c);
    }

    public interface PCanvasInterfaceTouch {
        void onTouch(float x, float y);
    }

    //private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Canvas mCanvas;
    private Bitmap mCurrentBmp;
    private Vector<Layer> mLayerFifo;
    private PCanvasInterfaceDraw pCanvasInterfaceDraw;
    private PCanvasInterfaceTouch pCanvasInterfaceTouch;
    private int currentLayer = -1;


    public void init() {
        //MLog.d(TAG, "init");
        WhatIsRunning.getInstance().add(this);

        mPaintFill = new Paint();
        mPaintStroke = new Paint();
        mPaintFill.setAntiAlias(true);
        mPaintStroke.setAntiAlias(true);

        mPaintBackground = new Paint();

    }

    public void initLayers() {
        //initLayers
        //MLog.d(TAG, "initLayers");

        mLayerFifo = new Vector<Layer>();

        Layer layer = createNewLayer();
        layer.visible = true;
        mCurrentBmp = layer.bmp;
        mLayerFifo.add(++currentLayer, layer);

        mCanvas = new Canvas(mCurrentBmp);
        draw(mCanvas);
        viewIsInit = true;
        //MLog.d(TAG, "viewIsInit " + viewIsInit);
    }


    public PCanvas(Context context) {
        super(context);
        //MLog.d(TAG, "onCreate");

        this.context = context;
        init();
        initLayers();
    }


    public PCanvas(Context context, int w, int h) {
        super(context);
        //MLog.d(TAG, "onCreate");
        mWidth = w;
        mHeight = h;

        this.context = context;
        init();
        initLayers();
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //MLog.d(TAG, "onMeasure");

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    //on draw
    @Override
    protected synchronized void onDraw(Canvas c) {
        //MLog.d(TAG, "onDraw " + viewIsInit);
        if (viewIsInit) {
            super.onDraw(c);

            //draw all the layers
            for (Layer layer : mLayerFifo) {
                if (layer.visible) {
                    c.drawBitmap(layer.bmp, 0, 0, null);
                }
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


    @ProtoMethod(description = "Redraws the canvas in a given interval", example = "")
    @ProtoMethodParam(params = {"speed", "function()"})
    public void loopDraw(int ms, final PCanvasInterfaceDraw pCanvasInterfaceDraw) {
        if (loop != null) {
            loop.stop();
            loop = null;
        }

        PUtil util = new PUtil(context);
        loop = util.loop(ms, new PLooper.LooperCB() {
            @Override
            public void event() {
                pCanvasInterfaceDraw.onDraw(mCanvas);
                invalidate();
            }
        }).start();

        mAutoDraw = true;
    }


    @ProtoMethod(description = "Manually refresh the canvas", example = "")
    @ProtoMethodParam(params = {})
    public void refresh() {
        if (mAutoDraw) {
            invalidate();
        }
    }


    @ProtoMethod(description = "For each change in the canvas it will redraw it self. Have in mind that mainly to try out things as is not very fast.", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void autoDraw(boolean b) {
        mAutoDraw = b;
    }

    public Canvas getCanvas() {

        return mCanvas;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //.d(TAG, "onAttachedToWindow");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //MLog.d(TAG, "onSizeChanged " + getWidth() + " " + getHeight());

        //enable this
        //Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        //Bitmap _bmp = Bitmap.createBitmap(getWidth(), getHeight(), conf);
        //mCurrentBmp = _bmp;
        //mCanvas = new Canvas(mCurrentBmp);
        //draw(mCanvas);

        //mLayerFifo.get(0).bmp = _bmp;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //MLog.d(TAG, "onLayout");

        //init();
    }

    @Override
    protected void onDetachedFromWindow() {
        //MLog.d(TAG, "onDetachedFromwindow");

        stop();
        super.onDetachedFromWindow();
    }

    public void stop() {
        if (loop != null) {
            loop.stop();
        }
    }

    //TODO drawPaint o drawARGB

    @ProtoMethod(description = "Change the background color with alpha value", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas background(int r, int g, int b, int alpha) {
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setARGB(alpha, r, g, b);
        mCanvas.drawRect(0, 0, mWidth, mHeight, mPaintBackground);
        refresh();

        return this;
    }

    //TODO drawPaint o drawARGB

    @ProtoMethod(description = "Change the background color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public PCanvas background(int r, int g, int b) {
        background(r, g, b, 255);

        refresh();

        return this;
    }


    @ProtoMethod(description = "Draw a point", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas point(float x, float y) {
        mCanvas.drawPoint(x, y, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    //
    //@APIMethod(description = "", example = "")
    //@APIParam(params = { "x", "y" })
    public PCanvas points(float[] points) {
        mCanvas.drawPoints(points, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    //
    //@APIMethod(description = "", example = "")
    //@APIParam(params = { "x", "y" })
    public PCanvas points(float[] points, int offset, int count) {
        mCanvas.drawPoints(points, offset, count, mPaintStroke);
        refresh();
        return this;
    }


    @ProtoMethod(description = "Draw a line", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2"})
    public PCanvas line(float x1, float y1, float x2, float y2) {
        mCanvas.drawLine(x1, y1, x2, y2, mPaintStroke);
        refresh();
        return this;
    }

    //TODO
    //
    //@APIMethod(description = "", example = "")
    //@APIParam(params = { "x", "y" })
    public Path createPath(float[][] points, boolean close) {
        Path path = new Path();

        path.moveTo(points[0][0], points[0][1]);

        for (int i = 1; i < points.length; i++) {
            path.lineTo(points[i][0], points[i][1]);
        }
        if (close) {
            path.close();
        }

        return path;
    }


    @ProtoMethod(description = "Draw a path", example = "")
    @ProtoMethodParam(params = {"path"})
    public PCanvas path(Path path) {
        if (fillOn) mCanvas.drawPath(path, mPaintFill);
        if (strokeOn) mCanvas.drawPath(path, mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Set a dashed stroke", example = "")
    @ProtoMethodParam(params = {"float[]", "phase"})
    public PCanvas strokeDashed(float[] intervals, float phase) {

        // Stamp mContext concave arrow along the line
        PathEffect effect = new DashPathEffect(intervals, phase);
        mPaintStroke.setPathEffect(effect);

        return this;
    }


    @ProtoMethod(description = "Draws and ellipse", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "width", "height"})
    public PCanvas ellipse(float x, float y, float width, float height) {
        if (fillOn) mCanvas.drawOval(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvas.drawOval(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Draws a rectangle", example = "")
    @ProtoMethodParam(params = {"x", "y", "width", "height"})
    public PCanvas rect(float x, float y, float width, float height) {
        if (fillOn) mCanvas.drawRect(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvas.drawRect(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Draws a rectangle with a given roundness value", example = "")
    @ProtoMethodParam(params = {"x", "y", "width", "height", "rx", "ry"})
    public PCanvas rect(float x, float y, float width, float height, float rx, float ry) {
        if (fillOn) mCanvas.drawRoundRect(place(x, y, width, height), rx, ry, mPaintFill);
        if (strokeOn) mCanvas.drawRoundRect(place(x, y, width, height), rx, ry, mPaintStroke);

        refresh();

        return this;
    }


    @ProtoMethod(description = "Draws an arc", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "initAngle", "sweepAngle", "center"})
    public PCanvas arc(float x1, float y1, float x2, float y2, float initAngle, float sweepAngle, boolean center) {
        if (fillOn)
            mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintFill);
        if (strokeOn)
            mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Writes text", example = "")
    @ProtoMethodParam(params = {"text", "x", "y"})
    public PCanvas text(String text, float x, float y) {
        if (fillOn) mCanvas.drawText(text, x, y, mPaintFill);
        if (strokeOn) mCanvas.drawText(text, x, y, mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Draws a text in a path", example = "")
    @ProtoMethodParam(params = {"text", "path", "initOffset", "outOffsett"})
    public PCanvas text(String text, Path path, float initOffset, float outOffset) {
        if (fillOn) mCanvas.drawTextOnPath(text, path, initOffset, outOffset, mPaintFill);
        if (strokeOn) mCanvas.drawTextOnPath(text, path, initOffset, outOffset, mPaintStroke);

        refresh();

        return this;
    }

    public void drawTextCentered(String text){
        int cx = mCanvas.getWidth() / 2;
        int cy = mCanvas.getHeight() / 2;

        mPaintFill.getTextBounds(text, 0, text.length(), textBounds);
        mCanvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), mPaintFill);
    }


    @ProtoMethod(description = "Load an image", example = "")
    @ProtoMethodParam(params = {"imagePath"})
    public Bitmap loadImage(String imagePath) {
        return Image.loadBitmap(ProjectManager.getInstance().getCurrentProject().getStoragePath() + File.separator + imagePath);
    }


    @ProtoMethod(description = "Draws an image", example = "")
    @ProtoMethodParam(params = {"bitmap", "x", "y"})
    public PCanvas image(Bitmap bmp, int x, int y) {
        //if (fillOn)
        mCanvas.drawBitmap(bmp, x, y, mPaintBackground);
        //if (strokeOn) mCanvas.drawBitmap(bmp, x, y, mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Draws an image", example = "")
    @ProtoMethodParam(params = {"bitmap", "x", "y", "w", "h"})
    public PCanvas image(Bitmap bmp, int x, int y, int w, int h) {
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF rectDst = new RectF(x, y, x + w, y + h);
        //if (fillOn)

        //if (strokeOn)
        mCanvas.drawBitmap(bmp, rectSrc, rectDst, mPaintStroke);
        refresh();

        return this;
    }


    @ProtoMethod(description = "Clear the canvas", example = "")
    @ProtoMethodParam(params = {})
    public void clear() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        refresh();
    }


    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas fill(int r, int g, int b, int alpha) {
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setARGB(alpha, r, g, b);
        fillOn = true;

        return this;
    }


    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public PCanvas fill(int r, int g, int b) {
        fill(r, g, b, 255);
        fillOn = true;

        return this;
    }

    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public PCanvas fill(String hex) {
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(Color.parseColor(hex));
        fillOn = true;

        return this;
    }

    @ProtoMethod(description = "Removes the filling color", example = "")
    @ProtoMethodParam(params = {})
    public void noFill() {
        fillOn = false;

    }


    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas stroke(int r, int g, int b, int alpha) {
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setARGB(alpha, r, g, b);
        strokeOn = true;

        return this;
    }


    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public PCanvas stroke(int r, int g, int b) {
        stroke(r, g, b, 255);
        strokeOn = true;

        return this;
    }

    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public PCanvas stroke(String c) {
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(Color.parseColor(c));
        strokeOn = true;

        return this;
    }


    @ProtoMethod(description = "Removes the stroke color", example = "")
    @ProtoMethodParam(params = {})
    public void noStroke() {
        strokeOn = false;
    }


    @ProtoMethod(description = "Sets a stroke width", example = "")
    @ProtoMethodParam(params = {"width"})
    public PCanvas strokeWidth(float w) {
        mPaintStroke.setStrokeWidth(w);

        return this;
    }


    @ProtoMethod(description = "Sets a stroke cap", example = "")
    @ProtoMethodParam(params = {"cap"})
    public PCanvas strokeCap(Paint.Cap cap) {
        mPaintStroke.setStrokeCap(cap);

        return this;
    }

//    @ProtoMethod(description = "Sets a stroke join", example = "")
//    @ProtoMethodParam(params = {"join"})
//    public PCanvas strokeJoin(Paint.Join join) {
//        mPaintStroke.setStrokeJoin(Paint.Join.ROUND);
//
//        return this;
//    }


    @ProtoMethod(description = "Sets a given font", example = "")
    @ProtoMethodParam(params = {"typeface"})
    public PCanvas font(Typeface typeface) {
        mPaintFill.setTypeface(typeface);
        mPaintStroke.setTypeface(typeface);

        return this;
    }


    @ProtoMethod(description = "Sets the size of the text", example = "")
    @ProtoMethodParam(params = {"size"})
    public PCanvas textSize(int size) {
        mPaintFill.setTextSize(size);
        mPaintStroke.setTextSize(size);

        return this;
    }

    public PCanvas textType() {
        mPaintFill.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mPaintStroke.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        return this;
    }

    public PCanvas textAlign(Paint.Align alignment) {
        mPaintFill.setTextAlign(alignment);
        mPaintStroke.setTextAlign(alignment);

        return this;
    }

    public PCanvas textSpacing(float spacing) {
        //mPaintFill.setLetterSpacing(spacing);
        //mPaintStroke.setLetterSpacing(spacing);

        return this;
    }


    @ProtoMethod(description = "Enable/Disable antialiasing", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PCanvas antiAlias(boolean b) {
        mPaintFill.setAntiAlias(b);
        mPaintStroke.setAntiAlias(b);

        return this;
    }


    @ProtoMethod(description = "Sets the shadow fill", example = "")
    @ProtoMethodParam(params = {"x", "y", "radius", "colorHext"})
    public PCanvas shadowFill(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintFill.setShadowLayer(radius, x, y, c);

        return this;
    }


    @ProtoMethod(description = "Set the shadow stroke", example = "")
    @ProtoMethodParam(params = {"x", "y", "radius", "colorHex"})
    public PCanvas shadowStroke(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintStroke.setShadowLayer(radius, x, y, c);

        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"filter"})
    public PCanvas filter(PorterDuff.Mode mode) {
        mPaintFill.setXfermode(new PorterDuffXfermode(mode));
        mPaintStroke.setXfermode(new PorterDuffXfermode(mode));

        return this;
    }


    @ProtoMethod(description = "save", example = "")
    @ProtoMethodParam(params = {})
    public PCanvas save() {
        mCanvas.save();

        return this;
    }


    @ProtoMethod(description = "Rotate given degrees", example = "")
    @ProtoMethodParam(params = {"degrees"})
    public PCanvas rotate(float degrees) {
        mCanvas.rotate(degrees);

        return this;
    }


    @ProtoMethod(description = "Translate", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas translate(float x, float y) {
        mCanvas.translate(x, y);

        return this;
    }


    @ProtoMethod(description = "Skew", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas skew(float x, float y) {
        mCanvas.skew(x, y);

        return this;
    }


    @ProtoMethod(description = "Scale", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas scale(float x, float y) {
        mCanvas.scale(x, y);

        return this;
    }


    @ProtoMethod(description = "Restore", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas restore() {
        mCanvas.restore();
        return this;
    }


    @ProtoMethod(description = "Creates a new layer returning its position", example = "")
    @ProtoMethodParam(params = {})
    public int newLayer() {
        //create mContext new bitmap
        Layer layer = createNewLayer();
        layer.visible = true;
        mLayerFifo.add(++currentLayer, layer);
        mCurrentBmp = layer.bmp;
        mCanvas.setBitmap(mCurrentBmp);

        return currentLayer;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"Deletes a layer in a position"})
    public void deleteLayer(int pos) {
        mLayerFifo.remove(pos);
    }


    @ProtoMethod(description = "Sets a given layer specifying if the rest have to be hidden", example = "")
    @ProtoMethodParam(params = {"position", "hideAllLayers"})
    public PCanvas setLayer(int pos, boolean hideAll) {
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


    @ProtoMethod(description = "Enable/Disables a layer", example = "")
    @ProtoMethodParam(params = {"position", "enable"})
    public PCanvas enableLayer(int pos, boolean b) {
        Layer layer = mLayerFifo.get(pos);
        layer.visible = b;
        mCurrentBmp = layer.bmp;

        refresh();

        return this;
    }


    @ProtoMethod(description = "Drawing will be done from a corner if true, otherwise from the center", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas mode(boolean mode) {
        mModeCorner = mode;

        return this;
    }


    @ProtoMethod(description = "Create a bitmap shader", example = "")
    @ProtoMethodParam(params = {"bitmap", "tileMode"})
    public Shader createBitmapShader(Bitmap bitmap, TileMode mode) {
        BitmapShader shader = new BitmapShader(bitmap, mode, mode);

        return shader;
    }


    @ProtoMethod(description = "Create a linear shader", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "colorHex1", "colorHex2", "tileMode"})
    public Shader linearShader(float x1, float y1, float x2, float y2, String c1, String c2, TileMode mode) {
        Shader shader = new LinearGradient(x1, y1, x2, y2, Color.parseColor(c1), Color.parseColor(c2), mode);
        return shader;
    }


    @ProtoMethod(description = "Create a linear shader", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "ArrayColorHex", "ArrayPositions", "tileMode"})
    public Shader linearShader(float x1, float y1, float x2, float y2, String[] colorsStr, float[] positions, TileMode mode) {
        int colors[] = new int[colorsStr.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(colorsStr[i]);
        }
        Shader shader = new LinearGradient(x1, y1, x2, y2, colors, positions, mode);

        return shader;
    }


    @ProtoMethod(description = "Creates a sweep shader", example = "")
    @ProtoMethodParam(params = {"x", "y", "colorHex", "colorHex"})
    public Shader sweepShader(int x, int y, String c1, String c2) {
        Shader shader = new SweepGradient(x, y, Color.parseColor(c1), Color.parseColor(c2));

        return shader;
    }


    @ProtoMethod(description = "Compose two shaders", example = "")
    @ProtoMethodParam(params = {"shader1", "shader2", "mode"})
    public Shader composeShader(Shader s1, Shader s2, PorterDuff.Mode mode) {
        Shader shader = new ComposeShader(s1, s2, mode);

        return shader;
    }


    @ProtoMethod(description = "Sets a shader", example = "")
    @ProtoMethodParam(params = {"shader", "mode"})
    public void setShader(Shader shader, TileMode mode) {
        mPaintFill.setAntiAlias(true);
        mPaintFill.setShader(shader);
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
        //MLog.d(TAG, "createNewLayer of " + mWidth + " " + mHeight);
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

    //WARNING this method is experimental, be careful!
    //stretching parameter resize the textures
    public synchronized void size (int w, int h, boolean stretching) {
//        this.getLayoutParams().width = w;
//        this.getLayoutParams().height = h;

        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.width = w;
        params.height = h;
        mWidth = w;
        mHeight = h;
        this.requestLayout();

        //reinit textures
        if (stretching) {
            currentLayer = -1;
            initLayers();
        }
    }

}