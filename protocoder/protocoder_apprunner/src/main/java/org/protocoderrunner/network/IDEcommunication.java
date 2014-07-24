package org.protocoderrunner.network;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class IDEcommunication {

	private static IDEcommunication inst;
	public WeakReference<Activity> a;

	public IDEcommunication(Activity appActivity) {
		this.a = new WeakReference<Activity>((Activity) appActivity);
	}

	// Singleton (one app view, different URLs)
	public static IDEcommunication getInstance(Activity a) {
		if (inst == null) {
			inst = new IDEcommunication(a);
		}
		return inst;
	}

	public void ready(boolean r) {

		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "ide");
			msg.put("action", "ready");

			JSONObject values = new JSONObject();

			values.put("ready", r);
			msg.put("values", values);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
