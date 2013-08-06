package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONException;
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
	public JWebAppPlot addWidget(String name, int x, int y, int w, int h) {
		
		JWebAppPlot jWebAppPlot = new JWebAppPlot(a.get());
		jWebAppPlot.add(name, x, y, w, h);
		
		return jWebAppPlot;
	}
	
	@JavascriptInterface 
	@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void showDashboard(boolean b) {
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
