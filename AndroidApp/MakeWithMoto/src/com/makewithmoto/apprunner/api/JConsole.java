package com.makewithmoto.apprunner.api;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.network.CustomWebsocketServer;

public class JConsole  extends JInterface {
	
	public JConsole(FragmentActivity mwmActivity) {
		super(mwmActivity);
		//EventBus.getDefault().register(this);
	}

	@JavascriptInterface
	public void log(String output) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "console");
			msg.put("action", "log");

			JSONObject values = new JSONObject();;
			values.put("val", output);
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
