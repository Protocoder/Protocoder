package org.protocoder.server;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoder.events.Events;

import java.lang.ref.WeakReference;

public class IDEcommunication {

    private String TAG = "IDECommunication";
    private static IDEcommunication inst;
    public WeakReference<Context> a;
    ProtocoderWebsocketServer ws = null;

    public IDEcommunication(Context appActivity) {
        this.a = new WeakReference<>(appActivity);

        /*
        try {
            ws = ProtocoderWebsocketServer.getInstance(a.get());
            MLog.d(TAG, "websocket started");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            MLog.d(TAG, "websocket :(");
        }
        */

        ws.addListener("protocoderApp", new ProtocoderWebsocketServer.WebSocketListener() {
            @Override
            public void onUpdated(JSONObject jsonObject) {
                try {
                    String type = jsonObject.getString("type");

                    if (type.equals("project_highlight")) {
                        String folder = jsonObject.getString("folder");
                        String name = jsonObject.getString("name");

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
    public static IDEcommunication getInstance(Context a) {
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

        ws.send(msg.toString());
    }

    public void sendCustomJs(String jsString) {

        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "ide");
            msg.put("action", "customjs");

            JSONObject values = new JSONObject();
            values.put("val", jsString);

            msg.put("values", values);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        ws.send(msg.toString());
    }

    public void send(JSONObject obj) {
        ws.send(obj.toString());
    }

}
