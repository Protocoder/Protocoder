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

package org.protocoder.apprunner;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoder.AppSettings;
import org.protocoder.MainActivity;
import org.protocoder.R;
import org.protocoder.apprunner.AppRunnerInterpreter.InterpreterInfo;
import org.protocoder.apprunner.api.PApp;
import org.protocoder.apprunner.api.PBoards;
import org.protocoder.apprunner.api.PConsole;
import org.protocoder.apprunner.api.PDashboard;
import org.protocoder.apprunner.api.PDevice;
import org.protocoder.apprunner.api.PEditor;
import org.protocoder.apprunner.api.PFileIO;
import org.protocoder.apprunner.api.PMedia;
import org.protocoder.apprunner.api.PNetwork;
import org.protocoder.apprunner.api.PProtocoder;
import org.protocoder.apprunner.api.PSensors;
import org.protocoder.apprunner.api.PUI;
import org.protocoder.apprunner.api.PUtil;
import org.protocoder.base.BaseActivity;
import org.protocoder.events.Events;
import org.protocoder.events.Events.ProjectEvent;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.fragments.EditorFragment;
import org.protocoder.fragments.EditorFragment.EditorFragmentListener;
import org.protocoder.network.CustomWebsocketServer;
import org.protocoder.network.IDEcommunication;
import org.protocoder.sensors.NFCUtil;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.MLog;
import org.protocoder.utils.StrUtils;
import org.protocoder.views.PadView.TouchEvent;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.FileObserver;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class AppRunnerActivity extends BaseActivity {

	private static final String TAG = "AppRunner";

	private static ArrayList<Class> classes = new ArrayList<Class>();

	private CustomWebsocketServer ws;
	private BroadcastReceiver mIntentReceiver;
	public AppRunnerInterpreter interp;
	private FileObserver fileObserver;
	private boolean isMainLayoutSetup = false;

	// listeners in the main activity that will pass the info to the API classes
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 55;
	private PApp.onAppStatus onAppStatusListener;
	private PUI.onKeyListener onKeyListener;
	private PDevice.onSmsReceivedListener onSmsReceivedListener;
	private PSensors.onNFCListener onNFCListener;
	private PSensors.onNFCWrittenListener onNFCWrittenListener;
	private PNetwork.onBluetoothListener onBluetoothListener;
	private PMedia.onVoiceRecognitionListener onVoiceRecognitionListener;

	// Layout
	private final int EDITOR_ID = 1231212345;
	public ActionBar actionBar;
	private boolean actionBarSet;
	private RelativeLayout mainLayout;
	private RelativeLayout consoleRLayout;

	// store currentProject reference
	private Project currentProject;

	private TextView consoleText;

	public boolean keyVolumeEnabled = true;
	public boolean keyBackEnabled = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setTheme(R.style.ProtocoderDark_Theme);
		// getWindow().setBackgroundDrawable(new
		// ColorDrawable(android.graphics.Color.TRANSPARENT));
		super.onCreate(savedInstanceState);

		interp = new AppRunnerInterpreter(this);

		interp.addInterface(PApp.class);
		interp.addInterface(PDevice.class);
		interp.addInterface(PBoards.class);
		interp.addInterface(PConsole.class);
		interp.addInterface(PDashboard.class);
		interp.addInterface(PEditor.class);
		interp.addInterface(PFileIO.class);
		interp.addInterface(PMedia.class);
		interp.addInterface(PNetwork.class);
		interp.addInterface(PProtocoder.class);
		interp.addInterface(PSensors.class);
		interp.addInterface(PUI.class);
		interp.addInterface(PUtil.class);

		try {
			MLog.d(TAG, "starting websocket server");
			ws = CustomWebsocketServer.getInstance(this);
		} catch (UnknownHostException e) {
			MLog.d(TAG, "cannot start websocket server");
			e.printStackTrace();
		}

		interp.createInterpreter();
		interp.addListener(new InterpreterInfo() {

			@Override
			public void onError(String message) {
				MLog.d(TAG, "error " + message);
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
			String projectName = intent.getStringExtra(Project.NAME);
			int projectType = intent.getIntExtra(Project.TYPE, -1);
			boolean wakeUpScreen = intent.getBooleanExtra("wakeUpScreen", false);

			MLog.d(TAG, " " + projectName + " " + projectType);
			currentProject = ProjectManager.getInstance().get(projectName, projectType);
			ProjectManager.getInstance().setCurrentProject(currentProject);
			MLog.d(TAG, "launching " + projectName + " " + projectType);

			AppRunnerSettings.get().project = currentProject;
			String script = ProjectManager.getInstance().getCode(currentProject);

			// wake up if intent says so
			if (wakeUpScreen) {
				final Window win = getWindow();
				win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

			}

			// loading the libraries
			interp.eval(AppRunnerInterpreter.scriptPrefix);

			// run the script
			if (null != script) {
				interp.eval(script, projectName);
			}
			interp.eval(AppRunnerInterpreter.SCRIPT_POSTFIX);

			// actionbar color
			Integer actionBarColor = null;
			if (projectType == ProjectManager.PROJECT_EXAMPLE) {
				actionBarColor = getResources().getColor(R.color.project_example_color);
			} else if (projectType == ProjectManager.PROJECT_USER_MADE) {
				actionBarColor = getResources().getColor(R.color.project_user_color);
			}
			if (actionBarSet == false) {
				setActionBar(actionBarColor, getResources().getColor(R.color.white));
			}

		}

		// Call the onCreate JavaScript function.
		interp.callJsFunction("onCreate", savedInstanceState);

		AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		initializeNFC();
		startFileObserver();

		// send ready to the ide
		IDEcommunication.getInstance(this).ready(true);

	}

	public void onEventMainThread(ProjectEvent evt) {
		MLog.d(TAG, "event -> " + evt.getAction());

		if (evt.getAction() == "run") {
			finish();
		}
	}

	public void onEventMainThread(Events.ExecuteCodeEvent evt) {
		MLog.d(TAG, "event -> " + evt.getCode());

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
	public void onResume() {
		super.onResume();

		if (onAppStatusListener != null) {
			onAppStatusListener.onResume();
		}

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
				String body = msg.substring(msg.lastIndexOf(":") + 1, msg.length());
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
		// if (onAppStatusListener != null) {
		// onAppStatusListener.onPause();
		// }
		interp.callJsFunction("onPause");
		EventBus.getDefault().unregister(this);

		if (nfcSupported) {
			mAdapter.disableForegroundDispatch(this);
		}
		this.unregisterReceiver(this.mIntentReceiver);
		IDEcommunication.getInstance(this).ready(false);
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
		IDEcommunication.getInstance(this).ready(false);
		WhatIsRunning.getInstance().stopAll();

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
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
			overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
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

		if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
			return super.onKeyDown(keyCode, event);
		}

		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (onKeyListener != null) {
			onKeyListener.onKeyUp(keyCode);
		}

		if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
			return super.onKeyDown(keyCode, event);
		}

		return true;
	}

	public boolean checkBackKey(int keyCode) {
		boolean r;

		if (keyBackEnabled && keyCode == KeyEvent.KEYCODE_BACK) {
			r = true;
		} else {
			r = false;
		}

		return r;
	}

	public boolean checkVolumeKeys(int keyCode) {
		boolean r;

		if (keyVolumeEnabled && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			r = true;
		} else {
			r = false;
		}

		return r;

	}

	public void addOnAppStatusListener(PApp.onAppStatus onAppStatus) {
		onAppStatus = onAppStatus;

	}

	public void addOnKeyListener(PUI.onKeyListener onKeyListener2) {
		onKeyListener = onKeyListener2;

	}

	public void addOnSmsReceivedListener(PDevice.onSmsReceivedListener onSmsReceivedListener2) {
		onSmsReceivedListener = onSmsReceivedListener2;

	}

	public void addScriptedLayout(RelativeLayout scriptedUILayout) {
		mainLayout.addView(scriptedUILayout);
	}

	public void initLayout() {

		if (!isMainLayoutSetup) {
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			// add main layout
			mainLayout = new RelativeLayout(this);
			mainLayout.setLayoutParams(layoutParams);
			mainLayout.setGravity(Gravity.BOTTOM);
			// mainLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
			mainLayout.setBackgroundColor(getResources().getColor(R.color.light_grey));

			// editor layout
			FrameLayout editorLayout = new FrameLayout(this);
			FrameLayout.LayoutParams editorParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			editorLayout.setLayoutParams(editorParams);
			editorLayout.setId(EDITOR_ID);
			mainLayout.addView(editorLayout);

			// console layout
			consoleRLayout = new RelativeLayout(this);
			RelativeLayout.LayoutParams consoleLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.apprunner_console));
			consoleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			consoleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			consoleRLayout.setLayoutParams(consoleLayoutParams);
			consoleRLayout.setGravity(Gravity.BOTTOM);
			consoleRLayout.setBackgroundColor(getResources().getColor(R.color.blacktransparent));
			consoleRLayout.setVisibility(View.GONE);
			mainLayout.addView(consoleRLayout);

			// Create the text view to add to the control
			consoleText = new TextView(this);
			LayoutParams textParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			consoleText.setBackgroundColor(getResources().getColor(R.color.transparent));
			consoleText.setTextColor(getResources().getColor(R.color.white));
			consoleText.setLayoutParams(textParams);
			int textPadding = getResources().getDimensionPixelSize(R.dimen.apprunner_console_text_padding);
			consoleText.setPadding(textPadding, textPadding, textPadding, textPadding);
			consoleRLayout.addView(consoleText);

			setContentView(mainLayout);
			isMainLayoutSetup = true;
		}
	}

	public void showEditor(boolean b) {

		if (b) {
			EditorFragment editorFragment = new EditorFragment(); // .newInstance(project);
			Bundle bundle = new Bundle();

			bundle.putString("project_name", currentProject.getName());
			bundle.putString("project_url", currentProject.getStoragePath());
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
			this.addFragment(editorFragment, EDITOR_ID, "editorFragment", true);

		} else {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag("editorFragment");

			if (fragment != null && fragment.isVisible()) {
				removeFragment(fragment);
			} else {

			}
		}
	}

	public void showConsole(boolean visible) {
		initLayout();

		if (visible) {
			consoleRLayout.setAlpha(0);
			consoleRLayout.setTranslationY(50);
			consoleRLayout.setVisibility(View.VISIBLE);
			consoleRLayout.animate().alpha(1).translationYBy(-50).setDuration(500);
		} else {
			consoleRLayout.animate().alpha(0).translationYBy(50).setDuration(500).setListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					consoleRLayout.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
		}
	}

	public void showConsole(final String message) {
		initLayout();
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				MLog.d(TAG, "showing console");
				showConsole(true);
				consoleText.setText(message);
				MLog.d(TAG, "msg text");

			}
		});
	}

	public void startFileObserver() {

		fileObserver = new FileObserver(currentProject.getStoragePath(),
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
					ws = CustomWebsocketServer.getInstance(getApplicationContext());
					ws.send(msg);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if ((FileObserver.CREATE & event) != 0) {
					MLog.d(TAG, "created " + file);

				} else if ((FileObserver.DELETE & event) != 0) {
					MLog.d(TAG, "deleted file " + file);

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
	private boolean nfcInit = false;

	public void initializeNFC() {

		if (nfcInit == false) {
			PackageManager pm = getPackageManager();
			nfcSupported = pm.hasSystemFeature(PackageManager.FEATURE_NFC);

			if (nfcSupported == false) {
				return;
			}

			// cuando esta en foreground
			MLog.d(TAG, "Starting NFC");
			mAdapter = NfcAdapter.getDefaultAdapter(this);
			/*
			 * mAdapter.setNdefPushMessageCallback(new
			 * NfcAdapter.CreateNdefMessageCallback() {
			 * 
			 * @Override public NdefMessage createNdefMessage(NfcEvent event) {
			 * // TODO Auto-generated method stub return null; } }, this, null);
			 */

			// Create a generic PendingIntent that will be deliver to this
			// activity.
			// The NFC stack will fill in the intent with the details of the
			// discovered tag before
			// delivering to this activity.
			mPendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
			nfcInit = true;
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		MLog.d(TAG, "New intent " + intent);

		if (intent.getAction() != null) {
			MLog.d(TAG, "Discovered tag with intent: " + intent);
			// mText.setText("Discovered tag " + ++mCount + " with intent: " +
			// intent);

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			String nfcID = StrUtils.bytetostring(tag.getId());

			// if there is a message waiting to be written
			if (NFCUtil.nfcMsg != null) {
				MLog.d(TAG, "->" + NFCUtil.nfcMsg);
				NFCUtil.writeTag(this, tag, NFCUtil.nfcMsg);
				onNFCWrittenListener.onNewTag();
				onNFCWrittenListener = null;
				NFCUtil.nfcMsg = null;

				// read the nfc tag info
			} else {

				// get NDEF tag details
				Ndef ndefTag = Ndef.get(tag);
				if (ndefTag == null) {
					return;
				}
				;

				int size = ndefTag.getMaxSize(); // tag size
				boolean writable = ndefTag.isWritable(); // is tag writable?
				String type = ndefTag.getType(); // tag type

				String nfcMessage = "";

				// get NDEF message details
				NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
				if (ndefMesg != null) {
					NdefRecord[] ndefRecords = ndefMesg.getRecords();
					int len = ndefRecords.length;
					String[] recTypes = new String[len]; // will contain the
															// NDEF record types
					String[] recPayloads = new String[len]; // will contain the
															// NDEF record types
					for (int i = 0; i < len; i++) {
						recTypes[i] = new String(ndefRecords[i].getType());
						recPayloads[i] = new String(ndefRecords[i].getPayload());
						MLog.d(TAG, "qq " + i + " " + recTypes[i] + " " + recPayloads[i]);

					}
					nfcMessage = recPayloads[0];

				}

				onNFCListener.onNewTag(nfcID, nfcMessage);
			}

		}

	}

	public void addNFCReadListener(PSensors.onNFCListener onNFCListener2) {
		onNFCListener = onNFCListener2;

	}

	public void addNFCWrittenListener(PSensors.onNFCWrittenListener onNFCWrittenListener2) {
		onNFCWrittenListener = onNFCWrittenListener2;

	}

	public void addBluetoothListener(PNetwork.onBluetoothListener onBluetoothListener2) {
		onBluetoothListener = onBluetoothListener2;

	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (String _string : matches) {
				MLog.d(TAG, "" + _string);

			}
			onVoiceRecognitionListener.onNewResult(matches.get(0));

		} else if (requestCode == 22 && resultCode == RESULT_OK) {
			String result = data.getStringExtra("json");
			interp.callJsFunction("onResult", result);
		}

		if (onBluetoothListener != null) {
			onBluetoothListener.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onResult(String result) {

	}

	public void addVoiceRecognitionListener(PMedia.onVoiceRecognitionListener onVoiceRecognitionListener2) {

		onVoiceRecognitionListener = onVoiceRecognitionListener2;
	}

	public void setActionBar(Integer colorBg, Integer colorText) {
		MLog.d(TAG, "" + actionBarSet + " " + actionBar);

		actionBarSet = true;
		// Set up the actionbar
		actionBar = getActionBar();

		if (actionBar != null) {

			// home clickable if is running inside protocoder
			if (AppSettings.standAlone == false) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
			// actionBar.setDisplayUseLogoEnabled(false);

			// set color
			if (colorBg != null) {
				ColorDrawable d = new ColorDrawable();
				d.setColor(colorBg);
				actionBar.setBackgroundDrawable(d);
			}

			// title
			actionBar.setTitle(currentProject.getName());

			// set title color
			if (colorText != null) {
				int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
				TextView textTitleView = (TextView) findViewById(titleId);
				textTitleView.setTextColor(colorText);
			}
		}

	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {

		int action = event.getAction();
		int actionCode = event.getActionMasked();

		ArrayList<TouchEvent> t = new ArrayList<TouchEvent>();

		// check finger if down or up
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {

		}

		if (event.getSource() == MotionEvent.TOOL_TYPE_MOUSE) {
			// if (event.getButtonState() == ac) {

			// }
		}

		// get positions per finger
		for (int i = 0; i < event.getPointerCount(); i++) {
			// TouchEvent o = new TouchEvent("finger", event.getPointerId(i),
			// action, (int) event.getX(),
			// (int) event.getY());
			// t.add(o);
		}

		//
		// FINGER 1 UP x y
		// FINGER 2 MOVE x y
		// MOUSE 3 MOVE x y

		// return touching;
		return super.onGenericMotionEvent(event);
	}

}
