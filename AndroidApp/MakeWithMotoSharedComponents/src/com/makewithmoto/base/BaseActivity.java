package com.makewithmoto.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (AppSettings.fullscreen) {
            setFullScreen();
        }

        if (AppSettings.hideHomeBar) {
            setHideHomeBar();
        }

        if (AppSettings.screenAlwaysOn) {
            setScreenAlwaysOn();
        }

        //setVolume(100);
        //setBrightness(1f);
        // Utils.playSound("http://outside.mediawerf.net/8-Light_2.mp3");
        // playSound("http://outside.mediawerf.net/music.ogg");
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/
    }

    protected void setFullScreen() {
        // activity in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void setHideHomeBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    public void setScreenAlwaysOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void changeFragment(int id, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    public void addFragment(Fragment fragment, int fragmentPosition, String tag, boolean addToBackStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragmentPosition, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void addFragment(Fragment fragment, int fragmentPosition, boolean addToBackStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //FIXME: Because we have no tagging system we need to use the int as a tag, which may cause collisions
        ft.add(fragmentPosition, fragment, String.valueOf(fragmentPosition));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void removeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    private void setBrightness(float f) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = f;
        getWindow().setAttributes(layoutParams);
    }

    public void setVolume(int value) {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * value / 100;

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }

    public void setWakeLock(boolean b) {

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");

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

    // override volume buttons
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d(TAG, "" + keyCode);

        if (AppSettings.overrideVolumeButtons && (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {

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
