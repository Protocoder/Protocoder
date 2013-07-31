package com.makewithmoto.apprunner.api;

import java.lang.ref.WeakReference;

import android.app.Activity;

import com.makewithmoto.apidoc.APIManager;
import com.makewithmoto.apprunner.AppRunnerActivity;


public class JInterface {
	
	protected static final String TAG = "JSInterface";
	public WeakReference<AppRunnerActivity> c;
	

	public JInterface(Activity appActivity) {
		super();
		this.c = new WeakReference<AppRunnerActivity>((AppRunnerActivity) appActivity); 
		APIManager.getInstance().addClass(this.getClass());

	}
	

	public <T> void callback(String fn, T...args){
          
	    try{
		       //  c.get().interpreter.callJsFunction(fn,"");
		       String f1 = fn;
		       boolean firstarg = true;
		       if(fn.contains("function")){
		           f1 = "var fn = " + fn + "\n fn(";
		           for(T t : args){
		        	   if(firstarg){
		        		   firstarg = false;
		        	   }else{
		        		   f1 = f1 + ",";
		        	   }
		        	   
		        	   f1 = f1 + t;
		           }
		           
		           f1=f1+");";
		       }
		
		       c.get().eval(f1);
		
		   }catch (Throwable e){
	
		                         // TODO
		   }
		              
	}
		      
		
	public void destroy() {
	}

}
