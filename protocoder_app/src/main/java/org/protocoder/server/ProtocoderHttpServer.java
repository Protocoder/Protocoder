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

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.protocoder.events.Events;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.model.ProtoFile;
import org.protocoder.server.networkexchangeobjects.NEOProject;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ProtocoderHttpServer extends NanoHTTPD {
    public static final String TAG = ProtocoderHttpServer.class.getSimpleName();

    private final int COMMAND = 3;
    private final int TYPE = 3;
    private final int FOLDER = 4;
    private final int PROJECT_NAME = 5;
    private final int PROJECT_ACTION = 6;
    private final int FILE_DELIMITER = 6;
    private final int FILE_ACTION = 7;
    private final int FILE_NAME = 8;


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

    /*
    // project global actions
     0   1   2       3
    "/api/project/list/"
    "/api/project/stop_all"
    "/api/project/execute_code"

    // project dependent actions
    0   1      2   3   4   5    6
    "/api/project/[folder]/[subfolder]/[p]/new"
    "/api/project/[ff]/[sf]/[p]/save/" (POST)
    "/api/project/[ff]/[sf]/[p]/load/"
    "/api/project/[ff]/[sf]/[p]/delete/"
    "/api/project/[ff]/[sf]/[p]/run/"
    "/api/project/[ff]/[sf]/[p]/stop/"

    // file actions
    0   1      2   3   4    5      6    7
    "/api/project/[ff]/[sf]/[p]/files/new" (POST)
    "/api/project/[ff]/[sf]/[p]/files/save" (POST)
    "/api/project/[ff]/[sf]/[p]/files/list"
    "/api/project/[ff]/[sf]/[p]/files/delete/" (POST)
    "/api/project/[ff]/[sf]/[p]/files/load/main.js"         7
    */
    private Response serveAPI(IHTTPSession session) {
        Response res = null;

        String uri = session.getUri();
        String uriSplitted[] = uri.split("/");

        HashMap<String, String> cmd;

        // debug the params
        for (int i = 0; i < uriSplitted.length; i++) {
            MLog.d(TAG, uriSplitted.length + " " + i + " " + uriSplitted[i]);
        }


        /**
         * Project global actions
         *
         * /api/project/command
         */

        if (uriSplitted.length >= 4 && uriSplitted.length <= 5) {
            if (uriSplitted[COMMAND].equals("list")) {
                ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder("./", 2);
                String jsonFiles = gson.toJson(files);

                // MLog.d("list", jsonFiles);
                EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_all"));

                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), jsonFiles.toString());
            } else if (uriSplitted[COMMAND].equals("execute_code")) {
                MLog.d(TAG, "run code");

                // POST DATA
                String json;
                final HashMap<String, String> map = new HashMap<String, String>();
                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");

                    json = map.get("postData");
                    NEOProject neo = gson.fromJson(json, NEOProject.class);
                    EventBus.getDefault().post(new Events.ExecuteCodeEvent(neo.code));

                    res = newFixedLengthResponse("OK");
                } catch (IOException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                } catch (ResponseException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                }
            } else if (uriSplitted[COMMAND].equals("stop_all")) {
                MLog.d(TAG, "stop all");
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_STOP_ALL, null));
                res = newFixedLengthResponse("OK");
            }

        /**
         * Project dependent actions
         *
         * /api/project/[ff]/[sf]/[p]/action
         */

        } else if (uriSplitted.length == 7) {
            Project p = new Project(uriSplitted[TYPE] + "/" + uriSplitted[FOLDER], uriSplitted[PROJECT_NAME]);

            if (uriSplitted[PROJECT_ACTION].equals("new")) {
                ProtoScriptHelper.createNewProject(mContext.get(), p.folder, p.name);
            } else if (uri.startsWith("/api/project/save")) {
                String json;
                final HashMap<String, String> map = new HashMap<String, String>();  // POST DATA

                try {
                    session.parseBody(map);
                    if (map.isEmpty()) return newFixedLengthResponse("BUG");

                    json = map.get("postData");
                    // MLog.d(TAG, "map " + map.toString());
                    // MLog.d(TAG, "post data " + json);
                } catch (IOException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                } catch (ResponseException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse("NOP");
                }

                NEOProject neo = gson.fromJson(json, NEOProject.class);

                // saving all the files changed
                for (ProtoFile file : neo.files) {
                    ProtoScriptHelper.saveCodeFromSandboxPath(file.path, file.code);
                }

                res = newFixedLengthResponse("OK");

            } else if (uriSplitted[PROJECT_ACTION].equals("load")) {
                ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder(p.getSandboxPath(), 0);

                // only load main.js
                for (int i = 0; i < files.size(); i++) {
                    if (files.get(i).name.equals(ProtocoderSettings.MAIN_FILENAME)) {
                        files.get(i).code = ProtoScriptHelper.getCode(p);
                    }
                }

                NEOProject neo = new NEOProject();
                neo.files = files;
                neo.project = p;
                neo.current_folder = '/';

                String json = gson.toJson(neo);
                // MLog.d(TAG, json);

                EventBus.getDefault().post(new Events.HTTPServerEvent("project_load", p));
                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), json.toString());

            } else if (uriSplitted[PROJECT_ACTION].equals("delete")) {
                String path = "";
                // ProtoScriptHelper.deleteFolder(path);

            } else if (uriSplitted[PROJECT_ACTION].equals("run")) {
                MLog.d(TAG, "run --> " + p.getFolder());
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_RUN, p));

                res = newFixedLengthResponse("OK");

            } else if (uriSplitted[PROJECT_ACTION].equals("stop")) {
                MLog.d(TAG, "stop");
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_STOP_ALL, null));

                res = newFixedLengthResponse("OK");
            }
        } else if (uriSplitted.length >= 8 && uriSplitted[FILE_DELIMITER].equals("files")) {
            MLog.d(TAG, "-> files ");
            Project p = new Project(uriSplitted[TYPE] + "/" + uriSplitted[FOLDER], uriSplitted[PROJECT_NAME]);
            if (uriSplitted[FILE_ACTION].equals("list")) {
                MLog.d("list_files");
                // MLog.d(TAG, uriSplitted);

                String path = StringUtils.join(uriSplitted, "/", FILE_ACTION + 1, uriSplitted.length);
                MLog.d("getting folder -> " + path);

                // here
                ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder(p.getSandboxPath() + path, 0);
                String jsonFiles = gson.toJson(files);

                MLog.d("list", jsonFiles);
                EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_all"));

                res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), jsonFiles.toString());
            } else if (uriSplitted[FILE_ACTION].equals("load")) {
                // fetch file
                String fileName = uriSplitted[FILE_NAME];
                String mime = getMimeType(fileName); // Get MIME type
                InputStream fi = null;
                try {
                    fi = new FileInputStream(p.getFullPathForFile(fileName));
                    res = newFixedLengthResponse(Response.Status.OK, mime, fi, fi.available());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            } else {
            res = NanoHTTPD.newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, ":(");
        }

        /*

            // get files
            ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder(p.getSandboxPath(), 0);

            for (int i = 0; i < files.size(); i++) {
                ProtoFile f = files.get(i);
                f.code = ProtoScriptHelper.getCode(p);
            }

            NEOProject neo = new NEOProject();
            neo.files = files;
            neo.project = p;

            String json = gson.toJson(neo);
            // MLog.d(TAG, json);

            */

            // EventBus.getDefault().post(new Events.HTTPServerEvent("project_list_files", p));
            // res = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, MIME_TYPES.get("json"), "" /* json.toString() */);

            // serve files

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
