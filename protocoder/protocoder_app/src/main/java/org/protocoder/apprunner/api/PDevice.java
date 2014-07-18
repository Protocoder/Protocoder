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

package org.protocoder.apprunner.api;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.Intents;
import org.protocoder.utils.MLog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.gson.Gson;

public class PDevice extends PInterface {

	private onSmsReceivedCB onSmsReceivedfn;
	private BroadcastReceiver batteryReceiver;

	public PDevice(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

		((AppRunnerActivity) a).addOnSmsReceivedListener(new onSmsReceivedListener() {

			@Override
			public void onSmsReceived(String number, String msg) {
				onSmsReceivedfn.event(number, msg);
			}
		});
	}

	@ProtocoderScript
	@APIMethod(description = "makes the phone vibrate", example = "android.vibrate(500);")
	@APIParam(params = { "duration" })
	public void vibrate(String duration) {
		MLog.d("TAG", "vibrate...");
		Vibrator v = (Vibrator) a.get().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(Integer.parseInt(duration));

	}

	@ProtocoderScript
	@APIMethod(description = "Change brightness", example = "")
	@APIParam(params = { "number", "message" })
	public void smsSend(String number, String msg) {

		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(number, null, msg, null, null);

	}

	// --------- onSmsReceived ---------//
	interface onSmsReceivedCB {
		void event(String number, String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(number, message)" })
	public void onSmsReceived(final onSmsReceivedCB fn) {
		onSmsReceivedfn = fn;
	}

	@ProtocoderScript
	@APIMethod(description = "Set brightness", example = "")
	@APIParam(params = { "brightness" })
	public void setBrightness(float val) {
		a.get().setBrightness(val);
	}

	@ProtocoderScript
	@APIMethod(description = "Change brightness", example = "")
	public void setGlobalBrightness(int b) {
		a.get().setGlobalBrightness(b);
	}

	@ProtocoderScript
	@APIMethod(description = "Change brightness", example = "")
	public float getBrightness() {
		return a.get().getCurrentBrightness();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void screenAlwaysOn(boolean b) {
		a.get().setScreenAlwaysOn(b);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public boolean isScreenOn() {
		return a.get().isScreenOn();
	}

	// @ProtocoderScript
	// @APIMethod(description = "", example = "")
	public void goToSleep() {
		a.get().goToSleep();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setScreenTimeout(int time) {
		a.get().setScreenTimeout(time);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public boolean isAirplaneMode() {
		return a.get().isAirplaneMode();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public boolean isTablet() {
		return a.get().isTablet();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void enableSoundEffects(boolean b) {
		a.get().setEnableSoundEffects(b);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setWakeLock(boolean b) {
		a.get().setWakeLock(b);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "recepient", "subject", "message" })
	public void launchIntent(String intent) {
		Intent market_intent = new Intent(intent);
		a.get().startActivity(market_intent);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "recepient", "subject", "message" })
	public void openEmailApp(String recepient, String subject, String msg) {
		Intents.sendEmail(a.get(), recepient, subject, msg);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "longitue", "latitude" })
	public void openMapApp(double longitude, double latitude) {
		Intents.openMap(a.get(), longitude, latitude);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void openDial() {
		Intents.openDial(a.get());
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "number" })
	public void call(String number) {
		Intents.call(a.get(), number);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "url" })
	public void openWebApp(String url) {
		Intents.openWeb(a.get(), url);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text" })
	public void openWebSearch(String text) {
		Intents.webSearch(a.get(), text);
	}

	// --------- battery ---------//
	interface StartBateryListenerCB {
		void event(BatteryReturn o);
	}

	class BatteryReturn {
		public int level;
		public int temperature;
		public boolean connected;
	}

	public void setClipboard(String label, String text) {
		ClipboardManager clipboard = (ClipboardManager) a.get().getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
	}

	public String getClipboard(String label, String text) {
		ClipboardManager clipboard = (ClipboardManager) a.get().getSystemService(Context.CLIPBOARD_SERVICE);
		return clipboard.getPrimaryClip().getItemAt(clipboard.getPrimaryClip().getItemCount()).getText().toString();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void startBatteryListener(final StartBateryListenerCB cb) {
		WhatIsRunning.getInstance().add(this);
		batteryReceiver = new BroadcastReceiver() {
			int scale = -1;
			int level = -1;
			int voltage = -1;
			int temp = -1;
			boolean isConnected = false;
			private int status;
			private final boolean alreadyKilled = false;

			@Override
			public void onReceive(Context context, Intent intent) {
				level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
				// isCharging =
				// intent.getBooleanExtra(BatteryManager.EXTRA_PLUGGED, false);
				// status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

				if (status == BatteryManager.BATTERY_PLUGGED_AC) {
					isConnected = true;
				} else if (status == BatteryManager.BATTERY_PLUGGED_USB) {
					isConnected = true;
				} else {
					isConnected = false;
				}

				BatteryReturn o = new BatteryReturn();

				o.level = level;
				o.temperature = temp;
				o.connected = isConnected;

				// plugConnected = isConnected;
				cb.event(o);
				Log.d("BATTERY", "level is " + level + " is connected " + isConnected);
			}
		};

		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		a.get().registerReceiver(batteryReceiver, filter);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public float getBatteryLevel() {
		Intent batteryIntent = a.get().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		// Error checking that probably isn't needed but I added just in case.
		if (level == -1 || scale == -1) {
			return 50.0f;
		}

		return ((float) level / (float) scale) * 100.0f;
	}

	class DeviceInfo {
		public int screenDpi;
		public String androidId;
		public String imei;
		public String versionRelease;
		public String sdk;
		public String board;
		public String brand;
		public String device;
		public String host;
		public String fingerPrint;
		public String id;
		public String cpuAbi;
		public String cpuAbi2;

		public String toJSON() {
			return new Gson().toJson(this);
		}

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public DeviceInfo getInfo() {
		DeviceInfo deviceInfo = new DeviceInfo();

		// density dpi
		DisplayMetrics metrics = new DisplayMetrics();
		a.get().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		deviceInfo.screenDpi = metrics.densityDpi;

		// id
		deviceInfo.androidId = Secure.getString(a.get().getContentResolver(), Secure.ANDROID_ID);

		// imei
		deviceInfo.imei = ((TelephonyManager) a.get().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

		deviceInfo.versionRelease = Build.VERSION.RELEASE;
		deviceInfo.versionRelease = Build.VERSION.INCREMENTAL;
		deviceInfo.sdk = Build.VERSION.SDK;
		deviceInfo.board = Build.BOARD;
		deviceInfo.brand = Build.BRAND;
		deviceInfo.device = Build.DEVICE;
		deviceInfo.fingerPrint = Build.FINGERPRINT;
		deviceInfo.host = Build.HOST;
		deviceInfo.id = Build.ID;
		deviceInfo.cpuAbi = Build.CPU_ABI;
		deviceInfo.cpuAbi2 = Build.CPU_ABI2;

		return deviceInfo;
	}

	class Memory {
		public long total;
		public long used;
		public long max;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public Memory getMemory() {
		Memory mem = new Memory();

		mem.total = Runtime.getRuntime().totalMemory();
		mem.used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		mem.max = Runtime.getRuntime().maxMemory();

		return mem;
	}

	public void stop() {
		a.get().unregisterReceiver(batteryReceiver);
	}

	public interface onSmsReceivedListener {
		public void onSmsReceived(String number, String msg);
	}

}