package org.protocoderrunner.network;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.utils.MLog;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import de.greenrobot.event.EventBus;

public class IDEcommunication {

    private String TAG = "IDECommunication";
	private static IDEcommunication inst;
	public WeakReference<Activity> a;
    CustomWebsocketServer ws;

	public IDEcommunication(Activity appActivity) {
		this.a = new WeakReference<Activity>((Activity) appActivity);

        try {
            ws = CustomWebsocketServer.getInstance(a.get());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ws.addListener("protocoderApp", new CustomWebsocketServer.WebSocketListener() {
            @Override
            public void onUpdated(JSONObject jsonObject) {
                try {
                    String type = jsonObject.getString("type");

                    if (type.equals("project_highlight")) {
                        String folder = jsonObject.getString("folder");
                        String name = jsonObject.getString("name");


                        MLog.d(TAG, "selected " + folder + " " + name);

                        Events.SelectedProjectEvent evt = new Events.SelectedProjectEvent(folder, name);
                        EventBus.getDefault().post(evt);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

        ws.send(msg);
	}

}
