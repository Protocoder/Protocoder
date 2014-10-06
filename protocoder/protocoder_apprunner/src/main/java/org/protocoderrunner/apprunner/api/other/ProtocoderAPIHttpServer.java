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

package org.protocoderrunner.apprunner.api.other;

import android.content.Context;

import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.network.NanoHTTPD;
import org.protocoderrunner.network.NetworkUtils;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class ProtocoderAPIHttpServer extends NanoHTTPD {
	public static final String TAG = "ProtocoderHttpServer";
	private final WeakReference<Context> ctx;

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
    private final HttpCB callbackfn;
    private final Project p;


    public interface HttpCB {
        Response event(String uri, String method);
    }

    public ProtocoderAPIHttpServer(Context aCtx, int port, HttpCB callbackfn) throws IOException {
        super(port);
        p = ProjectManager.getInstance().getCurrentProject();

        this.callbackfn = callbackfn;
		ctx = new WeakReference<Context>(aCtx);
		String ip = NetworkUtils.getLocalIpAddress(aCtx);
		if (ip == null) {
			MLog.d(TAG, "No IP found. Please connect to a newwork and try again");
		} else {
			MLog.d(TAG, "Launched server at http://" + ip.toString() + ":" + port);
		}
	}

    @ProtocoderScript
    @APIMethod(description = "Responds to the request with a given text", example = "")
    @APIParam(params = { "boolean" })    public Response respond(String data) {
        return new Response("200", MIME_TYPES.get("txt"), data);
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a http server in the current project directory", example = "")
    @APIParam(params = { "boolean" })
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {

		Response res = null;

        try {
            res = callbackfn.event(uri, method);
        } catch (Exception e) {

        }
       // MLog.network(ctx.get(), TAG, "lalalalal " + res.toString());

        if (res == null) {

            try {

                // file upload
                if (!files.isEmpty()) {

                    File src = new File(files.getProperty("pic").toString());
                    File dst = new File(p.getStoragePath() + "/" + parms.getProperty("pic").toString());

                    FileIO.copyFile(src, dst);

                    JSONObject data = new JSONObject();
                    data.put("result", "OK");

                    return new Response("200", MIME_TYPES.get("txt"), data.toString());

                    // normal file serving
                } else {
                    MLog.d(TAG, "received String" + uri + " " + method + " " + header + " " + " " + parms + " " + files);

                    String projectFolder = p.getStoragePath();


                    res = serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header,
                            new File(p.getStoragePath()), false);

                    // new Response(HTTP_NOTFOUND, MIME_HTML, "resource not found");
                }

                //  res =  new Response(HTTP_OK, MIME_PLAINTEXT,
                //          "INTERNAL ERRROR: serveFile(): given homeDir is not a directory.");

            } catch (Exception e) {
                MLog.d(TAG, "response error " + e.toString());
            }

        }

        return res;
    }

	public void stop() {
		super.stop();
	}

}
