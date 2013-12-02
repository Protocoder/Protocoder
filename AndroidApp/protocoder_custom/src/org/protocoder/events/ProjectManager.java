/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

package org.protocoder.events;

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
import org.protocoder.base.BaseMainApp;
import org.protocoder.utils.FileIO;

import android.content.Context;
import android.util.Log;

//TODO take out the file reading to FileIO 
public class ProjectManager {
    public static final int PROJECT_USER_MADE = 0;
    public static final int PROJECT_EXAMPLE = 1;
    private static final String TAG = "ProjectManager";
    public static int type;
    private Project currentProject;
    String mainFileStr = "main.js";

    private static ProjectManager INSTANCE;

    public static ProjectManager getInstance() {
	if (INSTANCE == null)
	    INSTANCE = new ProjectManager();

	return INSTANCE;
    }

    public void install(final Context c) {

	new Thread(new Runnable() {

	    @Override
	    public void run() {
		File dir = new File(BaseMainApp.baseDir + "/" + BaseMainApp.typeExampleStr);
		FileIO.deleteDir(dir);
		FileIO.copyFileOrDir(c.getApplicationContext(), BaseMainApp.typeExampleStr);
	    }
	}).start();
    }

    public String getCode(Project p) {
	String out = null;
	File f = new File(p.getFolder() + File.separator + mainFileStr);
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
	    Log.e("Project", e.toString());
	}
	return out;
    }

    public void writeNewCode(Project p, String code) {
	writeNewFile(p.getFolder() + File.separator + mainFileStr, code);
    }

    public void writeNewFile(String file, String code) {
	File f = new File(file);

	try {
	    if (!f.exists())
		f.createNewFile();
	    FileOutputStream fo = new FileOutputStream(f);
	    byte[] data = code.getBytes();
	    fo.write(data);
	    fo.flush();
	    fo.close();
	} catch (FileNotFoundException ex) {
	    Log.e("Project", ex.toString());
	} catch (IOException e) {
	    e.printStackTrace();
	    Log.e("Project", e.toString());
	}
    }

    public JSONObject to_json(Project p) {
	JSONObject json = new JSONObject();
	try {
	    json.put("name", p.getName());
	    json.put("type", p.getType());
	} catch (JSONException e) {
	    e.printStackTrace();
	}
	return json;
    }

    public ArrayList<Project> list(int type) {
	ArrayList<Project> projects = new ArrayList<Project>();
	File dir = null;

	Log.d(TAG, "project type" + type + " " + PROJECT_USER_MADE + " " + PROJECT_EXAMPLE);

	switch (type) {
	case PROJECT_USER_MADE:
	    dir = new File(BaseMainApp.projectsDir);
	    if (!dir.exists())
		dir.mkdir();

	    break;

	case PROJECT_EXAMPLE:
	    dir = new File(BaseMainApp.examplesDir);
	    if (!dir.exists())
		dir.mkdir();

	    break;
	default:
	    break;
	}

	File[] all_projects = dir.listFiles();

	for (int i = 0; i < all_projects.length; i++) {
	    File file = all_projects[i];
	    String projectURL = file.getAbsolutePath();
	    String projectName = file.getName();
	    Log.d("PROJECT", "Adding project named " + projectName);
	    boolean containsReadme = false;
	    boolean containsTutorial = false;
	    projects.add(new Project(projectName, projectURL, type, containsReadme, containsTutorial));
	}

	return projects;
    }

    public Project get(String name, int type) {
	Log.d(TAG, "looking for project types " + type);
	ArrayList<Project> projects = list(type);
	for (Project project : projects) {
	    if (name.equals(project.getName())) {
		Log.d("UU", "" + name + " " + type + " " + project.getName());
		setCurrentProject(project);
		return project;
	    }
	}
	return null;
    }

    public Project addNewProject(Context c, String newProjectName, String fileName, int type) {
	String newTemplateCode = FileIO.readAssetFile(c, "templates/new.js");

	if (newTemplateCode == null)
	    newTemplateCode = "";
	String file = FileIO.writeStringToFile(BaseMainApp.projectsDir, newProjectName, newTemplateCode);

	Project newProject = new Project(newProjectName, file, type);

	return newProject;

    }

    public JSONArray listFilesInProject(Project p) {

	File f = new File(p.getFolder());
	File file[] = f.listFiles();
	Log.d("Files", "Size: " + file.length);

	JSONArray array = new JSONArray();
	for (int i = 0; i < file.length; i++) {

	    JSONObject jsonObject = new JSONObject();
	    try {
		jsonObject.put("file_name", file[i].getName());
		jsonObject.put("file_size", file[i].length() / 1024);
	    } catch (JSONException e) {
		e.printStackTrace();
	    }

	    array.put(jsonObject);
	    Log.d("Files", "FileName:" + file[i].getName());
	}

	return array;
    }

    // TODO fix this hack
    public String getProjectURL(Project p) {
	String projectURL = p.getFolder();

	return projectURL;

    }

    public void setCurrentProject(Project project) {
	currentProject = project;

    }

    public Project getCurrentProject() {

	return currentProject;
    }

}
