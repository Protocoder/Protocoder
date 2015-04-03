/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import org.protocoderrunner.AppSettings;
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
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
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
        // FIXME: Because we have no tagging system we need to use the int as mContext
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
