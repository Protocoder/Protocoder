package org.protocoder;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by biquillo on 17/06/16.
 */
public class App extends Application {

    public static MyLifecycleHandler myLifecycleHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        myLifecycleHandler = new MyLifecycleHandler();
        registerActivityLifecycleCallbacks(myLifecycleHandler);
        Fabric.with(this, new Crashlytics());
    }
}
