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

package org.protocoderrunner.apprunner.api;

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

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.Intents;

public class PDevice extends PInterface {

    private BroadcastReceiver batteryReceiver;

    public PDevice(Context a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

	}

	@ProtocoderScript
	@APIMethod(description = "makes the phone vibrate", example = "android.vibrate(500);")
	@APIParam(params = { "duration" })
	public void vibrate(int duration) {
		Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(duration);
	}

	@ProtocoderScript
	@APIMethod(description = "send an sms to the given number", example = "")
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
	@APIMethod(description = "Gives back the number and sms of the sender", example = "")
	@APIParam(params = { "function(number, message)" })
	public void onSmsReceived(final onSmsReceivedCB fn) {
        mActivity.addOnSmsReceivedListener(new onSmsReceivedListener() {

            @Override
            public void onSmsReceived(String number, String msg) {
                fn.event(number, msg);
            }
        });
	}

	@ProtocoderScript
	@APIMethod(description = "Set brightness", example = "")
	@APIParam(params = { "brightness" })
	public void setBrightness(float val) {
		mActivity.setBrightness(val);
	}

	@ProtocoderScript
	@APIMethod(description = "Set the global brightness from 0 to 255", example = "")
    @APIParam(params = { "brightness" })
    public void setGlobalBrightness(int b) {
		AndroidUtils.setGlobalBrightness(mContext, b);
	}

	@ProtocoderScript
	@APIMethod(description = "Get the current brightness", example = "")
	public float getBrightness() {
		return mActivity.getCurrentBrightness();
	}

	@ProtocoderScript
	@APIMethod(description = "Set the screen always on", example = "")
    @APIParam(params = { "boolean" })
    public void setScreenAlwaysOn(boolean b) {
		mActivity.setScreenAlwaysOn(b);
	}

	@ProtocoderScript
	@APIMethod(description = "Check if the scrren is on", example = "")
	public boolean isScreenOn() {
		return AndroidUtils.isScreenOn(mContext);
	}

	// @ProtocoderScript
	// @APIMethod(description = "", example = "")
	//public void goToSleep() {
	//	AndroidUtils.goToSleep(mContext);
	//}

	@ProtocoderScript
	@APIMethod(description = "Set the screen timeout", example = "")
    @APIParam(params = { "time" })
    public void setScreenTimeout(int time) {
		AndroidUtils.setScreenTimeout(mContext, time);
	}

	@ProtocoderScript
	@APIMethod(description = "Check if is in airplane mode", example = "")
	public boolean isAirplaneMode() {
		return AndroidUtils.isAirplaneMode(mContext);
	}

	@ProtocoderScript
	@APIMethod(description = "Check what type of device is", example = "")
    @APIParam(params = { "" })
    public String getType() {
        if (AndroidUtils.isTablet(mContext)) {
            return "tablet";
        } else {
            return "phone";
        }
	}

	@ProtocoderScript
	@APIMethod(description = "Prevent the device suspend at any time. Good for long living operations.", example = "")
    @APIParam(params = { "boolean" })
    public void setWakeLock(boolean b) {
		AndroidUtils.setWakeLock(mContext, b);
	}

	@ProtocoderScript
	@APIMethod(description = "Launch an intent", example = "")
	@APIParam(params = { "intent" })
	public void launchIntent(String intent) {
		Intent market_intent = new Intent(intent);
		mContext.startActivity(market_intent);
	}

	@ProtocoderScript
	@APIMethod(description = "Open the default e-mail app", example = "")
	@APIParam(params = { "recipient", "subject", "message" })
	public void openEmailApp(String recipient, String subject, String msg) {
		Intents.sendEmail(mContext, recipient, subject, msg);
	}

	@ProtocoderScript
	@APIMethod(description = "Open the default Map app", example = "")
	@APIParam(params = { "longitude", "latitude" })
	public void openMapApp(double longitude, double latitude) {
		Intents.openMap(mContext, longitude, latitude);
	}

	@ProtocoderScript
	@APIMethod(description = "Open the phone dial", example = "")
	public void openDial() {
		Intents.openDial(mContext);
	}

	@ProtocoderScript
	@APIMethod(description = "Call a given phone number", example = "")
	@APIParam(params = { "number" })
	public void call(String number) {
		Intents.call(mContext, number);
	}

	@ProtocoderScript
	@APIMethod(description = "Open the default web browser with a given Url", example = "")
	@APIParam(params = { "url" })
	public void openWebApp(String url) {
		Intents.openWeb(mContext, url);
	}

	@ProtocoderScript
	@APIMethod(description = "Open the search app with the given text", example = "")
	@APIParam(params = { "text" })
	public void openWebSearch(String text) {
		Intents.webSearch(mContext, text);
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

    @ProtocoderScript
    @APIMethod(description = "Copy the content into the clipboard", example = "")
    @APIParam(params = { "label", "text" })
	public void setClipboard(String label, String text) {
		ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
	}

    @ProtocoderScript
    @APIMethod(description = "Get the content from the clipboard", example = "")
    @APIParam(params = { "label", "text" })
	public String getClipboard(String label, String text) {
		ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
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
		mContext.registerReceiver(batteryReceiver, filter);
	}

	@ProtocoderScript
	@APIMethod(description = "Get the device battery level", example = "")
	@APIParam(params = { "" })
	public float getBatteryLevel() {
		Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
	@APIMethod(description = "Get some device information", example = "")
	@APIParam(params = { "" })
	public DeviceInfo getInfo() {
		DeviceInfo deviceInfo = new DeviceInfo();

		// density dpi
		DisplayMetrics metrics = new DisplayMetrics();

		//TODO reenable this
		//contextUi.get().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		deviceInfo.screenDpi = metrics.densityDpi;

		// id
		deviceInfo.androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

		// imei
		deviceInfo.imei = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

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
	@APIMethod(description = "Get memory usage", example = "")
	@APIParam(params = { "" })
	public Memory getMemory() {
		Memory mem = new Memory();

		mem.total = Runtime.getRuntime().totalMemory();
		mem.used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		mem.max = Runtime.getRuntime().maxMemory();

		return mem;
	}


	public void stop() {
		mContext.unregisterReceiver(batteryReceiver);
	}

	public interface onSmsReceivedListener {
		public void onSmsReceived(String number, String msg);
	}

}