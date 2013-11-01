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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.APIRequires;
import com.makewithmoto.apidoc.annotation.APIVersion;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.sensors.WhatIsRunning;
import com.makewithmoto.utils.Intents;

public class JAndroid extends JInterface {

	private String onKeyDownfn;
	private String onKeyUpfn;
	private String onSmsReceivedfn;

	public JAndroid(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

		((AppRunnerActivity) a).addOnKeyListener(new onKeyListener() {

			@Override
			public void onKeyUp(int keyCode) {
				callback(onKeyDownfn, keyCode);
			}

			@Override
			public void onKeyDown(int keyCode) {
				callback(onKeyUpfn, keyCode);
			}
		});

		((AppRunnerActivity) a)
				.addOnSmsReceivedListener(new onSmsReceivedListener() {

					@Override
					public void onSmsReceived(String number, String msg) {
						callback(onSmsReceivedfn, number, "\"" + msg + "\"");
					}
				});

	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void launchScript(String name, int type) {
		Intent intent = new Intent(a.get(), AppRunnerActivity.class);
		intent.putExtra("projectName", name);
		intent.putExtra("projectType", type);
		//a.get().startActivity(intent);
	//	String code = StrUtils.generateRandomString();
		a.get().startActivityForResult(intent, 22);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIVersion(minLevel = "2")
	@APIRequires("android.permission.INTERNET")
	public void returnValueToScript(String returnValue) {
		Intent output = new Intent();
		output.putExtra("return", returnValue);
		a.get().setResult(22, output);
		a.get().finish();
	}
	
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void returnResult(String data) {

		Bundle conData = new Bundle();
		conData.putString("param_result", data);
		Intent intent = new Intent();
		intent.putExtras(conData);
		a.get().setResult(a.get().RESULT_OK, intent);
		a.get().finish();

	}

	@JavascriptInterface
	@APIMethod(description = "makes the phone vibrate", example = "android.vibrate(500);")
	public void vibrate(String duration) {
		Log.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) a.get().getSystemService(
				Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));
	}

	@JavascriptInterface
	@APIMethod(description = "Change brightness", example = "")
	public void smsSend(String number, String msg) {

		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(number, null, msg, null, null);

	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void onSmsReceived(final String fn) {
		onSmsReceivedfn = fn;
	}

	@JavascriptInterface
	@APIMethod(description = "Set brightness", example = "")
	public void setBrightness(float val) {
		((AppRunnerActivity) a.get()).setBrightness(val);
	}

	@JavascriptInterface
	@APIMethod(description = "Change brightness", example = "")
	public void getBrightness() {
		((AppRunnerActivity) a.get()).getCurrentBrightness();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void screenAlwaysOn() {
		((AppRunnerActivity) a.get()).setScreenAlwaysOn();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void openEmailApp(String recepient, String subject, String msg) {
		Intents.sendEmail(a.get(), recepient, subject, msg);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
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
	public void call(String number) {
		Intents.call(a.get(), number);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void openWebApp(String url) {
		Intents.openWeb(a.get(), url);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void openWebSearch(String text) {
		Intents.webSearch(a.get(), text);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void onKeyDown(final String fn) {
		onKeyDownfn = fn;
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void onKeyUp(final String fn) {
		onKeyUpfn = fn;
	}


	public void stop() {

	}

	public interface onKeyListener {
		public void onKeyDown(int keyCode);

		public void onKeyUp(int keyCode);
	}

	public interface onSmsReceivedListener {
		public void onSmsReceived(String number, String msg);
	}

}