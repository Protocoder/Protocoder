package com.makewithmoto;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.makewithmoto.base.AppSettings;
import com.makewithmoto.base.BaseActivity;

public class LauncherActivity extends BaseActivity {

	private static final long SPLASH_SCREEN_DURATION = 1500;
	protected Handler mExitHandler = null;
	protected Runnable mExitRunnable = null;
	Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// HSHIEH: This is making the status bar disappear and hence a jump on
		// the next screen
		// setFullScreen();
		// setHideHomeBar();

		// Hide the actionbar programmatically
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		// getActionBar().hide();

		// Take some memory logs
		// MemoryLogger.showMemoryStats("BEFORE LAUNCHER BOOTSCREEN");
		setContentView(R.layout.activity_bootscreen);
		// MemoryLogger.showMemoryStats("AFTER LAUNCHER BOOTSCREEN");

		// Prepare intent to exit the activity and move to the main one
		boolean firstLaunch; // If this is the first time the
		SharedPreferences userDetails = getSharedPreferences(
				"com.makewithmoto", MODE_PRIVATE);
		firstLaunch = userDetails.getBoolean(
				getResources().getString(R.string.pref_is_first_launch), true);
		if (firstLaunch) {
			intent = new Intent(this, WelcomeActivity.class);
		} else {
			intent = new Intent(this, MainActivity.class);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		mExitRunnable = new Runnable() {
			@Override
			public void run() {
				exitSplash();
			}
		};

		// Run the exitRunnable in in SPLASH_SCREEN_DURATION ms
		mExitHandler = new Handler();
		mExitHandler.postDelayed(mExitRunnable, SPLASH_SCREEN_DURATION);

	}

	/**
	 * onPause
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * onDestroy
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// HSHIEH: Android takes care of this for us actually
		ViewGroup vg = (ViewGroup) findViewById(R.layout.activity_bootscreen);
		if (vg != null) {
			vg.invalidate();
			vg.removeAllViews();
		}
	}

	/**
	 * On touch event, exit the splash screen (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Remove the exitRunnable callback from the handler queue
			mExitHandler.removeCallbacks(mExitRunnable);
			// Run the exit code manually
			exitSplash();
		}
		return true;
	}

	/**
	 * This is a private method to exit the splash screen
	 * 
	 */
	@SuppressLint("NewApi")
	private void exitSplash() {
		finish();

		Log.d("qq", Build.VERSION.SDK_INT + " " + AppSettings.CURRENT_VERSION);
		
		if (AppSettings.CURRENT_VERSION > Build.VERSION.SDK_INT) {
			ActivityOptions options = ActivityOptions.makeCustomAnimation(this,
					android.R.anim.fade_in, android.R.anim.fade_out);
			startActivity(intent, options.toBundle());
		} else {
			startActivity(intent);

		}
	}
}
