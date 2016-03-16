/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
*
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner;

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
import org.protocoderrunner.api.PApp;
import org.protocoderrunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

@SuppressLint("NewApi")
public class AppRunnerFragment extends Fragment {

    private static final String TAG = AppRunnerFragment.class.getSimpleName();

    private AppRunner mAppRunner;
    private AppRunnerActivity mActivity;
    private Context mContext;
    private FileObserver fileObserver;

    // listeners in the main activity that will pass the info to the API classes
    private PApp.onAppStatus onAppStatusListener;

    // Layout stuff
    private final int EDITOR_ID = 1231212345;
    public  RelativeLayout mainLayout;
    private RelativeLayout parentScriptedLayout;
    private RelativeLayout consoleRLayout;
    public  FrameLayout editorLayout;
    private TextView consoleText;
    public  PLiveCodingFeedback liveCoding;
    private View mMainView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;

        mActivity = (AppRunnerActivity) getActivity();

        mAppRunner = new AppRunner(mContext);
        mAppRunner.initDefaultObjects(this);

        //get parameters and set them in the AppRunner
        Bundle bundle = getArguments();
        mAppRunner.loadProject(bundle.getString(Project.FOLDER, ""), bundle.getString(Project.NAME, ""));
        mAppRunner.mIntentPrefixScript = bundle.getString(Project.PREFIX, "");
        mAppRunner.mIntentCode = bundle.getString(Project.INTENTCODE, "");
        mAppRunner.mIntentPostfixScript = bundle.getString(Project.POSTFIX, "");

        mAppRunner.initInterpreter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setTheme(R.style.ProtocoderDark_Dialog);
        super.onCreateView(inflater, container, savedInstanceState);

        //init the layout and pass it to the activity
        mMainView = initLayout();
        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String toolbarName = "";
        if (mAppRunner.getProject().getPath().equals("examples")) {
            toolbarName = "example > " + mAppRunner.getProject().getName();
        } else {
            toolbarName = mAppRunner.getProject().getName();
        }
        mActivity.setToolBar(toolbarName, null, null);

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
                    MLog.d(TAG, "error " + obj.toString(2));
                    //TODO change to events
                    // IDEcommunication.getInstance(mActivity).send(obj);
                } catch (JSONException er1) {
                    er1.printStackTrace();
                }
            }
        };

        mAppRunner.interp.addListener(appRunnerCb);
        mAppRunner.initProject();

        // Call the onCreate JavaScript function.
        mAppRunner.interp.callJsFunction("onCreate", savedInstanceState);

        //audio
        AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
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
        mAppRunner.interp.callJsFunction("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();

        // EventBus.getDefault().register(this);

        if (onAppStatusListener != null) {
            onAppStatusListener.onResume();
        }

        if (fileObserver != null) {
            fileObserver.startWatching();
        }
        mAppRunner.interp.callJsFunction("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        // EventBus.getDefault().unregister(this);

        mAppRunner.interp.callJsFunction("onPause");

        //TODO change to events
        /*
        IDEcommunication.getInstance(mActivity).ready(false);
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
        */
    }

    @Override
    public void onStop() {
        super.onStop();

        mAppRunner.interp.callJsFunction("onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAppRunner.interp.callJsFunction("onDestroy");
        mAppRunner.byebye();
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
        mainLayout = new RelativeLayout(mContext);
        mainLayout.setLayoutParams(layoutParams);
        mainLayout.setGravity(Gravity.BOTTOM);
        // mainLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
        mainLayout.setBackgroundColor(getResources().getColor(R.color.white));

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

        // Create the text view to add to the console layout
        consoleText = new TextView(mContext);
        LayoutParams consoleTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        consoleText.setBackgroundColor(getResources().getColor(R.color.transparent));
        consoleText.setTextColor(getResources().getColor(R.color.white));
        consoleText.setLayoutParams(consoleTextParams);
        int textPadding = getResources().getDimensionPixelSize(R.dimen.apprunner_console_text_padding);
        consoleText.setPadding(textPadding, textPadding, textPadding, textPadding);
        consoleRLayout.addView(consoleText);

        //add a close button
        Button closeBtn = new Button(mContext);
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


        liveCoding = new PLiveCodingFeedback(mContext);
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
//			bundle.putInt(Project.TYPE, mCurrentProject.getPath());
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

        if (mAppRunner.mIsProjectLoaded) {

            MLog.d(TAG, "fileObserver -> ");

            MLog.d(TAG, "qq1: " + mAppRunner);
            MLog.d(TAG, "qq2: " + mAppRunner.getProject());
            MLog.d(TAG, "qq3: " + mAppRunner.getProject().getSandboxPath());

            // set up a file observer to watch this directory on sd card
            fileObserver = new FileObserver(mAppRunner.getProject().getFullPath(), FileObserver.CREATE | FileObserver.DELETE) {

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
                        //TODO change to events
                        //IDEcommunication.getInstance(mActivity).send(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
        }
    }

    /*
    // execute lines
    public void onEventMainThread(AppRunnerEvents.ExecuteCodeEvent evt) {
        String code = evt.getCode(); // .trim();
        MLog.d(TAG, "event -> " + code);

        if (liveCoding != null) {
            liveCoding.write(code);
        }
        mAppRunner.interp.eval(code);
    }
    */

    public PLiveCodingFeedback liveCodingFeedback() {
        return liveCoding;
    }

}
