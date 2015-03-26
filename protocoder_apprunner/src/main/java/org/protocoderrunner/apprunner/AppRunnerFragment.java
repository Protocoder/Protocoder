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
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
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
import org.protocoderrunner.events.Events;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class AppRunnerFragment extends Fragment {

	private static final String TAG = "AppRunnerFragment";

    private AppRunnerActivity mActivity;

	public AppRunnerInterpreter interp;
	private FileObserver fileObserver;

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
    private View mMainView;
    private int mActionBarColorInt;
    private String mIntentPrefixScript = "";
    private String mIntentCode = "";
    private String mIntentPostfixScript = "";
    private boolean mIsProjectLoaded = false;
    //private EditorFragment editorFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (AppRunnerActivity) getActivity();

        //get parameters
        Bundle bundle = getArguments();
        mProjectName = bundle.getString(Project.NAME, "");
        mProjectFolder = bundle.getString(Project.FOLDER, "");
        mActionBarColorInt = bundle.getInt(Project.COLOR, 0);
        mIntentPrefixScript = bundle.getString(Project.PREFIX, "");
        mIntentCode = bundle.getString(Project.CODE, "");
        mIntentPostfixScript = bundle.getString(Project.POSTFIX, "");

        //load project checking if we got the folder and name in the intent
        mIsProjectLoaded = !mProjectName.isEmpty() && !mProjectFolder.isEmpty();
        if (mIsProjectLoaded) {
            mCurrentProject = ProjectManager.getInstance().get(mProjectFolder, mProjectName);
            ProjectManager.getInstance().setCurrentProject(mCurrentProject);
            AppRunnerSettings.get().project = mCurrentProject;
            AppRunnerSettings.get().hasUi = true;

            if (AppSettings.STANDALONE == true) {
                // Get code from assets
                mScript = ProjectManager.getInstance().getCodeFromAssets(mActivity, mCurrentProject);
            } else {
                // Get code from sdcard
                mScript = ProjectManager.getInstance().getCode(mCurrentProject);
            }

            //setup actionbar
            int actionBarColor;
            if (mProjectFolder.equals("examples")) {
                mActionBarColor = getResources().getColor(R.color.project_example_color);
            } else {
                mActionBarColor = getResources().getColor(R.color.project_user_color);
            }
        }



        //instantiate the objects that can be accessed from the interpreter

        //the reason to call initForParentFragment is because the class depends on the fragment ui.
        //its not very clean and at some point it will be change to a more elegant solution that will allow to
        //have services
        pApp = new PApp(mActivity);
        pApp.initForParentFragment(this);
        pBoards = new PBoards(mActivity);
        pConsole = new PConsole(mActivity);
        pDashboard = new PDashboard(mActivity);
        pDevice = new PDevice(mActivity);
        pDevice.initForParentFragment(this);
        pFileIO = new PFileIO(mActivity);
        pMedia = new PMedia(mActivity);
        pMedia.initForParentFragment(this);
        pNetwork = new PNetwork(mActivity);
        pNetwork.initForParentFragment(this);
        pProtocoder = new PProtocoder(mActivity);
        pSensors = new PSensors(mActivity);
        pSensors.initForParentFragment(this);
        pUi = new PUI(mActivity);
        pUi.initForParentFragment(this);
        pUtil  = new PUtil(mActivity);


        //create mContext new interpreter and add the objects to it
        interp = new AppRunnerInterpreter(mActivity);
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

        AppRunnerSettings.get().interp = interp;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setTheme(R.style.ProtocoderDark_Dialog);
        super.onCreateView(inflater, container, savedInstanceState);
        MLog.d(TAG, "onCreateView");

        //init the layout and pass it to the activity
        mMainView = initLayout();
        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MLog.d(TAG, "onActivityCreated");

        mActivity.setToolBar(mCurrentProject, mActionBarColor, 0xFFFFFF);

        //catch errors and send them to the webIDE or the app console
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
                    //MLog.d(TAG, "error " + obj.toString(2));
                    IDEcommunication.getInstance(mActivity).send(obj);
                } catch (JSONException er1) {
                    er1.printStackTrace();
                }
            }
        };
        interp.addListener(appRunnerCb);

        // load the libraries
        MLog.d(TAG, "loaded preloaded script" + mIntentPrefixScript);
        interp.eval(AppRunnerInterpreter.SCRIPT_PREFIX);
        if (!mIntentPostfixScript.isEmpty()) interp.eval(mIntentPostfixScript);

        // run the script
        if (null != mScript) {
            interp.eval(mScript, mProjectName);
        }
        //can accept intent code if no project is loaded
        if (!mIsProjectLoaded) {
            interp.eval(mIntentCode);
        }

        //script postfix
        if (!mIntentPostfixScript.isEmpty()) interp.eval(mIntentPostfixScript);
        interp.eval(AppRunnerInterpreter.SCRIPT_POSTFIX);

        //call the javascript method setup
        interp.callJsFunction("setup");

        // TODO fix actionbar color
        if (mActivity.mActionBarSet == false) {
            mActivity.setToolBar(null, mActionBarColor, getResources().getColor(R.color.white));
        }
        // Call the onCreate JavaScript function.
        interp.callJsFunction("onCreate", savedInstanceState);

        //audio
        AudioManager audio = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //nfc
        mActivity.initializeNFC();

        //file observer will notifify project file changes
        startFileObserver();

        // send ready to the webIDE
        //TODO this is gone ?!
	}

    public static AppRunnerFragment newInstance(Bundle bundle) {
        AppRunnerFragment myFragment = new AppRunnerFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

	@Override
	public void onStart() {
        MLog.d(TAG, "onStart");

        super.onStart();
		interp.callJsFunction("onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
        MLog.d(TAG, "onResume");

        EventBus.getDefault().register(this);

        if (onAppStatusListener != null) {
			onAppStatusListener.onResume();
		}

        if (fileObserver != null) {
            fileObserver.startWatching();
        }
		interp.callJsFunction("onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
        MLog.d(TAG, "onPause");

        EventBus.getDefault().unregister(this);

        interp.callJsFunction("onPause");

		IDEcommunication.getInstance(mActivity).ready(false);
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
	}

	@Override
	public void onStop() {
		super.onStop();
        MLog.d(TAG, "onStop");

        interp.callJsFunction("onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        MLog.d(TAG, "onDestroy");

        interp.callJsFunction("onDestroy");

		interp = null;
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MLog.d(TAG, "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MLog.d(TAG, "onDetach");
        WhatIsRunning.getInstance().stopAll();

        //mContext = null;
        //mActivity = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //menu.add("llala");

    }

	public void addOnAppStatusListener(PApp.onAppStatus onAppStatus) {

	}

	public void addScriptedLayout(RelativeLayout scriptedUILayout) {
		parentScriptedLayout.addView(scriptedUILayout);
	}

	public RelativeLayout initLayout() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // add main layout
        mainLayout = new RelativeLayout(mActivity);
        mainLayout.setLayoutParams(layoutParams);
        mainLayout.setGravity(Gravity.BOTTOM);
        // mainLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
        mainLayout.setBackgroundColor(getResources().getColor(R.color.light_grey));

        // set the parent
        parentScriptedLayout = new RelativeLayout(mActivity);
        parentScriptedLayout.setLayoutParams(layoutParams);
        parentScriptedLayout.setGravity(Gravity.BOTTOM);
        parentScriptedLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
        mainLayout.addView(parentScriptedLayout);

        // editor layout
        editorLayout = new FrameLayout(mActivity);
        FrameLayout.LayoutParams editorParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        editorLayout.setLayoutParams(editorParams);
        editorLayout.setId(EDITOR_ID);
        mainLayout.addView(editorLayout);

        // console layout
        consoleRLayout = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams consoleLayoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.apprunner_console));
        consoleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        consoleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        consoleRLayout.setLayoutParams(consoleLayoutParams);
        consoleRLayout.setGravity(Gravity.BOTTOM);
        consoleRLayout.setBackgroundColor(getResources().getColor(R.color.blacktransparent));
        consoleRLayout.setVisibility(View.GONE);
        mainLayout.addView(consoleRLayout);

        // Create the text view to add to the console layout
        consoleText = new TextView(mActivity);
        LayoutParams consoleTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        consoleText.setBackgroundColor(getResources().getColor(R.color.transparent));
        consoleText.setTextColor(getResources().getColor(R.color.white));
        consoleText.setLayoutParams(consoleTextParams);
        int textPadding = getResources().getDimensionPixelSize(R.dimen.apprunner_console_text_padding);
        consoleText.setPadding(textPadding, textPadding, textPadding, textPadding);
        consoleRLayout.addView(consoleText);

        //add a close button
        Button closeBtn = new Button(mActivity);
        closeBtn.setText("x");
        closeBtn.setPadding(5, 5, 5, 5);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConsole(false);
            }
        });
        RelativeLayout.LayoutParams closeBtnLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        closeBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeBtn.setLayoutParams(closeBtnLayoutParams);
        consoleRLayout.addView(closeBtn);


        liveCoding = new PLiveCodingFeedback(mActivity);
        mainLayout.addView(liveCoding.add());

        return mainLayout;
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
        mActivity.runOnUiThread(new Runnable() {

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

        if (mIsProjectLoaded) {

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
                        IDEcommunication.getInstance(mActivity).send(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
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

    public PLiveCodingFeedback liveCodingFeedback() {
        return liveCoding;
    }

}
