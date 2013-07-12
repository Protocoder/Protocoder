package com.makewithmoto;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.utils.MemoryLogger;

public class LauncherActivity extends BaseActivity {

	protected int _splashTime = 2000;
	protected Handler _exitHandler = null;
	protected Runnable _exitRunnable = null;
	Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setFullScreen();
		setHideHomeBar();

		MemoryLogger.showMemoryStats("BEFORE LAUNCHER BOOTSCREEN");
		setContentView(R.layout.activity_bootscreen);
		MemoryLogger.showMemoryStats("AFTER LAUNCHER BOOTSCREEN");

		intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		_exitRunnable = new Runnable() {
			public void run() {
				exitSplash();
			}
		};

		// Run the exitRunnable in in _splashTime ms
		_exitHandler = new Handler();
		_exitHandler.postDelayed(_exitRunnable, _splashTime);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Remove the exitRunnable callback from the handler queue
			_exitHandler.removeCallbacks(_exitRunnable);
			// Run the exit code manually
			exitSplash();
		}
		return true;
	}

	@SuppressLint("NewApi")
	private void exitSplash() {
		finish();

		ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_left, R.anim.slide_out_left);
		startActivity(intent, options.toBundle());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		ViewGroup vg = (ViewGroup) findViewById(R.layout.activity_bootscreen);
		if (vg != null) {
			vg.invalidate();
			vg.removeAllViews();
		}
	}

}
