package com.makewithmoto.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.makewithmoto.apidoc.APIManager;
import com.makewithmoto.apprunner.api.JAndroid;
import com.makewithmoto.apprunner.api.JBrowser;
import com.makewithmoto.apprunner.api.JCamera;
import com.makewithmoto.apprunner.api.JIOIO;
import com.makewithmoto.apprunner.api.JConsole;
import com.makewithmoto.apprunner.api.JMakr;
import com.makewithmoto.apprunner.api.JMedia;
import com.makewithmoto.apprunner.api.JSensors;
import com.makewithmoto.apprunner.api.JUI;
import com.makewithmoto.apprunner.api.JWebApp;
import com.makewithmoto.apprunner.api.JWebAppPlot;
import com.makewithmoto.base.BaseMainApp;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.events.ProjectManager;
import com.makewithmoto.utils.FileIO;

import de.greenrobot.event.EventBus;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class MyHTTPServer extends NanoHTTPD {
	public static final String TAG = "myHTTPServer";
	private WeakReference<Context> ctx;
	private String WEBAPP_DIR = "webapp/";

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
		InetAddress ip = NetworkUtils.getLocalIpAddress();
		if (ip == null) {
			Log.d(TAG,
					"No IP found. Please connect to a newwork and try again");

		//	throw (new IOException());
		} else {
			Log.d(TAG, "Launched server at http://" + ip.toString() + ":"
					+ port);
		}
		//throw (new IOException());
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {

		Response res = null;

		try {

			Log.d(TAG, "received String" + uri + " " + method + " " + header + " " + " " + parms + " " + files);
			
			if (!files.isEmpty() ) { 
		
				String name = parms.getProperty("name").toString();
				String fileType = parms.getProperty("fileType").toString();
				
				int projectType = -1;
				if (fileType.equals("list_projects")) {
					projectType = ProjectManager.PROJECT_USER_MADE; 
				} else if (fileType.equals("list_examples")) { 
					projectType = ProjectManager.PROJECT_EXAMPLE; 
				} 
				
				Project p = ProjectManager.getInstance().get(name, projectType); 
				
				File src = new File(files.getProperty("pic").toString()); 
				File dst = new File(p.getUrl() + "/" + parms.getProperty("pic").toString());
				Log.d("qwqw", p.getUrl() + "/" + parms.getProperty("pic").toString());
				Log.d(TAG, " " + src.toString() + " " + dst.toString());
				
				FileIO.copyFile(src, dst);
				
				JSONObject data = new JSONObject();
				data.put("result", "OK");

				return new Response("200", MIME_TYPES.get("txt"), data.toString());

			} 
			
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
				
				//fetch code 
				if (cmd.equals("fetch_code")) {
					Log.d(TAG, "--> fetch code");
					name = obj.getString("name");
					type = obj.getString("type"); 
					
					if (type.equals("list_projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE; 
					} else if (type.equals("list_examples")) { 
						projectType = ProjectManager.PROJECT_EXAMPLE; 
					} 
					
					Project p = ProjectManager.getInstance().get(name, projectType);
					
					//TODO add type
					data.put("code", ProjectManager.getInstance().getCode(p));
				
				//list apps 
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
				
				//run app
				} else if (cmd.equals("run_app")) {
					Log.d(TAG, "--> run app");

					// Save and run
					name = obj.getString("name");
					type = obj.getString("type");

					if (type.equals("list_projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE; 
					} else if (type.equals("list_examples")) { 
						projectType = ProjectManager.PROJECT_EXAMPLE; 
					} 
					

					ProjectEvent evt = new ProjectEvent(ProjectManager.getInstance().get(name, projectType), "run");
					EventBus.getDefault().post(evt);
					ALog.i("Running...");

				//save_code
				} else if (cmd.equals("push_code")) {
					Log.d(TAG, "--> push code " +  method + " " + header);
					Log.d(TAG, "---->" + parms.toString() + " " + files.toString());
					Log.d(TAG, "" + parms.get("code"));
					name = parms.get("name").toString();
					newCode = parms.get("code").toString();
					Log.d("ww", newCode);
		
					type = parms.get("type").toString();
					
					if (type.equals("list_projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE; 
					} else if (type.equals("list_examples")) { 
						projectType = ProjectManager.PROJECT_EXAMPLE; 
					} 
					
					//add type
					Project p = ProjectManager.getInstance().get(name, projectType);
					ProjectManager.getInstance().writeNewCode(p, newCode);
					data.put("project", ProjectManager.getInstance().to_json(p));
					ProjectEvent evt = new ProjectEvent(p, "save");
					EventBus.getDefault().post(evt);

					ALog.i("Saved");
				
				//list files in project 
				} else if (cmd.equals("list_files_in_project")) {
					Log.d(TAG, "--> create new project");
					name = obj.getString("name");
					type = obj.getString("type");

					if (type.equals("list_projects")) {
						projectType = ProjectManager.PROJECT_USER_MADE; 
					} else if (type.equals("list_examples")) { 
						projectType = ProjectManager.PROJECT_EXAMPLE; 
					} 
					
					Project p = new Project(name, projectType);
					JSONArray array = ProjectManager.getInstance().listFilesInProject(p);
					data.put("files", array);
					//ProjectEvent evt = new ProjectEvent(p, "new");
					//EventBus.getDefault().post(evt);
					
					
					//remove app
				} else if (cmd.equals("remove_app")) {
				//create new app
				} else if (cmd.equals("create_new_project")) {
					Log.d(TAG, "--> create new project");

					name = obj.getString("name");
					Project p = new Project(name, "", ProjectManager.PROJECT_USER_MADE);
					ProjectEvent evt = new ProjectEvent(p, "new");
					EventBus.getDefault().post(evt);

					
				//remove app
				} else if (cmd.equals("remove_app")) {
					Log.d(TAG, "--> remove app");
				
				//get help 
				} else if (cmd.equals("get_documentation")) {
					Log.d(TAG, "--> get documentation");
					
					//TODO do it automatically 
					APIManager.getInstance().addClass(JAndroid.class); 
					APIManager.getInstance().addClass(JBrowser.class); 
					APIManager.getInstance().addClass(JCamera.class); 
					APIManager.getInstance().addClass(JConsole.class); 
					APIManager.getInstance().addClass(JIOIO.class); 
					APIManager.getInstance().addClass(JConsole.class); 
					APIManager.getInstance().addClass(JMakr.class); 
					APIManager.getInstance().addClass(JMedia.class); 
					APIManager.getInstance().addClass(JSensors.class); 
					APIManager.getInstance().addClass(JUI.class); 
					APIManager.getInstance().addClass(JMedia.class); 
					APIManager.getInstance().addClass(JWebApp.class); 
					APIManager.getInstance().addClass(JWebAppPlot.class); 
					
					data.put("api", APIManager.getInstance().getDocumentation());
				}

				res = new Response("200", MIME_TYPES.get("txt"), data.toString());
			} else {

				res = sendFile(uri, method, header, parms, files);
			}

		} catch (Exception e) {
			Log.d(TAG, "response error " + e.toString());
		}

		// return serveFile(uri, header, servingFolder, true);
		return res;
	}

	private Response sendFile(String uri, String method, Properties header,
			Properties parms, Properties files) {
		Response res = null;

		Log.d(TAG, "" + method + " '" + uri + " " + /* header + */" " + parms);

		String escapedCode = parms.getProperty("code");
		String unescapedCode = StringEscapeUtils
				.unescapeEcmaScript(escapedCode);
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
			Log.d(TAG, WEBAPP_DIR  + uri);
			InputStream fi = am.open(WEBAPP_DIR  + uri);

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
			res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: "
					+ e.getMessage());
		}

		return res;

	}

	public void close() { 
		stop();
		instance = null;
		
	}

}
