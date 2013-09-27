package com.makewithmoto.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.View;

public class MyView extends View {
	private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Canvas canvas;
	private Bitmap bmp;

	public MyView(Context context, int w, int h) {
		super(context);

		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(w, h, conf);
		canvas = new Canvas(bmp);
		draw(canvas);
	}

	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		c.drawBitmap(bmp, 0, 0, null);
	}
	

	
	public Canvas getCanvas() {

		return canvas;
	}

}