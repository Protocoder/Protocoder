package org.protocoderrunner.api.widgets;

/**
 * Created by biquillo on 6/09/16.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

public class MyRoundCornerDrawable extends Drawable {

    private Paint mPaintBg;
    private final Paint mPaintBorder;
    private float mRadius = 2;

    public MyRoundCornerDrawable(/* Bitmap bitmap */) {
        /*
        BitmapShader shader;
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        */

        // background
        mPaintBg = new Paint();
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setAntiAlias(true);
        mPaintBg.setStrokeWidth(0);
        // mPaintBg.setShadowLayer(10, 22, 22, Color.BLACK);

        // border
        mPaintBorder = new Paint();
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setAntiAlias(true);

        // mPaintBg.setShader(shader);
    }

    @Override
    public void draw(Canvas canvas) {
        int height = getBounds().height();
        int width = getBounds().width();

        // allow drawing out of bounds vertically
        Rect clipBounds = canvas.getClipBounds();
        clipBounds.inset(-12, -12);
        canvas.clipRect(clipBounds, Region.Op.REPLACE);

        RectF rect = new RectF(0.0f, 0.0f, width, height);

        // background
        canvas.drawRoundRect(rect, mRadius, mRadius, mPaintBg);

        // border
        canvas.drawRoundRect(rect, mRadius, mRadius, mPaintBorder);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaintBg.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaintBg.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setBackground(int c) {
        this.mPaintBg.setColor(c);
        invalidateSelf();
    }

    public void setBorderColor(int c) {
        this.mPaintBorder.setColor(c);
        invalidateSelf();
    }

    public void setBorderWidth(int w) {
        this.mPaintBorder.setStrokeWidth(w);
        invalidateSelf();
    }

    public void setBorderRadius(int r) {
        mRadius = r;
        invalidateSelf();
    }

}

