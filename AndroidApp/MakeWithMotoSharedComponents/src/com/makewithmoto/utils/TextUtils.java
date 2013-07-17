package com.makewithmoto.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class TextUtils {
	public static void changeFont(Context c, int id, View parentView, String fontName) { 	
		TextView txt = (TextView) parentView.findViewById(id);

		//Typeface font = Typeface.createFromAsset(c.getAssets(), "brownproregular.otf");  
		Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);  
		txt.setTypeface(font);  
	} 
	
	public static void changeFont(Context c, View txt, String fontName) { 	
		
		//Typeface font = Typeface.createFromAsset(c.getAssets(), "brownproregular.otf");  
		Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);  
		((TextView) txt).setTypeface(font);  
	} 

	public static void changeFont(Activity activity, int id, String fontName) { 	
		TextView txt = (TextView) activity.findViewById(id);
		
		//Typeface font = Typeface.createFromAsset(c.getAssets(), "brownproregular.otf");  
		Typeface font = Typeface.createFromAsset(activity.getAssets(), fontName);  
		txt.setTypeface(font);  
	} 
	
	public static void changeFont(Context c, TextView txt, String fontName) { 	

		Typeface font = Typeface.createFromAsset(c.getAssets(), fontName);  
		txt.setTypeface(font);  
	} 
	
	
}