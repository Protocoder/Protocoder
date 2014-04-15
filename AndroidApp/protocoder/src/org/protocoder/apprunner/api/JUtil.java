/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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

package org.protocoder.apprunner.api;

import java.util.ArrayList;
import java.util.Iterator;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.sensors.WhatIsRunning;

import android.app.Activity;
import android.os.Handler;

public class JUtil extends JInterface {

	private final Handler handler;
	ArrayList<Runnable> rl = new ArrayList<Runnable>();

	public JUtil(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);
		handler = new Handler();

	}

	// --------- getRequest ---------//
	interface getRequestCB {
		void event(int eventType, String responseString);
	}

	public class Looper {
		Runnable task;
		public int delay;

		Looper(final int duration, final LooperCB callbackkfn) {
			delay = duration;
			task = new Runnable() {
				@Override
				public void run() {
					callbackkfn.event();
					handler.postDelayed(this, delay);
				}
			};
			handler.post(task);

			rl.add(task);
		}

		@ProtocoderScript
		@APIMethod(description = "", example = "")
		@APIParam(params = { "duration" })
		public void setDelay(int duration) {
			this.delay = duration;
		}

		@ProtocoderScript
		@APIMethod(description = "", example = "")
		public void stop() {
			handler.removeCallbacks(task);
		}
	}

	// --------- Looper ---------//
	interface LooperCB {
		void event();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "milliseconds", "function()" })
	public Looper loop(final int duration, final LooperCB callbackkfn) {

		return new Looper(duration, callbackkfn);
	}

	// --------- delay ---------//
	interface delayCB {
		void event();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "milliseconds", "function()" })
	public void delay(final int duration, final delayCB fn) {

		Runnable task = new Runnable() {
			@Override
			public void run() {
				// handler.postDelayed(this, duration);
				fn.event();
				handler.removeCallbacks(this);
				rl.remove(this);
			}
		};
		handler.postDelayed(task, duration);

		rl.add(task);
	}

	public void stopAllTimers() {
		Iterator<Runnable> ir = rl.iterator();
		while (ir.hasNext()) {
			handler.removeCallbacks(ir.next());
			// handler.post(ir.next());
		}
	}

	public void stop() {
		stopAllTimers();
	}

}