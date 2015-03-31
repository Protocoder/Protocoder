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

package org.protocoder.appApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Settings {

    private final SharedPreferences mSharedPrefs;

    public Settings(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
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

    public void setScreenOn(boolean isChecked) {
        mSharedPrefs.edit().putBoolean("pref_screen_on", isChecked).commit();
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

    public boolean setColor(String color) {
        return mSharedPrefs.edit().putString("pref_app_color", "#FFFFFFFF").commit();
    }

    public String getColor() {
        return mSharedPrefs.getString("pref_app_color", "#FFFFFFFF");
    }
}
