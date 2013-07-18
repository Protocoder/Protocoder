package com.makewithmoto.apprunner;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class JAndroid extends JInterface {



		public JAndroid(Activity a) {
          super(a);
		}

		
		public void vibrate(String duration) {
			Log.d("TAG", "vibrate...");
			Vibrator v = (Vibrator) c.get().getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(Integer.parseInt(duration));
		}
		
		public void toast(String msg, int duration) { 
			Toast.makeText(c.get(), msg, duration).show();			
		}
		
		
		public void vibrate_and_callback(String duration, String fn){
			vibrate(duration);
			callback(fn);
		}

		public void toast_and_callback(String msg, int duration, String fn){
			toast(msg, duration);
			callback(fn);
		}
		
		public void button(int x, int y, int w, int h, String fn){
			
			Button button = new Button(c.get());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
			params.leftMargin = x;
			params.topMargin = y;

			button.setLayoutParams(params);
			
			LinearLayout mainLayout = new LinearLayout(c.get());
			    mainLayout.setOrientation(LinearLayout.VERTICAL);
			    mainLayout.setLayoutParams(new LayoutParams(
			        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			    mainLayout.addView(button);

			    c.get().setContentView(mainLayout);  
		}
		
		
}