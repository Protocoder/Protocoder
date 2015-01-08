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

package org.protocoderrunner.apprunner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.protocoderrunner.utils.MLog;

import java.lang.ref.WeakReference;

public class PInterface {

    public Handler mHandler = new Handler(Looper.getMainLooper());

	protected String TAG = getClass().getSimpleName();

	private WeakReference<Context> mContext;
    private AppRunnerFragment mFragment;
    private AppRunnerActivity mActivity;

    public PInterface(Context context) {
		super();
		this.mContext = new WeakReference <Context> (context.getApplicationContext());

    }

	public void destroy() {
	}

    public void initForParentFragment(AppRunnerFragment fragment) {
        this.mFragment = fragment;
        this.mActivity = (AppRunnerActivity)(mFragment.getActivity());
    }

    public Context getContext() { return mContext.get(); }
    public AppRunnerFragment getFragment() { return mFragment; }
    public AppRunnerActivity getActivity() { return mActivity; }

    public void setActivity(AppRunnerActivity activity) {
        this.mActivity = activity;
    }
}
