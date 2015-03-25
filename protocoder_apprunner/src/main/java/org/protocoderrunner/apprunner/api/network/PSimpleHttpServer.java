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

package org.protocoderrunner.apprunner.api.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
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
 * An example of subclassing NanoHTTPD to make mContext custom HTTP server.
 */
public class PSimpleHttpServer extends NanoHTTPD {
    public static final String TAG = "ProtocoderHttpServer";
    public Handler mHandler = new Handler(Looper.getMainLooper());

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
    private HttpCB mCallback = null;
    private final Project p;


    public interface HttpCB {
        Response event(String uri, String method, Properties header, Properties parms, Properties files);
    }

    public PSimpleHttpServer(Context aCtx, int port) throws IOException {
        super(port);
        p = ProjectManager.getInstance().getCurrentProject();

        ctx = new WeakReference<Context>(aCtx);
        String ip = NetworkUtils.getLocalIpAddress(aCtx);
        if (ip == null) {
            MLog.d(TAG, "No IP found. Please connect to a newwork and try again");
        } else {
            MLog.d(TAG, "Launched server at http://" + ip.toString() + ":" + port);
        }

        WhatIsRunning.getInstance().add(this);
    }

    public void onNewRequest(HttpCB callbackfn) {
        this.mCallback = callbackfn;
    }


    @ProtoMethod(description = "Responds to the request with a given text", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public Response response(String data) {
        return new Response("200", MIME_TYPES.get("txt"), data);
    }

    @ProtoMethod(description = "Responds to the request with a given text", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public Response response(String code, String data) {
        return new Response(code, MIME_TYPES.get("txt"), data);
    }


    @ProtoMethod(description = "Serves a file", example = "")
    @ProtoMethodParam(params = {"uri", "header"})
    public Response serve(final String uri, final String method, final Properties header, final Properties parms, final Properties files) {

        final Response[] res = {null};
        try {
            if (mCallback != null) {
                res[0] = mCallback.event(uri, method, header, parms, files);
            }
        } catch (Exception e) {
            MLog.e(TAG, e.toString());
        }

        if (res[0] == null) {

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


                    res[0] = serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header,
                            new File(p.getStoragePath()), false);

                }

            } catch (Exception e) {
                MLog.d(TAG, "response error " + e.toString());
            }

        }

        //MLog.d(TAG, "im returning " + res[0] + " " + res[0].status + " " + res[0].data);

        return res[0];
    }

    @ProtoMethod(description = "Stops the http server", example = "")
    @ProtoMethodParam(params = {""})
    public void stop() {
        super.stop();
    }

}
