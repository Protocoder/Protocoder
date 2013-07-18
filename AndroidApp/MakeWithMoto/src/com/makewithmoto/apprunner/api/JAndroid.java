package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.makewithmoto.apprunner.JInterface;


public class JAndroid extends JInterface {



		public JAndroid(Activity a) {
          super(a);
		}

		@JavascriptInterface
		public void vibrate(String duration) {
			Log.d("TAG", "vibrate...");
			Vibrator v = (Vibrator) c.get().getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(Integer.parseInt(duration));
		}
		
		@JavascriptInterface
		public void toast(String msg, int duration) { 
			Toast.makeText(c.get(), msg, duration).show();			
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