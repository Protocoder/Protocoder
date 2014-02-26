/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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

package org.protocoder.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.protocoder.apidoc.APIManager;
import org.protocoder.apprunner.api.JAndroid;
import org.protocoder.apprunner.api.JCamera;
import org.protocoder.apprunner.api.JConsole;
import org.protocoder.apprunner.api.JDashboard;
import org.protocoder.apprunner.api.JEditor;
import org.protocoder.apprunner.api.JFileIO;
import org.protocoder.apprunner.api.JMedia;
import org.protocoder.apprunner.api.JNetwork;
import org.protocoder.apprunner.api.JProtocoder;
import org.protocoder.apprunner.api.JPureData;
import org.protocoder.apprunner.api.JSensors;
import org.protocoder.apprunner.api.JUI;
import org.protocoder.apprunner.api.JUtil;
import org.protocoder.apprunner.api.JVideo;
import org.protocoder.apprunner.api.boards.JIOIO;
import org.protocoder.apprunner.api.boards.JMakr;
import org.protocoder.apprunner.api.dashboard.JDashboardButton;
import org.protocoder.apprunner.api.dashboard.JDashboardHTML;
import org.protocoder.apprunner.api.dashboard.JDashboardImage;
import org.protocoder.apprunner.api.dashboard.JDashboardLabel;
import org.protocoder.apprunner.api.dashboard.JDashboardPlot;
import org.protocoder.apprunner.api.widgets.JButton;
import org.protocoder.apprunner.api.widgets.JCanvasView;
import org.protocoder.apprunner.api.widgets.JCheckBox;
import org.protocoder.apprunner.api.widgets.JEditText;
import org.protocoder.apprunner.api.widgets.JImageButton;
import org.protocoder.apprunner.api.widgets.JImageView;
import org.protocoder.apprunner.api.widgets.JPlotView;
import org.protocoder.apprunner.api.widgets.JRadioButton;
import org.protocoder.apprunner.api.widgets.JSeekBar;
import org.protocoder.apprunner.api.widgets.JSwitch;
import org.protocoder.apprunner.api.widgets.JTextView;
import org.protocoder.apprunner.api.widgets.JToggleButton;
import org.protocoder.apprunner.api.widgets.JWebView;
import org.protocoder.events.Events;
import org.protocoder.events.Events.ProjectEvent;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.utils.FileIO;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import de.greenrobot.event.EventBus;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class MyHTTPServer extends NanoHTTPD {
    public static final String TAG = "myHTTPServer";
    private WeakReference<Context> ctx;
    private String WEBAPP_DIR = "webapp/";
    String projectURLPrefix = "/apps";

    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
	{
	    put("css", "text/css");
	    put("htm", "text/html");
	    put("html", "text/html");
	    put("xml", "text/xml");
	    put("txt", "text/plain");
	    put("asc", "text/plain");
	    put("gif", "image/gif");
	    put("jpg", "image/jpeg");
	    put("jpeg", "image/jpeg");
	    put("png", "image/png");
	    put("mp3", "audio/mpeg");
	    put("m3u", "audio/mpeg-url");
	    put("mp4", "video/mp4");
	    put("ogv", "video/ogg");
	    put("flv", "video/x-flv");
	    put("mov", "video/quicktime");
	    put("swf", "application/x-shockwave-flash");
	    put("js", "application/javascript");
	    put("pdf", "application/pdf");
	    put("doc", "application/msword");
	    put("ogg", "application/x-ogg");
	    put("zip", "application/octet-stream");
	    put("exe", "application/octet-stream");
	    put("class", "application/octet-stream");
	}
    };

    private static MyHTTPServer instance;

    public static MyHTTPServer getInstance(Context aCtx, int port) {
	Log.d(TAG, "launching web server...");
	if (instance == null)
	    try {
		Log.d(TAG, "ok...");
		instance = new MyHTTPServer(aCtx, port);
	    } catch (IOException e) {
		Log.d(TAG, "nop :(...");
		e.printStackTrace();
	    }

	return instance;
    }

    public MyHTTPServer(Context aCtx, int port) throws IOException {
	super(port);
	ctx = new WeakReference<Context>(aCtx);
	String ip = NetworkUtils.getLocalIpAddress(aCtx);
	if (ip == null) {
	    Log.d(TAG, "No IP found. Please connect to a newwork and try again");
	} else {
	    Log.d(TAG, "Launched server at http://" + ip.toString() + ":" + port);
	}
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {

	Response res = null;

	try {

	    Log.d(TAG, "received String" + uri + " " + method + " " + header + " " + " " + parms + " " + files);

	    if (uri.startsWith(projectURLPrefix)) {

		// checking if we are inside the directory so we sandbox the app
		// TODO its pretty hack so this deserves coding it again
		Project p = ProjectManager.getInstance().getCurrentProject();

		String projectFolder = "/" + p.getTypeString() + "/" + p.getName();
		Log.d("qq", "project folder is " + projectFolder);
		if (uri.replace(projectURLPrefix, "").contains(projectFolder)) {
		    Log.d("qq", "inside project");
		    return serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header, new File(p
			    .getFolder()), false);
		} else {
		    Log.d("qq", "outside project");
		    new Response(HTTP_NOTFOUND, MIME_HTML, "resource not found");
		}

	    }

	    // file upload
	    if (!files.isEmpty()) {

		String name = parms.getProperty("name").toString();
		String fileType = parms.getProperty("fileType").toString();

		int projectType = -1;
		if (fileType.equals("user")) {
		    projectType = ProjectManager.PROJECT_USER_MADE;
		} else if (fileType.equals("example")) {
		    projectType = ProjectManager.PROJECT_EXAMPLE;
		}

		Project p = ProjectManager.getInstance().get(name, projectType);

		File src = new File(files.getProperty("pic").toString());
		File dst = new File(p.getFolder() + "/" + parms.getProperty("pic").toString());
		Log.d("qwqw", p.getFolder() + "/" + parms.getProperty("pic").toString());
		Log.d(TAG, " " + src.toString() + " " + dst.toString());

		FileIO.copyFile(src, dst);

		JSONObject data = new JSONObject();
		data.put("result", "OK");

		return new Response("200", MIME_TYPES.get("txt"), data.toString());

	    }

	    // webapi
	    JSONObject data = new JSONObject();

	    // splitting the string into command and parameters
	    String[] m = uri.split("=");

	    String userCmd = m[0];
	    String params = "";
	    if (m.length > 1) {
		params = m[1];
	    }
	    Log.d(TAG, "cmd " + userCmd);

	    if (userCmd.contains("cmd")) {
		JSONObject obj = new JSONObject(params);

		Project foundProject;
		String name, url, newCode;
		String type;
		int projectType = -1;

		Log.d(TAG, "params " + obj.toString(2));

		String cmd = obj.getString("cmd");

		// fetch code
		if (cmd.equals("fetch_code")) {
		    Log.d(TAG, "--> fetch code");
		    name = obj.getString("name");
		    type = obj.getString("type");

		    if (type.equals("user")) {
			projectType = ProjectManager.PROJECT_USER_MADE;
		    } else if (type.equals("example")) {
			projectType = ProjectManager.PROJECT_EXAMPLE;
		    }

		    Project p = ProjectManager.getInstance().get(name, projectType);

		    // TODO add type
		    data.put("code", ProjectManager.getInstance().getCode(p));

		    // list apps
		} else if (cmd.equals("list_apps")) {
		    Log.d(TAG, "--> list apps");

		    type = obj.getString("filter");

		    if (type.equals("user")) {
			projectType = ProjectManager.PROJECT_USER_MADE;
		    } else if (type.equals("example")) {
			projectType = ProjectManager.PROJECT_EXAMPLE;
		    }
		    ArrayList<Project> projects = ProjectManager.getInstance().list(projectType);
		    JSONArray projectsArray = new JSONArray();
		    for (Project project : projects) {
			projectsArray.put(ProjectManager.getInstance().to_json(project));
		    }
		    data.put("projects", projectsArray);

		    // run app
		} else if (cmd.equals("run_app")) {
		    Log.d(TAG, "--> run app");

		    // Save and run
		    name = obj.getString("name");
		    type = obj.getString("type");

		    if (type.equals("user")) {
			projectType = ProjectManager.PROJECT_USER_MADE;
		    } else if (type.equals("example")) {
			projectType = ProjectManager.PROJECT_EXAMPLE;
		    }

		    ProjectEvent evt = new ProjectEvent(ProjectManager.getInstance().get(name, projectType), "run");
		    EventBus.getDefault().post(evt);
		    ALog.i("Running...");

		    // run app
		} else if (cmd.equals("execute_code")) {
		    Log.d(TAG, "--> execute code");

		    // Save and run
		    String code = parms.get("codeToSend").toString();

		    Events.ExecuteCodeEvent evt = new Events.ExecuteCodeEvent(code);
		    EventBus.getDefault().post(evt);
		    ALog.i("Execute...");

		    // save_code
		} else if (cmd.equals("push_code")) {
		    Log.d(TAG, "--> push code " + method + " " + header);
		    Log.d(TAG, "---->" + parms.toString() + " " + files.toString());
		    Log.d(TAG, "" + parms.get("code"));
		    name = parms.get("name").toString();
		    newCode = parms.get("code").toString();
		    Log.d("ww", newCode);

		    type = parms.get("type").toString();

		    if (type.equals("user")) {
			projectType = ProjectManager.PROJECT_USER_MADE;
		    } else if (type.equals("example")) {
			projectType = ProjectManager.PROJECT_EXAMPLE;
		    }

		    // add type
		    Project p = ProjectManager.getInstance().get(name, projectType);
		    ProjectManager.getInstance().writeNewCode(p, newCode);
		    data.put("project", ProjectManager.getInstance().to_json(p));
		    ProjectEvent evt = new ProjectEvent(p, "save");
		    EventBus.getDefault().post(evt);

		    ALog.i("Saved");

		    // list files in project
		} else if (cmd.equals("list_files_in_project")) {
		    Log.d(TAG, "--> create new project");
		    name = obj.getString("name");
		    type = obj.getString("type");

		    if (type.equals("user")) {
			projectType = ProjectManager.PROJECT_USER_MADE;
		    } else if (type.equals("list_examples")) {
			projectType = ProjectManager.PROJECT_EXAMPLE;
		    }

		    Project p = new Project(name, projectType);
		    JSONArray array = ProjectManager.getInstance().listFilesInProject(p);
		    data.put("files", array);
		    // ProjectEvent evt = new ProjectEvent(p, "new");
		    // EventBus.getDefault().post(evt);

		} else if (cmd.equals("create_new_project")) {
		    Log.d(TAG, "--> create new project");

		    name = obj.getString("name");
		    Project p = new Project(name, "", ProjectManager.PROJECT_USER_MADE);
		    ProjectEvent evt = new ProjectEvent(p, "new");
		    EventBus.getDefault().post(evt);

		    Project newProject = ProjectManager.getInstance().addNewProject(ctx.get(), name, name,
			    ProjectManager.PROJECT_USER_MADE);

		    // remove app
		} else if (cmd.equals("remove_app")) {
		    Log.d(TAG, "--> remove app");

		    // get help
		} else if (cmd.equals("get_documentation")) {
		    Log.d(TAG, "--> get documentation");

		    // TODO do it automatically
		    APIManager.getInstance().clear();
		    APIManager.getInstance().addClass(JAndroid.class);
		    APIManager.getInstance().addClass(JCamera.class);
		    APIManager.getInstance().addClass(JConsole.class);
		    APIManager.getInstance().addClass(JEditor.class);
		    APIManager.getInstance().addClass(JDashboard.class);
		    APIManager.getInstance().addClass(JFileIO.class);
		    APIManager.getInstance().addClass(JIOIO.class);
		    APIManager.getInstance().addClass(JMakr.class);
		    APIManager.getInstance().addClass(JMedia.class);
		    APIManager.getInstance().addClass(JNetwork.class);
		    APIManager.getInstance().addClass(JProtocoder.class);
		    APIManager.getInstance().addClass(JPureData.class);
		    APIManager.getInstance().addClass(JSensors.class);
		    APIManager.getInstance().addClass(JUtil.class);
		    APIManager.getInstance().addClass(JUI.class);
		    APIManager.getInstance().addClass(JVideo.class);

		    APIManager.getInstance().addClass(JCheckBox.class);
		    APIManager.getInstance().addClass(JTextView.class);
		    APIManager.getInstance().addClass(JDashboardButton.class);
		    APIManager.getInstance().addClass(JDashboardHTML.class);
		    APIManager.getInstance().addClass(JDashboardImage.class);
		    APIManager.getInstance().addClass(JDashboardLabel.class);
		    APIManager.getInstance().addClass(JDashboardPlot.class);

		    APIManager.getInstance().addClass(JButton.class);
		    APIManager.getInstance().addClass(JCanvasView.class);
		    APIManager.getInstance().addClass(JEditText.class);
		    APIManager.getInstance().addClass(JImageButton.class);
		    APIManager.getInstance().addClass(JImageView.class);
		    APIManager.getInstance().addClass(JPlotView.class);
		    APIManager.getInstance().addClass(JRadioButton.class);
		    APIManager.getInstance().addClass(JSeekBar.class);
		    APIManager.getInstance().addClass(JSwitch.class);
		    APIManager.getInstance().addClass(JToggleButton.class);
		    APIManager.getInstance().addClass(JWebView.class);

		    data.put("api", APIManager.getInstance().getDocumentation());
		}

		res = new Response("200", MIME_TYPES.get("txt"), data.toString());

	    } else if (uri.contains("apps")) {
		String[] u = uri.split("/");

		// server webui
	    } else {

		res = sendWebAppFile(uri, method, header, parms, files);
	    }

	} catch (Exception e) {
	    Log.d(TAG, "response error " + e.toString());
	}

	// return serveFile(uri, header, servingFolder, true);
	return res;
    }

    private Response sendProjectFile(String uri, String method, Properties header, Properties parms, Properties files) {

	Response res = null;

	// Clean up uri
	uri = uri.trim().replace(File.separatorChar, '/');
	Log.d(TAG, uri);

	// have the object build the directory structure, if needed.
	AssetManager am = ctx.get().getAssets();
	try {
	    Log.d(TAG, WEBAPP_DIR + uri);
	    InputStream fi = am.open(WEBAPP_DIR + uri);

	    // Get MIME type from file name extension, if possible
	    String mime = null;
	    int dot = uri.lastIndexOf('.');
	    if (dot >= 0)
		mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
	    if (mime == null)
		mime = NanoHTTPD.MIME_DEFAULT_BINARY;

	    res = new Response(HTTP_OK, mime, fi);
	} catch (IOException e) {
	    e.printStackTrace();
	    ALog.d(TAG, e.getStackTrace().toString());
	    res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
	}

	return res;

    }

    private Response sendWebAppFile(String uri, String method, Properties header, Properties parms, Properties files) {
	Response res = null;

	Log.d(TAG, "" + method + " '" + uri + " " + /* header + */" " + parms);

	String escapedCode = parms.getProperty("code");
	String unescapedCode = StringEscapeUtils.unescapeEcmaScript(escapedCode);
	Log.d("HTTP Code", "" + escapedCode);
	Log.d("HTTP Code", "" + unescapedCode);

	// Clean up uri
	uri = uri.trim().replace(File.separatorChar, '/');
	if (uri.indexOf('?') >= 0)
	    uri = uri.substring(0, uri.indexOf('?'));

	// We never want to request just the '/'
	if (uri.length() == 1)
	    uri = "index.html";

	// We're using assets, so we can't have a leading '/'
	if (uri.charAt(0) == '/')
	    uri = uri.substring(1, uri.length());

	// have the object build the directory structure, if needed.
	AssetManager am = ctx.get().getAssets();
	try {
	    Log.d(TAG, WEBAPP_DIR + uri);
	    InputStream fi = am.open(WEBAPP_DIR + uri);

	    // Get MIME type from file name extension, if possible
	    String mime = null;
	    int dot = uri.lastIndexOf('.');
	    if (dot >= 0)
		mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
	    if (mime == null)
		mime = NanoHTTPD.MIME_DEFAULT_BINARY;

	    res = new Response(HTTP_OK, mime, fi);
	} catch (IOException e) {
	    e.printStackTrace();
	    ALog.d(TAG, e.getStackTrace().toString());
	    res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
	}

	return res;

    }

    public void close() {
	stop();
	instance = null;

    }

}
