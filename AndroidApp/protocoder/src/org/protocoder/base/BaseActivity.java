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

package org.protocoder.base;

import java.util.ArrayList;

import org.protocoder.media.Audio;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity {

	private static final String TAG = "BaseActivity";
	public int screenWidth;
	public int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if (AppSettings.fullscreen) {
		// setFullScreen();
		// }
		//
		// if (AppSettings.hideHomeBar) {
		// setHideHomeBar();
		// }
		//
		// if (AppSettings.screenAlwaysOn) {
		// setScreenAlwaysOn();
		// }
		//
		// setVolume(100);
		// setBrightness(1f);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		screenWidth = size.x;
		screenHeight = size.y;
	}

	public void setFullScreen() {
		// activity in full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	
	
	public void setImmersive() {
		// activity in full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
				View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
	
	

	public void showHomeBar(boolean b) {

		if (b == true) {
			if (Build.VERSION.SDK_INT > AppSettings.CURRENT_VERSION) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_VISIBLE);
			} else {
				
			}

		} else {
			if (Build.VERSION.SDK_INT > AppSettings.CURRENT_VERSION) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
			} else {

			}
		}
	}

	public void setScreenAlwaysOn() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void changeFragment(int id, Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		fragmentTransaction.replace(id, fragment);
		fragmentTransaction.commit();
	}

	public void addFragment(Fragment fragment, int fragmentPosition,
			String tag, boolean addToBackStack) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		ft.add(fragmentPosition, fragment, tag);
		//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	public void addFragment(Fragment fragment, int fragmentPosition,
			boolean addToBackStack) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// FIXME: Because we have no tagging system we need to use the int as a
		// tag, which may cause collisions
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		ft.add(fragmentPosition, fragment, String.valueOf(fragmentPosition));
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	public void removeFragment(Fragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		ft.remove(fragment);
		ft.commit();
	}

	public boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

	public void setBrightness(float f) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = f;
		getWindow().setAttributes(layoutParams);
	}

	public float getCurrentBrightness() {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

		return layoutParams.screenBrightness;
	}

	public void setVolume(int value) {

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * value
				/ 100;

		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value,
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

	}

	public void setWakeLock(boolean b) {

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "My Tag");

		if (b) {
			wl.acquire();
		} else {
			wl.release();
		}

	}

	// override home buttons
	@Override
	public void onAttachedToWindow() {
		if (AppSettings.overrideHomeButtons) {
			this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
			super.onAttachedToWindow();
		}
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Audio.VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (String _string : matches) {
				Log.d(TAG, "" + _string);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// override volume buttons
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Log.d(TAG, "" + keyCode);

		if (AppSettings.overrideVolumeButtons
				&& (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {

			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && AppSettings.closeWithBack) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void superMegaForceKill() {
		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	protected void onResume() {
		System.gc();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.gc();
	}

}
