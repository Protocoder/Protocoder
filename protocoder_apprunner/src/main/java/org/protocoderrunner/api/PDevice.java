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

package org.protocoderrunner.api;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.google.gson.Gson;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.Intents;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;

public class PDevice extends ProtoBase {

    private BroadcastReceiver batteryReceiver;
    private BroadcastReceiver onNotification;
    private BroadcastReceiver smsReceiver;

    private ReturnInterface mOnKeyDownfn;
    private ReturnInterface mOnKeyUpfn;
    private ReturnInterface mOnKeyEventfn;

    private boolean isKeyPressInit = false;


    /**
     * Interface for key up / down
     */
    public interface onKeyListener {
        public void onKeyDown(KeyEvent event);
        public void onKeyUp(KeyEvent event);
        public void onKeyEvent(KeyEvent event);
    }

    public PDevice(AppRunner appRunner) {
        super(appRunner);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void getInputDevices() {
        InputManager inputManager = (InputManager) getAppRunner().getAppContext().getSystemService(Context.INPUT_SERVICE);
        int[] devicesId = inputManager.getInputDeviceIds();

        MLog.d(TAG, "" + devicesId.length);

        ArrayList gameControllerDeviceIds = new ArrayList();


        for (int i = 0; i < devicesId.length; i++) {
            InputDevice device = inputManager.getInputDevice(devicesId[i]);

            MLog.d(TAG, "controller number: " + device.getControllerNumber());
            MLog.d(TAG, "keyboard type: " + device.getKeyboardType());
            MLog.d(TAG, "product id: " + device.getProductId());
            MLog.d(TAG, "name: " + device.getName());
            MLog.d(TAG, "descriptor: " + device.getDescriptor());
            KeyCharacterMap qq = device.getKeyCharacterMap();

            int sources = device.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(devicesId[i])) {
                    gameControllerDeviceIds.add(devicesId[i]);
                }
            }
        }

        Handler handler = new Handler();
        inputManager.registerInputDeviceListener(new InputManager.InputDeviceListener() {
            @Override
            public void onInputDeviceAdded(int deviceId) {
                MLog.d(TAG, "added " + deviceId);
            }

            @Override
            public void onInputDeviceRemoved(int deviceId) {
                MLog.d(TAG, "removed " + deviceId);
            }

            @Override
            public void onInputDeviceChanged(int deviceId) {
                MLog.d(TAG, "removed " + deviceId);
            }
        }, handler);

    }


    private ReturnObject keyEventToJs(KeyEvent event) {
        ReturnObject o = new ReturnObject();
        o.put("key", event.getKeyCode());
        o.put("id", event.getDeviceId());

        String action = "unknown";
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                action = "down";
                break;

            case KeyEvent.ACTION_UP:
                action = "up";
                break;

            case KeyEvent.ACTION_MULTIPLE:
                action = "multiple";
                break;
        }

        o.put("action", action);

        o.put("alt", event.isAltPressed());
        o.put("ctrl", event.isCtrlPressed());
        o.put("fn", event.isFunctionPressed());
        o.put("meta", event.isMetaPressed());
        o.put("chars", event.getCharacters());
        o.put("number", event.getNumber());

        return o;
    }

    private void keyInit() {
        if (isKeyPressInit) return;
        isKeyPressInit = true;

        (getActivity()).addOnKeyListener(new onKeyListener() {
            @Override
            public void onKeyUp(KeyEvent event) {
                if (mOnKeyUpfn != null) mOnKeyUpfn.event(keyEventToJs(event));
            }

            @Override
            public void onKeyDown(KeyEvent event) {
                if (mOnKeyDownfn != null) mOnKeyDownfn.event(keyEventToJs(event));
            }

            @Override
            public void onKeyEvent(KeyEvent event) {
                if (mOnKeyEventfn != null) mOnKeyEventfn.event(keyEventToJs(event));
            }
        });
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(keyNumber)"})
    public void onKeyDown(ReturnInterface fn) {
        keyInit();
        mOnKeyDownfn = fn;
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(keyNumber)"})
    public void onKeyUp(ReturnInterface fn) {
        keyInit();
        mOnKeyUpfn = fn;
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(keyNumber)"})
    public void onKeyEvent(ReturnInterface fn) {
        keyInit();
        mOnKeyEventfn = fn;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void ignoreVolumeKeys(boolean b) {
        getActivity().ignoreVolumeEnabled = b;
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void ignoreBackKey(boolean b) {
        getActivity().ignoreBackEnabled = b;
    }

    @ProtoMethod(description = "makes the phone vibrate", example = "android.vibrate(500)")
    @ProtoMethodParam(params = {"duration"})
    public void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

    @ProtoMethod(description = "makes the phone vibrate", example = "android.vibrate(500)")
    @ProtoMethodParam(params = {"duration"})
    public void vibrate(long[] pattern, int repeat) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern, repeat);
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

    @ProtoMethod(description = "Gives back the number and sms of the sender", example = "")
    @ProtoMethodParam(params = {"function(number, message)"})
    public void onSmsReceived(final onSmsReceivedCB fn) {

        // SMS receive
        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

                // Process the sms format and extract body and phone number
                msg = msg.replace("\n", "");
                String body = msg.substring(msg.lastIndexOf(":") + 1, msg.length());
                String pNumber = msg.substring(0, msg.lastIndexOf(":"));

                fn.event(pNumber, body);
            }
        };
        getContext().registerReceiver(smsReceiver, intentFilter);
    }

    @ProtoMethod(description = "Get the current brightness", example = "")
    @ProtoMethodParam(params = {""})
    public float brightness() {
        int brightness = -1;

        try {
            brightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return brightness;
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

    @ProtoMethod(description = "Get the current device orientation", example = "")
    @ProtoMethodParam(params = {""})
    public String orientation() {
        int orientation = getContext().getResources().getConfiguration().orientation;
        String orientationStr = "";

        switch (orientation) {
            case 1:
                orientationStr = "portrait";
                break;
            case 2:
                orientationStr = "landscape";
                break;
            default:
                orientationStr = "unknown";
        }

        return orientationStr;
    }

    public class DeviceInfo {
        public String androidId;
        public String board;
        public String brand;
        public String cpuAbi;
        public String cpuAbi2;
        public String device;
        public String display;
        public String fingerPrint;
        public String host;
        public String id;
        public String imei;
        public boolean keyboardPresent;
        public String manufacturer;
        public String model;
        public String os;
        public int screenDpi;
        public int screenWidth;
        public int screenHeight;
        public String sdk;
        public String versionRelease;

        public String toJSON() {
            return new Gson().toJson(this);
        }
    }

    @ProtoMethod(description = "Get some device information", example = "")
    @ProtoMethodParam(params = {""})
    public DeviceInfo info() {
        DeviceInfo deviceInfo = new DeviceInfo();

        // density dpi
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        deviceInfo.screenDpi = metrics.densityDpi;
        deviceInfo.screenWidth = metrics.widthPixels;
        deviceInfo.screenHeight = metrics.heightPixels;

        // id
        deviceInfo.androidId = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);

        // imei
        deviceInfo.imei = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        deviceInfo.manufacturer = Build.MANUFACTURER;
        deviceInfo.model = Build.MODEL;
        deviceInfo.display = Build.DISPLAY;
        deviceInfo.versionRelease = Build.VERSION.RELEASE;
        deviceInfo.os = Build.VERSION.BASE_OS;
        deviceInfo.board = Build.BOARD;
        deviceInfo.brand = Build.BRAND;
        deviceInfo.device = Build.DEVICE;
        deviceInfo.fingerPrint = Build.FINGERPRINT;
        deviceInfo.host = Build.HOST;
        deviceInfo.id = Build.ID;
        deviceInfo.cpuAbi = Build.CPU_ABI;
        deviceInfo.cpuAbi2 = Build.CPU_ABI2;
        deviceInfo.keyboardPresent = getContext().getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;


        return deviceInfo;
    }

    public class Memory {
        public long total;
        public long used;
        public long max;

        public String summary() {
            return used + " (" + max + ") " + "/ " + total;
        }
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

    @Override
    public void __stop() {
        getContext().unregisterReceiver(batteryReceiver);
        getContext().unregisterReceiver(onNotification);
        getContext().unregisterReceiver(smsReceiver);

        batteryReceiver = null;
        onNotification = null;
    }

}