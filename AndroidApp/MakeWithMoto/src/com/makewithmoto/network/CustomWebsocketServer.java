package com.makewithmoto.network;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class CustomWebsocketServer extends WebSocketServer {
	private static CustomWebsocketServer inst;
	private static int counter = 0;
	private static final String TAG = "WebSocketServer";
	private static Context ctx;
	private List<WebSocket> connections = new ArrayList<WebSocket>();

	// Singleton (one app view, different URLs)
	public static CustomWebsocketServer getInstance(Context aCtx, int port,
			Draft d) throws UnknownHostException {
		if (inst == null) {
			inst = new CustomWebsocketServer(aCtx, port, d);
			inst.start();
		}
		return inst;
	} 
	
	// Singleton (one app view, different URLs)
	public static CustomWebsocketServer getInstance(Context aCtx) throws UnknownHostException {
		if (inst == null) {
		}
		return inst;
	}

	public CustomWebsocketServer(Context aCtx, int port, Draft d)
			throws UnknownHostException {
		super(new InetSocketAddress(port), Collections.singletonList(d));
		ctx = aCtx;
		Log.d(TAG, "Launched websocket server at on port " + aCtx);
	}

	public CustomWebsocketServer(InetSocketAddress address, Draft d) {
		super(address, Collections.singletonList(d));
	}

	@Override
	public void onOpen(WebSocket aConn, ClientHandshake handshake) {
		counter++;
		Log.d(TAG, "New websocket connection " + counter);
		connections.add(aConn);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Log.d(TAG, "closed");
		connections.remove(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Log.d(TAG, "Error:");
		ex.printStackTrace();
	}

	public void send(JSONObject obj) {

		for (WebSocket sock : connections) {
			if (sock.isOpen())
				sock.send(obj.toString());
		}

	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		ALog.d(TAG, "Received message " + message);
		JSONObject json, res;
		try {
			json = new JSONObject(message);
			String type = json.getString("type");
			res = handleMessage(type, json);
		} catch (JSONException e) {
			e.printStackTrace();
			ALog.e(TAG, "Error in handleMessage" + e.toString());
			res = new JSONObject();
			try {
				res = res.put("Error", e.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		conn.send(res.toString());
	}

	/*
	 * Message from webapp
	 */
	public enum MessageType {
		random_info, unknown;
		public static MessageType fromString(String str) {
			try {
				return valueOf(str);
			} catch (Exception e) {
				return unknown;
			}
		}
	}

	// handle message from the webapp
	private JSONObject handleMessage(String type, JSONObject msg)
			throws JSONException {
		JSONObject data = new JSONObject();

		ALog.d(TAG, "handle Message " + msg);

		switch (MessageType.fromString(type)) {
		case random_info:

			break;
		case unknown:

			break;

		default:
			break;
		}
		return data;
	}

}
