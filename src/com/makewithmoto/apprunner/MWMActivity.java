package com.makewithmoto.apprunner;

import ioio.lib.api.Closeable;

import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.makewithmoto.R;
import com.makewithmoto.appruner.webrunner.ApplicationWebView;
import com.makewithmoto.appruner.webrunner.WhatIsRunning;
import com.makewithmoto.apprunner.api.JAndroid;
import com.makewithmoto.apprunner.api.JInterface;
import com.makewithmoto.apprunner.api.JIoio;
import com.makewithmoto.apprunner.api.JLog;
import com.makewithmoto.apprunner.api.JMoto;
import com.makewithmoto.apprunner.api.JNetwork;
import com.makewithmoto.apprunner.api.JSensors;
import com.makewithmoto.apprunner.api.JUI;
import com.makewithmoto.apprunner.events.Events.ProjectEvent;
import com.makewithmoto.apprunner.fragments.MoldableFragment;
import com.makewithmoto.apprunner.hardware.HardwareCallback;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.fragments.EditorFragment;
import com.makewithmoto.network.IWebSocketService;
import com.makewithmoto.utils.ALog;

import de.greenrobot.event.EventBus;

//TODO remove IOIO closeable reference
@SuppressLint({ "NewApi", "ValidFragment" })
public class MWMActivity extends BaseActivity implements HardwareCallback {

	private final String TAG = "MWM";
	private ApplicationWebView applicationWebView;
	private MoldableFragment moldableFragment;
	public EditorFragment editorFragment;
	private boolean showingEditor = false;
	
	private Project project;

	private static final int TOGGLE_EDITOR = 0;
	private HashMap<String, Object> components = new HashMap<String, Object>();
	
	// javascript interfaces
	private HashMap<String, Object> _jsInterfaces = new HashMap<String, Object>();
	
	/////// WEBSOCKET INTERFACE
	private IWebSocketService wsServiceInterface;
	private Boolean isConnectedToWebsockets = false;
	private Intent wsIntent;	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			isConnectedToWebsockets = false;
		}
		@Override
		public void onServiceConnected(ComponentName name, final IBinder service) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					isConnectedToWebsockets = true;
					Log.d(TAG, "----> connected to websockets");
					wsServiceInterface = IWebSocketService.Stub.asInterface((IBinder)service);
					try {
						wsServiceInterface.start();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						wsServiceInterface.sendToSockets("Connected");
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}

		Log.d(TAG, "Launching project. Fetching name...");
		String project_name = extras.getString("project_name");
		Log.d(TAG, "Found name: " + project_name);
		project = Project.get(project_name);
		Log.d(TAG, "Project: " + project);
		
		setContentView(R.layout.activity_forfragments1);

		Log.d(TAG, "Creating applicationWebView ");
		applicationWebView = new ApplicationWebView(this);
		Log.d(TAG, "Adding applicationWebView fragment");
		addFragment((Fragment) applicationWebView, R.id.f1, false); 

		ALog.i("Launching project");
		
		moldableFragment = new MoldableFragment();
		Log.d(TAG, "Adding moldeableFragment");
		addFragment(moldableFragment, R.id.f2, false);

		editorFragment = new EditorFragment();
		Log.d(TAG, "Adding editorFragment");
		addFragment(editorFragment, R.id.fragmentEditor, false);
		hideSourceView();

		Log.d(TAG, "Registering MWMActivity for EventBus");
		EventBus.getDefault().register(this);
		
		Log.d(TAG, "Starting up IWebSocketService");
		wsIntent = new Intent(IWebSocketService.class.getName());
		Log.d(TAG, "Binding to WebSocketService");
		bindService(wsIntent, conn, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "WebSocketService bound");
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onPause() {
		super.onPause();
		safeClose();
	}
	
	

	private void safeClose() {
		Log.d(TAG, "safeClose for MWMActivity");
		Log.d(TAG, "WhatIsRunning stopAll");
		WhatIsRunning.getInstance().stopAll();
		Iterator it = _jsInterfaces.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			JInterface iface = (JInterface)_jsInterfaces.get(key);
			Log.d(TAG, "Destroying interface: " + key);
			iface.destroy();
		}
		Log.d(TAG, "Removing fragments");
		removeFragment(applicationWebView);
		removeFragment(moldableFragment);
		removeFragment(editorFragment);
		
		Log.d(TAG, "Stopping application thread");
		if (_webViewLooper != null) _webViewLooper.cancel(true);
		
		Log.d(TAG, "Closing components...");
		closeComponents();
		Log.d(TAG, "Unregistering as an EventBust listener");
		EventBus.getDefault().unregister(this);
		Log.d(TAG, "Unbinding from the service");
		if (isConnectedToWebsockets) {
			unbindService(conn);
		} 
		superMegaForceKill();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		applicationWebView.setVisible(false);
		
		_jsInterfaces.put("_moto", 		new JMoto(this));
		_jsInterfaces.put("_console", 	new JLog(this));
		_jsInterfaces.put("_sensors", 	new JSensors(this));
		_jsInterfaces.put("_ioio", 		new JIoio(this));
		_jsInterfaces.put("_android", 	new JAndroid(this));
		_jsInterfaces.put("_network", 	new JNetwork(this));
		_jsInterfaces.put("_ui", 		new JUI(this));
		
		// Run through the interfaces and add them to the webview
		Iterator<String> it = _jsInterfaces.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			JInterface iface = (JInterface)_jsInterfaces.get(key);
			applicationWebView.addJavascriptInterface(iface, key);
		}
		applicationWebView.launchProject(project);
	}

	// Show source editor view
	private void showSourceView() {
		FrameLayout f = (FrameLayout) findViewById(R.id.fragmentEditor);
		f.setVisibility(View.VISIBLE);
		
		showingEditor = true;
	}

	private void hideSourceView() {
		
		FrameLayout f = (FrameLayout) findViewById(R.id.fragmentEditor);
		f.setVisibility(View.GONE);

		showingEditor = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (showingEditor) {
				hideSourceView();
				return true;
			} else {
				//board.powerOff();
				return super.onKeyDown(keyCode, event);
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	// ///////////////////
	// Helpers
	// ///////////////////
	private void closeComponents() {
		Iterator<String> iterator = components.keySet().iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object component = components.get(key);
			((Closeable) component).close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, TOGGLE_EDITOR, 0, "Source").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case TOGGLE_EDITOR:
			if (showingEditor) {
				hideSourceView();
			} else {
				showSourceView();
				editorFragment.loadProject(project);
			}
			return true;
	
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onEventAsync(ProjectEvent evt) {
		// Quit the project
		if (evt.getAction().equals("stop")) {
			finish();
		}
	}
	
	// MUST IMPLEMENT THIS
	public void onEventAsync(LogEvent evt) {
		Log.d(TAG, "Log Event onEventAsync " + evt.getMessage());
		String msg = evt.getMessage();;
		try {
			tryLogToSockets(evt.getTag(), msg);
		} catch (RemoteException e) {
			Log.d(TAG, "ERROR SENDING TO SOCKETS");
			e.printStackTrace();
		}
	}
	
	public void tryLogToSockets(String tag, String msg) throws RemoteException {
		if (isConnectedToWebsockets) {
			try {
				wsServiceInterface.logToSockets(tag, msg);
			} catch (Exception e) {}
		} else {
			Log.e(TAG, "Can't send message, not connected to websockets");
		}
	}
	
	public void tryWriteToSockets(String tag, String msg) throws RemoteException {
		if (isConnectedToWebsockets) {
			try {
				wsServiceInterface.sendJsonToSockets(tag, msg);
			} catch (Exception e) {
			}
		} else {
			Log.e(TAG, "Not connected to webSockets. Cannot send message: " + msg);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		ViewGroup vg = (ViewGroup) findViewById(R.layout.activity_forfragments);
		if (vg != null) {
			vg.invalidate();
			vg.removeAllViews();
		}
		safeClose();
	}
	
	// LOOPER
	class WebViewLooper extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while(true) {
				loop();
				try {
					Thread.sleep(100); // Sleep a small amount of time
					if (!callbackReply) {
						finish(); // Kill the app
						break;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Sleep 
			}
			return null;
		}
	}
	
	// Called upon webview load
	public void setWebViewReady() {
		Log.d(TAG, "setWebViewReady");
		setup();
	}

	private WebViewLooper _webViewLooper;
	
	@Override
	public void setup() {
		Log.i(TAG, "Called setup... window['moto'].setup();");
		applicationWebView.runJavascript("window['moto'].setup();");
		_webViewLooper = new WebViewLooper();
		_webViewLooper.execute(); // Start looping
	}

	/**
	 * Control variables
	 */
	public Boolean callbackReply = true;

	@Override
	public void loop() {
		applicationWebView.runJavascript("window['moto'].loop();");
	}
	
	/**
	 * While waiting for javascript to send us a callback we'll wait in a
	 * blocking loop. If the loop retries enough times and the function doesn't
	 * send us back, we'll loop through and call loop again
	 * 
	 * @param retriesLeft
	 */
	private void waitForCallback(int retriesLeft) {
		while (callbackReply == null) {
			try {
				if (retriesLeft < 0)
					callbackReply = true;
				Thread.sleep(50);
				retriesLeft -= 1;
			} catch (InterruptedException e) {
				e.printStackTrace();
				retriesLeft -= 1;
			}
		}
		return;
	}

	@Override
	public void onComplete() {
		Log.d(TAG, "onComplete for MWMActivity");
		finish();
	}

	public ApplicationWebView getApplicationWebView() {
		return applicationWebView;
	}

	public MoldableFragment getMoldableFragment() {
		return moldableFragment;
	}

	public HashMap<String, Object> getComponents() {
		return components;
	}

	/**
	 * TODO: Get rid of this
	 */
	@Override
	public void onConnect(Object obj) {
		// TODO Auto-generated method stub
	}
}
