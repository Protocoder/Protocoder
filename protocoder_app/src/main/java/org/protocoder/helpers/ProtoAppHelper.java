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

import android.content.Context;
import android.content.Intent;

import org.protocoder.gui.LicenseActivity;
import org.protocoder.gui.editor.EditorActivity;
import org.protocoder.gui.settings.SettingsActivity;
import org.protocoderrunner.AppRunnerActivity;
import org.protocoderrunner.AppRunnerService;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

public class ProtoAppHelper {

    private static final String TAG = ProtoAppHelper.class.getSimpleName();
    private static boolean multiWindowEnabled = true;

    public static void launchScript(Context context, Project p) {
        if (p.getName().toLowerCase().endsWith("service")) {
            Intent intent = new Intent(context, AppRunnerService.class);
            intent.putExtra(Project.FOLDER, p.getPath());
            intent.putExtra(Project.NAME, p.getName());
            context.startService(intent);
        } else {
            Intent intent = new Intent(context, AppRunnerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Project.FOLDER, p.getPath());
            intent.putExtra(Project.NAME, p.getName());

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
        Intent intent = new Intent(context, EditorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Project.FOLDER, p.getPath());
        intent.putExtra(Project.NAME, p.getName());
        context.startActivity(intent);
    }

    public static void launchLicense(Context context) {
        Intent intent = new Intent(context, LicenseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
