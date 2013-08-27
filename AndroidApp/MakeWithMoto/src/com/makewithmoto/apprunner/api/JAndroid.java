package com.makewithmoto.apprunner.api;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.utils.Intents;

public class JAndroid extends JInterface {

	private Handler handler;
	ArrayList<Runnable> rl = new ArrayList<Runnable>();
	private String onKeyDownfn;
	private String onKeyUpfn;

	public JAndroid(Activity a) {
		super(a);
		handler = new Handler();

		((AppRunnerActivity) a).addOnKeyListener(new onKeyListener() {

			@Override
			public void onKeyUp(int keyCode) {
				callback(onKeyDownfn, keyCode);
			}

			@Override
			public void onKeyDown(int keyCode) {
				callback(onKeyUpfn, keyCode);
			}
		});
	}

	@JavascriptInterface
	@APIMethod(description = "makes the phone vibrate", example = "android.vibrate(500);")
	public void vibrate(String duration) {
		Log.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) a.get().getSystemService(
				Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));
	}
	
	
	
	@JavascriptInterface
	@APIMethod(description = "Change brightness", example = "ui.button(\"button\"); ")
	public void toast(String text, int duration) {
		Toast.makeText(a.get(), text, duration).show();
	}
	
	
	
	
	@JavascriptInterface
	@APIMethod(description = "Set brightness", example = "ui.button(\"button\"); ")
	public void setBrightness(float val) {
		((AppRunnerActivity) a.get()).setBrightness(val);
	}
	
	
	
	@JavascriptInterface
	@APIMethod(description = "Change brightness", example = "ui.button(\"button\"); ")
	public void getBrightness() {
		((AppRunnerActivity) a.get()).getCurrentBrightness();
	}
	
	
	
	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void screenAlwaysOn() {
		((AppRunnerActivity) a.get()).setScreenAlwaysOn();
	}

	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void openEmailApp(String recepient, String subject, String msg) {
		Intents.sendEmail(a.get(), recepient, subject, msg);
	}
	
	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void openMapApp(double longitude, double latitude) {
		Intents.openMap(a.get(), longitude, latitude);
	}
	
	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void openDial(String msg, int duration) {
		Toast.makeText(a.get(), msg, duration).show();
	}
	
	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void call(String number) {
		Intents.call(a.get(), number);
	}
	
	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void openWebApp(String url) {
		Intents.openWeb(a.get(), url);
	}
	
	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void openWebSearch(String text) {
		Intents.webSearch(a.get(), text);
	}


	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void onKeyDown(final String fn) {
		onKeyDownfn = fn;
	}


	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void onKeyUp(final String fn) {
		onKeyUpfn = fn;
	}

	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")
	public void timer(final int duration, final String fn) {

		Runnable task = new Runnable() {
			@Override
			public void run() {
				// handler.postDelayed(this, duration);
				callback(fn);
				handler.postDelayed(this, duration);
			}
		};

		rl.add(task);
	}


	@JavascriptInterface
	@APIMethod(description = "Shows a small popup with a given text", example = "android.toast(\"hello world!\", 2000);")	
	public void stopAllTimers() {
		Iterator<Runnable> ir = rl.iterator();
		while (ir.hasNext()) {
			handler.removeCallbacks(ir.next());
			// handler.post(ir.next());
		}
	}

	public interface onKeyListener {

		public void onKeyDown(int keyCode);

		public void onKeyUp(int keyCode);

	}

}