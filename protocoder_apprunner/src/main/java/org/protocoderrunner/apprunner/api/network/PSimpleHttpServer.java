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

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
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
    private HttpCB mCallbackfn = null;
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
    }

    public void onNewData(PSimpleHttpServer.HttpCB callbackfn) {
        this.mCallbackfn = callbackfn;

    }

    @ProtoMethod(description = "Responds to the request with a given text", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public Response respond(String data, String fileExtension) {
        return new Response("200", MIME_TYPES.get(fileExtension), data);
    }


    @ProtoMethod(description = "Serves a file", example = "")
    @ProtoMethodParam(params = {"uri", "header"})
    public Response serveFile(String uri, Properties header) {
        super.serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header,
                new File(p.getStoragePath()), false);

        // MLog.network(ctx.get(), TAG, "lalalalal " + res.toString());


        Response res = null;
        try {
            //MLog.d(TAG, "received String" + uri + " " + method + " " + header + " " + " " + parms + " " + files);

            String projectFolder = p.getStoragePath();

            File file = new File(p.getStoragePath());

            if (file.exists()) {
                res = super.serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header, file, false);
            } else {
                res = new Response(HTTP_NOTFOUND, MIME_HTML, "resource not found");
            }
        } catch (Exception e) {
            MLog.d(TAG, "response error " + e.toString());
        }


        return res;
    }

    public Response acceptUpload(Properties parms, Properties files) {

        Response res = null;
        // file upload
        if (!files.isEmpty()) {
            File src = new File(files.getProperty("pic").toString());
            File dst = new File(p.getStoragePath() + "/" + parms.getProperty("pic").toString());

            try {
                FileIO.copyFile(src, dst);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject data = new JSONObject();
            try {
                data.put("result", "OK");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            res = new Response("200", MIME_TYPES.get("txt"), data.toString());
        } else {

        }

        return res;
    }


    @ProtoMethod(description = "Stops the http server", example = "")
    @ProtoMethodParam(params = {""})
    public void stop() {
        super.stop();
    }


    public Response serve(final String uri, final String method, final Properties header, final Properties parms, final Properties files) {
        final Response[] res = {null};
        MLog.d(TAG, uri + " " + method + " " + header + " " + parms + " " + files);

        try {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //if (mCallbackfn != null)
                        res[0] = mCallbackfn.event(uri, method, header, parms, files);
                }
            });

        } catch (Exception e) {
            MLog.d(TAG, e.toString());
        }

        return res[0];
    }


}
