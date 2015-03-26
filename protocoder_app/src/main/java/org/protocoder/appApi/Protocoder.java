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
package org.protocoder.appApi;

import android.content.Context;

import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PFileIO;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.apprunner.api.PProtocoder;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.base.BaseActivity;

public class Protocoder {

    public static BaseActivity mActivityContext;
    private static Protocoder instance;

    public App app;
    public ProtoScripts protoScripts;
    public WebEditor webEditor;
    public Editor editor;
    public Settings settings;


    //instances of some Protocoder AppRunner objects
    //public PUtil mPUtil = new PUtil(mActivityContext);
    PUI mPUi = new PUI(mActivityContext);
    PNetwork mPNetwork = new PNetwork(mActivityContext);
    PFileIO mPFileIO = new PFileIO(mActivityContext);
    PMedia mPMedia = new PMedia(mActivityContext);
    PDevice mPDevice = new PDevice(mActivityContext);
    PProtocoder mProtocoder = new PProtocoder(mActivityContext);

    //public AppRunnerInterpreter interp;

    private boolean debugApp = false;


    String remoteFile = "";
    String versionName;
    int versionCode;


    Protocoder() {
        settings = new Settings(mActivityContext);

    }

    public void init() {
        app = new App(this);
        protoScripts = new ProtoScripts(this);
        editor = new Editor(this);
        webEditor = new WebEditor(this);

        //TODO reenable
        //check if new version is available

        if (mPNetwork.isNetworkAvailable() && settings.getNewVersionCheckEnabled()) {
            mPNetwork.httpGet("http://www.protocoder.org/downloads/list_latest.php", new PNetwork.HttpGetCB() {
                @Override
                public void event(int eventType, String responseString) {
                    //console.log(event + " " + data);
                    String[] splitted = responseString.split(":");
                    remoteFile = "http://www.protocoder.org/downloads/" + splitted[0];
                    versionName = splitted[1];
                    versionCode = Integer.parseInt(splitted[2]);

                    if (versionCode > mProtocoder.versionCode()) {
                        mPUi.popupInfo("New version available", "The new version " + versionName + " is available in the Protocoder.org website. Do you want to get it?", "Yes!", "Later", new PUI.popupCB() {
                            @Override
                            public void event(boolean b) {
                                if (b) {
                                    mPDevice.openWebApp("http://www.protocoder.org#download");
                                }
                            }
                        });
                    } else {
                        //console.log("updated");
                    }
                }
            });
        }

        // if (debugApp) {
        //     interp = new AppRunnerInterpreter(mActivityContext);
        //     interp.createInterpreter(true);

        // interp.interpreter.addObjectToInterface("ui", mPUi);
        // interp.interpreter.addObjectToInterface("util", mPUtil);
        // interp.interpreter.addObjectToInterface("protocoder", mProtocoder);
        // }

    }


    public static Protocoder getInstance(Context activity) {
        mActivityContext = (BaseActivity) activity;
        if (instance == null) {
            instance = new Protocoder();
        }

        return instance;
    }

}
