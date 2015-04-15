package org.protocoder.appinterpreter;

import android.content.Context;

import org.protocoderrunner.apprunner.AppRunner;


public class AppRunnerCustom extends AppRunner {

    public ProtocoderApp protocoderApp;

    public AppRunnerCustom(Context context) {
        super(context);
    }

    public AppRunnerCustom initDefaultObjects() {
        protocoderApp = new ProtocoderApp(this);
        return (AppRunnerCustom) super.initDefaultObjects();
    }

    public AppRunnerCustom initInterpreter() {
        super.initInterpreter();
        interp.interpreter.addObjectToInterface("protocoderApp", protocoderApp);

        return this;
    }
}
