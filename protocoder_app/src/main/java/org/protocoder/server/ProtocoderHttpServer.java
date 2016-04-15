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

import org.greenrobot.eventbus.EventBus;
import org.protocoder.events.Events;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.model.ProtoFile;
import org.protocoder.server.networkexchangeobjects.NEOProject;
import org.protocoder.settings.ProtocoderSettings;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ProtocoderHttpServer extends NanoHTTPD {
    public static final String TAG = ProtocoderHttpServer.class.getSimpleName();

    private static WeakReference<Context> mContext;
    private static String WEBAPP_DIR = "webide/";

    Gson gson;

    public ProtocoderHttpServer(Context context, int port) { //} throws IOException {
        super(port);

        mContext = new WeakReference<Context>(context);
        MLog.d(TAG, "port -> " + port);

        gson = new GsonBuilder().setPrettyPrinting().create();
        //addMappings();

        try {
            // start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response res = null;

        String uri = session.getUri();
        MLog.d(TAG, "--> " + uri);

        if (uri.startsWith("/api")) res = serveAPI(session);
        else res = serveWebIDE(session);

        //adding CORS mode for WebIDE debugging from the computer
        if (ProtocoderSettings.DEBUG) {
            res.addHeader("Access-Control-Allow-Methods", "DELETE, GET, POST, PUT");
            res.addHeader("Access-Control-Allow-Origin",  "*");
            res.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Access-Control-Allow-Headers, Authorization");
        }

        return res;
    }

    // list, run, stop, load, new, delete
    private Response serveAPI(IHTTPSession session) {
        Response res = null;

        String uri = session.getUri();
        String uriSplitted[] = uri.split("/");

        // debug the params
        for (int i = 0; i < uriSplitted.length; i++) {
            MLog.d(TAG, i + " " + uriSplitted[i]);
        }
        /*
        MLog.d(TAG, "serving ... ");
        final HashMap<String, String> map2 = new HashMap<String, String>();
        try {
            session.parseBody(map2);
            MLog.d(TAG, session.getUri() + " " + " " + session.getMethod() + " map --> " + map2.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        MLog.d(TAG, "params -> " + session.getParms().toString());
        */

        // project_list
        if (uri.startsWith("/api/project/list/")) {

            ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder("./", 2);
            String jsonFiles = gson.toJson(files);
            // MLog.d(TAG, "list examples folders -> " + jsonFiles);

            EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_all"));

            res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), jsonFiles.toString());

        // project_new :folder/:name
        } else if ( uri.startsWith("/api/project/new") ) {
            Project p = new Project(uriSplitted[3], uriSplitted[4]);
            ProtoScriptHelper.createNewProject(mContext.get(), p.folder, p.name);

        // project_save :folder/:name
        } else if ( uri.startsWith("/api/project/save") ) {
            MLog.d(TAG, "mm project saving");
            Project p = new Project(uriSplitted[4] + "/" + uriSplitted[5], uriSplitted[6]);

            // POST DATA

            String json;
            final HashMap<String, String> map = new HashMap<String, String>();

            try {
                session.parseBody(map);
                if (map.isEmpty()) return newFixedLengthResponse("BUG");

                json = map.get("postData");
                MLog.d(TAG, "map " + map.toString());
                MLog.d(TAG, "post data " + json);
            } catch (IOException e) {
                e.printStackTrace();
                return newFixedLengthResponse("NOP");
            } catch (ResponseException e) {
                e.printStackTrace();
                return newFixedLengthResponse("NOP");
            }

            NEOProject neo = gson.fromJson(json, NEOProject.class);
            MLog.d(TAG, "CMD -> " + neo.project.getName());

            // saving all the files changed
            for (ProtoFile file : neo.files) {
                ProtoScriptHelper.saveCode(file.path, file.code);
            }

            res = newFixedLengthResponse("OK");

        // project_load :folder/:name
        } else if ( uri.startsWith("/api/project/load") ) {
            Project p = new Project(uriSplitted[4] + "/" + uriSplitted[5], uriSplitted[6]);

            ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder(p.getSandboxPath(), 0, ".js");

            for (int i = 0; i < files.size(); i++) {
                ProtoFile f = files.get(i);
                f.code = ProtoScriptHelper.getCode(p);
            }

            NEOProject neo = new NEOProject();
            neo.files = files;
            neo.project = p;

            String json = gson.toJson(neo);
            // MLog.d(TAG, json);

            EventBus.getDefault().post(new Events.HTTPServerEvent("project_load", p));
            res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), json.toString());

        // file_delete :folder/:name
        } else if ( uri.startsWith("/api/project/delete") ) {
            Project p = new Project(uriSplitted[3], uriSplitted[4]);
            String path = "";
            // ProtoScriptHelper.deleteFolder(path);

        // project_run :folder/:name
        } else if ( uri.startsWith("/api/project/run") ) {
            Project p = new Project(uriSplitted[4] + "/" + uriSplitted[5], uriSplitted[6]);
            MLog.d(TAG, "run --> " + p.getPath());
            //Project p = new Project("Examples/Media", "Camera");
            EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_RUN, p));

            res = newFixedLengthResponse("OK");
        // project_stop :folder/:name
        } else if ( uri.startsWith("/api/project/stop/all") ) {
            MLog.d(TAG, "stop all");
            EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_STOP_ALL, null));

            res = newFixedLengthResponse("OK");
        }

        return res;
    }

    private Response serveWebIDE(IHTTPSession session) {
        Response res = null;

        String uri = session.getUri();

        // Clean up uri
        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.indexOf('?') >= 0) uri = uri.substring(0, uri.indexOf('?'));
        if (uri.length() == 1) uri = "index.html"; // We never want to request just the '/'
        if (uri.charAt(0) == '/') uri = uri.substring(1, uri.length()); // using assets, so we can't have leading '/'

        String mime = getMimeType(uri); // Get MIME type

        // Read file and return it, otherwise NOT_FOUND is returned
        AssetManager am = mContext.get().getAssets();
        try {
            MLog.d(TAG, WEBAPP_DIR + uri);
            InputStream fi = am.open(WEBAPP_DIR + uri);
            res = newFixedLengthResponse(Response.Status.OK, mime, fi, fi.available());
        } catch (IOException e) {
            e.printStackTrace();
            MLog.d(TAG, e.getStackTrace().toString());
            NanoHTTPD.newFixedLengthResponse( Response.Status.NOT_FOUND, MIME_TYPES.get("txt"), "ERROR: " + e.getMessage());
        }

        return res; //NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), inp, fontSize);
    }

    /*
     * MIME types
     */
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
            put("binary", "application/octet-stream");
            put("zip", "application/octet-stream");
            put("exe", "application/octet-stream");
            put("class", "application/octet-stream");
        }
    };

    String getMimeType(String uri) {
        String mime = null;
        int dot = uri.lastIndexOf('.');
        if (dot >= 0) mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
        if (mime == null) mime = MIME_TYPES.get("binary");

        return mime;
    }

    public void close() {
        stop();
    }
}
