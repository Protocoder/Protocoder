package org.protocoderrunner.api.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by biquillo on 11/09/16.
 */
public class SeekBarBackgroundDrawable extends Drawable {

    private Paint mPaint = new Paint();
    private float dy;

    public SeekBarBackgroundDrawable(Context ctx) {
        mPaint.setColor(Color.WHITE);
        dy = 10; // ctx.getResources().getDimension(R.dimen.one_dp);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds().left, getBounds().centerY() - dy / 2, getBounds().right, getBounds().centerY() + dy / 2, mPaint);
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