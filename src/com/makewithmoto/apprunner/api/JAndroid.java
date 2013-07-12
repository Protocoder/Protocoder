package com.makewithmoto.apprunner.api;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.makewithmoto.apprunner.MWMActivity;

public class JAndroid extends JInterface {

	public JAndroid(MWMActivity mwmActivity) {
		super(mwmActivity);
	}

	@JavascriptInterface
	public void vibrate(String duration) {
		Log.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) c.get().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));
	}
	
	public void toast(String msg, int duration) { 
		Toast.makeText(c.get(), msg, duration).show();
		
	}

	@JavascriptInterface
	public void sendSMS(String duration) {
	}

	@JavascriptInterface
	public void playSound(String file) {
	}

	@JavascriptInterface
	public void playVideo(String file) {
	}

}
