package com.makewithmoto.network;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.makewithmoto.events.Project;
import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.utils.FileIO;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class WebSocketService extends Service {
	private static CustomWebsocketServer inst;
	protected static final String TAG = "WebSocketService";
	private static List<WebSocket> connections = new ArrayList<WebSocket>();
	private List<Runnable> callbacks = new ArrayList<Runnable>();
	private static int counter = 0;
	private WeakReference<Context> _context;
	
    // Binder given to clients
    //private final IBinder mBinder = new WebSocketBinder();
	private final IWebSocketService.Stub mBinder = new IWebSocketService.Stub() {
		@Override
		public void sendToSockets(String msg) throws RemoteException {
			for (Iterator<WebSocket> iterator = connections.iterator(); iterator.hasNext();) {
				WebSocket sock = (WebSocket) iterator.next();
				sock.send(msg);
			}
		}
		
		public WebSocketService getService() {
			return WebSocketService.this;
		}

		@Override
		public void start() throws RemoteException {
			Log.d(TAG, "onCreate called for WebSocketService");
			try {
				if (inst == null) {
					inst = CustomWebsocketServer.getInstance(8081, new Draft_17(), getService());
					Log.d(TAG, "PORT: " + inst.getPort());
				}
			} catch (Exception e) {
				Log.e(TAG, "UnknownHost: ");
				e.printStackTrace();
			}
		}

		@Override
		public void stop() throws RemoteException {
			inst.stop();
		}

		@Override
		public void logToSockets(String tag, String msg) throws RemoteException {
			//EventBus.getDefault().post(new LogEvent(tag, msg));
			JSONObject res = new JSONObject();
			try {
				res.put("type", "log_event");
				res.put("tag", tag);
				res.put("msg", msg);
				sendToSockets(res.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void sendJsonToSockets(String type, String json) throws RemoteException {
	        json = json.replaceAll("\n", "\\n");
			try {
				JSONObject res = new JSONObject(json);
				res.put("type", type);
				res.put("msg", json);
				sendToSockets(res.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
    
    public static class CustomWebsocketServer extends WebSocketServer {
		
		// Singleton (one app view, different URLs)
		public static CustomWebsocketServer getInstance(int port, Draft d, Context aCtx) throws UnknownHostException {
			if (inst == null) {
				inst = new CustomWebsocketServer(port, d, aCtx);
				inst.start();
			}
			return inst;
		}
		
		public CustomWebsocketServer(int port, Draft d, Context aCtx)
				throws UnknownHostException {
			super(new InetSocketAddress(port), Collections.singletonList(d));
			Log.d(TAG, "Launched websocket server at on port " + port);
			try {
				EventBus.getDefault().register(this);
				Log.d(TAG, "onEventBus registered");
			} catch (EventBusException e) {
				Log.d(TAG, "Event bus exception");
				e.printStackTrace();
			}
		}

		public CustomWebsocketServer(InetSocketAddress address, Draft d) {
			super(address, Collections.singletonList(d));
		}
		
		@Override
		public void stop() {
			EventBus.getDefault().unregister(this);
			try {
				super.stop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onClose(WebSocket conn, int code, String reason, boolean remote) {
			System.out.println("closed");
			connections.remove(conn);
		}
		
		public void logToConnections(String tag, String msg) {
			JSONObject res = new JSONObject();
			try {
				res.put("type", "log_event");
				res.put("tag", tag);
				res.put("msg", msg);
				sendToConnections(res.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void sendToConnections(String msg) {
			for (WebSocket sock: connections){
				if (sock.isOpen())
					sock.send(msg.toString());
			}
		}
		
		public void onEventAsync(LogEvent evt) {
			Log.d(TAG, "LogEvent onEventAsync " + evt.getMessage() + " (" + evt.getTag() + ")");
			String msg = evt.getMessage();
			JSONObject res = new JSONObject();
			logToConnections(evt.getTag(), msg);
		}
		
		public void onEventAsync(ProjectEvent evt) {
			Project project = evt.getProject();
			JSONObject res = new JSONObject();
			
			if (evt.getAction() == "save_event") {
				EventBus.getDefault().post(new LogEvent("info", "Saved " + project.getName()));
			}
		}

		@Override
		public void onError(WebSocket conn, Exception ext) {
			// TODO Auto-generated method stub
			System.out.println("ERROR: ");
			ext.printStackTrace();
		}

		@Override
		public void onMessage(WebSocket conn, String message) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Received message " + message);
			JSONObject json, res;
			try {
				json = new JSONObject(message);
				String type = json.getString("type");
				res = handleMessage(type, json);
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e(TAG, "Error in handleMessage" + e.toString());
				res = new JSONObject();
				try {
					res = res.put("Error", e.toString());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
			conn.send(res.toString());
		}
		
		


		// Helpers
		/**
		 * This should NOT be here. Move this to an appropriate place
		 */
		private JSONObject handleMessage(String type, JSONObject msg)
				throws JSONException {
			JSONObject data = new JSONObject();
			Project foundProject;
			String name, newCode;

			Log.d(TAG, "handle Message " + msg + " type: " + type);
			// Save the callback_id
			try {
				Integer callback_id = msg.getInt("callback_id");
				data.put("callback_id", callback_id);
			} catch(JSONException ex) {
				Log.e(TAG, "ERROR: ");
				ex.printStackTrace();
			}
			try {
				switch (MessageType.fromString(type)) {
				case create_new_project:
					String newProjectName = msg.getString("name");

					// create a new project and add to the ui
					Project newProject = createNewProject(newProjectName, newProjectName);
					
					// tell the webinterface
					JSONObject newProjectObject = new JSONObject();
					newProjectObject.put("name", newProject.getName());
					newProjectObject.put("url", newProject.getUrl());
					data.put("project", newProjectObject);
					logToConnections("info", "Creating new project [" + newProjectName + "]");
					break;
//				case get_new_code:
//					String code = FileIO.readAssetFile(ctx, "assets/new.js");
//					data.put("code", code);
//					break;
				case get_code:
					name = msg.getString("name");
					logToConnections("debug", "Looking up code for " + name);
					foundProject = Project.get(name);
					data.put("code", foundProject.getCode());
					break;
				case get_projects:
					ArrayList<Project> projects = Project.all();
					JSONArray projectsArray = new JSONArray();
					for (Project project : projects) {
						logToConnections("debug", "Found project " + project.getName());
						projectsArray.put(project.to_json());
					}
					data.put("projects", projectsArray);
					break;
				case save_file:
					newCode = msg.getString("code");
					name = msg.getString("name");
					foundProject = Project.get(name);
					foundProject.writeNewCode(newCode);
					data.put("project", foundProject.to_json());
					foundProject.writeNewCode(newCode);
					
					ProjectEvent evtSave = new ProjectEvent(foundProject, "save");
					EventBus.getDefault().post(evtSave);

					logToConnections("info", "Saved project " + name);
					break;
				case run_project:
					// Save and run
					name = msg.getString("name");
					foundProject = Project.get(name);
					try {
						newCode = msg.getString("code");
						if (newCode != null) {
							foundProject.writeNewCode(newCode);
						}
					} catch (JSONException e) {
						// No code associated with this project
					}
					ProjectEvent evt = new ProjectEvent(foundProject, name, "run");
					Log.d(TAG, "run_project event: " + name);
					EventBus.getDefault().post(evt);
					logToConnections("info", "Running project " + name);
					break;
				default:
					Log.d(TAG, "Uhandled event type: " + type);
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				logToConnections("debug", "{error: '" + e.getMessage() + '}');
				data.put("error", e.toString());
			}
			return data;
		}
		
		// Create a new project
		public Project createNewProject(String newProjectName, String fileName) {
			String newTemplateCode = "function setup() {\n" +
				"// This is your initialization code\n" +
				"// Setup your interface here\n" +
			"}\n" +
			"function loop() {\n" + 
			"// This gets called for every run\n" +
			"// return true in this function to continue\n" +
			"// to run, and return false to stop/quit\n" +
			"}\n";
			String file = FileIO.writeStringToFile(newProjectName, newTemplateCode);
			Project newProject = new Project(newProjectName, fileName); 
			return newProject;
		}
		
		@Override
		public void onMessage(WebSocket conn, ByteBuffer blob) {
			conn.send(blob);
		}
		
		public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
			FrameBuilder builder = (FrameBuilder) frame;
			builder.setTransferemasked(false);
			conn.sendFrame(frame);
		}
		
		@Override
		public void onOpen(WebSocket aConn, ClientHandshake arg1) {
			counter++;
			System.out.println("///////////Opened connection number" + counter);
			Log.d(TAG, "New websocket connection");
			connections.add(aConn);
		}
    }

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
//		try {
//			EventBus.getDefault().register(this);
//			Log.d(TAG, "onEventBus registered");
//		} catch (EventBusException e) {
//			Log.d(TAG, "Event bus exception");
//			e.printStackTrace();
//		}
	}
	
	public void start(Intent in) {
		Log.d(TAG, "START WITH INTENT: " + in.toString());
		//startService(in);
		//bindService(in, null, BIND_AUTO_CREATE);
	}
	
	public void setContext(Context aCtx) {
		Log.d(TAG, "setContext");
		_context = new WeakReference<Context>(aCtx);
	}
}
