package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.makewithmoto.apidoc.APIAnnotation;

public class JAndroid extends JInterface {

	public JAndroid(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIAnnotation(description = "makes the phone vibrate", example = "android.vibrate(500);")
	public void vibrate(String duration) {
		Log.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) a.get().getSystemService(
				Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));
	}

	@JavascriptInterface
	@APIAnnotation(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void toast(String msg, int duration) {
		Toast.makeText(a.get(), msg, duration).show();
	}

		
	@JavascriptInterface
	public void vibrate_and_callback(String duration, String fn){
			vibrate(duration);
			callback(fn);
	}

	@JavascriptInterface
	public void toast_and_callback(String msg, int duration, String fn){
			toast(msg, duration);
			callback(fn);
	}
			

}