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

package org.protocoderrunner.apprunner;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.R;
import org.protocoderrunner.apprunner.api.PApp;
import org.protocoderrunner.apprunner.api.PBoards;
import org.protocoderrunner.apprunner.api.PConsole;
import org.protocoderrunner.apprunner.api.PDashboard;
import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PFileIO;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.apprunner.api.PProtocoder;
import org.protocoderrunner.apprunner.api.PSensors;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.apprunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.apprunner.api.widgets.PPadView;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.sensors.NFCUtil;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class AppRunnerActivity extends BaseActivity {

	private static final String TAG = "AppRunner";


    private Context context;
    private static ArrayList<Class> classes = new ArrayList<Class>();

	private BroadcastReceiver mIntentReceiver;
	public AppRunnerInterpreter interp;
	private FileObserver fileObserver;
	private boolean isMainLayoutSetup = false;

	// listeners in the main activity that will pass the info to the API classes
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 55;
	private PApp.onAppStatus onAppStatusListener;
	private PDevice.onKeyListener onKeyListener;
	private PDevice.onSmsReceivedListener onSmsReceivedListener;
	private PSensors.onNFCListener onNFCListener;
	private PSensors.onNFCWrittenListener onNFCWrittenListener;
	private PNetwork.onBluetoothListener onBluetoothListener;
	private PMedia.onVoiceRecognitionListener onVoiceRecognitionListener;

	// Layout
	private final int EDITOR_ID = 1231212345;
	public ActionBar actionBar;
	private boolean actionBarSet;
	public RelativeLayout mainLayout;

	private RelativeLayout parentScriptedLayout;
	private RelativeLayout consoleRLayout;
    public FrameLayout editorLayout;

    // store currentProject reference
	private Project currentProject;

	private TextView consoleText;

	public boolean keyVolumeEnabled = true;
	public boolean keyBackEnabled = true;

	public PLiveCodingFeedback liveCoding;

    //API Objects for the interpreter
    public PApp pApp;
    public PBoards pBoards;
    public PConsole pConsole;
    public PDashboard pDashboard;
    public PDevice pDevice;
    public PFileIO pFileIO;
    public PMedia pMedia;
    public PNetwork pNetwork;
    public PProtocoder pProtocoder;
    public PSensors pSensors;
    public PUI pUi;
    public PUtil pUtil;
    //private EditorFragment editorFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        context = this;

        //instantiate the objects that can be accessed from the interpreter
        pApp = new PApp(this);
        pBoards = new PBoards(this);
        pConsole = new PConsole(this);
        pDashboard = new PDashboard(this);
        pDevice = new PDevice(this);
        pFileIO = new PFileIO(this);
        pMedia = new PMedia(this);
        pNetwork = new PNetwork(this);
        pProtocoder = new PProtocoder(this);
        pSensors = new PSensors(this);
        pUi = new PUI(this);
        pUtil  = new PUtil(this);



        // Read in the script given in the intent.
		Intent intent = getIntent();
		if (null != intent) {
			boolean isService = intent.getBooleanExtra("isService", false);

			if (isService) {
				Intent i = new Intent(this, AppRunnerActivity.class);
				i.putExtras(i);
				// potentially add data to the intent
				// i.putExtra("KEY1", "Value to be used by the service");
				this.startService(i);
				finish();
			}

//    TODO Protocoder L
//        ActivityManager activityManager = (ActivityManager) a.get().getSystemService(a.get().ACTIVITY_SERVICE);
//        List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
//        tasks.get(0).getTaskInfo().
//        for (ActivityManager.AppTask task in tasks) {
//
//        }

			// get projects intent
			String projectName = intent.getStringExtra(Project.NAME);
			String projectFolder = intent.getStringExtra(Project.FOLDER);
			boolean wakeUpScreen = intent.getBooleanExtra("wakeUpScreen", false);

            //TODO colors
            int actionBarColor = intent.getIntExtra("color", 0);

            if (projectFolder.equals("examples")) {
                actionBarColor = getResources().getColor(R.color.project_example_color);
            } else {
                actionBarColor = getResources().getColor(R.color.project_user_color);
            }



            MLog.d(TAG, "load " + projectName + " in " + projectFolder);
			currentProject = ProjectManager.getInstance().get(projectFolder, projectName);
			ProjectManager.getInstance().setCurrentProject(currentProject);

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


            //create a new interpreter and add the objects to it
            interp = new AppRunnerInterpreter(this);
            interp.createInterpreter(true);
            interp.interpreter.addObjectToInterface("app", pApp);
            interp.interpreter.addObjectToInterface("boards", pBoards);
            interp.interpreter.addObjectToInterface("console", pConsole);
            interp.interpreter.addObjectToInterface("dashboard", pDashboard);
            interp.interpreter.addObjectToInterface("device", pDevice);
            interp.interpreter.addObjectToInterface("fileio", pFileIO);
            interp.interpreter.addObjectToInterface("media", pMedia);
            interp.interpreter.addObjectToInterface("network", pNetwork);
            interp.interpreter.addObjectToInterface("protocoder", pProtocoder);
            interp.interpreter.addObjectToInterface("sensors", pSensors);
            interp.interpreter.addObjectToInterface("ui", pUi);
            interp.interpreter.addObjectToInterface("util", pUtil);

            AppRunnerInterpreter.InterpreterInfo appRunnerCb = new AppRunnerInterpreter.InterpreterInfo() {

				@Override
				public void onError(String message) {
					MLog.d(TAG, "error " + message);
					showConsole(message);

					// send to web ide
					JSONObject obj = new JSONObject();
					try {
						obj.put("type", "error");
						obj.put("values", message);
                        MLog.d(TAG, "error " + obj.toString(2));
                        IDEcommunication.getInstance(context).send(obj);
					} catch (JSONException er1) {
						er1.printStackTrace();
					}

				}
			};

            MLog.d(TAG, "adding Listener 1");
            MLog.d(TAG, "adding Listener " + interp + " " + appRunnerCb);
            interp.addListener(appRunnerCb);
            MLog.d(TAG, "adding Listener 2");


            // loading the libraries
			interp.eval(AppRunnerInterpreter.scriptPrefix);

			// run the script
			if (null != script) {
				interp.eval(script, projectName);
			}
            interp.eval(AppRunnerInterpreter.SCRIPT_POSTFIX);

            interp.callJsFunction("setup");

			// TODO fix actionbar color
            if (actionBarSet == false) {
				setActionBar(actionBarColor, getResources().getColor(R.color.white));
			}
			// Call the onCreate JavaScript function.
			interp.callJsFunction("onCreate", savedInstanceState);

            //audio
			AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
			this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

			initializeNFC();
			startFileObserver();

			// send ready to the ide
			IDEcommunication.getInstance(this).ready(true);
		}
	}

	public void onEventMainThread(Events.ProjectEvent evt) {
		MLog.d(TAG, "event -> " + evt.getAction());

		if (evt.getAction() == "run") {
			finish();
		}
	}

	// execute lines
	public void onEventMainThread(Events.ExecuteCodeEvent evt) {
		String code = evt.getCode(); // .trim();
		MLog.d(TAG, "event -> " + code);

		if (liveCoding != null) {
			liveCoding.write(code);
		}
		interp.eval(code);
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
//			Intent intentHome = new Intent(this, MainActivity.class);
//			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intentHome);
			overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (onKeyListener != null) {
            onKeyListener.onKeyDown(keyCode);
		}

		if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
			return super.onKeyDown(keyCode, event);
		}

        return true;
		//return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (onKeyListener != null) {
			onKeyListener.onKeyUp(keyCode);
		}

		if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
			return super.onKeyUp(keyCode, event);
		}

		return true;
	}

/*
    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        if (onKeyListener != null) {
            onKeyListener.onKeyDown(keyCode);
        }

        if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
            return super.onKeyDown(keyCode, event);
        }

        return true;

        //return super.onKeyMultiple(keyCode, repeatCount, event);

    }
*/


//    @Override
//    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
//
//        MLog.d(TAG, "action " + ev.getAction());
//        if (onKeyListener != null) {
//            onKeyListener.onKeyDown(ev.getAction());
//        }
//
//        return super.dispatchGenericMotionEvent(ev);
//    }

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

	public void addOnKeyListener(PDevice.onKeyListener onKeyListener2) {
		onKeyListener = onKeyListener2;

	}

	public void addOnSmsReceivedListener(PDevice.onSmsReceivedListener onSmsReceivedListener2) {
		onSmsReceivedListener = onSmsReceivedListener2;

	}

	public void addScriptedLayout(RelativeLayout scriptedUILayout) {
		parentScriptedLayout.addView(scriptedUILayout);
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

			// set the parent
			parentScriptedLayout = new RelativeLayout(this);
			parentScriptedLayout.setLayoutParams(layoutParams);
			parentScriptedLayout.setGravity(Gravity.BOTTOM);
			parentScriptedLayout.setBackgroundColor(this.getResources().getColor(R.color.transparent));
			mainLayout.addView(parentScriptedLayout);

			// editor layout
			editorLayout = new FrameLayout(this);
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
			LayoutParams consoleTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			consoleText.setBackgroundColor(getResources().getColor(R.color.transparent));
			consoleText.setTextColor(getResources().getColor(R.color.white));
			consoleText.setLayoutParams(consoleTextParams);
			int textPadding = getResources().getDimensionPixelSize(R.dimen.apprunner_console_text_padding);
			consoleText.setPadding(textPadding, textPadding, textPadding, textPadding);
			consoleRLayout.addView(consoleText);

			liveCoding = new PLiveCodingFeedback(this);
			mainLayout.addView(liveCoding.add());

			setContentView(mainLayout);
			isMainLayoutSetup = true;
		}
	}
//
//	public void showEditor(boolean b) {
//
//		if (b && editorFragment == null) {
//			editorFragment = new EditorFragment(); // .newInstance(project);
//			Bundle bundle = new Bundle();
//
//			bundle.putString(Project.NAME, currentProject.getName());
//			//bundle.putString(, currentProject.getStoragePath());
//			bundle.putInt(Project.TYPE, currentProject.getFolder());
//			editorFragment.setArguments(bundle);
//			editorFragment.addListener(new EditorFragmentListener() {
//
//				@Override
//				public void onLoad() {
//
//				}
//
//				@Override
//				public void onLineTouched() {
//
//				}
//			});
//
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.add(editorLayout.getId(), editorFragment, String.valueOf(editorLayout.getId()));
//            // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            // ft.setCustomAnimations(android.R.anim.fade_in,
//            // android.R.anim.fade_out);
//            ft.addToBackStack(null);
//            ft.commit();
//
//			//this.addFragment(editorFragment, EDITOR_ID, "editorFragment", true);
//
//		} else {
//
//
//			if (editorFragment != null && editorFragment.isVisible()) {
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.remove(editorFragment);
//                ft.commit();
//                editorFragment = null;
//            } else {
//
//			}
//		}
//	}

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

        // set up a file observer to watch this directory on sd card
        fileObserver = new FileObserver(currentProject.getStoragePath(), FileObserver.CREATE | FileObserver.DELETE) {

			@Override
			public void onEvent(int event, String file) {
                JSONObject msg = new JSONObject();
                String action = null;

                if ((FileObserver.CREATE & event) != 0) {
					MLog.d(TAG, "created " + file);
                    action = "new_files_in_project";

                } else if ((FileObserver.DELETE & event) != 0) {
					MLog.d(TAG, "deleted file " + file);
                    action = "deleted_files_in_project";
				}

                try {
                    msg.put("action", action);
                    msg.put("type", "ide");
                    IDEcommunication.getInstance(context).send(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
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
	public boolean isCodeExecutedShown;

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
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			for (String _string : matches) {
				MLog.d(TAG, "" + _string);

			}
			onVoiceRecognitionListener.onNewResult(matches.get(0));

		} else if (requestCode == 22 && resultCode == Activity.RESULT_OK) {
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
		//MLog.d(TAG, "" + actionBarSet + " " + actionBar);

		actionBarSet = true;
		// Set up the actionbar
		actionBar = getSupportActionBar();

		if (actionBar != null) {

			// home clickable if is running inside org.apprunner.protocoder
			if (AppSettings.STANDALONE == false) {
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

                // apparently android-l doesnt have that resource
                if (textTitleView != null) {
                    textTitleView.setTextColor(colorText);
                }
			}
		}

	}

	public void showCodeExecuted() {

	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {

		int action = event.getAction();
		int actionCode = event.getActionMasked();

		ArrayList<PPadView.TouchEvent> t = new ArrayList<PPadView.TouchEvent>();

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
