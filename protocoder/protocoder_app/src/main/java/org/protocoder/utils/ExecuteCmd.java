package org.protocoder.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.protocoder.sensors.WhatIsRunning;

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

    public void stop() {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 0;
        mHandler.dispatchMessage(msg);
       // mHandler.

    }
}