package org.protocoderrunner.api.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;

public class SeekBarProgressDrawable extends ClipDrawable {

    private Paint mPaint = new Paint();
    private float dy;
    private Rect mRect;


    public SeekBarProgressDrawable(Drawable drawable, int gravity, int orientation, Context ctx) {
        super(drawable, gravity, orientation);
        mPaint.setColor(Color.WHITE);
        dy = 10; // ctx.getResources().getDimension(R.dimen.two_dp);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mRect == null) {
            mRect = new Rect(getBounds().left, (int) (getBounds().centerY() - dy / 2), getBounds().right, (int) (getBounds().centerY() + dy / 2));
            setBounds(mRect);
        }

        super.draw(canvas);
    }


    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}