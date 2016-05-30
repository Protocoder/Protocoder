package org.protocoderrunner.api.widgets;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import org.protocoderrunner.api.other.PLooper;
import org.protocoderrunner.apidoc.annotation.ProtoField;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.Image;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;
import java.util.Collections;

public class PCanvas extends View implements PViewInterface {

    private static final String TAG = PCanvas.class.getSimpleName();

    private final AppRunner mAppRunner;

    @ProtoField(description = "Object that contains the layers", example = "")
    public Layers layers;
    public Canvas mCanvas;

    @ProtoField(description = "Canvas width", example = "")
    private int width;

    @ProtoField(description = "Canvas height", example = "")
    private int height;

    @ProtoField(description = "Time interval between draws", example = "")
    private int drawInterval = 35;


    public boolean MODE_CORNER = true;
    public boolean MODE_CENTER = false;

    private RectF mRectf;
    private Paint mPaintBackground;
    private Paint mPaintFill;
    private Paint mPaintStroke;
    private boolean mAutoDraw = false;
    private boolean fillOn = true;
    private boolean strokeOn = false;
    private boolean mModeCorner = MODE_CORNER;


    // Autodraw
    private PLooper loop;
    int looperSpeed = 35;

    public interface OnSetupCallback { void event (PCanvas c); }
    public interface OnDrawCallback { void event (PCanvas c); }

    public OnSetupCallback setup;
    public OnDrawCallback draw;


    public PCanvas(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        prepareLooper();
        init();
    }

    private void prepareLooper() {
        loop = mAppRunner.pUtil.loop(drawInterval, new PLooper.LooperCB() {
            @Override
            public void event() {
                if (draw != null) {
                    draw.event(PCanvas.this);
                    invalidate();
                }
            }
        });
        loop.speed(looperSpeed);
    }

    private void startLooper() {
        loop.start();
        mAutoDraw = true;
    }

    public void drawInterval(int ms) {
        loop.speed(ms);
    }

    public void init() {
        mRectf = new RectF();
        mPaintBackground = new Paint();
        mPaintFill = new Paint();
        mPaintStroke = new Paint();
        mPaintFill.setAntiAlias(true);
        mPaintStroke.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        MLog.d("new size --> ", w + " " + h);

        width = w;
        height = h;

        mCanvas = new Canvas();
        layers = new Layers();

        if (setup != null) setup.event(PCanvas.this);
        // startLooper();
    }

    public void onDraw(OnDrawCallback callback) {
        draw = callback;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        MLog.d(TAG, "1");
        if (!mAutoDraw) draw.event(this);
        layers.drawAll(canvas);
        MLog.d(TAG, "2");
    }

    /**
     * Fill and stroke colors
     */
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
    public PCanvas noStroke() {
        strokeOn = false;
        return this;
    }

    @ProtoMethod(description = "Sets a stroke width", example = "")
    @ProtoMethodParam(params = {"width"})
    public PCanvas strokeWidth(float w) {
        mPaintStroke.setStrokeWidth(w);
        return this;
    }

    @ProtoMethod(description = "Sets a stroke cap", example = "")
    @ProtoMethodParam(params = {"cap"})
    public PCanvas strokeCap(String cap) {

        Paint.Cap c = Paint.Cap.SQUARE;

        switch (cap) {
            case "round":
                c = Paint.Cap.ROUND;
                break;

            case "butt":
                c = Paint.Cap.BUTT;

                break;

            case "square":
                c = Paint.Cap.SQUARE;

                break;
        }

        mPaintStroke.setStrokeCap(c);
        return this;
    }

    @ProtoMethod(description = "Change the background color with alpha value", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas background(int r, int g, int b, int alpha) {
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setARGB(alpha, r, g, b);
        mCanvas.drawRect(0, 0, width, height, mPaintBackground);
        refresh();

        return this;
    }

    /**
     * Drawing mode
     */
    @ProtoMethod(description = "Drawing will be done from a corner if true, otherwise from the center", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas mode(boolean mode) {
        mModeCorner = mode;

        return this;
    }

    /**
     * Drawing thingies
     */
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

    @ProtoMethod(description = "Draws and ellipse", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "width", "height"})
    public PCanvas ellipse(float x, float y, float width, float height) {
        if (fillOn) mCanvas.drawOval(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvas.drawOval(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draws an arc", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "initAngle", "sweepAngle", "center"})
    public PCanvas arc(float x1, float y1, float x2, float y2, float initAngle, float sweepAngle, boolean center) {
        if (fillOn) mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintFill);
        if (strokeOn) mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintStroke);
        refresh();

        return this;
    }


    /**
     * Text stuff
     */
    @ProtoMethod(description = "Sets the size of the text", example = "")
    @ProtoMethodParam(params = {"size"})
    public PCanvas textSize(int size) {
        mPaintFill.setTextSize(size);
        mPaintStroke.setTextSize(size);
        return this;
    }

    public PCanvas textAlign(String alignTo) {
        Paint.Align alignment = Paint.Align.LEFT;

        switch (alignTo) {
            case "left":
                alignment = Paint.Align.LEFT;
                break;

            case "center":
                alignment = Paint.Align.CENTER;
                break;

            case "right":
                alignment = Paint.Align.RIGHT;
                break;
        }

        mPaintFill.setTextAlign(alignment);
        mPaintStroke.setTextAlign(alignment);

        return this;
    }

    // TODO this only works on api 21
    //public PCanvas textSpacing(float spacing) {
        //mPaintFill.setLetterSpacing(spacing);
        //mPaintStroke.setLetterSpacing(spacing);
    //    return this;
    //}

    @ProtoMethod(description = "Writes text", example = "")
    @ProtoMethodParam(params = {"text", "x", "y"})
    public PCanvas text(String text, float x, float y) {
        if (fillOn) mCanvas.drawText(text, x, y, mPaintFill);
        if (strokeOn) mCanvas.drawText(text, x, y, mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draws a text on a path", example = "")
    @ProtoMethodParam(params = {"text", "path", "initOffset", "outOffsett"})
    public PCanvas text(String text, Path path, float initOffset, float outOffset) {
        if (fillOn) mCanvas.drawTextOnPath(text, path, initOffset, outOffset, mPaintFill);
        if (strokeOn) mCanvas.drawTextOnPath(text, path, initOffset, outOffset, mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Load an image", example = "")
    @ProtoMethodParam(params = {"imagePath"})
    public Bitmap loadImage(String imagePath) {
        return Image.loadBitmap(mAppRunner.getProject().getFullPathForFile(imagePath));
    }

    @ProtoMethod(description = "Draws an image", example = "")
    @ProtoMethodParam(params = {"bitmap", "x", "y"})
    public PCanvas image(Bitmap bmp, int x, int y) {
        mCanvas.drawBitmap(bmp, x, y, mPaintBackground);
        refresh();
        return this;
    }

    @ProtoMethod(description = "Draws an image", example = "")
    @ProtoMethodParam(params = {"bitmap", "x", "y", "w", "h"})
    public PCanvas image(Bitmap bmp, int x, int y, int w, int h) {
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF rectDst = new RectF(x, y, x + w, y + h);
        mCanvas.drawBitmap(bmp, rectSrc, rectDst, mPaintStroke);
        refresh();

        return this;
    }


    /**
     *
     */
    @ProtoMethod(description = "push", example = "")
    @ProtoMethodParam(params = {})
    public PCanvas push() {
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

    @ProtoMethod(description = "Skew values 0 - 1)", example = "")
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
    public PCanvas pop() {
        mCanvas.restore();
        return this;
    }

    public Camera getCamera() {
        Camera camera = new Camera();
        return camera;
    }

    public Matrix getMatrix() {
        Matrix matrix = new Matrix();
        getCamera().getMatrix(matrix);

        return matrix;
    }

    /**
     * Shadows
     */
    @ProtoMethod(description = "Sets the shadow fill", example = "")
    @ProtoMethodParam(params = {"x", "y", "radius", "colorHext"})
    public PCanvas shadow(int x, int y, float radius, String colorHex) {
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

    /**
     * Shaders
     */
    @ProtoMethod(description = "Sets a shader", example = "")
    @ProtoMethodParam(params = {"shader"})
    public void setShader(Shader shader) {
        mPaintFill.setAntiAlias(true);
        mPaintFill.setShader(shader);
    }

    @ProtoMethod(description = "Create a linear shader", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "colorHex1", "colorHex2", "tileMode"})
    public Shader linearShader(float x1, float y1, float x2, float y2, String c1, String c2) {
        Shader.TileMode mode = Shader.TileMode.REPEAT;

        Shader shader = new LinearGradient(x1, y1, x2, y2, Color.parseColor(c1), Color.parseColor(c2), mode);
        return shader;
    }

    /**
     *
     */
    @ProtoMethod(description = "Enable/Disable antialiasing", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PCanvas antiAlias(boolean b) {
        mPaintFill.setAntiAlias(b);
        mPaintStroke.setAntiAlias(b);
        return this;
    }

    @ProtoMethod(description = "For each change in the canvas it will redraw it self. Have in mind that mainly to try out things as is not very fast.", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PCanvas autoDraw(boolean b) {
        mAutoDraw = b;
        return this;
    }

    private RectF place(float x, float y, float w, float h) {
        if (mModeCorner == MODE_CORNER) {
            mRectf.set(x, y, x + w, y + h);
        } else {
            mRectf.set(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
        }

        return mRectf;
    }

    /**
     * refresh the canvas after painting if the flag is true
     */
    private PCanvas refresh() {
        if (mAutoDraw) invalidate();
        return this;
    }


    /**
     * Layer class stuff
     */
    public class Layer {
        public Bitmap bitmap;
        public boolean visibility = true;

        Layer() {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
    }

    public class Layers {

        ArrayList<Layer> layers = new ArrayList<>();

        Layers() {
            create(); //add default layer
        }

        public void create() {
            create(layers.size());
        }

        public void create(int index) {
            Layer layer = new Layer();
            layers.add(index, layer);
            setCurrent(layers.size() - 1);
        }

        public void move(int from, int to) {
            Collections.swap(layers, from, to);
        }

        public void delete(int index) {
            layers.remove(index);
        }

        public void clear() {
            layers.clear();
        }

        public void setCurrent(int index) {
            MLog.d(TAG, index + " " + layers.size());
            mCanvas.setBitmap(layers.get(index).bitmap);
        }

        public void show(int index, boolean b) {
            layers.get(index).visibility = b;
        }

        public int size() {
            return layers.size();
        }

        public void drawAll(Canvas canvas) {
            for (Layer layer : layers) {
                if (layer.visibility) canvas.drawBitmap(layer.bitmap, 0, 0, null);
            }
        }
    }

}
