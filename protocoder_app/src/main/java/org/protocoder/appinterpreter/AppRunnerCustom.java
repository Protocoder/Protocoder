package org.protocoder.appinterpreter;

import android.content.Context;

import org.protocoderrunner.AppRunner;


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
        interp.addJavaObjectToJs("protocoderApp", protocoderApp);

        return this;
    }
}
