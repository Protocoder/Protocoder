/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package com.makewithmoto;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.makewithmoto.base.AppSettings;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.utils.StrUtils;

public class LauncherActivity extends BaseActivity {

	private static final long SPLASH_SCREEN_DURATION = 0;
	protected Handler mExitHandler = null;
	protected Runnable mExitRunnable = null;
	Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setTheme(android.R.style.Theme_NoDisplay);
		//setContentView(R.layout.activity_bootscreen);
		
		// Prepare intent to exit the activity and move to the main one
		boolean firstLaunch; // If this is the first time the
		SharedPreferences userDetails = getSharedPreferences(
				"com.makewithmoto", MODE_PRIVATE);
		firstLaunch = userDetails.getBoolean(
				getResources().getString(R.string.pref_is_first_launch), true);
		
		
		if (firstLaunch) {
			intent = new Intent(this, WelcomeActivity.class);
			userDetails.edit().putString("device_id", StrUtils.generateRandomString());
		} else {
			intent = new Intent(this, MainActivity.class);
		}
		
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		finish();

		/*
		mExitRunnable = new Runnable() {
			@Override
			public void run() {
				exitSplash();
			}
		};

		// Run the exitRunnable in in SPLASH_SCREEN_DURATION ms
		mExitHandler = new Handler();
		mExitHandler.postDelayed(mExitRunnable, SPLASH_SCREEN_DURATION);
		 */
	
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

		if (AppSettings.CURRENT_VERSION > Build.VERSION.SDK_INT) {
			ActivityOptions options = ActivityOptions.makeCustomAnimation(this,
					android.R.anim.fade_in, android.R.anim.fade_out);
			startActivity(intent, options.toBundle());
		} else {
			startActivity(intent);

		}
	}
}
