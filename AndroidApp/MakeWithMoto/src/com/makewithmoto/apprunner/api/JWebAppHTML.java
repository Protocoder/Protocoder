package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.makewithmoto.network.CustomWebsocketServer;

public class JWebAppHTML extends JInterface {


	private static final String TAG = "JWebAppImage";
	String id; 
	
	public JWebAppHTML(Activity a) {
		super(a);
	}
	

	public void add(String id, String html, int posx, int posy) { 
		this.id = id;
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "add");

			JSONObject values = new JSONObject();
			values.put("id", id);
			values.put("type", "html");
			values.put("x", posx);
			values.put("y", posy);
			values.put("html", html);

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
