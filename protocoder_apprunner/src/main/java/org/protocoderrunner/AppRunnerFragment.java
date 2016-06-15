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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.AppRunnerInterpreter;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

@SuppressLint("NewApi")
public class AppRunnerFragment extends Fragment {

    private static final String TAG = AppRunnerFragment.class.getSimpleName();

    private AppRunner mAppRunner;
    private AppRunnerActivity mActivity;
    private Context mContext;
    private FileObserver fileObserver;

    // Layout stuff
    public  RelativeLayout mainLayout;
    private RelativeLayout parentScriptedLayout;
    public  PLiveCodingFeedback liveCoding;
    private View mMainView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
        mActivity = (AppRunnerActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // init the layout and pass it to the activity
        mMainView = initLayout();

        // create the apprunner
        mAppRunner = new AppRunner(mContext);
        mAppRunner.initDefaultObjects(this);

        // get parameters and set them in the AppRunner
        Bundle bundle = getArguments();
        mAppRunner.loadProject(bundle.getString(Project.FOLDER, ""), bundle.getString(Project.NAME, ""));
        mAppRunner.mIntentPrefixScript = bundle.getString(Project.PREFIX, "");
        mAppRunner.mIntentCode = bundle.getString(Project.INTENTCODE, "");
        mAppRunner.mIntentPostfixScript = bundle.getString(Project.POSTFIX, "");

        mAppRunner.initInterpreter();
        mAppRunner.pUi.screenOrientation("portrait");

        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // catch errors and send them to the WebIDE or the app console
        AppRunnerInterpreter.InterpreterInfo appRunnerCb = new AppRunnerInterpreter.InterpreterInfo() {
            @Override
            public void onError(String message) {
                mAppRunner.pConsole.error(message);
            }
        };

        mAppRunner.interp.addListener(appRunnerCb);
        mAppRunner.initProject();

        // nfc
        mActivity.initializeNFC();

        // file observer will notify project file changes
        startFileObserver();

        // Call the onCreate JavaScript function.
        mAppRunner.interp.callJsFunction("app.onCreate", savedInstanceState);

        // send ready to the webIDE
        //TODO this is gone ?!
    }

    public static AppRunnerFragment newInstance(Bundle bundle) {
        AppRunnerFragment myFragment = new AppRunnerFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppRunner.interp.callJsFunction("app.onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mAppRunner.interp.callJsFunction("app.onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppRunner.interp.callJsFunction("app.onDestroy");
        mAppRunner.byebye();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    public void addScriptedLayout(RelativeLayout scriptedUILayout) {
        parentScriptedLayout.addView(scriptedUILayout);
    }

    public RelativeLayout initLayout() {
        View v = getLayoutInflater(null).inflate(R.layout.apprunner_fragment, null);

        // add main layout
        mainLayout = (RelativeLayout) v.findViewById(R.id.main);

        // set the parent
        parentScriptedLayout = (RelativeLayout) v.findViewById(R.id.scriptedLayout);

        liveCoding = new PLiveCodingFeedback(mContext);
        mainLayout.addView(liveCoding.add());

        return mainLayout;
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
    
    public PLiveCodingFeedback liveCodingFeedback() {
        return liveCoding;
    }

    public AppRunner getAppRunner() {
        return mAppRunner;
    }
}
