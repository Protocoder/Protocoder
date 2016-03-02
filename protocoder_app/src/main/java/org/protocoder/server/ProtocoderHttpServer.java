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
import android.os.Looper;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.protocoder.settings.WebEditorManager;
import org.protocoderrunner.apidoc.APIManager;
import org.protocoderrunner.api.PApp;
import org.protocoderrunner.api.PBoards;
import org.protocoderrunner.api.PConsole;
import org.protocoderrunner.api.PDashboard;
import org.protocoderrunner.api.PDevice;
import org.protocoderrunner.api.PFileIO;
import org.protocoderrunner.api.PMedia;
import org.protocoderrunner.api.PNetwork;
import org.protocoderrunner.api.PProtocoder;
import org.protocoderrunner.api.PSensors;
import org.protocoderrunner.api.PUI;
import org.protocoderrunner.api.PUtil;
import org.protocoderrunner.api.boards.PArduino;
import org.protocoderrunner.api.boards.PIOIO;
import org.protocoderrunner.api.boards.PSerial;
import org.protocoderrunner.api.dashboard.PDashboardBackground;
import org.protocoderrunner.api.dashboard.PDashboardButton;
import org.protocoderrunner.api.dashboard.PDashboardCustomWidget;
import org.protocoderrunner.api.dashboard.PDashboardHTML;
import org.protocoderrunner.api.dashboard.PDashboardImage;
import org.protocoderrunner.api.dashboard.PDashboardInput;
import org.protocoderrunner.api.dashboard.PDashboardPlot;
import org.protocoderrunner.api.dashboard.PDashboardSlider;
import org.protocoderrunner.api.dashboard.PDashboardText;
import org.protocoderrunner.api.dashboard.PDashboardVideoCamera;
import org.protocoderrunner.api.media.PCamera;
import org.protocoderrunner.api.media.PMidi;
import org.protocoderrunner.api.media.PPureData;
import org.protocoderrunner.api.network.PSocketIOClient;
import org.protocoderrunner.api.other.PDeviceEditor;
import org.protocoderrunner.api.other.PEvents;
import org.protocoderrunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.api.other.PProcessing;
import org.protocoderrunner.api.other.PSqLite;
import org.protocoderrunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.api.widgets.PButton;
import org.protocoderrunner.api.widgets.PCanvas;
import org.protocoderrunner.api.widgets.PCard;
import org.protocoderrunner.api.widgets.PCheckBox;
import org.protocoderrunner.api.widgets.PEditText;
import org.protocoderrunner.api.widgets.PGrid;
import org.protocoderrunner.api.widgets.PGridRow;
import org.protocoderrunner.api.widgets.PImageButton;
import org.protocoderrunner.api.widgets.PImageView;
import org.protocoderrunner.api.widgets.PList;
import org.protocoderrunner.api.widgets.PListItem;
import org.protocoderrunner.api.widgets.PMap;
import org.protocoderrunner.api.widgets.PNumberPicker;
import org.protocoderrunner.api.widgets.PPadView;
import org.protocoderrunner.api.widgets.PPlotView;
import org.protocoderrunner.api.widgets.PPopupCustomFragment;
import org.protocoderrunner.api.widgets.PProgressBar;
import org.protocoderrunner.api.widgets.PRadioButton;
import org.protocoderrunner.api.widgets.PRow;
import org.protocoderrunner.api.widgets.PScrollView;
import org.protocoderrunner.api.widgets.PSlider;
import org.protocoderrunner.api.widgets.PSpinner;
import org.protocoderrunner.api.widgets.PSwitch;
import org.protocoderrunner.api.widgets.PTextView;
import org.protocoderrunner.api.widgets.PToggleButton;
import org.protocoderrunner.api.widgets.PVerticalSeekbar;
import org.protocoderrunner.api.widgets.PVideo;
import org.protocoderrunner.api.widgets.PWebEditor;
import org.protocoderrunner.api.widgets.PWebView;
import org.protocoderrunner.api.widgets.PWindow;
import org.protocoderrunner.models.Project;
import org.protocoderrunner.base.network.NanoHTTPD;
import org.protocoderrunner.base.network.NetworkUtils;
import org.protocoderrunner.base.utils.MLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ProtocoderHttpServer extends NanoHTTPD {
    public static final String TAG = ProtocoderHttpServer.class.getSimpleName();

    private static ConnectedUser mConnectedUsers;
    private final WeakReference<Context> mContext;
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

    public ProtocoderHttpServer(Context context, int port) throws IOException {
        super(port);
        mContext = new WeakReference<Context>(context);
        mConnectedUsers = ConnectedUser.getInstance();

        String ip = NetworkUtils.getLocalIpAddress(context);
        if (ip == null) {
            MLog.i(TAG, "No IP found. Please connect to a network and try again");
        } else {
            MLog.i(TAG, "Launched server at http://" + ip.toString() + ":" + port);
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
                        Toast.makeText(mContext.get(), "Connection from " + ip, Toast.LENGTH_LONG).show();
                    }
                });
            }

            if (uri.startsWith(projectURLPrefix)) {

//                // checking if we are inside the directory so we sandbox the app
//                // TODO its pretty hack so this deserves to code it again
//                Project p = AppRunnerHelper.getInstance().getCurrentProject();
//
//                String projectFolder = "/" + p.getPath() + "/" + p.getName();
//                // MLog.d("qq", "project folder is " + projectFolder);
//                if (uri.replace(projectURLPrefix, "").contains(projectFolder)) {
//                    // MLog.d("qq", "inside project");
//                    return serveFile(uri.substring(uri.lastIndexOf('/') + 1, uri.length()), header,
//                            new File(p.getStoragePath()), false);
//                } else {
//                    // MLog.d("qq", "outside project");
//                    new Response(HTTP_NOTFOUND, MIME_HTML, "resource not found");
//                }
            }

            // file upload
            if (!files.isEmpty()) {

//                String name = parms.getProperty("name").toString();
//                String folder = parms.getProperty("fileType").toString();
//
//                Project p = AppRunnerHelper.getInstance().get(folder, name);
//
//                File src = new File(files.getProperty("pic").toString());
//                File dst = new File(p.getStoragePath() + "/" + parms.getProperty("pic").toString());
//
//                FileIO.copyFile(src, dst);
//
//                JSONObject data = new JSONObject();
//                data.put("result", "OK");
//
//                return new Response("200", MIME_TYPES.get("txt"), data.toString());

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
//                    MLog.d(TAG, "--> fetch code");
//                    name = obj.getString("name");
//                    folder = obj.getString("type");
//
//                    Project p = AppRunnerHelper.getInstance().get(folder, name);
//
//                    // TODO add type
//                    data.put("code", AppRunnerHelper.getInstance().getCode(p));

                    // list apps
                } else if (cmd.equals("list_apps")) {
//                    MLog.d(TAG, "--> list apps");
//
//                    folder = obj.getString("filter");
//
//                    ArrayList<Project> projects = null;
//                    if (folder.equals(AppRunnerHelper.FOLDER_EXAMPLES)) {
//                        projects = AppRunnerHelper.getInstance().list(folder, true);
//                    } else {
//                        projects = AppRunnerHelper.getInstance().list(folder, false);
//                    }
//                    JSONArray projectsArray = new JSONArray();
//                    for (Project project : projects) {
//                        projectsArray.put(AppRunnerHelper.getInstance().toJson(project));
//                    }
//                    data.put("projects", projectsArray);

                    // run app
                } else if (cmd.equals("run_app")) {
//                    MLog.d(TAG, "--> run app");
//
//                    name = obj.getString("name");
//                    folder = obj.getString("type");
//
//                    Project p = AppRunnerHelper.getInstance().get(folder, name);
//                    AppRunnerHelper.getInstance().setRemoteIP(obj.getString("remoteIP"));
//
//                    ProjectEvent evt = new ProjectEvent(p, Events.PROJECT_RUN);
//                    EventBus.getDefault().post(evt);

                    // execute app
                } else if (cmd.equals("execute_code")) {
//                    MLog.d(TAG, "--> execute code");
//
//                    // Save and run
//                    String code = parms.get("codeToSend").toString();
//
//                    Events.ExecuteCodeEvent evt = new Events.ExecuteCodeEvent(code);
//                    EventBus.getDefault().post(evt);
//                    MLog.i(TAG, "Execute...");

                    // save_code
                } else if (cmd.equals("push_code")) {
//                    MLog.d(TAG, "--> push code " + method + " " + header);
//                    name = parms.get("name").toString();
//                    String fileName = parms.get("fileName").toString();
//                    newCode = parms.get("code").toString();
//                    MLog.d(TAG, "fileName -> " + fileName);
//                    // MLog.d(TAG, "code -> " + newCode);
//
//                    folder = parms.get("type").toString();
//
//                    // add type
//                    Project p = AppRunnerHelper.getInstance().get(folder, name);
//                    AppRunnerHelper.getInstance().writeNewCode(p, newCode, fileName);
//                    data.put("project", AppRunnerHelper.getInstance().toJson(p));
//                    ProjectEvent evt = new ProjectEvent(p, "save");
//                    EventBus.getDefault().post(evt);
//
//                    MLog.i(TAG, "Saved");

                // hack, this should be changed
                } else if (cmd.equals("push_code_and_run")) {
//                    MLog.d(TAG, "--> push code " + method + " " + header);
//                    name = parms.get("name").toString();
//                    String fileName = parms.get("fileName").toString();
//                    newCode = parms.get("code").toString();
//                    // MLog.d(TAG, "code -> " + newCode);
//                    folder = parms.get("type").toString();
//
//                    // add type
//                    Project p = AppRunnerHelper.getInstance().get(folder, name);
//                    MLog.d(TAG, "qqm" + p.getName() + " " + p.getPath() + " " + fileName + " " + newCode);
//
//                    AppRunnerHelper.getInstance().writeNewCode(p, newCode, fileName);
//                    data.put("project", AppRunnerHelper.getInstance().toJson(p));
//                    ProjectEvent evt = new ProjectEvent(p, "save");
//                    EventBus.getDefault().post(evt);
//
//                    MLog.i(TAG, "Saved");

                    // list files in project
                } else if (cmd.equals("list_files_in_project")) {
//                    MLog.d(TAG, "--> create new project");
//                    name = obj.getString("name");
//                    folder = obj.getString("type");
//
//                    Project p = new Project(folder, name);
//                    JSONArray array = AppRunnerHelper.getInstance().listFilesInProjectJSON(p);
//                    data.put("files", array);
                    // ProjectEvent evt = new ProjectEvent(p, "new");
                    // EventBus.getDefault().post(evt);

                } else if (cmd.equals("create_new_project")) {
//                    MLog.d(TAG, "--> create new project");
//
//                    name = obj.getString("name");
//                    Project p = new Project(AppRunnerHelper.FOLDER_USER_PROJECTS, name);
//                    ProjectEvent evt = new ProjectEvent(p, "new");
//                    EventBus.getDefault().post(evt);
//
//                    Project newProject = AppRunnerHelper.getInstance().addNewProject(mContext.get(), name, AppRunnerHelper.FOLDER_USER_PROJECTS, name);

                    // remove app
                } else if (cmd.equals("remove_app")) {
//                    MLog.d(TAG, "--> remove app");
//                    name = obj.getString("name");
//                    folder = obj.getString("type");
//
//                    Project p = new Project(folder, name);
//                    AppRunnerHelper.getInstance().deleteProject(p);
//                    ProjectEvent evt = new ProjectEvent(p, "update");
//                    EventBus.getDefault().post(evt);

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
                    //APIManager.getInstance().addClass(PSimpleHttpServer.class, false);
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
        AssetManager am = mContext.get().getAssets();
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

        String currentEditor = WebEditorManager.getInstance().getCurrentEditor(mContext.get());
        if (currentEditor.equals(WebEditorManager.DEFAULT)) {

            // have the object build the directory structure, if needed.
            AssetManager am = mContext.get().getAssets();
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
            String path = WebEditorManager.getInstance().getUrlEditor(mContext.get()) + uri;
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
    }

}
