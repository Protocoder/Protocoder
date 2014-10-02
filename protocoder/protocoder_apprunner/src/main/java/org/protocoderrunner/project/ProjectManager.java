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

package org.protocoderrunner.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

import android.content.Context;
import android.os.Environment;

public class ProjectManager {

    private static final String TAG = "ProjectManager";
    public static String MAIN_FILE_NAME = "main.js";
    public static final String FOLDER_EXAMPLES = "examples";
    public static final String FOLDER_USER_PROJECTS = "projects";
    private static String PROTOCODER_EXTENSION = ".proto";

    private Project currentProject;
    private String remoteIP;

    private static ProjectManager instance;

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }

        return instance;
    }

    public String getBackupFolderUrl() {
        return getProjectFolderUrl("backup");
    }

    public String getProjectFolderUrl(String folder) {
        return getBaseDir() + folder;
    }

    public String getBaseDir() {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AppSettings.APP_FOLDER
                + File.separator;

        return baseDir;
    }

    public String createBackup(Project p) {

        // TODO: Use a thread

        String givenName = getBackupFolderUrl() + File.separator + p.getFolder() + "_" + p.getName();

        //check if file exists and rename it if so
        File f = new File(givenName + ProjectManager.PROTOCODER_EXTENSION);
        int num = 1;
        while(f.exists()) {
            f = new File(givenName + "_" + num++ + ProjectManager.PROTOCODER_EXTENSION);
        }

        //compress
        try {
            FileIO.zipFolder(p.getStoragePath(), f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return the filepath of the backup
        return f.getAbsolutePath();
    }

    public boolean isProjectExisting(String folder, String name) {
        ArrayList<Project> projects = list(folder);

        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getName().equals(name)) {
                return true;
            }
        }


        return false;
    }

    public boolean installProject(String folder, String zipFilePath) {

        // TODO: Use a thread

        //decompress
        try {
            FileIO.extractZip(zipFilePath, getProjectFolderUrl(folder));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public interface InstallListener {
        void onReady();
    }

    public void install(final Context c, final String assetsName, final InstallListener l) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                File dir = new File(getBaseDir() + "/" + assetsName);
                FileIO.deleteDir(dir);
                FileIO.copyFileOrDir(c.getApplicationContext(), assetsName);
                l.onReady();
            }
        }).start();
    }

    public String getCode(Project p) {
        String out = null;
        File f = new File(p.getStoragePath() + File.separator + MAIN_FILE_NAME);
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

    public void writeNewCode(Project p, String code, String fileName) {
        MLog.d(TAG, "--> " + fileName);
        writeNewFile(p.getStoragePath() + File.separator + fileName, code);
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
            // Log.e("Project", ex.toString());
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

    public ArrayList<Project> list(String folder) {
        ArrayList<Project> projects = new ArrayList<Project>();
        File dir = null;

        dir = new File(getProjectFolderUrl(folder));
        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] all_projects = dir.listFiles();

        for (File file : all_projects) {
            String projectURL = file.getAbsolutePath();
            String projectName = file.getName();

            boolean containsReadme = false;
            boolean containsTutorial = false;
            projects.add(new Project(folder, projectName, containsReadme, containsTutorial));
        }

        return projects;
    }

    public Project get(String folder, String name) {
        MLog.d(TAG, "looking for project " + name + " in " + folder);
        ArrayList<Project> projects = list(folder);
        for (Project project : projects) {
            if (name.equals(project.getName())) {
                setCurrentProject(project);
                return project;
            }
        }
        return null;
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

    public ArrayList<File> listFilesInProject(Project p) {
        ArrayList<File> files = new ArrayList<File>();

        File f = new File(p.getStoragePath());
        File file[] = f.listFiles();

        for (File element : file) {
            files.add(element);
        }

        return files;
    }

    public JSONArray listFilesInProjectJSON(Project p) {

        File f = new File(p.getStoragePath());
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

    // TODO fix this hack
    public String getProjectURL(Project p) {
        String projectURL = getProjectFolderUrl(p.folder) + "/" + p.getName();

        return projectURL;
    }

    public void setCurrentProject(Project project) {
        currentProject = project;
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

    public void deleteProject(Project p) {
        File dir = new File(p.getStoragePath());

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                new File(dir, element).delete();
            }
        }
        dir.delete();
    }

}