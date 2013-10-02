package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.network.CustomWebsocketServer;
import com.makewithmoto.network.CustomWebsocketServer.WebSocketListener;
import com.makewithmoto.utils.StrUtils;

public class JDashboard extends JInterface {

	String TAG = "JDashboard";

	public JDashboard(Activity a) {
        super(a); 
	}

	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public JWebAppPlot addPlot(String name, int x, int y, int w, int h) {
		
		JWebAppPlot jWebAppPlot = new JWebAppPlot(a.get());
		jWebAppPlot.add(StrUtils.generateRandomString(), name, x, y, w, h);
		
		return jWebAppPlot;
	}
	
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public JWebAppHTML addHTML(String html, int posx, int posy) throws UnknownHostException {
		
		JWebAppHTML jWebAppHTML = new JWebAppHTML(a.get());
		jWebAppHTML.add(StrUtils.generateRandomString(), html, posx, posy);
		
		
		return jWebAppHTML;
	}
	
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public JWebAppButton addButton(String name, int x, int y, int w, int h, final String callbackfn) throws UnknownHostException {
		Log.d(TAG, "callback " + callbackfn);
		
		String id = StrUtils.generateRandomString();
		JWebAppButton jWebAppButton = new JWebAppButton(a.get());
		jWebAppButton.add(id, name, x, y, w, h);
		
		CustomWebsocketServer.getInstance(a.get()).addListener(id, new WebSocketListener() {
			
			@Override
			public void onUpdated(JSONObject jsonObject) {
				callback(callbackfn);
			}
		});
		

		return jWebAppButton;
	}
	
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public JWebAppLabel addLabel(String name, int x, int y, int size, String color) {
		String id = StrUtils.generateRandomString();

		JWebAppLabel jWebAppLabel = new JWebAppLabel(a.get());
		jWebAppLabel.add(id, name, x, y, size, color);
				
		return jWebAppLabel;
	}
	
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public JWebAppImage addImage(String url, int x, int y, int w, int h) {
		String id = StrUtils.generateRandomString();

		JWebAppImage jWebAppImage = new JWebAppImage(a.get());
		jWebAppImage.add(id, url, x, y, w, h);
		
		return jWebAppImage;
	}
	
	
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void setBackgroundColor(int r, int g, int b, float alpha) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "setBackgroundColor");
			
			JSONObject values = new JSONObject();
			values.put("r", r);
			values.put("g", g);
			values.put("b", b);
			values.put("alpha", alpha);
			msg.put("values", values);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		
	}
	
	@JavascriptInterface 
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void show(boolean b) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "showDashboard");

			JSONObject values = new JSONObject();
			values.put("val", b);
			msg.put("values", values);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		
	}
}
