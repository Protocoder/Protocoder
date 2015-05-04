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
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apprunner.project.AppRunnerProjectManager;
import org.protocoderrunner.apprunner.project.Project;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ProjectManager extends AppRunnerProjectManager {

    private static String PROTOCODER_EXTENSION = ".proto";

    private Project currentProject;
    private String remoteIP;

    public ProjectManager(Context context, Project p) {
        super(context, p);
    }

    public ProjectManager(Context context) {
        super(context, null);
    }

    public String getBackupFolderUrl() {
        return getProjectFolderUrl("backup");
    }

    public String getProjectFolderUrl(String folder) {
        return getBaseDir() + folder;
    }

    public String getBaseDir() {
        String baseDir;

        baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + AppSettings.PROTOCODER_FOLDER + File.separator;

        return baseDir;
    }

    public String createBackup(Project p) {

        // TODO: Use thread

        String givenName = getBackupFolderUrl() + File.separator + p.getFolder() + "_" + p.getName();

        //check if file exists and rename it if so
        File f = new File(givenName + ProjectManager.PROTOCODER_EXTENSION);
        int num = 1;
        while (f.exists()) {
            f = new File(givenName + "_" + num++ + PROTOCODER_EXTENSION);
        }

        //compress
        try {
            FileIO.zipFolder(getProjectPath(), f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return the filepath of the backup
        return f.getAbsolutePath();
    }

    public boolean isProjectExisting(String folder, String name) {
        ArrayList<Project> projects = ProtocoderAppHelper.listProjects(folder, false);

        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean installProject(String folder, String zipFilePath) {

        // TODO: Use thread

        //decompress
        try {
            FileIO.extractZip(zipFilePath, getProjectFolderUrl(folder));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Get code from assets
    public String getCodeFromAssets(Context c, Project p) {
        return FileIO.readAssetFile(c, getProjectPath() + File.separator + MAIN_FILENAME);
    }

    // Get code from sdcard
    public String getCode() {
        String out = null;
        File f = new File(getProjectPath() + MAIN_FILENAME);
        try {
            InputStream in = new FileInputStream(f);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int i;
            try {
                i = in.read();
                while (i != -1) {
                    buf.write(i);
                    i = in.read();
                }
                in.close();
            } catch (IOException ex) {
            }
            out = buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // Log.e("Project", e.toString());
        }
        return out;
    }

    public void writeNewCode(String code, String fileName) {
        writeNewFile(getProjectPath() + fileName, code);
    }

    public void writeNewFile(String file, String code) {
        File f = new File(file);

        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(f);
            byte[] data = code.getBytes();
            fo.write(data);
            fo.flush();
            fo.close();

        } catch (FileNotFoundException ex) {
            MLog.e(TAG, ex.toString());
        } catch (IOException e) {
            e.printStackTrace();
            // Log.e("Project", e.toString());
        }
    }

    public JSONObject toJson(Project p) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", p.getName());
            json.put("folder", p.getFolder());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public Project addNewProject(Context c, String newProjectName, String folder, String fileName) {
        String newTemplateCode = FileIO.readAssetFile(c, "templates/new.js");

        if (newTemplateCode == null) {
            newTemplateCode = "";
        }
        FileIO.writeStringToFile(getProjectFolderUrl(folder), newProjectName, newTemplateCode);
        Project newProject = new Project(folder, newProjectName);

        return newProject;
    }

    public ArrayList<File> listFilesInProject() {
        ArrayList<File> files = new ArrayList<File>();

        File f = new File(getProjectPath());
        File file[] = f.listFiles();

        for (File element : file) {
            files.add(element);
        }

        return files;
    }

    public JSONArray listFilesInProjectJSON(Project p) {

        File f = new File(getProjectPath());
        File file[] = f.listFiles();
        MLog.d("Files", "Size: " + file.length);

        JSONArray array = new JSONArray();
        for (File element : file) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("file_name", element.getName());
                jsonObject.put("file_size", element.length() / 1024);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            array.put(jsonObject);
            MLog.d("Files", "FileName:" + element.getName());
        }

        return array;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP + ":" + AppSettings.HTTP_PORT;
    }

    public String getRemoteIP() {
        String url = remoteIP;
        // add / if doesnt contain it
        if (url.charAt(url.length() - 1) != '/') {
            url += "/";
        }

        return url;
    }

    public void deleteProject() {
        File dir = new File(getProjectPath());

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                new File(dir, element).delete();
            }
        }
        dir.delete();
    }

}