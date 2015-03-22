/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoderrunner.network;

import android.content.Context;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.utils.MLog;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomWebsocketServer extends WebSocketServer {
    private static CustomWebsocketServer inst;
    private static int counter = 0;
    private static final String TAG = "WebSocketServer";
    private static Context ctx;
    private final List<WebSocket> connections = new ArrayList<WebSocket>();
    private static HashMap<String, WebSocketListener> listeners = new HashMap<String, WebSocketListener>();
    ;

    public interface WebSocketListener {

        public void onUpdated(JSONObject jsonObject);

    }

    // Singleton (one app view, different URLs)
    public static CustomWebsocketServer getInstance(Context aCtx, int port, Draft d) throws UnknownHostException {
        if (inst == null) {
            inst = new CustomWebsocketServer(aCtx, port, d);
            inst.start();
        }
        return inst;
    }

    // Singleton (one app view, different URLs)
    public static CustomWebsocketServer getInstance(Context aCtx) throws UnknownHostException {
        if (inst == null) {
            inst = new CustomWebsocketServer(aCtx, AppSettings.WEBSOCKET_PORT, new Draft_17());
            inst.start();
        }
        return inst;
    }

    public CustomWebsocketServer(Context aCtx, int port, Draft d) throws UnknownHostException {
        super(new InetSocketAddress(port), Collections.singletonList(d));
        ctx = aCtx;
        MLog.d(TAG, "Launched websocket server at on port " + aCtx);
    }

    public CustomWebsocketServer(InetSocketAddress address, Draft d) {
        super(address, Collections.singletonList(d));
    }

    @Override
    public void onOpen(WebSocket aConn, ClientHandshake handshake) {
        counter++;
        MLog.d(TAG, "New websocket connection " + counter);
        connections.add(aConn);

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        MLog.d(TAG, "closed");
        connections.remove(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        MLog.d(TAG, "Error:");
        ex.printStackTrace();
    }

    public void send(JSONObject obj) {

        for (WebSocket sock : connections) {
            if (sock.isOpen()) {
                sock.send(obj.toString());
            }
        }

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //MLog.d(TAG, "Received message " + message);
        JSONObject json, res;
        try {
            json = new JSONObject(message);
            String type = json.getString("type");
            res = handleMessage(type, json);
        } catch (JSONException e) {
            e.printStackTrace();
            MLog.e(TAG, "Error in handleMessage" + e.toString());
            res = new JSONObject();
            try {
                res = res.put("Error", e.toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        conn.send(res.toString());
    }

    public void addListener(String name, WebSocketListener l) {
        listeners.put(name, l);

    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void send(String type, String action, String... values) {

        // send device ip address
        /*
		 * JSONObject msg = new JSONObject(); try { msg.put("type", "device");
		 * msg.put("action", "info");
		 * 
		 * JSONObject values = new JSONObject();; values.put("address_ip",
		 * NetworkUtils.getLocalIpAddress().toString());
		 * values.put("address_port", AppSettings.HTTP_PORT);
		 * values.put("device_name", android.os.Build.MANUFACTURER +
		 * android.os.Build.PRODUCT + " " + Build.MODEL); msg.put("values",
		 * values);
		 * 
		 * } catch (JSONException e1) { e1.printStackTrace(); }
		 * 
		 * try { CustomWebsocketServer ws =
		 * CustomWebsocketServer.getInstance(this); ws.send(msg); } catch
		 * (UnknownHostException e) { e.printStackTrace(); }
		 */

    }

    // handle message from the webapp
    private JSONObject handleMessage(String type, JSONObject msg) throws JSONException {
        JSONObject data = new JSONObject();
        WebSocketListener l = listeners.get(msg.get("id"));
        l.onUpdated(msg);

        return data;
    }

}
