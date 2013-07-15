package com.makewithmoto.events;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.makewithmoto.base.BaseMainApp;

//TODO take out the file reading to FileIO 
public class Project {
	String name; 
	String url;

	public Project(String projectName, String projectURL) {
		this.name = projectName;
		this.url = projectURL;
	}
	
	public String getName() { return this.name; }
	public String getUrl() { return this.url; }
	public String getCode() {
		String out = null;
		File f = new File(getUrl() + File.separator + "script.js");
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
			} catch (IOException ex) {}
			out = buf.toString();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Project", e.toString());
		}
		return out;
	}
	
	public void writeNewCode(String code) {
		writeNewFile(getUrl() + File.separator + "script.js", code);
	}
	
	public void writeNewFile(String file, String code) {
		File f = new File(file);
		
		try {
			if (!f.exists()) f.createNewFile();
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
	
	public JSONObject to_json() {
		JSONObject json = new JSONObject();
		try {
			json.put("name", getName());
			json.put("url", getUrl());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static ArrayList<Project> all() {
		ArrayList<Project> projects = new ArrayList<Project>();
				
		File dir = new File(BaseMainApp.baseDir);
		if (!dir.exists()) dir.mkdir();
		File[] all_projects = dir.listFiles();
		
		for (int i = 0; i < all_projects.length; i++) {
			File file = all_projects[i];
			String projectURL = file.getAbsolutePath();
			String projectName = file.getName();
			Log.d("PROJECT", "Adding project named " + projectName);
			projects.add(new Project(projectName, projectURL));
		}
		
		return projects;
	}
	
	public static Project get(String name) {
		ArrayList<Project> projects = all();
		
		for (Project project: projects) {
			if (name.equals(project.getName())) {
				return project;
			}
		}
		return null;
	}
}
