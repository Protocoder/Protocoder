package org.protocoderrunner.apprunner.api.other;

import android.content.Context;


public class PFtpServer extends org.protocoderrunner.network.FtpServer {

    final String TAG = "PFtpServer";

    public PFtpServer(Context c, int port) {
        super(port);

        WhatIsRunning.getInstance().add(this);
    }

    public void addUser(String name, String pass, String root, boolean canWrite) {
        super.addUser(name, pass, root, canWrite);
    }

    public void start() {
        super.start();
    }

    public void stop() {
        super.stop();
    }
}
