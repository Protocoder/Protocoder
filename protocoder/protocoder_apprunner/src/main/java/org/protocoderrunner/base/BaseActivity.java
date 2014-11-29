/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoderrunner.base;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.protocoderrunner.AppSettings;
import org.protocoderrunner.R;
import org.protocoderrunner.media.Audio;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class BaseActivity extends ActionBarActivity {

	private static final String TAG = "BaseActivity";
	public boolean actionBarAllowed = true;
	private boolean lightsOutMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if (AppSettings.FULLSCREEN) {
		// setFullScreen();
		// }
		//
		// if (AppSettings.HIDE_HOME_BAR) {
		// setHideHomeBar();
		// }
		//
		// if (AppSettings.SCREEN_ALWAYS_ON) {
		// setScreenAlwaysOn();
		// }
		//
		// setVolume(100);
		// setBrightness(1f);

	}

    public Point getScrenSize() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

	public void setFullScreen() {
		actionBarAllowed = true;
		// activity in full screen
		//supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void setImmersive() {
		actionBarAllowed = false;
        getSupportActionBar().hide();
		// activity in full screen
		//supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		// requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	public void showHomeBar(boolean b) {

        if (Build.VERSION.SDK_INT > AppSettings.MIN_SUPPORTED_VERSION) {

            if (b == true) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            }
        }
	}

	public void lightsOutMode() {
		lightsOutMode = true;
		final View rootView = getWindow().getDecorView();
		rootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
		rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

		rootView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				MLog.d(TAG, "" + visibility);
				rootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
				rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
			}
		});
	}

	public void setScreenAlwaysOn(boolean b) {
		if (b) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	public boolean isScreenOn() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();

	}

	public void goToSleep() {
		//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//pm.goToSleep(100);
	}

	public boolean isAirplaneMode() {
		return Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
	}

	public boolean isUSBMassStorageEnabled() {
		return Settings.System.getInt(getContentResolver(), Settings.Global.USB_MASS_STORAGE_ENABLED, 0) != 0;
	}

	public boolean isADBEnabled() {
		return Settings.System.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0;
	}

	public void setEnableSoundEffects(boolean b) {
		if (b) {
			Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
		} else {
			Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);

		}
	}

	public void changeFragment(int id, Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.replace(id, fragment);
		fragmentTransaction.commit();
	}

	public void addFragment(Fragment fragment, int fragmentPosition, String tag, boolean addToBackStack) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.add(fragmentPosition, fragment, tag);
		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	public void addFragment(Fragment fragment, int fragmentPosition, boolean addToBackStack) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// FIXME: Because we have no tagging system we need to use the int as a
		// tag, which may cause collisions
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.add(fragmentPosition, fragment, String.valueOf(fragmentPosition));
		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	public void removeFragment(Fragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.remove(fragment);
		ft.commit();
	}

	public boolean isTablet() {
		boolean xlarge = ((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

    public boolean isWear() {
        boolean b = false;
        //b = getResources().getBoolean(R.bool.isWatch);

        return b;
    }

	public void setBrightness(float f) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		layoutParams.screenBrightness = f;
		getWindow().setAttributes(layoutParams);
	}

	public float getCurrentBrightness() {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

		return layoutParams.screenBrightness;
	}

	public void setVolume(int value) {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float val = (float) (value / 100.0 * maxValue);

		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(val),
				AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}

	PowerManager.WakeLock wl;

	public void setWakeLock(boolean b) {

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (wl == null) {
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			if (b) {
				wl.acquire();
			}
		} else {
			if (!b) {
				wl.release();
			}
		}

	}

	public void setGlobalBrightness(int brightness) {

		// constrain the value of brightness
		if (brightness < 0) {
			brightness = 0;
		} else if (brightness > 255) {
			brightness = 255;
		}

		ContentResolver cResolver = this.getApplicationContext().getContentResolver();
		Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);

	}

	public void setScreenTimeout(int time) {
		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
	}

	// override home buttons
	@Override
	public void onAttachedToWindow() {
		if (AppSettings.OVERRIDE_HOME_BUTTONS) {
			//this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
			super.onAttachedToWindow();
		}
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Audio.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (String _string : matches) {
				MLog.d(TAG, "" + _string);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// override volume buttons
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		MLog.d(TAG, "" + keyCode);

		if (AppSettings.OVERRIDE_VOLUME_BUTTONS
				&& (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {

			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && AppSettings.CLOSE_WITH_BACK) {
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
