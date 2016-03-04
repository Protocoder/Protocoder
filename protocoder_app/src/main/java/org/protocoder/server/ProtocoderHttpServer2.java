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

package org.protocoder.server;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;
import org.protocoder.events.Events;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.networkexchangeobjects.NEOProject;
import org.protocoder.server.model.ProtoFile;
import org.protocoder.settings.ProtocoderSettings;
import org.protocoder.settings.WebEditorManager;
import org.protocoderrunner.base.network.NanoHTTPD;
import org.protocoderrunner.base.utils.MLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProtocoderHttpServer2 extends NanoHTTPD {
    public static final String TAG = ProtocoderHttpServer2.class.getSimpleName();

    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
        {
            put("css", "text/css");
            put("htm", "text/html");
            put("html", "text/html");
            put("xml", "text/xml");
            put("txt", "text/plain");
            put("json", "application/json");
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

    private final Gson gson;
    private final WeakReference<Context> mContext;
    private String WEBAPP_DIR = "webide/";


    public ProtocoderHttpServer2(Context context, int port) throws IOException {
        super(port);
        mContext = new WeakReference<Context>(context);

        gson = new GsonBuilder().create();
    }

    class ServeParams {
        String uri;
        String method;
        Properties header;
        Properties params;
        Properties files;
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties params, Properties files) {
        //repacking response, this will do a smooth transition after upgrading to the new HTTPD
        ServeParams serveParams = new ServeParams();
        serveParams.uri = uri;
        serveParams.method = method;
        serveParams.header = header;
        serveParams.params = params;
        serveParams.files = files;

        MLog.d(TAG, "uri -> " + serveParams.uri);
        MLog.d(TAG, "params -> " + serveParams.params);

        Response res = null;

        try {
            // file upload

            // web api
            if (serveParams.uri.equals("/api")) {
                String p = serveParams.params.toString();
                NEOProject neo = gson.fromJson(p, NEOProject.class);
                MLog.d(TAG, "CMD -> " + neo.cmd);

                if      (neo.cmd.equals("list_projects")) res = project_list_all(serveParams);
                //else if (neo.cmd.equals("")) readFile(serveParams);
                //else if (neo.cmd.equals("")) runApp(serveParams);
                //else if (neo.cmd.equals("")) stopApp(serveParams);
                //else if (neo.cmd.equals("")) new_project(serveParams);
                //else if (neo.cmd.equals("")) save_files(serveParams);
            } else res = sendWebAppFile(serveParams);

        } catch(Exception ioe) {
            MLog.w(TAG, ioe.toString());
        }

        //adding CROSS mode for WebIDE debugging from the computer
        if (ProtocoderSettings.DEBUG) {
            res.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT");
            res.addHeader("Access-Control-Allow-Origin",  "*");
            res.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
        }

        return res;
    }

    public Response project_list_all(ServeParams serveParams) {

        Gson gson = new GsonBuilder().create();
        ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder("./examples", 1);
        String jsonFiles = gson.toJson(files);
        MLog.d(TAG, "list examples folders -> " + jsonFiles);

        EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_all"));

        return new Response(HTTP_OK, MIME_TYPES.get("json"), jsonFiles.toString());
    }



    private Response sendWebAppFile(ServeParams serveParams) {
        Response res = null;
        String uri = serveParams.uri;

        MLog.d(TAG, "" + serveParams.method + " '" + serveParams.uri + " " + /* header + */" " + serveParams.params);

        String escapedCode = serveParams.params.getProperty("code");
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

        // We're using assets, so we can't have  leading '/'
        if (uri.charAt(0) == '/') {
            uri = uri.substring(1, uri.length());
        }

        // Get MIME type from file name extension, if possible
        String mime = null;
        int dot = uri.lastIndexOf('.');
        if (dot >= 0) {
            mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
        }
        if (mime == null) {
            mime = NanoHTTPD.MIME_DEFAULT_BINARY;
        }

        /*
         * return a default webeditor or a custom one
         */
        String currentEditor = WebEditorManager.getInstance().getCurrentEditor(mContext.get());
        if (currentEditor.equals(WebEditorManager.DEFAULT)) {
            // have the object build the directory structure, if needed.
            AssetManager am = mContext.get().getAssets();
            try {
                MLog.d(TAG, WEBAPP_DIR + uri);
                InputStream fi = am.open(WEBAPP_DIR + uri);

                res = new NanoHTTPD.Response(HTTP_OK, mime, fi);
            } catch (IOException e) {
                e.printStackTrace();
                MLog.d(TAG, e.getStackTrace().toString());
                res = new NanoHTTPD.Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
            }
        } else {
            String path = WebEditorManager.getInstance().getUrlEditor(mContext.get()) + uri;
            try {
                FileInputStream fi = new FileInputStream(path);
                res = new NanoHTTPD.Response(HTTP_OK, mime, fi);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    public void close() {
        stop();
    }

}
