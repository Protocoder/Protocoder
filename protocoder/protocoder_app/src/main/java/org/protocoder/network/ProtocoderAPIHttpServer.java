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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.utils.FileIO;
import org.protocoder.utils.MLog;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class ProtocoderAPIHttpServer extends NanoHTTPD {
	public static final String TAG = "PHttpServer";
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


	public ProtocoderAPIHttpServer(Context aCtx, int port) throws IOException {
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

                FileIO.copyFile(src, dst);

                JSONObject data = new JSONObject();
                data.put("result", "OK");

                return new Response("200", MIME_TYPES.get("txt"), data.toString());

            // normal file serving
            } else {
                MLog.d(TAG, "received String" + uri + " " + method + " " + header + " " + " " + parms + " " + files);
                Project p = ProjectManager.getInstance().getCurrentProject();

                String projectFolder = p.getStoragePath();

                res = serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header,
                        new File(p.getStoragePath()), false);

                // new Response(HTTP_NOTFOUND, MIME_HTML, "resource not found");
            }

		} catch (Exception e) {
			MLog.d(TAG, "response error " + e.toString());
		}

		return res;
	}

	public void stop() {
		super.stop();
	}

}
