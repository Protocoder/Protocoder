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

package org.protocoderrunner.apprunner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class PInterface {

    protected String TAG = getClass().getSimpleName();

    private AppRunner mAppRunner;
    public Handler mHandler = new Handler(Looper.getMainLooper());

    private Context mContext;
    private AppRunnerFragment mFragment;
    private AppRunnerService mService;
    private AppRunnerActivity mActivity;

    public PInterface(AppRunner appRunner) {
        super();
        this.mAppRunner = appRunner;
        this.mContext = appRunner.getAppContext();

        appRunner.whatIsRunning.add(this);
    }

    public void destroy() {
    }

    public void initForParentFragment(AppRunnerFragment fragment) {
        if (fragment != null) {
            this.mFragment = fragment;
            this.mActivity = (AppRunnerActivity) (mFragment.getActivity());
        }
    }

    public void initForParentService(AppRunnerService service) {
        this.mService = service;
    }

    public AppRunner getAppRunner() {
        return mAppRunner;
    }

    public Context getContext() {
        return mContext;
    }

    public AppRunnerFragment getFragment() {
        return mFragment;
    }
    public AppRunnerService getService() {
        return mService;
    }

    public AppRunnerActivity getActivity() {
        return mActivity;
    }

    public void setActivity(AppRunnerActivity activity) {
        this.mActivity = activity;
    }
}
