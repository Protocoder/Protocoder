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

package org.protocoder.network;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Looper;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.protocoder.appApi.EditorManager;
import org.protocoderrunner.apidoc.APIManager;
import org.protocoderrunner.apprunner.api.PApp;
import org.protocoderrunner.apprunner.api.PBoards;
import org.protocoderrunner.apprunner.api.PConsole;
import org.protocoderrunner.apprunner.api.PDashboard;
import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PFileIO;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.apprunner.api.PProtocoder;
import org.protocoderrunner.apprunner.api.PSensors;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.apprunner.api.boards.PArduino;
import org.protocoderrunner.apprunner.api.boards.PIOIO;
import org.protocoderrunner.apprunner.api.boards.PSerial;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardBackground;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardButton;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardCustomWidget;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardHTML;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardImage;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardInput;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardPlot;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardSlider;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardText;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardVideoCamera;
import org.protocoderrunner.apprunner.api.media.PCamera;
import org.protocoderrunner.apprunner.api.media.PMidi;
import org.protocoderrunner.apprunner.api.media.PPureData;
import org.protocoderrunner.apprunner.api.network.PSimpleHttpServer;
import org.protocoderrunner.apprunner.api.network.PSocketIOClient;
import org.protocoderrunner.apprunner.api.other.PDeviceEditor;
import org.protocoderrunner.apprunner.api.other.PEvents;
import org.protocoderrunner.apprunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.apprunner.api.other.PProcessing;
import org.protocoderrunner.apprunner.api.other.PSqLite;
import org.protocoderrunner.apprunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.apprunner.api.widgets.PButton;
import org.protocoderrunner.apprunner.api.widgets.PCanvas;
import org.protocoderrunner.apprunner.api.widgets.PCard;
import org.protocoderrunner.apprunner.api.widgets.PCheckBox;
import org.protocoderrunner.apprunner.api.widgets.PEditText;
import org.protocoderrunner.apprunner.api.widgets.PGrid;
import org.protocoderrunner.apprunner.api.widgets.PGridRow;
import org.protocoderrunner.apprunner.api.widgets.PImageButton;
import org.protocoderrunner.apprunner.api.widgets.PImageView;
import org.protocoderrunner.apprunner.api.widgets.PList;
import org.protocoderrunner.apprunner.api.widgets.PListItem;
import org.protocoderrunner.apprunner.api.widgets.PMap;
import org.protocoderrunner.apprunner.api.widgets.PNumberPicker;
import org.protocoderrunner.apprunner.api.widgets.PPadView;
import org.protocoderrunner.apprunner.api.widgets.PPlotView;
import org.protocoderrunner.apprunner.api.widgets.PPopupCustomFragment;
import org.protocoderrunner.apprunner.api.widgets.PProgressBar;
import org.protocoderrunner.apprunner.api.widgets.PRadioButton;
import org.protocoderrunner.apprunner.api.widgets.PRow;
import org.protocoderrunner.apprunner.api.widgets.PScrollView;
import org.protocoderrunner.apprunner.api.widgets.PSlider;
import org.protocoderrunner.apprunner.api.widgets.PSpinner;
import org.protocoderrunner.apprunner.api.widgets.PSwitch;
import org.protocoderrunner.apprunner.api.widgets.PTextView;
import org.protocoderrunner.apprunner.api.widgets.PToggleButton;
import org.protocoderrunner.apprunner.api.widgets.PVerticalSeekbar;
import org.protocoderrunner.apprunner.api.widgets.PVideo;
import org.protocoderrunner.apprunner.api.widgets.PWebEditor;
import org.protocoderrunner.apprunner.api.widgets.PWebView;
import org.protocoderrunner.apprunner.api.widgets.PWindow;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.network.NanoHTTPD;
import org.protocoderrunner.network.NetworkUtils;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.MLog;

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

import de.greenrobot.event.EventBus;

/**
 * An example of subclassing NanoHTTPD to make mContext custom HTTP server.
 */
public class ProtocoderHttpServer extends NanoHTTPD {
    public static final String TAG = "myHTTPServer";
    private static ConnectedUser mConnectedUsers;
    private final WeakReference<Context> ctx;
    private final String WEBAPP_DIR = "webide/";
    String projectURLPrefix = "/apps";
    public android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());


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
                mConnectedUsers = ConnectedUser.getInstance();
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

            //display toast when new ip is connected
            final String ip = header.getProperty("ip");
            if (!mConnectedUsers.isIpRegistered(ip)) {
                mConnectedUsers.addUserIp(ip);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx.get(), "Connection from " + ip, Toast.LENGTH_LONG).show();
                    }
                });

            }

            if (uri.startsWith(projectURLPrefix)) {

                // checking if we are inside the directory so we sandbox the app
                // TODO its pretty hack so this deserves coding it again
                Project p = ProjectManager.getInstance().getCurrentProject();

                String projectFolder = "/" + p.getFolder() + "/" + p.getName();
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
                String folder = parms.getProperty("fileType").toString();

                Project p = ProjectManager.getInstance().get(folder, name);

                File src = new File(files.getProperty("pic").toString());
                File dst = new File(p.getStoragePath() + "/" + parms.getProperty("pic").toString());

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
                String name;
                String url;
                String newCode;
                String folder;

                MLog.d(TAG, "params " + obj.toString(2));

                String cmd = obj.getString("cmd");
                // fetch code
                if (cmd.equals("fetch_code")) {
                    MLog.d(TAG, "--> fetch code");
                    name = obj.getString("name");
                    folder = obj.getString("type");

                    Project p = ProjectManager.getInstance().get(folder, name);

                    // TODO add type
                    data.put("code", ProjectManager.getInstance().getCode(p));

                    // list apps
                } else if (cmd.equals("list_apps")) {
                    MLog.d(TAG, "--> list apps");

                    folder = obj.getString("filter");

                    ArrayList<Project> projects = null;
                    if (folder.equals(ProjectManager.FOLDER_EXAMPLES)) {
                        projects = ProjectManager.getInstance().list(folder, true);
                    } else {
                        projects = ProjectManager.getInstance().list(folder, false);
                    }
                    JSONArray projectsArray = new JSONArray();
                    for (Project project : projects) {
                        projectsArray.put(ProjectManager.getInstance().toJson(project));
                    }
                    data.put("projects", projectsArray);

                    // run app
                } else if (cmd.equals("run_app")) {
                    MLog.d(TAG, "--> run app");

                    name = obj.getString("name");
                    folder = obj.getString("type");

                    Project p = ProjectManager.getInstance().get(folder, name);
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

                    folder = parms.get("type").toString();

                    // add type
                    Project p = ProjectManager.getInstance().get(folder, name);
                    ProjectManager.getInstance().writeNewCode(p, newCode, fileName);
                    data.put("project", ProjectManager.getInstance().toJson(p));
                    ProjectEvent evt = new ProjectEvent(p, "save");
                    EventBus.getDefault().post(evt);

                    MLog.i(TAG, "Saved");

                // hack, this should be changed
                } else if (cmd.equals("push_code_and_run")) {
                    MLog.d(TAG, "--> push code " + method + " " + header);
                    name = parms.get("name").toString();
                    String fileName = parms.get("fileName").toString();
                    newCode = parms.get("code").toString();
                    // MLog.d(TAG, "code -> " + newCode);
                    folder = parms.get("type").toString();

                    // add type
                    Project p = ProjectManager.getInstance().get(folder, name);
                    MLog.d(TAG, "qqm" + p.getName() + " " + p.getFolder() + " " + fileName + " " + newCode);

                    ProjectManager.getInstance().writeNewCode(p, newCode, fileName);
                    data.put("project", ProjectManager.getInstance().toJson(p));
                    ProjectEvent evt = new ProjectEvent(p, "save");
                    EventBus.getDefault().post(evt);

                    MLog.i(TAG, "Saved");

                   // ProjectManager.getInstance().setRemoteIP(parms.get("remoteIP").toString());
                    //ProjectEvent evt2 = new ProjectEvent(p, "run");
                    //EventBus.getDefault().post(evt2);
                    //MLog.i(TAG, "Running...");

                    // list files in project
                } else if (cmd.equals("list_files_in_project")) {
                    MLog.d(TAG, "--> create new project");
                    name = obj.getString("name");
                    folder = obj.getString("type");

                    Project p = new Project(folder, name);
                    JSONArray array = ProjectManager.getInstance().listFilesInProjectJSON(p);
                    data.put("files", array);
                    // ProjectEvent evt = new ProjectEvent(p, "new");
                    // EventBus.getDefault().post(evt);

                } else if (cmd.equals("create_new_project")) {
                    MLog.d(TAG, "--> create new project");

                    name = obj.getString("name");
                    Project p = new Project(ProjectManager.FOLDER_USER_PROJECTS, name);
                    ProjectEvent evt = new ProjectEvent(p, "new");
                    EventBus.getDefault().post(evt);

                    Project newProject = ProjectManager.getInstance().addNewProject(ctx.get(), name, ProjectManager.FOLDER_USER_PROJECTS, name);

                    // remove app
                } else if (cmd.equals("remove_app")) {
                    MLog.d(TAG, "--> remove app");
                    name = obj.getString("name");
                    folder = obj.getString("type");

                    Project p = new Project(folder, name);
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
                    //main objects
                    APIManager.getInstance().clear();
                    APIManager.getInstance().addClass(PApp.class, true);
                    APIManager.getInstance().addClass(PBoards.class, true);
                    APIManager.getInstance().addClass(PConsole.class, true);
                    APIManager.getInstance().addClass(PDashboard.class, true);
                    APIManager.getInstance().addClass(PDevice.class, true);
                    APIManager.getInstance().addClass(PFileIO.class, true);
                    APIManager.getInstance().addClass(PMedia.class, true);
                    APIManager.getInstance().addClass(PNetwork.class, true);
                    APIManager.getInstance().addClass(PProtocoder.class, true);
                    APIManager.getInstance().addClass(PSensors.class, true);
                    APIManager.getInstance().addClass(PUI.class, true);
                    APIManager.getInstance().addClass(PUtil.class, true);

                    //boards
                    APIManager.getInstance().addClass(PArduino.class, false);
                    APIManager.getInstance().addClass(PSerial.class, false);
                    APIManager.getInstance().addClass(PIOIO.class, false);

                    //other
                    APIManager.getInstance().addClass(PCamera.class, false);
                    APIManager.getInstance().addClass(PDeviceEditor.class, false);
                    APIManager.getInstance().addClass(PEvents.class, false);
                    APIManager.getInstance().addClass(PMidi.class, false);
                    APIManager.getInstance().addClass(PProcessing.class, false);
                    APIManager.getInstance().addClass(PLiveCodingFeedback.class, false);
                    APIManager.getInstance().addClass(PPureData.class, false);
                    APIManager.getInstance().addClass(PSimpleHttpServer.class, false);
                    APIManager.getInstance().addClass(PSocketIOClient.class, false);
                    APIManager.getInstance().addClass(PSqLite.class, false);
                    APIManager.getInstance().addClass(PVideo.class, false);
                    APIManager.getInstance().addClass(PWebEditor.class, false);

                    //widgets
                    APIManager.getInstance().addClass(PAbsoluteLayout.class, false);
                    APIManager.getInstance().addClass(PButton.class, false);
                    APIManager.getInstance().addClass(PCanvas.class, false);
                    APIManager.getInstance().addClass(PCard.class, false);
                    APIManager.getInstance().addClass(PCheckBox.class, false);
                    APIManager.getInstance().addClass(PEditText.class, false);
                    APIManager.getInstance().addClass(PGrid.class, false);
                    APIManager.getInstance().addClass(PGridRow.class, false);
                    APIManager.getInstance().addClass(PImageButton.class, false);
                    APIManager.getInstance().addClass(PImageView.class, false);
                    // TODO add plist item
                    APIManager.getInstance().addClass(PList.class, false);
                    APIManager.getInstance().addClass(PListItem.class, false);
                    APIManager.getInstance().addClass(PMap.class, false);
                    APIManager.getInstance().addClass(PNumberPicker.class, false);
                    APIManager.getInstance().addClass(PPadView.class, false);
                    APIManager.getInstance().addClass(PPlotView.class, false);
                    APIManager.getInstance().addClass(PPopupCustomFragment.class, false);
                    APIManager.getInstance().addClass(PProgressBar.class, false);
                    APIManager.getInstance().addClass(PRadioButton.class, false);
                    APIManager.getInstance().addClass(PRow.class, false);
                    APIManager.getInstance().addClass(PScrollView.class, false);
                    APIManager.getInstance().addClass(PSlider.class, false);
                    APIManager.getInstance().addClass(PSpinner.class, false);
                    APIManager.getInstance().addClass(PSwitch.class, false);
                    APIManager.getInstance().addClass(PTextView.class, false);
                    APIManager.getInstance().addClass(PToggleButton.class, false);
                    APIManager.getInstance().addClass(PVerticalSeekbar.class, false);
                    APIManager.getInstance().addClass(PWebView.class, false);
                    APIManager.getInstance().addClass(PWindow.class, false);

                    //dashboard
                    APIManager.getInstance().addClass(PDashboardBackground.class, false);
                    APIManager.getInstance().addClass(PDashboardButton.class, false);
                    APIManager.getInstance().addClass(PDashboardCustomWidget.class, false);
                    APIManager.getInstance().addClass(PDashboardHTML.class, false);
                    APIManager.getInstance().addClass(PDashboardImage.class, false);
                    APIManager.getInstance().addClass(PDashboardInput.class, false);
                    APIManager.getInstance().addClass(PDashboardPlot.class, false);
                    APIManager.getInstance().addClass(PDashboardSlider.class, false);
                    APIManager.getInstance().addClass(PDashboardText.class, false);
                    APIManager.getInstance().addClass(PDashboardVideoCamera.class, false);

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

        String currentEditor = EditorManager.getInstance().getCurrentEditor(ctx.get());
        if (currentEditor.equals(EditorManager.DEFAULT)) {

            // have the object build the directory structure, if needed.
            AssetManager am = ctx.get().getAssets();
            try {
                MLog.d(TAG, WEBAPP_DIR + uri);
                InputStream fi = am.open(WEBAPP_DIR + uri);

                res = new Response(HTTP_OK, mime, fi);
            } catch (IOException e) {
                e.printStackTrace();
                MLog.d(TAG, e.getStackTrace().toString());
                res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
            }
        } else {
            String path = EditorManager.getInstance().getUrlEditor(ctx.get()) + uri;
            try {
                FileInputStream fi = new FileInputStream(path);
                res = new Response(HTTP_OK, mime, fi);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return res;

    }

    public void close() {
        stop();
        instance = null;

    }

}
