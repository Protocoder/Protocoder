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

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.JavascriptInterface;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.Intents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

public class JDevice extends JInterface {

	private String onSmsReceivedfn;

	public JDevice(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

		((AppRunnerActivity) a).addOnSmsReceivedListener(new onSmsReceivedListener() {

			@Override
			public void onSmsReceived(String number, String msg) {
				callback(onSmsReceivedfn, number, "\"" + msg + "\"");
			}
		});
	}

	@JavascriptInterface
	@APIMethod(description = "makes the phone vibrate", example = "android.vibrate(500);")
	@APIParam(params = { "duration" })
	public void vibrate(String duration) {
		Log.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) a.get().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));

	}

	@JavascriptInterface
	@APIMethod(description = "Change brightness", example = "")
	@APIParam(params = { "number", "message" })
	public void smsSend(String number, String msg) {

		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(number, null, msg, null, null);

	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(number, message)" })
	public void onSmsReceived(final String fn) {
		onSmsReceivedfn = fn;
	}

	@JavascriptInterface
	@APIMethod(description = "Set brightness", example = "")
	@APIParam(params = { "brightness" })
	public void setBrightness(float val) {
		a.get().setBrightness(val);
	}

	@JavascriptInterface
	@APIMethod(description = "Change brightness", example = "")
	public float getBrightness() {
		return a.get().getCurrentBrightness();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void screenAlwaysOn() {
		a.get().setScreenAlwaysOn();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "recepient", "subject", "message" })
	public void launchIntent(String intent) {
		Intent market_intent = new Intent(intent);
		a.get().startActivity(market_intent);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "recepient", "subject", "message" })
	public void openEmailApp(String recepient, String subject, String msg) {
		Intents.sendEmail(a.get(), recepient, subject, msg);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "longitue", "latitude" })
	public void openMapApp(double longitude, double latitude) {
		Intents.openMap(a.get(), longitude, latitude);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void openDial() {
		Intents.openDial(a.get());
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "number" })
	public void call(String number) {
		Intents.call(a.get(), number);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "url" })
	public void openWebApp(String url) {
		Intents.openWeb(a.get(), url);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text" })
	public void openWebSearch(String text) {
		Intents.webSearch(a.get(), text);
	}

	public void stop() {

	}

	public interface onSmsReceivedListener {
		public void onSmsReceived(String number, String msg);
	}

}