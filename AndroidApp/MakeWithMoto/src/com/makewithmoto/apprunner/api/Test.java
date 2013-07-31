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
		Vibrator v = (Vibrator) a.get().getSystemService(
				Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));
	}

	public void toast(String msg, int duration) {
		Toast.makeText(a.get(), msg, duration).show();
	}


}