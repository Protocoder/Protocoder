package com.makewithmoto.apprunner.api;

import java.lang.ref.WeakReference;

import android.app.Activity;

import com.makewithmoto.apprunner.AppRunnerActivity;

public class JInterface {

	protected static final String TAG = "JSInterface";
	public WeakReference<AppRunnerActivity> a;

	public JInterface(Activity appActivity) {
		super();
		this.a = new WeakReference<AppRunnerActivity>(
				(AppRunnerActivity) appActivity);

	}

	public <T> void callback(String fn, T... args) {

		try {
			// c.get().interpreter.callJsFunction(fn,"");
			String f1 = fn;
			boolean firstarg = true;
			if (fn.contains("function")) {
				f1 = "var fn = " + fn + "\n fn(";
				for (T t : args) {
					if (firstarg) {
						firstarg = false;
					} else {
						f1 = f1 + ",";
					}

					f1 = f1 + t;
				}

				f1 = f1 + ");";
			}
			a.get().eval(f1);

		} catch (Throwable e) {

			// TODO
		}

	}

	public void destroy() {
	}

}
