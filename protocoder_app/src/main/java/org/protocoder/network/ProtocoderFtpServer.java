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

import android.content.Context;
import android.content.res.AssetManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.protocoder.appApi.EditorManager;
import org.protocoder.appApi.Protocoder;
import org.protocoder.appApi.Settings;
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
import org.protocoderrunner.apprunner.api.other.PCamera;
import org.protocoderrunner.apprunner.api.other.PDeviceEditor;
import org.protocoderrunner.apprunner.api.other.PEvents;
import org.protocoderrunner.apprunner.api.other.PFtpServer;
import org.protocoderrunner.apprunner.api.other.PLiveCodingFeedback;
import org.protocoderrunner.apprunner.api.other.PMidi;
import org.protocoderrunner.apprunner.api.other.PProcessing;
import org.protocoderrunner.apprunner.api.other.PPureData;
import org.protocoderrunner.apprunner.api.other.PSimpleHttpServer;
import org.protocoderrunner.apprunner.api.other.PSocketIOClient;
import org.protocoderrunner.apprunner.api.other.PSqLite;
import org.protocoderrunner.apprunner.api.other.PVideo;
import org.protocoderrunner.apprunner.api.other.PWebEditor;
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
import org.protocoderrunner.apprunner.api.widgets.PWebView;
import org.protocoderrunner.apprunner.api.widgets.PWindow;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.network.FtpServer;
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

public class ProtocoderFtpServer extends FtpServer {
	public static final String TAG = "ProtocoderFtpServer";
	private final WeakReference<Context> ctx;

	private static ProtocoderFtpServer instance = null;
    private static boolean started = false;


    public static ProtocoderFtpServer getInstance(Context aCtx, int port) {
        MLog.d(TAG, "launching ftp server... " + instance);
        if (instance == null) {
            instance = new ProtocoderFtpServer(aCtx, port);
        }

        return instance;
    }

    public ProtocoderFtpServer(Context c, int port) {
        super(port);
        MLog.d(TAG, "" + port);

        ctx = new WeakReference<Context>(c);
        Settings settings = Protocoder.getInstance(ctx.get()).settings;

        MLog.d(TAG, "" + settings.getFtpUserName() + " " + settings.getFtpUserPassword());
        addUser(settings.getFtpUserName(), settings.getFtpUserPassword(), ProjectManager.getInstance().getBaseDir(), true);
    }

	public void stopServer() {
		instance = null;

        if (instance != null) {
            stop();
            started = false;
        }
	}

    public void startServer() {
        if (!started) {
            start();
            MLog.d(TAG, "start 4");
            started = true;
        }
    }

    public boolean isStarted() {
        return started;
    }
}
