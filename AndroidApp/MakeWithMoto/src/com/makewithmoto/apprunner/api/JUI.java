package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.makewithmoto.apidoc.APIAnnotation;


public class JUI extends JInterface {

	LinearLayout mainLayout;
	Boolean isMainLayoutSetup = false;


		public JUI(Activity a) {
          super(a);
        
		}

		private void initializeLayout(){
			if(!isMainLayoutSetup){
			  mainLayout = new LinearLayout(c.get());
			  mainLayout.setOrientation(LinearLayout.VERTICAL);
			  mainLayout.setLayoutParams(new LayoutParams(
			        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			  
			  
			  c.get().setContentView(mainLayout);
			  isMainLayoutSetup = true;
			}
		}
		

		@JavascriptInterface
		@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
		public void button(String label, int x, int y, int w, int h, final String callbackfn){
			initializeLayout();
			
			Button mButton = new Button(c.get());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
			params.leftMargin = x;
			params.topMargin = y;
			mButton.setText(label);

			mButton.setLayoutParams(params);
			
			
			mButton.setOnClickListener(new OnClickListener()
			{
			    @Override
			    public void onClick(View v) {
			        callback(callbackfn);        
			    }
			});
			

			mainLayout.addView(mButton);
		}
		
		public void postLayout(){
			// TODO: Do we even need this??
			//  c.get().setContentView(mainLayout);  
		}
		
		
		
		@JavascriptInterface
		public void seekbar(String label, String min, String max, String progress, boolean circular,
				final String callbackfn){
			
			// Add a seekbar, circular flag to indicate if its a circular seekbar. If the slider position is changed,
			// call the callback function
			
			
		}
		
		@JavascriptInterface
		public void addTextLabel(String label, int x, int y, int w, int h ) {
			//TODO 
			// Adds a text label to display text on screen
		}
		
		public void input(String label, int x, int y, int w, int h, final String callbackfn){
			
			// Adds an edit box. on out of focus calls the callback fn
			
		}
		
		
		@JavascriptInterface
		public void addAToggleButton(final String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {
			/*
			moldableFragment.get().addAToggleButton(label, new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Log.d("addAToggleButton", "is checked: " + isChecked);
					applicationWebView.runJavascript("window['" + callback + "']('"
							+ isChecked + "')");
				}
			});
			*/
			
			//TODO: Add a toggle button and set the initial state as initstate, if the button state changes, call the callbackfn
			// 
		} 
		
		public void checkbox(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn){
			
			// Adds a checkbox and set the initial state as initstate. if the button state changes, call the callbackfn
			
		}
		
		
		public void radiobutton(String label, int x, int y, int w, int h, boolean initstate,  final String callbackfn){
			
			// Adds a radio button and set the initial state as initstate. if the button state changes, call the callbackfn
			
		}
		
		
		public void image(String label, int x, int y, int w, int h, String location){
			
			// Adds an image from a location on the device. Can we also add a url to load the image from the web?
			
		}
		
		public void imagebutton(String label, int x, int y, int w, int h, String location, final String callbackfn){
			
			// Adds an image button.. on click calls callbackfn 	
			
		}
		
       public void background(String label, int x, int y, int w, int h, boolean colimg, String color_location){
			
			// Sets the background image or color. colimg flag indicates if its color or an image
			
		}
		
		
	//	PlotView plotView;
	//	Plot plot;
		
		@JavascriptInterface
		public void addPlot(final String callback) {
	//		plotView = moldableFragment.get().addPlot();
	//		plot = plotView.new Plot(Color.RED);
	//		plotView.addPlot(plot);
		} 

		@JavascriptInterface 
		public void addPlotValue(float value) { 
	//		plotView.setValue(plot, value);
		}

		@JavascriptInterface
		public void showToast(final String text, final String duration) {
	//		moldableFragment.get().showToast(text, duration);
		}

		@JavascriptInterface
		public void startTrackingTouches(String b) {
		}
}