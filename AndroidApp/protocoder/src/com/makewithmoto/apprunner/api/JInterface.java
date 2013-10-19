/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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
			a.get().interp.eval(f1);

		} catch (Throwable e) {

			// TODO
		}

	}

	public void destroy() {
	}

}
