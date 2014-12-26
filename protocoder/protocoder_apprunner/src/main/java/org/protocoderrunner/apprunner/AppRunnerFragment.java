/*

 * Protocoder 
 * A prototyping platform for Android devices 
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

package org.protocoderrunner.apprunner;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
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
import org.protocoderrunner.events.Events;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class AppRunnerFragment extends Fragment {

	private static final String TAG = "AppRunner";

    private AppRunnerActivity mActivity;
    private Context mContext;
    private static ArrayList<Class> classes = new ArrayList<Class>();

	public AppRunnerInterpreter interp;
	private FileObserver fileObserver;
	//private boolean isMainLayoutSetup = false;

	// listeners in the main activity that will pass the info to the API classes
	private PApp.onAppStatus onAppStatusListener;

	// Layout
	private final int EDITOR_ID = 1231212345;

	public RelativeLayout mainLayout;

	private RelativeLayout parentScriptedLayout;
	private RelativeLayout consoleRLayout;
    public FrameLayout editorLayout;
	private TextView consoleText;

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

    private Project mCurrentProject;
    private String mProjectName;
    private String mProjectFolder;
    private String mScript;
    private int mActionBarColor;
    //private EditorFragment editorFragment;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setTheme(R.style.ProtocoderDark_Dialog);
        super.onCreateView(inflater, container, savedInstanceState);

        mContext = getActivity();

        Bundle bundle = getArguments();
        mProjectName = bundle.getString(Project.NAME);
        mProjectFolder = bundle.getString(Project.FOLDER);
        mActionBarColor = bundle.getInt(Project.COLOR, 0);

        mCurrentProject = ProjectManager.getInstance().get(mProjectFolder, mProjectName);
        ProjectManager.getInstance().setCurrentProject(mCurrentProject);

        AppRunnerSettings.get().project = mCurrentProject;
        mScript = ProjectManager.getInstance().getCode(mCurrentProject);


        return initLayout();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //instantiate the objects that can be accessed from the interpreter
        pApp = new PApp(mContext);
        pApp.initForParentFragment(this);
        pBoards = new PBoards(mContext);
        pConsole = new PConsole(mContext);
        pDashboard = new PDashboard(mContext);
        pDevice = new PDevice(mContext);
        pDevice.initForParentFragment(this);
        pFileIO = new PFileIO(mContext);
        pMedia = new PMedia(mContext);
        pMedia.initForParentFragment(this);
        pNetwork = new PNetwork(mContext);
        pProtocoder = new PProtocoder(mContext);
        pSensors = new PSensors(mContext);
        pUi = new PUI(mContext);
        pUi.initForParentFragment(this);
        pUtil  = new PUtil(mContext);


        //create mContext new interpreter and add the objects to it
        interp = new AppRunnerInterpreter(mContext);
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
                    IDEcommunication.getInstance(mContext).send(obj);
                } catch (JSONException er1) {
                    er1.printStackTrace();
                }

            }
        };

        interp.addListener(appRunnerCb);

        // loading the libraries
        interp.eval(AppRunnerInterpreter.scriptPrefix);

        // run the script
        if (null != mScript) {
            interp.eval(mScript, mProjectName);
        }
        interp.eval(AppRunnerInterpreter.SCRIPT_POSTFIX);

        interp.callJsFunction("setup");

        mActivity = (AppRunnerActivity) getActivity();

        // TODO fix actionbar color
        if (mActivity.mActionBarSet == false) {
        	mActivity.setActionBar(null, mActionBarColor, getResources().getColor(R.color.white));
        }
        // Call the onCreate JavaScript function.
        interp.callJsFunction("onCreate", savedInstanceState);

        //audio
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mActivity.initializeNFC();

        startFileObserver();

        // send ready to the ide
        IDEcommunication.getInstance(mContext).ready(true);

	}

	@Override
	public void onStart() {
		super.onStart();
		interp.callJsFunction("onStart");
	}

	@Override
	public void onResume() {
		super.onResume();

		if (onAppStatusListener != null) {
			onAppStatusListener.onResume();
		}

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

		IDEcommunication.getInstance(mContext).ready(false);
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
		IDEcommunication.getInstance(mContext).ready(false);
		WhatIsRunning.getInstance().stopAll();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		interp.callJsFunction("onOptionsItemSelected", item);
		switch (item.getItemId()) {
		case android.R.id.home:
			// Up button pressed
			Intent intentHome = new Intent(mActivity, AppRunnerActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);

			mActivity.overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
			mActivity.finish();

            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addOnAppStatusListener(PApp.onAppStatus onAppStatus) {
		onAppStatus = onAppStatus;

	}

	public void addScriptedLayout(RelativeLayout scriptedUILayout) {
		parentScriptedLayout.addView(scriptedUILayout);
	}

	public RelativeLayout initLayout() {

	//	if (!isMainLayoutSetup) {
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			// add main layout
			mainLayout = new RelativeLayout(mContext);
			mainLayout.setLayoutParams(layoutParams);
			mainLayout.setGravity(Gravity.BOTTOM);
			// mainLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
			mainLayout.setBackgroundColor(getResources().getColor(R.color.light_grey));

			// set the parent
			parentScriptedLayout = new RelativeLayout(mContext);
			parentScriptedLayout.setLayoutParams(layoutParams);
			parentScriptedLayout.setGravity(Gravity.BOTTOM);
			parentScriptedLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
			mainLayout.addView(parentScriptedLayout);

			// editor layout
			editorLayout = new FrameLayout(mContext);
			FrameLayout.LayoutParams editorParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			editorLayout.setLayoutParams(editorParams);
			editorLayout.setId(EDITOR_ID);
			mainLayout.addView(editorLayout);

			// console layout
			consoleRLayout = new RelativeLayout(mContext);
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
			consoleText = new TextView(mContext);
			LayoutParams consoleTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			consoleText.setBackgroundColor(getResources().getColor(R.color.transparent));
			consoleText.setTextColor(getResources().getColor(R.color.white));
			consoleText.setLayoutParams(consoleTextParams);
			int textPadding = getResources().getDimensionPixelSize(R.dimen.apprunner_console_text_padding);
			consoleText.setPadding(textPadding, textPadding, textPadding, textPadding);
			consoleRLayout.addView(consoleText);

			liveCoding = new PLiveCodingFeedback(mContext);
			mainLayout.addView(liveCoding.add());


			//isMainLayoutSetup = true;

            return mainLayout;
	//	}
      //  return null;
    }
//
//	public void showEditor(boolean b) {
//
//		if (b && editorFragment == null) {
//			editorFragment = new EditorFragment(); // .newInstance(project);
//			Bundle bundle = new Bundle();
//
//			bundle.putString(Project.NAME, mCurrentProject.getName());
//			//bundle.putString(, mCurrentProject.getStoragePath());
//			bundle.putInt(Project.TYPE, mCurrentProject.getFolder());
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
        ((AppRunnerActivity) mActivity).runOnUiThread(new Runnable() {

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

        // set up mContext file observer to watch this directory on sd card
        fileObserver = new FileObserver(mCurrentProject.getStoragePath(), FileObserver.CREATE | FileObserver.DELETE) {

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
                    IDEcommunication.getInstance(mContext).send(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


			}
		};

	}


}
