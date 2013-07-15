package com.makewithmoto.network;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.projectlist.ProjectManager;
import com.makewithmoto.utils.ALog;
import com.makewithmoto.utils.FileIO;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

public class WebsocketServerConnection implements ServiceConnection {
	private static final String TAG = "WebsocketServerConnection";
	IWebSocketService service;
	WebsocketServerConnection connection;
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder boundService) {
		// TODO Auto-generated method stub
		service = IWebSocketService.Stub.asInterface((IBinder) boundService);
		Log.d(TAG, "onServiceConnected() connected");
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		service = null;
	}
}

//public class CustomWebsocketServer extends WebSocketServer {
//	private static CustomWebsocketServer inst;
//	private static int counter = 0;
//	private static final String TAG = "WebSocketServer";
//	private static Context ctx;
//	private List<WebSocket> connections = new ArrayList<WebSocket>();
//
//	// Singleton (one app view, different URLs)
//	public static CustomWebsocketServer getInstance(int port, Draft d,
//			Context aCtx) throws UnknownHostException {
//		if (inst == null) {
//			inst = new CustomWebsocketServer(port, d, aCtx);
//			inst.start();
//		}
//		return inst;
//	}
//
//	public CustomWebsocketServer(int port, Draft d, Context aCtx)
//			throws UnknownHostException {
//		super(new InetSocketAddress(port), Collections.singletonList(d));
//		ctx = aCtx;
//		Log.d(TAG, "Launched websocket server at on port " + port);
//	}
//
//	public CustomWebsocketServer(InetSocketAddress address, Draft d) {
//		super(address, Collections.singletonList(d));
//	}
//
//	@Override
//	public void onOpen(WebSocket aConn, ClientHandshake handshake) {
//		counter++;
//		System.out.println("///////////Opened connection number" + counter);
//		Log.d(TAG, "New websocket connection");
//		connections.add(aConn);
//		try {
//			EventBus.getDefault().register(this);
//		} catch (EventBusException e) {
//		}
//	}
//
//	@Override
//	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//		System.out.println("closed");
//		connections.remove(conn);
//		EventBus.getDefault().unregister(this);
//	}
//
//	@Override
//	public void onError(WebSocket conn, Exception ex) {
//		System.out.println("Error:");
//		ex.printStackTrace();
//	}
//	
//	public void sendToConnections(String msg) {
//		for (Iterator<WebSocket> iterator = connections.iterator(); iterator.hasNext();) {
//			WebSocket sock = (WebSocket) iterator.next();
//			onMessage(sock, msg);
//		}
//	}
//
//	public void onEventAsync(LogEvent evt) {
//		String msg = evt.getMessage();
//		JSONObject res = new JSONObject();
//		try {
//			res.put("type", "log_event");
//			res.put("tag", evt.getTag());
//			res.put("msg", msg);
//			for (WebSocket sock : connections) {
//				if (sock.isOpen())
//					sock.send(res.toString());
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void onEventAsync(ProjectEvent evt) {
//		Project project = evt.getProject();
//		JSONObject res = new JSONObject();
//
//		if (evt.getAction() == "save_event") {
//			try {
//				res.put("type", "save_event");
//				res.put("name", project.getName());
//				for (WebSocket sock : connections) {
//					if (sock.isOpen())
//						sock.send(res.toString());
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//				ALog.i(e.getMessage());
//			}
//		}
//	}
//
//	@Override
//	public void onMessage(WebSocket conn, String message) {
//		ALog.d(TAG, "Received message " + message);
//		JSONObject json, res;
//		try {
//			json = new JSONObject(message);
//			String type = json.getString("type");
//			res = handleMessage(type, json);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			ALog.e(TAG, "Error in handleMessage" + e.toString());
//			res = new JSONObject();
//			try {
//				res = res.put("Error", e.toString());
//			} catch (JSONException e1) {
//				e1.printStackTrace();
//			}
//		}
//		conn.send(res.toString());
//	}
//
//	public enum MessageType {
//		create_new_project, get_new_code, get_code, get_projects, save_file, run_project, unknown;
//		public static MessageType fromString(String str) {
//			try {
//				return valueOf(str);
//			} catch (Exception e) {
//				return unknown;
//			}
//		}
//	}
//
//	// Helpers
//	private JSONObject handleMessage(String type, JSONObject msg)
//			throws JSONException {
//		JSONObject data = new JSONObject();
//		Project foundProject;
//		String name, newCode;
//
//		ALog.d(TAG, "handle Message " + msg);
//		// Save the callback_id
//		Integer callback_id = msg.getInt("callback_id");
//		data.put("callback_id", callback_id);
//		try {
//			switch (MessageType.fromString(type)) {
//			case create_new_project:
//				String newProjectName = msg.getString("name");
//
//				// create a new project and add to the ui
//				Project newProject = ProjectManager.getInstance()
//						.addNewProject(ctx, newProjectName, newProjectName);
//
//				// tell the webinterface
//				JSONObject newProjectObject = new JSONObject();
//				newProjectObject.put("name", newProject.getName());
//				newProjectObject.put("url", newProject.getUrl());
//				data.put("project", newProjectObject);
//				ALog.i("Creating new project [" + newProjectName + "]");
//				break;
//			case get_new_code:
//				String code = FileIO.readAssetFile(ctx, "assets/new.js");
//				data.put("code", code);
//				break;
//			case get_code:
//				name = msg.getString("name");
//				foundProject = Project.get(name);
//				data.put("code", foundProject.getCode());
//				break;
//			case get_projects:
//				ArrayList<Project> projects = Project.all();
//				JSONArray projectsArray = new JSONArray();
//				for (Project project : projects) {
//					projectsArray.put(project.to_json());
//				}
//				data.put("projects", projectsArray);
//				break;
//			case save_file:
//				newCode = msg.getString("code");
//				name = msg.getString("name");
//				foundProject = Project.get(name);
//				foundProject.writeNewCode(newCode);
//				data.put("project", foundProject.to_json());
//				ALog.i("Saved");
//				break;
//			case run_project:
//				// Save and run
//				name = msg.getString("name");
//				newCode = msg.getString("code");
//				foundProject = Project.get(name);
//				foundProject.writeNewCode(newCode);
//				ProjectEvent evt = new ProjectEvent(foundProject, "run");
//				EventBus.getDefault().post(evt);
//				ALog.i("Running...");
//			default:
//				break;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			ALog.e(TAG, "Error " + e.getMessage());
//			data.put("error", e.toString());
//		}
//		return data;
//	}
//
//	@Override
//	public void onMessage(WebSocket conn, ByteBuffer blob) {
//		conn.send(blob);
//	}
//
//	// @Override
//	public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
//		FrameBuilder builder = (FrameBuilder) frame;
//		builder.setTransferemasked(false);
//		conn.sendFrame(frame);
//	}
//
//	/*
//	 * public static void main( String[] args ) throws UnknownHostException {
//	 * WebSocketImpl.DEBUG = false; int port; try { port = new Integer( args[ 0
//	 * ] ); } catch ( Exception e ) { System.out.println(
//	 * "No port specified. Defaulting to 9003" ); port = 9003; } new
//	 * CustomWebsocketServer( port, new Draft_17() ).start(); }
//	 */
//
//}
