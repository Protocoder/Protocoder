/*

 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

package com.makewithmoto.apprunner;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.FileObserver;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makewithmoto.MainActivity;
import com.makewithmoto.R;
import com.makewithmoto.apprunner.AppRunnerInterpreter.InterpreterInfo;
import com.makewithmoto.apprunner.api.JAndroid;
import com.makewithmoto.apprunner.api.JConsole;
import com.makewithmoto.apprunner.api.JDashboard;
import com.makewithmoto.apprunner.api.JEditor;
import com.makewithmoto.apprunner.api.JFileIO;
import com.makewithmoto.apprunner.api.JMedia;
import com.makewithmoto.apprunner.api.JNetwork;
import com.makewithmoto.apprunner.api.JProtocoder;
import com.makewithmoto.apprunner.api.JSensors;
import com.makewithmoto.apprunner.api.JUI;
import com.makewithmoto.apprunner.api.JUtil;
import com.makewithmoto.apprunner.api.boards.JIOIO;
import com.makewithmoto.apprunner.api.boards.JMakr;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.events.Events;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.events.ProjectManager;
import com.makewithmoto.fragments.EditorFragment;
import com.makewithmoto.fragments.EditorFragment.EditorFragmentListener;
import com.makewithmoto.network.CustomWebsocketServer;
import com.makewithmoto.sensors.WhatIsRunning;
import com.makewithmoto.utils.StrUtils;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class AppRunnerActivity extends BaseActivity {

	String scriptFileName;
	private Project currentProject;
	public ActionBar actionBar;
	private CustomWebsocketServer ws;
	private JAndroid.onKeyListener onKeyListener;
	private JAndroid.onSmsReceivedListener onSmsReceivedListener;
	private JSensors.onNFCListener onNFCListener;
	private JNetwork.onBluetoothListener onBluetoothListener;
	private JMedia.onVoiceRecognitionListener onVoiceRecognitionListener;
	private BroadcastReceiver mIntentReceiver;
	public AppRunnerInterpreter interp;
	private FileObserver fileObserver;

	private static final String TAG = "AppRunner";

	public static final int VOICE_RECOGNITION_REQUEST_CODE = 55;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setImmersive(true);
		//setImmersive();
		
		setContentView(R.layout.activity_apprunner);

		interp = new AppRunnerInterpreter(this);

		interp.addInterface(JAndroid.class);
		interp.addInterface(JConsole.class);
		interp.addInterface(JDashboard.class);
		interp.addInterface(JEditor.class);
		interp.addInterface(JFileIO.class);
		interp.addInterface(JDashboard.class);
		interp.addInterface(JMakr.class);
		interp.addInterface(JIOIO.class);
		interp.addInterface(JMedia.class);
		interp.addInterface(JNetwork.class);
		interp.addInterface(JSensors.class);
		interp.addInterface(JUI.class);
		interp.addInterface(JUtil.class);
		interp.addInterface(JProtocoder.class);

		try {
			Log.d(TAG, "starting websocket server");
			ws = CustomWebsocketServer.getInstance(this);
		} catch (UnknownHostException e) {
			Log.d(TAG, "cannot start websocket server");
			e.printStackTrace();
		}

		String projectName = "";

		interp.createInterpreter();
		interp.addListener(new InterpreterInfo() {

			@Override
			public void onError(String message) {

				Log.d(TAG, "lallala " + message);

				showConsole(message);

				// send to web ide
				JSONObject obj = new JSONObject();
				try {
					obj.put("type", "error");
					obj.put("values", message);
					ws.send(obj);
				} catch (JSONException er1) {
					er1.printStackTrace();
				}

			}
		});

		// Read in the script given in the intent.
		Intent intent = getIntent();
		if (null != intent) {
			// get projects intent
			projectName = intent.getStringExtra("projectName");
			int projectType = intent.getIntExtra("projectType",
					ProjectManager.type);

			Log.d(TAG, "launching " + projectName + " " + projectType);

			currentProject = ProjectManager.getInstance().get(projectName,
					projectType);

			AppRunnerSettings.get().project = currentProject;

			String script = ProjectManager.getInstance()
					.getCode(currentProject);

			// loading the libraries
			interp.eval(interp.scriptPrefix);

			// Set up the actionbar
			actionBar = getActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setTitle(projectName);
			}

			// run the script
			if (null != script) {
				interp.eval(script, projectName);
			}

			interp.eval(interp.SCRIPT_POSTFIX);

		}

		// Call the onCreate JavaScript function.
		interp.callJsFunction("onCreate", savedInstanceState);

		AudioManager audio = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		initializeNFC();
		startFileObserver();

		// send ready to the ide
		ready(true);

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
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(this);
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onEventMainThread(ProjectEvent evt) {
		Log.d(TAG, "event -> " + evt.getAction());

		if (evt.getAction() == "run") {
			finish();
		}
	}

	public void onEventMainThread(Events.ExecuteCodeEvent evt) {
		Log.d(TAG, "event -> " + evt.getCode());

		interp.eval(evt.getCode());

	}

	@Override
	public void onStart() {
		super.onStart();
		interp.callJsFunction("onStart");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		interp.callJsFunction("onRestart");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// MainActivity.mMapIsTouched = true;
			break;
		case MotionEvent.ACTION_UP:
			// MainActivity.mMapIsTouched = false;
			break;

		case MotionEvent.ACTION_MOVE:
			int x = (int) event.getX();
			int y = (int) event.getY();
			Log.d(TAG, "" + x + " " + y);

			// callback(callbackfn, x, y);
			// Point point = new Point(x, y);
			// LatLng latLng = map.getProjection().fromScreenLocation(point);
			// Point pixels = map.getProjection().toScreenLocation(latLng);;
			// mapCustomFragment.setTouch(latLng);

			// Log.d("qq2", x + " " + y + " " + latLng.latitude + " " +
			// latLng.longitude);
			break;
		}

		// return true; //a.get().dispatchTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);

		if (nfcSupported) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
		// sms receive

		IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
		mIntentReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(android.content.Context context, Intent intent) {
				String msg = intent.getStringExtra("get_msg");

				// Process the sms format and extract body &amp; phoneNumber
				msg = msg.replace("\n", "");
				String body = msg.substring(msg.lastIndexOf(":") + 1,
						msg.length());
				String pNumber = msg.substring(0, msg.lastIndexOf(":"));

				if (onSmsReceivedListener != null) {
					onSmsReceivedListener.onSmsReceived(pNumber, body);
				}
				// Add it to the list or do whatever you wish to

			}
		};
		this.registerReceiver(mIntentReceiver, intentFilter);
		fileObserver.startWatching();
		interp.callJsFunction("onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);

		interp.callJsFunction("onPause");
		interp.callJsFunction("onAndroidPause");

		if (nfcSupported) {
			mAdapter.disableForegroundDispatch(this);
		}
		this.unregisterReceiver(this.mIntentReceiver);
		ready(false);
		fileObserver.stopWatching();
	}

	@Override
	public void onStop() {
		super.onStop();
		interp.callJsFunction("onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		interp.callJsFunction("onDestroy");
		interp = null;
		WhatIsRunning.getInstance().stopAll();

	}

	// @Override
	// public Object onRetainNonConfigurationInstance() {
	// // TODO: We will need to somehow also allow JS to save
	// data and rebuild the UI.
	// return interpreter;
	// }

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenu.ContextMenuInfo info) {
		interp.callJsFunction("onCreateContextMenu", menu, view, info);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		interp.callJsFunction("onContextItemSelected", item);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		interp.callJsFunction("onCreateOptionsMenu", menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		interp.callJsFunction("onPrepareOptionsMenu", menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		interp.callJsFunction("onOptionsItemSelected", item);
		switch (item.getItemId()) {
		case android.R.id.home:
			// Up button pressed
			Intent intentHome = new Intent(this, MainActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);
			overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set,
					R.anim.splash_slide_out_anim_reverse_set);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.makewithmoto.base.BaseActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 * 
	 * key handling, it will pass it to the javascript interface
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (onKeyListener != null) {
			onKeyListener.onKeyDown(keyCode);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (onKeyListener != null) {
			onKeyListener.onKeyUp(keyCode);
		}

		return super.onKeyUp(keyCode, event);
	}

	public void addOnKeyListener(JAndroid.onKeyListener onKeyListener2) {
		onKeyListener = onKeyListener2;

	}

	public void addOnSmsReceivedListener(
			JAndroid.onSmsReceivedListener onSmsReceivedListener2) {
		onSmsReceivedListener = onSmsReceivedListener2;

	}

	public void showEditor(boolean b) {

		if (b) {
			EditorFragment editorFragment = new EditorFragment(); // .newInstance(project);
			Bundle bundle = new Bundle();

			bundle.putString("project_name", currentProject.getName());
			bundle.putString("project_url", currentProject.getFolder());
			bundle.putInt("project_type", currentProject.getType());
			editorFragment.setArguments(bundle);
			editorFragment.addListener(new EditorFragmentListener() {

				@Override
				public void onLoad() {

				}

				@Override
				public void onLineTouched() {

				}
			});
			this.addFragment(editorFragment, R.id.fragmentEditor,
					"editorFragment", true);

		} else {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(
					"editorFragment");

			if (fragment != null && fragment.isVisible()) {
				removeFragment(fragment);
			} else {

			}
		}
	}

	public void showConsole(boolean visible) {
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.console);
		if (visible) {
			rl.setAlpha(0);
			rl.setTranslationY(50);
			rl.setVisibility(View.VISIBLE);
			rl.animate().alpha(1).translationYBy(-50).setDuration(500);
		} else {
			rl.animate().alpha(0).translationYBy(50).setDuration(500)
					.setListener(new AnimatorListener() {

						@Override
						public void onAnimationStart(Animator animation) {
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							rl.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationCancel(Animator animation) {
						}
					});
		}
	}

	public void showConsole(final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "showing console");
				showConsole(true);
				RelativeLayout rl = (RelativeLayout) findViewById(R.id.console);
				TextView t = (TextView) findViewById(R.id.console_content);
				t.setText(message);
				Log.d(TAG, "msg text");

			}
		});
	}

	public void startFileObserver() {

		fileObserver = new FileObserver(currentProject.getFolder(),
		// set up a file obs`erver to
		// watch this directory on sd card
				FileObserver.CREATE | FileObserver.DELETE) {

			@Override
			public void onEvent(int event, String file) {

				CustomWebsocketServer ws;
				try {
					JSONObject msg = new JSONObject();
					msg.put("type", "ide");
					msg.put("action", "new_files_in_project");
					ws = CustomWebsocketServer
							.getInstance(getApplicationContext());
					ws.send(msg);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if ((FileObserver.CREATE & event) != 0) {
					Log.d(TAG, "created " + file);

				} else if ((FileObserver.DELETE & event) != 0) {
					Log.d(TAG, "deleted file " + file);

				}
			}
		};

	}

	/*
	 * NFC
	 */

	private NfcAdapter mAdapter;

	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private boolean nfcSupported;

	public void initializeNFC() {

		PackageManager pm = getPackageManager();
		nfcSupported = pm.hasSystemFeature(PackageManager.FEATURE_NFC);

		if (nfcSupported == false)
			return;

		// cuando esta en foreground
		Log.d(TAG, "Starting NFC");
		mAdapter = NfcAdapter.getDefaultAdapter(this);

		// Create a generic PendingIntent that will be deliver to this activity.
		// The NFC stack will fill in the intent with the details of the
		// discovered tag before
		// delivering to this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Setup an intent filter for all MIME based dispatches
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };

		// Setup a tech list for all NfcF tags
		mTechLists = new String[][] { new String[] { NfcF.class.getName() } };

	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.d(TAG, "New intent " + intent);

		if (intent.getAction() != null) {
			Log.d(TAG, "Discovered tag with intent: " + intent);
			// mText.setText("Discovered tag " + ++mCount + " with intent: " +
			// intent);

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			// TechFilter t = new TechFilter();
			byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
			// NdefMessage[] msgs = (NdefMessage[]) intent
			// .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

			String nfcID = StrUtils.bytetostring(tag.getId());
			// Toast.makeText(this, "Tag detected: " + nfcID,
			// Toast.LENGTH_LONG).show();
			onNFCListener.onNewTag(nfcID);

		}

	}

	public void addNFCListener(JSensors.onNFCListener onNFCListener2) {
		onNFCListener = onNFCListener2;

	}

	public void addBluetoothListener(
			JNetwork.onBluetoothListener onBluetoothListener2) {
		onBluetoothListener = onBluetoothListener2;

	}

	public void scanBluetooth() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		mBluetoothAdapter.startDiscovery();
		BroadcastReceiver mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

					int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
							Short.MIN_VALUE);

					onBluetoothListener.onDeviceFound(device.getName(),
							device.getAddress(), rssi);
					Log.d(TAG, device.getName() + "\n" + device.getAddress()
							+ " " + rssi);
				}
			}
		};

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (String _string : matches) {
				Log.d(TAG, "" + _string);

			}
			onVoiceRecognitionListener.onNewResult(matches.get(0));

		} else if (requestCode == 22 && resultCode == RESULT_OK) { 
			String result = data.getStringExtra("json");
			interp.callJsFunction("onResult", result);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onResult(String result) { 
		
		
	}

	public void addVoiceRecognitionListener(
			JMedia.onVoiceRecognitionListener onVoiceRecognitionListener2) {

		onVoiceRecognitionListener = onVoiceRecognitionListener2;
	}

}
