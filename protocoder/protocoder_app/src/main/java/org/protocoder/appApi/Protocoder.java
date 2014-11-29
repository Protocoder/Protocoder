/*
 * Protocoder
 * A prototyping platform for Android devices
 *
 * Victor Diaz Barrales victormdb@gmail.com
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
package org.protocoder.appApi;

import android.app.Activity;

import org.protocoder.MainActivity;
import org.protocoderrunner.apprunner.AppRunnerInterpreter;
import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PFileIO;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.apprunner.api.PProtocoder;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.PUtil;

public class Protocoder {

    public static MainActivity a;
    private static Protocoder instance;

    public App app;
    public ProtoScripts protoScripts;
    public WebEditor webEditor;
    public Editor editor;

    PUtil mPUtil = new PUtil(a);
    PUI mPUi = new PUI(a);
    PNetwork mPNetwork = new PNetwork(a);
    PFileIO mPFileIO = new PFileIO(a);
    PMedia mPMedia = new PMedia(a);
    PDevice mPDevice = new PDevice(a);
    PProtocoder mProtocoder = new PProtocoder(a);

    public AppRunnerInterpreter interp;

    private boolean debugApp = false;


    String remoteFile = "";
    String versionName;
    int versionCode;


    Protocoder() {

    }

    public void init() {
        app = new App(this);
        protoScripts = new ProtoScripts(this);
        editor = new Editor(this);
        webEditor = new WebEditor(this);


        //check if new version is available


        if (mPNetwork.isNetworkAvailable()) {
            mPNetwork.httpGet("http://www.protocoder.org/downloads/list_latest.php", new PNetwork.HttpGetCB() {
                @Override
                public void event(int eventType, String responseString) {
                    //console.log(event + " " + data);
                    String[] splitted = responseString.split(":");
                    remoteFile = "http://www.protocoder.org/downloads/" + splitted[0];
                    versionName = splitted[1];
                    versionCode = Integer.parseInt(splitted[2]);

                    if (versionCode > mProtocoder.getVersionCode()) {
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



        if (debugApp) {
            interp = new AppRunnerInterpreter(a);
            interp.createInterpreter(true);

            // interp.interpreter.addObjectToInterface("ui", mPUi);
            // interp.interpreter.addObjectToInterface("util", mPUtil);
            // interp.interpreter.addObjectToInterface("protocoder", mProtocoder);
        }




    }


    public static Protocoder getInstance(Activity activity) {
        a = (MainActivity) activity;
        if (instance == null) {
            instance = new Protocoder();
        }

        return instance;
    }

}
