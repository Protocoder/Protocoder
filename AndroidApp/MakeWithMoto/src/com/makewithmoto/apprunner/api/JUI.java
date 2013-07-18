package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.makewithmoto.apprunner.JInterface;


public class JUI extends JInterface {

	LinearLayout mainLayout;


		public JUI(Activity a) {
          super(a);
          mainLayout = new LinearLayout(c.get());
		  mainLayout.setOrientation(LinearLayout.VERTICAL);
		  mainLayout.setLayoutParams(new LayoutParams(
		        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		  
		  
		  c.get().setContentView(mainLayout);  
		}

		
		public void button(String label, int x, int y, int w, int h, final String fn){
			
			Button button = new Button(c.get());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
			params.leftMargin = x;
			params.topMargin = y;
			button.setText(label);

			button.setLayoutParams(params);
			
			
			button.setOnClickListener(new OnClickListener()
			{
			     @Override
			     public void onClick(View v) {
			           callback(fn);
			     }
			});

			mainLayout.addView(button);
		}
		
		public void postLayout(){
			//  c.get().setContentView(mainLayout);  
		}
		
		
}