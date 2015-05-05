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

package org.protocoder;

import android.content.Context;
import android.content.Intent;

import org.protocoder.editor.EditorActivity;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerService;
import org.protocoderrunner.apprunner.project.Folder;
import org.protocoderrunner.apprunner.project.Project;
import org.protocoderrunner.utils.FileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ProtocoderAppHelper {

    public static void launchScript(Context context, Project p) {
        if (p.getName().toLowerCase().endsWith("service")) {
            Intent intent = new Intent(context, AppRunnerService.class);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            context.startService(intent);
        } else {
            Intent intent = new Intent(context, AppRunnerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            context.startActivity(intent);
        }
    }

    public static void launchSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void launchEditor(Context context, Project project) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public interface InstallListener {
        void onReady();
    }

    public static void installExamples(final Context c, final String assetsName, final InstallListener l) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(ProtocoderAppSettings.getBaseDir() + assetsName);
                FileIO.deleteDir(dir);
                FileIO.copyFileOrDir(c, assetsName);
                l.onReady();
            }
        }).start();
    }

    public static ArrayList<Folder> listFolders(String folder, boolean orderByName) {
        ArrayList<Folder> folders = new ArrayList<Folder>();
        File dir = new File(ProtocoderAppSettings.getFolderPath(folder));

        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();

        if (orderByName) {
            Arrays.sort(all_projects);
        }

        for (File file : all_projects) {
            String projectURL = file.getAbsolutePath();
            String projectName = file.getName();
            folders.add(new Folder(folder, projectName));
        }

        return folders;
    }

    public static ArrayList<Project> listProjects(String folder, boolean orderByName) {
        ArrayList<Project> projects = new ArrayList<Project>();
        File dir = new File(ProtocoderAppSettings.getFolderPath(folder));

        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();

        if (orderByName) {
            Arrays.sort(all_projects);
        }

        for (File file : all_projects) {
            String projectURL = file.getAbsolutePath();
            String projectName = file.getName();

            projects.add(new Project(folder, projectName));
        }

        return projects;
    }


}
