package com.makewithmoto.views;

import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private CountDownLatch latch;
	private Canvas canvas;

	public MySurfaceView(Context context) {
		super(context);
		sh = getHolder();
		sh.addCallback(this);
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.FILL);
		latch = new CountDownLatch(1);

	}

	public void surfaceCreated(SurfaceHolder holder) {
		canvas = sh.lockCanvas();
		canvas.drawColor(Color.BLACK);
		canvas.drawCircle(100, 200, 50, paint);
		sh.unlockCanvasAndPost(canvas);
		latch.countDown();
	}

	public Canvas getCanvas() {

		try {
			latch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return canvas;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}
}