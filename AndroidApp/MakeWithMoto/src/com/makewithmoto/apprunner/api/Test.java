package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class Test extends JInterface {

	public Test(Activity a) {
		super(a);
	}

	public void vibrate(String duration) {
		Log.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) c.get().getSystemService(
				Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));
	}

	public void toast(String msg, int duration) {
		Toast.makeText(c.get(), msg, duration).show();
	}

	public void vibrate_and_callback(String duration, String fn) {
		vibrate(duration);
		callback(fn);
	}

	public void toast_and_callback(String msg, int duration, String fn) {
		toast(msg, duration);
		callback(fn);
	}

}