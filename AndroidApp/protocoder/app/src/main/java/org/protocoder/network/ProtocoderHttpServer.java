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
import org.protocoder.apprunner.api.PApp;
import org.protocoder.apprunner.api.PBoards;
import org.protocoder.apprunner.api.PConsole;
import org.protocoder.apprunner.api.PDashboard;
import org.protocoder.apprunner.api.PDevice;
import org.protocoder.apprunner.api.PEditor;
import org.protocoder.apprunner.api.PFileIO;
import org.protocoder.apprunner.api.PMedia;
import org.protocoder.apprunner.api.PNetwork;
import org.protocoder.apprunner.api.PProtocoder;
import org.protocoder.apprunner.api.PSensors;
import org.protocoder.apprunner.api.PUI;
import org.protocoder.apprunner.api.PUtil;
import org.protocoder.apprunner.api.boards.PArduino;
import org.protocoder.apprunner.api.boards.PIOIO;
import org.protocoder.apprunner.api.dashboard.PDashboardButton;
import org.protocoder.apprunner.api.dashboard.PDashboardHTML;
import org.protocoder.apprunner.api.dashboard.PDashboardImage;
import org.protocoder.apprunner.api.dashboard.PDashboardInput;
import org.protocoder.apprunner.api.dashboard.PDashboardLabel;
import org.protocoder.apprunner.api.dashboard.PDashboardPlot;
import org.protocoder.apprunner.api.dashboard.PDashboardSlider;
import org.protocoder.apprunner.api.other.PCamera;
import org.protocoder.apprunner.api.other.PProcessing;
import org.protocoder.apprunner.api.other.PProtocoderFeedback;
import org.protocoder.apprunner.api.other.PPureData;
import org.protocoder.apprunner.api.other.PSqlLite;
import org.protocoder.apprunner.api.other.PVideo;
import org.protocoder.apprunner.api.widgets.PButton;
import org.protocoder.apprunner.api.widgets.PCanvasView;
import org.protocoder.apprunner.api.widgets.PCard;
import org.protocoder.apprunner.api.widgets.PCheckBox;
import org.protocoder.apprunner.api.widgets.PEditText;
import org.protocoder.apprunner.api.widgets.PAbsoluteLayout;
import org.protocoder.apprunner.api.widgets.PImageButton;
import org.protocoder.apprunner.api.widgets.PImageView;
import org.protocoder.apprunner.api.widgets.PMap;
import org.protocoder.apprunner.api.widgets.PPlotView;
import org.protocoder.apprunner.api.widgets.PProgressBar;
import org.protocoder.apprunner.api.widgets.PRadioButton;
import org.protocoder.apprunner.api.widgets.PRow;
import org.protocoder.apprunner.api.widgets.PSeekBar;
import org.protocoder.apprunner.api.widgets.PSwitch;
import org.protocoder.apprunner.api.widgets.PTextView;
import org.protocoder.apprunner.api.widgets.PToggleButton;
import org.protocoder.apprunner.api.widgets.PWebView;
import org.protocoder.events.Events;
import org.protocoder.events.Events.ProjectEvent;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.utils.FileIO;
import org.protocoder.utils.MLog;

import android.content.Context;
import android.content.res.AssetManager;
import de.greenrobot.event.EventBus;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class ProtocoderHttpServer extends NanoHTTPD {
	public static final String TAG = "myHTTPServer";
	private final WeakReference<Context> ctx;
	private final String WEBAPP_DIR = "webapp/";
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

	private static ProtocoderHttpServer instance;

	public static ProtocoderHttpServer getInstance(Context aCtx, int port) {
		MLog.d(TAG, "launching web server...");
		if (instance == null) {
			try {
				MLog.d(TAG, "ok...");
				instance = new ProtocoderHttpServer(aCtx, port);
			} catch (IOException e) {
				MLog.d(TAG, "nop :(...");
				e.printStackTrace();
			}
		}

		return instance;
	}

	public ProtocoderHttpServer(Context aCtx, int port) throws IOException {
		super(port);
		ctx = new WeakReference<Context>(aCtx);
		String ip = NetworkUtils.getLocalIpAddress(aCtx);
		if (ip == null) {
			MLog.d(TAG, "No IP found. Please connect to a newwork and try again");
		} else {
			MLog.d(TAG, "Launched server at http://" + ip.toString() + ":" + port);
		}
	}

	@Override
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {

		Response res = null;

		try {

			MLog.d(TAG, "received String" + uri + " " + method + " " + header + " " + " " + parms + " " + files);

			if (uri.startsWith(projectURLPrefix)) {

				// checking if we are inside the directory so we sandbox the app
				// TODO its pretty hack so this deserves coding it again
				Project p = ProjectManager.getInstance().getCurrentProject();

				String projectFolder = "/" + p.getTypeString() + "/" + p.getName();
				// MLog.d("qq", "project folder is " + projectFolder);
				if (uri.replace(projectURLPrefix, "").contains(projectFolder)) {
					// MLog.d("qq", "inside project");
					return serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header,
							new File(p.getStoragePath()), false);
				} else {
					// MLog.d("qq", "outside project");
					new Response(HTTP_NOTFOUND, MIME_HTML, "resource not found");
				}

			}

			// file upload
			if (!files.isEmpty()) {

				String name = parms.getProperty("name").toString();
				String fileType = parms.getProperty("fileType").toString();

				int projectType = -1;

				if (fileType.equals("projects")) {
					projectType = ProjectManager.PROJECT_USER_MADE;
				} else if (fileType.equals("examples")) {
					projectType = ProjectManager.PROJECT_EXAMPLE;
				}

				Project p = ProjectManager.getInstance().get(name, projectType);

				File src = new File(files.getProperty("pic").toString());
				File dst = new File(p.getStoragePath() + "/" + parms.getProperty("pic").toString());
				// MLog.d("qwqw", p.getStoragePath() + "/" +
				// parms.getProperty("pic").toString());
				// MLog.d(TAG, " " + src.toString() + " " + dst.toString());

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
			MLog.d(TAG, "cmd " + userCmd);

			if (userCmd.contains("cmd")) {
				JSONObject obj = new JSONObject(params);

				Project foundProject;
				String name, url, newCode;
				String type;
				int projectType = -1;

				MLog.d(TAG, "params " + obj.toString(2));

				String cmd = obj.getString("cmd");
				// fetch code
				if (cmd.equals("fetch_code")) {
					MLog.d(TAG, "--> fetch code");
					name = obj.getString("name");
					type = obj.getString("type");

					if (type.equals("projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE;
					} else if (type.equals("examples")) {
						projectType = ProjectManager.PROJECT_EXAMPLE;
					}

					Project p = ProjectManager.getInstance().get(name, projectType);

					// TODO add type
					data.put("code", ProjectManager.getInstance().getCode(p));

					// list apps
				} else if (cmd.equals("list_apps")) {
					MLog.d(TAG, "--> list apps");

					type = obj.getString("filter");

					if (type.equals("projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE;
					} else if (type.equals("examples")) {
						projectType = ProjectManager.PROJECT_EXAMPLE;
					}
					ArrayList<Project> projects = ProjectManager.getInstance().list(projectType);
					JSONArray projectsArray = new JSONArray();
					for (Project project : projects) {
						projectsArray.put(ProjectManager.getInstance().toJson(project));
					}
					data.put("projects", projectsArray);

					// run app
				} else if (cmd.equals("run_app")) {
					MLog.d(TAG, "--> run app");

					name = obj.getString("name");
					type = obj.getString("type");

					if (type.equals("projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE;
					} else if (type.equals("examples")) {
						projectType = ProjectManager.PROJECT_EXAMPLE;
					}

					Project p = ProjectManager.getInstance().get(name, projectType);
					ProjectManager.getInstance().setRemoteIP(obj.getString("remoteIP"));
					ProjectEvent evt = new ProjectEvent(p, "run");
					EventBus.getDefault().post(evt);
					MLog.i(TAG, "Running...");

					// execute app
				} else if (cmd.equals("execute_code")) {
					MLog.d(TAG, "--> execute code");

					// Save and run
					String code = parms.get("codeToSend").toString();

					Events.ExecuteCodeEvent evt = new Events.ExecuteCodeEvent(code);
					EventBus.getDefault().post(evt);
					MLog.i(TAG, "Execute...");

					// save_code
				} else if (cmd.equals("push_code")) {
					MLog.d(TAG, "--> push code " + method + " " + header);
					name = parms.get("name").toString();
					String fileName = parms.get("fileName").toString();
					newCode = parms.get("code").toString();
					MLog.d(TAG, "fileName -> " + fileName);
					// MLog.d(TAG, "code -> " + newCode);

					type = parms.get("type").toString();

					if (type.equals("projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE;
					} else if (type.equals("examples")) {
						projectType = ProjectManager.PROJECT_EXAMPLE;
					}

					// add type
					Project p = ProjectManager.getInstance().get(name, projectType);
					ProjectManager.getInstance().writeNewCode(p, newCode, fileName);
					data.put("project", ProjectManager.getInstance().toJson(p));
					ProjectEvent evt = new ProjectEvent(p, "save");
					EventBus.getDefault().post(evt);

					MLog.i(TAG, "Saved");

					// list files in project
				} else if (cmd.equals("list_files_in_project")) {
					MLog.d(TAG, "--> create new project");
					name = obj.getString("name");
					type = obj.getString("type");

					if (type.equals("projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE;
					} else if (type.equals("examples")) {
						projectType = ProjectManager.PROJECT_EXAMPLE;
					}

					Project p = new Project(name, projectType);
					JSONArray array = ProjectManager.getInstance().listFilesInProjectJSON(p);
					data.put("files", array);
					// ProjectEvent evt = new ProjectEvent(p, "new");
					// EventBus.getDefault().post(evt);

				} else if (cmd.equals("create_new_project")) {
					MLog.d(TAG, "--> create new project");

					name = obj.getString("name");
					Project p = new Project(name, "", ProjectManager.PROJECT_USER_MADE);
					ProjectEvent evt = new ProjectEvent(p, "new");
					EventBus.getDefault().post(evt);

					Project newProject = ProjectManager.getInstance().addNewProject(ctx.get(), name, name,
							ProjectManager.PROJECT_USER_MADE);

					// remove app
				} else if (cmd.equals("remove_app")) {
					MLog.d(TAG, "--> remove app");
					name = obj.getString("name");
					type = obj.getString("type");

					if (type.equals("projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE;
					} else if (type.equals("examples")) {
						projectType = ProjectManager.PROJECT_EXAMPLE;
					}

					Project p = new Project(name, projectType);
					ProjectManager.getInstance().deleteProject(p);
					ProjectEvent evt = new ProjectEvent(p, "update");
					EventBus.getDefault().post(evt);

					// rename app
				} else if (cmd.equals("rename_app")) {
					MLog.d(TAG, "--> rename app");

					// get help
				} else if (cmd.equals("get_documentation")) {
					MLog.d(TAG, "--> get documentation");

					// TODO do it automatically
					APIManager.getInstance().clear();
					APIManager.getInstance().addClass(PApp.class);
					APIManager.getInstance().addClass(PBoards.class);
					APIManager.getInstance().addClass(PConsole.class);
					APIManager.getInstance().addClass(PDashboard.class);
					APIManager.getInstance().addClass(PDevice.class);
					APIManager.getInstance().addClass(PEditor.class);
					APIManager.getInstance().addClass(PFileIO.class);
					APIManager.getInstance().addClass(PMedia.class);
					APIManager.getInstance().addClass(PNetwork.class);
					APIManager.getInstance().addClass(PProtocoder.class);
					APIManager.getInstance().addClass(PSensors.class);
					APIManager.getInstance().addClass(PUI.class);
					APIManager.getInstance().addClass(PUtil.class);

					APIManager.getInstance().addClass(PArduino.class);
					APIManager.getInstance().addClass(PIOIO.class);

					APIManager.getInstance().addClass(PCamera.class);
					APIManager.getInstance().addClass(PProcessing.class);
					APIManager.getInstance().addClass(PProtocoderFeedback.class);
					APIManager.getInstance().addClass(PPureData.class);
					APIManager.getInstance().addClass(PSqlLite.class);
					APIManager.getInstance().addClass(PVideo.class);

					APIManager.getInstance().addClass(PButton.class);
					APIManager.getInstance().addClass(PCanvasView.class);
					APIManager.getInstance().addClass(PCard.class);
					APIManager.getInstance().addClass(PCheckBox.class);
					APIManager.getInstance().addClass(PEditText.class);
					APIManager.getInstance().addClass(PAbsoluteLayout.class);
					APIManager.getInstance().addClass(PImageButton.class);
					APIManager.getInstance().addClass(PImageView.class);
					// plist item
					APIManager.getInstance().addClass(PMap.class);
					APIManager.getInstance().addClass(PPlotView.class);
					APIManager.getInstance().addClass(PProgressBar.class);

					APIManager.getInstance().addClass(PRadioButton.class);
					APIManager.getInstance().addClass(PRow.class);
					APIManager.getInstance().addClass(PSeekBar.class);
					APIManager.getInstance().addClass(PSwitch.class);
					APIManager.getInstance().addClass(PTextView.class);
					APIManager.getInstance().addClass(PToggleButton.class);
					APIManager.getInstance().addClass(PWebView.class);

					APIManager.getInstance().addClass(PDashboardButton.class);
					APIManager.getInstance().addClass(PDashboardHTML.class);
					APIManager.getInstance().addClass(PDashboardImage.class);
					APIManager.getInstance().addClass(PDashboardInput.class);
					APIManager.getInstance().addClass(PDashboardLabel.class);
					APIManager.getInstance().addClass(PDashboardPlot.class);
					APIManager.getInstance().addClass(PDashboardSlider.class);

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
			MLog.d(TAG, "response error " + e.toString());
		}

		// return serveFile(uri, header, servingFolder, true);
		return res;
	}

	private Response sendProjectFile(String uri, String method, Properties header, Properties parms, Properties files) {

		Response res = null;

		// Clean up uri
		uri = uri.trim().replace(File.separatorChar, '/');
		MLog.d(TAG, uri);

		// have the object build the directory structure, if needed.
		AssetManager am = ctx.get().getAssets();
		try {
			MLog.d(TAG, WEBAPP_DIR + uri);
			InputStream fi = am.open(WEBAPP_DIR + uri);

			// Get MIME type from file name extension, if possible
			String mime = null;
			int dot = uri.lastIndexOf('.');
			if (dot >= 0) {
				mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
			}
			if (mime == null) {
				mime = NanoHTTPD.MIME_DEFAULT_BINARY;
			}

			res = new Response(HTTP_OK, mime, fi);
		} catch (IOException e) {
			e.printStackTrace();
			MLog.d(TAG, e.getStackTrace().toString());
			res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
		}

		return res;

	}

	private Response sendWebAppFile(String uri, String method, Properties header, Properties parms, Properties files) {
		Response res = null;

		MLog.d(TAG, "" + method + " '" + uri + " " + /* header + */" " + parms);

		String escapedCode = parms.getProperty("code");
		String unescapedCode = StringEscapeUtils.unescapeEcmaScript(escapedCode);
		MLog.d("HTTP Code", "" + escapedCode);
		MLog.d("HTTP Code", "" + unescapedCode);

		// Clean up uri
		uri = uri.trim().replace(File.separatorChar, '/');
		if (uri.indexOf('?') >= 0) {
			uri = uri.substring(0, uri.indexOf('?'));
		}

		// We never want to request just the '/'
		if (uri.length() == 1) {
			uri = "index.html";
		}

		// We're using assets, so we can't have a leading '/'
		if (uri.charAt(0) == '/') {
			uri = uri.substring(1, uri.length());
		}

		// have the object build the directory structure, if needed.
		AssetManager am = ctx.get().getAssets();
		try {
			MLog.d(TAG, WEBAPP_DIR + uri);
			InputStream fi = am.open(WEBAPP_DIR + uri);

			// Get MIME type from file name extension, if possible
			String mime = null;
			int dot = uri.lastIndexOf('.');
			if (dot >= 0) {
				mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
			}
			if (mime == null) {
				mime = NanoHTTPD.MIME_DEFAULT_BINARY;
			}

			res = new Response(HTTP_OK, mime, fi);
		} catch (IOException e) {
			e.printStackTrace();
			MLog.d(TAG, e.getStackTrace().toString());
			res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
		}

		return res;

	}

	public void close() {
		stop();
		instance = null;

	}

}
