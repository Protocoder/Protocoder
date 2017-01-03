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

package org.protocoder.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.protocoder.MainActivity;
import org.protocoder.gui.HelpActivity;
import org.protocoder.gui.InfoScriptActivity;
import org.protocoder.gui.LicenseActivity;
import org.protocoder.gui.editor.EditorActivity;
import org.protocoder.gui.settings.NewUserPreferences;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoder.gui.settings.SettingsActivity;
import org.protocoderrunner.AppRunnerActivity;
import org.protocoderrunner.AppRunnerService;
import org.protocoderrunner.apprunner.AppRunnerHelper;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

import java.util.Map;

public class ProtoAppHelper {

    private static final String TAG = ProtoAppHelper.class.getSimpleName();
    private static boolean multiWindowEnabled = true;

    public static void launchScript(Context context, Project p) {

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId("launched script"));

        Map<String, Object> map = AppRunnerHelper.readProjectProperties(context, p);
        boolean isService = (boolean) map.get("background_service");

        if (isService) {
            Intent intent = new Intent(context, AppRunnerService.class);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra(Project.SERVER_PORT, ProtocoderSettings.HTTP_PORT);
            intent.putExtra("device_id", (String) NewUserPreferences.getInstance().get("device_id"));
            context.startService(intent);
        } else {
            Intent intent = new Intent(context, AppRunnerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra(Project.SERVER_PORT, ProtocoderSettings.HTTP_PORT);
            intent.putExtra("device_id", (String) NewUserPreferences.getInstance().get("device_id"));
            MLog.d(TAG, "1 ------------> launching side by side " + AndroidUtils.isVersionN());

            if (AndroidUtils.isVersionN() && multiWindowEnabled) {
                MLog.d(TAG, "2 ------------> launching side by side");
                // intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
            }
            context.startActivity(intent);
        }
    }

    public static void launchSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void launchEditor(Context context, Project p) {
        MLog.d(TAG, "starting editor");
        Intent intent = new Intent(context, EditorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Project.FOLDER, p.getFolder());
        intent.putExtra(Project.NAME, p.getName());

        context.startActivity(intent);
    }

    public static void launchLicense(Context context) {
        Intent intent = new Intent(context, LicenseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchScriptInfoActivity(Context context, Project p) {
        Intent intent = new Intent(context, InfoScriptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Project.FOLDER, p.getFolder());
        intent.putExtra(Project.NAME, p.getName());

        context.startActivity(intent);
    }


    public static void launchHelp(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launchWifiSettings(Context context) {
        // context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
        }
    }

    public static void launchHotspotSettings(Context context) {
        // context.startActivity(new Intent(WifiManager.AC));

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
        intent.setComponent(cn);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
        }
    }
}
