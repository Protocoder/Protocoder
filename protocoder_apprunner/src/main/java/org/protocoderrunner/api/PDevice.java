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
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import org.protocoderrunner.AppRunnerFragment;
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.api.other.ApplicationInfo;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apidoc.annotation.ProtoObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.Intents;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ProtoObject
public class PDevice extends ProtoBase {

    private BroadcastReceiver batteryReceiver;
    private BroadcastReceiver onNotification;
    private BroadcastReceiver smsReceiver;

    private ReturnInterface mOnKeyDownfn;
    private ReturnInterface mOnKeyUpfn;
    private ReturnInterface mOnKeyEventfn;

    private boolean isKeyPressInit = false;

    public String deviceId;


    /**
     * Interface for key up / down
     */
    public interface onKeyListener {
        void onKeyDown(KeyEvent event);
        void onKeyUp(KeyEvent event);
        void onKeyEvent(KeyEvent event);
    }

    public PDevice(AppRunner appRunner) {
        super(appRunner);
    }

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);
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


    @ProtoMethod(description = "send an sms to a given number", example = "")
    @ProtoMethodParam(params = {"number", "message"})
    public void smsSend(String number, String msg) {
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(number, null, msg, null, null);
    }

    @ProtoMethod(description = "Gives back the number and sms of the sender", example = "")
    @ProtoMethodParam(params = {"function(number, message)"})
    public void onSmsReceived(final ReturnInterface callback) {

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

                ReturnObject ret = new ReturnObject();
                ret.put("from", pNumber);
                ret.put("message", "body");

                callback.event(ret);
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
        Intents.call(getActivity(), number);
    }

    @ProtoMethod(description = "Open the default web browser with a given Url", example = "")
    @ProtoMethodParam(params = {"url"})
    public void openWebApp(String url) {
        Intents.openWeb(getActivity(), url);
    }

    @ProtoMethod(description = "Open the search app with the given text", example = "")
    @ProtoMethodParam(params = {"text"})
    public void openWebSearch(String text) {
        Intents.webSearch(getActivity(), text);
    }


    //TODO reenable this
    // @ProtoMethod(description = "opens a file with a given app provided as package name ", example = "")
    // @ProtoMethodParam(params = {"fileName", "packageName"})
    public void runApp(final String src, String packageName) {
        final String projectPath = null; //ProjectManager.getInstance().getCurrentProject().getStoragePath();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + projectPath + "/" + src), packageName);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getContext().startActivity(intent);
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
    public void battery(final ReturnInterface callback) {
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

                ReturnObject o = new ReturnObject();

                o.put("level", level);
                o.put("temperature", temp);
                o.put("connected", isConnected);
                o.put("scale", scale);
                o.put("temperature", temp);
                o.put("voltage", voltage);

                callback.event(o);
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
    @ProtoMethod(description = "Get some device information", example = "")
    @ProtoMethodParam(params = {""})
    public ReturnObject info() {
        ReturnObject ret = new ReturnObject();

        // density dpi
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        ret.put("screenDpi", metrics.densityDpi);
        ret.put("screenWidth", metrics.widthPixels);
        ret.put("screenHeight", metrics.heightPixels);

        // id
        ret.put("androidId", Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID));

        // imei
        // deviceInfo.imei = ((TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        ret.put("manufacturer", Build.MANUFACTURER);
        ret.put("model", Build.MODEL);
        ret.put("display", Build.DISPLAY);
        ret.put("versionRelease", Build.VERSION.RELEASE);

        String os = "";
        if (AndroidUtils.isVersionMarshmallow()) {
            os = Build.VERSION.BASE_OS;
        }
        ret.put("os", os);

        ret.put("board", Build.BOARD);
        ret.put("brand", Build.BRAND);
        ret.put("device", Build.DEVICE);
        ret.put("fingerPrint", Build.FINGERPRINT);
        ret.put("host", Build.HOST);
        ret.put("id", Build.ID);
        ret.put("keyboardPresent",  getContext().getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);

        ret.put("totalMem", Runtime.getRuntime().totalMemory());
        ret.put("usedMem", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        ret.put("maxMem", Runtime.getRuntime().maxMemory());

        return ret;
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

    @ProtoMethod(description = "Check if the device has GPS", example = "")
    public boolean hasGPS() {
        PackageManager pm = getContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
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

    public boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
    }

    private void showNotificationsManager() {
        if (AndroidUtils.isVersionMarshmallow()) {
            getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } else {
            getActivity().startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }

    }

    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContext().getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getContext().getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    public void onNewNotification(final ReturnInterface callback) {
        if (!isNotificationServiceRunning()) {
            showNotificationsManager();
        }

        onNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ReturnObject ret = new ReturnObject();

                ret.put("package", intent.getStringExtra("package"));
                ret.put("title", intent.getStringExtra("title"));
                ret.put("text", intent.getStringExtra("text"));

                callback.event(ret);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(onNotification, new IntentFilter("Msg"));
    }


    /**
     * Loads the list of installed applications in mApplications.
     */
    public List listInstalledApps() {
        ArrayList<ApplicationInfo> mApplications = new ArrayList<ApplicationInfo>();;

        // get installed apps
        PackageManager pm = getContext().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        if (apps != null) {
            int count = apps.size();

            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);

                application.title = info.loadLabel(pm);
                application.packageName = info.activityInfo.packageName;
                application.setActivity(new ComponentName(info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.iconDrawable = info.activityInfo.loadIcon(pm);
                application.permission = info.activityInfo.permission;

                MLog.d(TAG, application.title + " " + application.packageName); // " " + application.iconURL);

                application.iconBitmap = ((BitmapDrawable) application.iconDrawable).getBitmap();

                // Bitmap icon =
                // BitmapFactory.decodeResource(this.getResources(),
                // application.icon);


                /*
                save icon in path
                 */
                /*
                String path = Environment.getExternalStorageDirectory().toString();
                application.iconURL = path + "/" + application.packageName + ".png";

                try {
                    FileOutputStream out = new FileOutputStream(application.iconURL);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                mApplications.add(application);
            }
        }

        return  mApplications;
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