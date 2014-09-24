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

package org.protocoderrunner.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.sensors.WhatIsRunning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecuteCmd {

    public interface ExecuteCommandCB {
        void event(String buffer);
    }

    private final String cmd;
    private final ExecuteCommandCB callbackfn;


    private Handler mHandler;
    private Thread mThread;

    public ExecuteCmd(final String cmd, final ExecuteCommandCB callbackfn) {
        this.cmd = cmd;
        this.callbackfn = callbackfn;

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                int count = 0;
                String str = "";
                try {
                    final Process process = Runtime.getRuntime().exec(cmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            process.getInputStream()));


                    mHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            Log.d("New Thread", "Proccess Complete. " + msg.getData().toString());
                            process.destroy();

                            mThread.interrupt();
                            mThread = null;

                            try {
                                process.waitFor();
                            } catch (InterruptedException e) {
                                Log.d("qq", "kk 1");
                                e.printStackTrace();
                            }
                        }
                    };


                    int i;
                    final char[] buffer = new char[4096];
                    StringBuffer output = new StringBuffer();
                 //   Log.d("qq", "qq ");
                    while ((i = reader.read(buffer)) > 0) {
                        output.append(buffer, 0, i);
                       // Log.d("qq", "qq " + String.valueOf(buffer));
                        Handler h = new Handler(Looper.getMainLooper());
                        final int finalI = i;
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                callbackfn.event(finalI + " " + String.valueOf(buffer));

                            }
                        });

                    }
                    reader.close();

                    str = output.toString();
                    // Log.d(TAG, str);
                } catch (IOException e) {
                    // Log.d(TAG, "Error");
                    e.printStackTrace();
                }
                Looper.loop();
            }

        });
        mThread.start();

        WhatIsRunning.getInstance().add(this);
    }

    @ProtocoderScript
    @APIMethod(description = "stop the running command", example = "")
    public void stop() {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 0;
        mHandler.dispatchMessage(msg);
       // mHandler.

    }
}