package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.makewithmoto.network.CustomWebsocketServer;

public class JWebAppImage extends JInterface {


	private static final String TAG = "JWebAppImage";
	String id; 
	
	public JWebAppImage(Activity a) {
		super(a);
	}
	

	public void add(String id, String url, int x, int y, int w, int h) { 
		this.id = id;
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "add");

			JSONObject values = new JSONObject();
			values.put("id", id);
			values.put("url", url);
			values.put("type", "image");
			values.put("x", x);
			values.put("y", y);
			values.put("w", w);
			values.put("h", h);

			msg.put("values", values);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		Log.d(TAG, "added widget ");

		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
	}
	
	
	public void changeImage(String url) { 
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "changeImage");

			JSONObject values = new JSONObject();
			values.put("id", id);
			values.put("type", "label");
			values.put("url", url);
			msg.put("values", values);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		Log.d(TAG, "update");

		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
	}
}
