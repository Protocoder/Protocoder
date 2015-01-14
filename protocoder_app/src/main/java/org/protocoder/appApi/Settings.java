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

package org.protocoder.appApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.protocoderrunner.base.BaseActivity;


public class Settings {

    private final SharedPreferences mSharedPrefs;
    private final BaseActivity mContext;

    public Settings(Protocoder protocoder) {
        mContext = protocoder.mActivityContext;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

    }


    //---------------- save / load methods

    public void setId(String id) {
        mSharedPrefs.edit().putString("pref_id", id);
        mSharedPrefs.edit().commit();
    }

    public String getId() {
        String id = mSharedPrefs.getString("pref_id", "-1");

        return id;
    }

    public void setListPreference(boolean id) {
        mSharedPrefs.edit().putBoolean("pref_list_mode", id).commit();
    }

    public boolean getListPreference() {
        boolean pref = mSharedPrefs.getBoolean("pref_list_mode", false);

        return pref;
    }


    public boolean getScreenOn() {
        boolean pref = mSharedPrefs.getBoolean("pref_screen_on", false);

        return pref;
    }

    public boolean getBackgroundMode() {
        boolean pref = mSharedPrefs.getBoolean("pref_background_mode", false);

        return pref;
    }

    public boolean getNotifyNewVersion() {
        boolean pref = mSharedPrefs.getBoolean("pref_notify_new_version", false);

        return pref;
    }

    public void setConnectionAlert(boolean b) {
        mSharedPrefs.edit().putBoolean("pref_connection_alert", b).commit();
    }

    public boolean getConnectionAlert(Context c) {
        boolean pref = mSharedPrefs.getBoolean("pref_connection_alert", false);

        return pref;
    }

    public void setFtp(boolean checked, String userName, String saltedPassword) {
        mSharedPrefs.edit().putBoolean("pref_ftp_checked", checked).commit();
        mSharedPrefs.edit().putString("pref_ftp_username", userName).commit();
        mSharedPrefs.edit().putString("pref_ftp_password", saltedPassword).commit();
    }

    public boolean getFtpChecked() {
        boolean pref = mSharedPrefs.getBoolean("pref_ftp_checked", false);

        return pref;
    }

    public String getFtpUserName() {
        return mSharedPrefs.getString("pref_ftp_username", "");
    }


    public String getFtpUserPassword() {
        return mSharedPrefs.getString("pref_ftp_password", "");
    }


    public boolean setNewVersionCheckEnabled(boolean enable) {
        return mSharedPrefs.edit().putBoolean("pref_new_version_check", enable).commit();
    }

    public boolean getNewVersionCheckEnabled() {
        return mSharedPrefs.getBoolean("pref_new_version_check", true);
    }
}
