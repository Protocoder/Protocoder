package org.protocoderrunner;

import android.content.Context;

/**
 * Created by josejuansanchez on 29/03/15.
 */
public class AppRunnerContext {
    private static AppRunnerContext instance;
    private Context mAppContext;

    public static AppRunnerContext get() {
        if (instance == null)
            instance = new AppRunnerContext();
        return instance;
    }

    public void init(Context context){
        if (mAppContext == null) {
            this.mAppContext = context.getApplicationContext();
        }
    }

    public Context getAppContext() {
        return get().mAppContext;
    }

}
