package org.protocoderrunner.api.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;

public class PAbsoluteLayout extends FixedLayout {

    private static final String TAG = PAbsoluteLayout.class.getSimpleName();

    private AppRunner mAppRunner;

    private static final int PIXELS = 0;
    private static final int DP = 1;
    private static final int NORMALIZED = 2;
    private int mode = NORMALIZED;

    public int mWidth = -1;
    public int mHeight = -1;
    private Context mContext;

    public PAbsoluteLayout(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();

        mWidth = appRunner.pDevice.info().screenWidth;
        mHeight = appRunner.pDevice.info().screenHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        MLog.d(TAG, l + " " + t + " " + r + " " + b);
        // mWidth = t;
        // mHeight = b;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        MLog.d(TAG, w + " " + h);

        mWidth = w;
        mHeight = h;
    }

    @ProtoMethod(description = "Sets the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void backgroundColor(String c) {
        this.setBackgroundColor(Color.parseColor(c));
    }


    @ProtoMethod(description = "Adds a view", example = "")
    @ProtoMethodParam(params = {"view", "x", "y", "w", "h"})
    public void addView(View v, float x, float y, float w, float h) {
        MLog.d(TAG, "adding view (normalized) -> " + x + " " + y + " " + w + " "  + h);
        switch (mode) {
            case PIXELS:
                break;
            case DP:
                x = AndroidUtils.pixelsToDp(mContext, (int)x);
                y = AndroidUtils.pixelsToDp(mContext, (int)y);
                w = AndroidUtils.pixelsToDp(mContext, (int)w);
                h = AndroidUtils.pixelsToDp(mContext, (int)h);
                break;
            case NORMALIZED:
                MLog.d(TAG, "width " + w + " " + mWidth);
                MLog.d(TAG, "height " + h + " " + mHeight);
                x = x * mWidth;
                y = y * mHeight;
                w = w * mWidth;
                h = h * mHeight;
                break;
        }

        if (w < 0) w = LayoutParams.WRAP_CONTENT;
        if (h < 0) h = LayoutParams.WRAP_CONTENT;

        MLog.d(TAG, "adding a view (denormalized) -> " +  v + " in " + x + " " + y + " " + w + " " + h);
        addView(v, new LayoutParams((int)w, (int)h, (int)x, (int)y));
    }

    public void mode(String type) {
        switch (type) {
            case "pixels":
                this.mode = PIXELS;
                break;
            case "dp":
                this.mode = DP;
                break;
            case "normalized":
                this.mode = NORMALIZED;
                break;
            default:
                this.mode = NORMALIZED;
        }
    }

    public int width() {
        return mWidth;
    }

    public int height() {
        return mHeight;
    }


    /**
     * This is what we use to actually position and size the views
     */
    /*
    protected void positionView(View v, int x, int y, int w, int h) {
		if (w == -1) {
			w = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		if (h == -1) {
			h = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
		params.leftMargin = x;
		params.topMargin = y;
		v.setLayoutParams(params);
	}
	*/

}
