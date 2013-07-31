package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.APIAnnotation;
import com.makewithmoto.network.CustomWebsocketServer;

public class JWebApp extends JInterface {

	String TAG = "JWebApp";

	public JWebApp(Activity a) {
        super(a); 
	}

	@JavascriptInterface 
    @APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void addWidget(JSONObject obj) { 
		Log.d(TAG, "added widget ");
		
		try {
			CustomWebsocketServer.getInstance(a.get()).send(obj);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		JSONObject obj = new JSONObject(); 
		try {
			obj.put("type", "plot");
			obj.put("x", 100);
			obj.put("y", 100);
			obj.put("w", 100);
			obj.put("h", 100);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		*/
		//ws.send(obj);
	}
}
