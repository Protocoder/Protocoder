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

package org.protocoderrunner.apprunner.api;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.Intents;

public class PDevice extends PInterface {

    private BroadcastReceiver batteryReceiver;
    private BroadcastReceiver onNotification;

    public PDevice(Context a) {
        super(a);
        WhatIsRunning.getInstance().add(this);

    }


    @ProtoMethod(description = "makes the phone vibrate", example = "android.vibrate(500);")
    @ProtoMethodParam(params = {"duration"})
    public void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }


    @ProtoMethod(description = "send an sms to the given number", example = "")
    @ProtoMethodParam(params = {"number", "message"})
    public void smsSend(String number, String msg) {
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(number, null, msg, null, null);
    }

    // --------- onSmsReceived ---------//
    interface onSmsReceivedCB {
        void event(String number, String responseString);
    }

    public interface onSmsReceivedListener {
        public void onSmsReceived(String number, String msg);
    }

    @ProtoMethod(description = "Gives back the number and sms of the sender", example = "")
    @ProtoMethodParam(params = {"function(number, message)"})
    public void onSmsReceived(final onSmsReceivedCB fn) {
        getActivity().addOnSmsReceivedListener(new onSmsReceivedListener() {

            @Override
            public void onSmsReceived(String number, String msg) {
                fn.event(number, msg);
            }
        });
    }


    @ProtoMethod(description = "Set brightness", example = "")
    @ProtoMethodParam(params = {"brightness"})
    public void brightness(float val) {
        getActivity().setBrightness(val);
    }


    @ProtoMethod(description = "Set the global brightness from 0 to 255", example = "")
    @ProtoMethodParam(params = {"brightness"})
    public void globalBrightness(int b) {
        AndroidUtils.setGlobalBrightness(getContext(), b);
    }


    @ProtoMethod(description = "Get the current brightness", example = "")
    public float brightness() {
        return getActivity().getCurrentBrightness();
    }


    @ProtoMethod(description = "Set the screen always on", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void screenAlwaysOn(boolean b) {
        getActivity().setScreenAlwaysOn(b);
    }


    @ProtoMethod(description = "Check if the scrren is on", example = "")
    public boolean isScreenOn() {
        return AndroidUtils.isScreenOn(getContext());
    }

    //
    // @APIMethod(description = "", example = "")
    //public void goToSleep() {
    //	AndroidUtils.goToSleep(mContext);
    //}


    @ProtoMethod(description = "Set the screen timeout", example = "")
    @ProtoMethodParam(params = {"time"})
    public void screenTimeout(int time) {
        AndroidUtils.setScreenTimeout(getContext(), time);
    }


    @ProtoMethod(description = "Check if is in airplane mode", example = "")
    public boolean isAirplaneMode() {
        return AndroidUtils.isAirplaneMode(getContext());
    }


    @ProtoMethod(description = "Check what type of device is", example = "")
    @ProtoMethodParam(params = {""})
    public String type() {
        if (AndroidUtils.isTablet(getContext())) {
            return "tablet";
        } else {
            return "phone";
        }
    }


    @ProtoMethod(description = "Prevent the device suspend at any time. Good for long living operations.", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void wakeLock(boolean b) {
        AndroidUtils.setWakeLock(getContext(), b);
    }


    @ProtoMethod(description = "Launch an intent", example = "")
    @ProtoMethodParam(params = {"intent"})
    public void launchIntent(String intent) {
        Intent market_intent = new Intent(intent);
        getContext().startActivity(market_intent);
    }


    @ProtoMethod(description = "Open the default e-mail app", example = "")
    @ProtoMethodParam(params = {"recipient", "subject", "message"})
    public void openEmailApp(String recipient, String subject, String msg) {
        Intents.sendEmail(getContext(), recipient, subject, msg);
    }


    @ProtoMethod(description = "Open the default Map app", example = "")
    @ProtoMethodParam(params = {"longitude", "latitude"})
    public void openMapApp(double longitude, double latitude) {
        Intents.openMap(getContext(), longitude, latitude);
    }


    @ProtoMethod(description = "Open the phone dial", example = "")
    public void openDial() {
        Intents.openDial(getContext());
    }


    @ProtoMethod(description = "Call a given phone number", example = "")
    @ProtoMethodParam(params = {"number"})
    public void call(String number) {
        Intents.call(getContext(), number);
    }


    @ProtoMethod(description = "Open the default web browser with a given Url", example = "")
    @ProtoMethodParam(params = {"url"})
    public void openWebApp(String url) {
        Intents.openWeb(getContext(), url);
    }


    @ProtoMethod(description = "Open the search app with the given text", example = "")
    @ProtoMethodParam(params = {"text"})
    public void openWebSearch(String text) {
        Intents.webSearch(getContext(), text);
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


    @ProtoMethod(description = "Copy the content into the clipboard", example = "")
    @ProtoMethodParam(params = {"label", "text"})
    public void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
    }


    @ProtoMethod(description = "Get the content from the clipboard", example = "")
    @ProtoMethodParam(params = {"label", "text"})
    public String getFromClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboard.getPrimaryClip().getItemAt(clipboard.getPrimaryClip().getItemCount()).getText().toString();
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void battery(final StartBateryListenerCB cb) {
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
        getContext().registerReceiver(batteryReceiver, filter);
    }


    @ProtoMethod(description = "Get the current device battery level", example = "")
    @ProtoMethodParam(params = {""})
    public float battery() {
        Intent batteryIntent = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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


    @ProtoMethod(description = "Get some device information", example = "")
    @ProtoMethodParam(params = {""})
    public DeviceInfo info() {
        DeviceInfo deviceInfo = new DeviceInfo();

        // density dpi
        DisplayMetrics metrics = new DisplayMetrics();

        //TODO reenable this
        //contextUi.get().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        deviceInfo.screenDpi = metrics.densityDpi;

        // id
        deviceInfo.androidId = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);

        // imei
        deviceInfo.imei = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

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


    @ProtoMethod(description = "Get memory usage", example = "")
    @ProtoMethodParam(params = {""})
    public Memory memory() {
        Memory mem = new Memory();

        mem.total = Runtime.getRuntime().totalMemory();
        mem.used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        mem.max = Runtime.getRuntime().maxMemory();

        return mem;
    }


    @ProtoMethod(description = "Check if the device has camera", example = "")
    public boolean hasCamera() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    @ProtoMethod(description = "Check if the device has front", example = "")
    public boolean hasFrontCamera() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }


    @ProtoMethod(description = "Check if the device has camera flash", example = "")
    public boolean hasCameraFlash() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    @ProtoMethod(description = "Check if the device has bluetooth", example = "")
    public boolean hasBluetooth() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }


    @ProtoMethod(description = "Check if the device has Bluetooth Low Energy", example = "")
    public boolean isBluetoothLEAvailable() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }


    @ProtoMethod(description = "Check if the device has microphone", example = "")
    public boolean hasMic() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }


    @ProtoMethod(description = "Check if the device has wifi", example = "")
    public boolean hasWifi() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
    }


    @ProtoMethod(description = "Check if the device has mobile communication", example = "")
    public boolean hasMobileCommunication() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }


    @ProtoMethod(description = "Check if the device has accelerometer", example = "")
    public boolean hasAccelerometer() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }


    @ProtoMethod(description = "Check if the device has compass", example = "")
    public boolean isCompassAvailable() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
    }


    @ProtoMethod(description = "Check if the device has gyroscope", example = "")
    public boolean hasGyroscope() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }


    @ProtoMethod(description = "Check if the device has GPS", example = "")
    public boolean hasGPS() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }


    @ProtoMethod(description = "Check if the device has light sensor", example = "")
    public boolean hasLightSensor() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
    }


    @ProtoMethod(description = "Check if the device has proximity sensor", example = "")
    public boolean hasProximitySensor() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);
    }


    @ProtoMethod(description = "Check if the device has step detector", example = "")
    public boolean hasStepDetector() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
    }


    @ProtoMethod(description = "Check if the device has barometer", example = "")
    public boolean hasBarometer() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
    }


    public interface OnNotificationCallback {
        public void event(String[] notification);
    }

    public void onNewNotification(final OnNotificationCallback callback) {
        final String[] notification = new String[3];

        onNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notification[0] = intent.getStringExtra("package");
                notification[1] = intent.getStringExtra("title");
                notification[2] = intent.getStringExtra("text");

                callback.event(notification);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(onNotification, new IntentFilter("Msg"));
    }

    public void stop() {
        getContext().unregisterReceiver(batteryReceiver);
        getContext().unregisterReceiver(onNotification);
        batteryReceiver = null;
        onNotification = null;
    }

}