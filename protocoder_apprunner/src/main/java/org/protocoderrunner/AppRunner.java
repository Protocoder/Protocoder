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

import android.content.Context;

import org.mozilla.javascript.Scriptable;
import org.protocoderrunner.api.PApp;
import org.protocoderrunner.api.PBoards;
import org.protocoderrunner.api.PConsole;
import org.protocoderrunner.api.PDashboard;
import org.protocoderrunner.api.PDevice;
import org.protocoderrunner.api.PFileIO;
import org.protocoderrunner.api.PMedia;
import org.protocoderrunner.api.PNetwork;
import org.protocoderrunner.api.PProtocoder;
import org.protocoderrunner.api.PSensors;
import org.protocoderrunner.api.PUI;
import org.protocoderrunner.api.PUtil;
import org.protocoderrunner.api.other.WhatIsRunning;
import org.protocoderrunner.models.Project;
import org.protocoderrunner.base.utils.MLog;

import java.io.File;

public class AppRunner {

    private static final String TAG = AppRunner.class.getSimpleName();

    private static AppRunner instance;
    private final Context mContext;
    public Project project;
    public boolean hasUserInterface = false;
    public WhatIsRunning whatIsRunning;

    //public boolean hasCustomJSInterpreter = true;

    //Project properties
    public Project mCurrentProject;

    public String mProjectName;
    public String mProjectFolder;
    private String mScript;
    public String mIntentPrefixScript = "";
    public String mIntentCode = "";
    public String mIntentPostfixScript = "";

    public boolean mIsProjectLoaded = false;

    //Interpreter
    public AppRunnerInterpreter interp;

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

    public AppRunner(Context context) {
        this.mContext = context;
        whatIsRunning = new WhatIsRunning();
    }

    public AppRunner initDefaultObjects() {
        initDefaultObjects(null);

        return this;
    }

    public AppRunner initDefaultObjects(AppRunnerFragment appRunnerFragment) {
        hasUserInterface = true;

        //instantiate the objects that can be accessed from the interpreter

        //the reason to call initForParentFragment is because the class depends on the fragment ui.
        //its not very clean and at some point it will be change to a more elegant solution that will allow to
        //have services
        pApp = new PApp(this);
        pApp.initForParentFragment(appRunnerFragment);
        pBoards = new PBoards(this);
        pConsole = new PConsole(this);
        pDashboard = new PDashboard(this);
        pDevice = new PDevice(this);
        pDevice.initForParentFragment(appRunnerFragment);
        pFileIO = new PFileIO(this);
        pMedia = new PMedia(this);
        pMedia.initForParentFragment(appRunnerFragment);
        pNetwork = new PNetwork(this);
        // pNetwork.initForParentFragment(appRunnerFragment);
        pProtocoder = new PProtocoder(this);
        pSensors = new PSensors(this);
        pSensors.initForParentFragment(appRunnerFragment);
        pUi = new PUI(this);
        pUi.initForParentFragment(appRunnerFragment);
        pUtil = new PUtil(this);

        return this;
    }

    public AppRunner initInterpreter() {

        //create mContext new interpreter and add the objects to it
        interp = new AppRunnerInterpreter();
        interp.addJavaObjectToJs("app", pApp);
        interp.addJavaObjectToJs("boards", pBoards);
        interp.addJavaObjectToJs("console", pConsole);
        interp.addJavaObjectToJs("dashboard", pDashboard);
        interp.addJavaObjectToJs("device", pDevice);
        interp.addJavaObjectToJs("fileio", pFileIO);
        interp.addJavaObjectToJs("media", pMedia);
        interp.addJavaObjectToJs("network", pNetwork);
        interp.addJavaObjectToJs("protocoder", pProtocoder);
        interp.addJavaObjectToJs("sensors", pSensors);
        interp.addJavaObjectToJs("ui", pUi);
        interp.addJavaObjectToJs("util", pUtil);

        return this;
    }

    public void addObject(String name, Object object) {
        interp.addJavaObjectToJs(name, object);
    }

    public AppRunner loadProject() {

        //load project checking if we got the folder and name in the intent
        mIsProjectLoaded = !mProjectName.isEmpty() && !mProjectFolder.isEmpty();
        if (mIsProjectLoaded) {
            mCurrentProject = new Project(mProjectFolder, mProjectName);
            mScript = AppRunnerHelper.getCode(mContext, mCurrentProject);

            MLog.d(TAG, "project loaded for " + mProjectFolder + " " + mProjectName + " " + mScript);
        }

        return this;
    }

    public AppRunner initProject() {

        // preloaded script
        if (!mIntentPrefixScript.isEmpty()) evaluate(mIntentPrefixScript, "");

        // run the script
        if (null != mScript) {
            evaluate(mScript, mProjectName);
        }
        //can accept intent code if no project is loaded
        if (!mIsProjectLoaded) {
            evaluate(mIntentCode, "");
        }

        //script postfix
        if (!mIntentPostfixScript.isEmpty()) evaluate(mIntentPostfixScript, "");

        //call the javascript method setup
        interp.callJsFunction("setup");

        return this;
    }

    public void evaluate(String script, String projectName) {
        interp.eval(script, projectName);
    }


    public Context getAppContext() {
        return mContext.getApplicationContext();
    }

    public Scriptable newArray() {
        return interp.newArray();
    }

    public Scriptable newArray(File[] files) {
        return interp.newArray(files);
    }

    public void byebye() {
        interp = null;
    }

}
