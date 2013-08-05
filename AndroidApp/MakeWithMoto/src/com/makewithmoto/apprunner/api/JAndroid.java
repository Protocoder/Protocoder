package com.makewithmoto.apprunner.api;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.makewithmoto.apidoc.APIAnnotation;
import com.makewithmoto.apprunner.AppRunnerActivity;

public class JAndroid extends JInterface {
	
	private static JAndroid inst;
	private Handler  handler;
	ArrayList<Runnable> rl = new ArrayList<Runnable>();

	
	// Singleton (one app view, different URLs)
	public static JAndroid getInstance(Context aCtx) {
		if (inst == null) {
		}
		return inst;
	}

	public JAndroid(Activity a) {
		super(a);
		handler = new Handler();
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

	
	public void onKeyPressed(final String fn) { 
		//((AppRunnerActivity) a.get()).onKeyDown(keyCode, event)
		
	}
	
	@JavascriptInterface
	public void timer(final int duration, final String fn){
		
		Runnable task = new Runnable() {
		    @Override
		    public void run() {
		    	//handler.postDelayed(this, duration);
		    	callback(fn);
		        handler.postDelayed(this, duration);
		    }
		};
		
		rl.add(task);
	}
	
	
	@JavascriptInterface
	public void stopAllTimers(){
		 Iterator<Runnable> ir = rl.iterator();
	        while (ir.hasNext()) {
	            handler.removeCallbacks(ir.next());
	            //handler.post(ir.next());
	        }
	}

	public void onKeyDown(int keyCode, KeyEvent event) {

		
	}

}