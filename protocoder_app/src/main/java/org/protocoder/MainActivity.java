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

package org.protocoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.appinterpreter.ProtocoderApp;
import org.protocoder.events.Events;
import org.protocoder.events.EventsProxy;
import org.protocoder.gui.IntroductionFragment;
import org.protocoder.gui.folderchooser.FolderChooserDialog;
import org.protocoder.gui.folderchooser.FolderChooserFragment;
import org.protocoder.gui.projectlist.ProjectListFragment;
import org.protocoder.helpers.ProtoAppHelper;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.ProtocoderHttpServer2;
import org.protocoder.server.ProtocoderServerService;
import org.protocoder.server.model.ProtoFileCode;
import org.protocoder.server.model.NetworkExchangeObject;
import org.protocoder.server.model.ProtoFile;
import org.protocoder.settings.ProtocoderSettings;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // custom app runner
    protected AppRunnerCustom appRunner;
    EventsProxy eventsReceiver;

    // ui
    private Toolbar mToolbar;
    private ProjectListFragment mListFragmentBase;
    private FolderChooserFragment mFolderChooserFragment;
    private Button btnFolderChooser;

    private ProtocoderHttpServer2 protocoderHttpServer2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Init the event proxy
         */
        eventsReceiver = new EventsProxy();

        /*
         * Setup the ui
         */
        setContentView(R.layout.activity_main);
        setupActivity();
        addProjectFolderChooser(savedInstanceState);
        addProjectListFragment(savedInstanceState);
        //showIntroduction(savedInstanceState);

        /*
         * init custom appRunner
         */
        appRunner = new AppRunnerCustom(this);
        appRunner.initDefaultObjects().initInterpreter();
        ProtocoderApp protocoderApp = new ProtocoderApp(appRunner);
        protocoderApp.network.checkVersion();
        appRunner.interp.eval("device.vibrate(100);");

        /*
         * Servers
         */


        /*
        * Poor man testing area
        */
        /*
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                MLog.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });
        */

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // gson serialization
        if (false) {
            NetworkExchangeObject networkExchangeObject = new NetworkExchangeObject();
            networkExchangeObject.action = "run";
            networkExchangeObject.project = new Project("name", "folder");

            ProtoFileCode codefile = new ProtoFileCode("name", "path", "code");
            networkExchangeObject.files.add(codefile);
            networkExchangeObject.files.add(codefile);

            String json = gson.toJson(networkExchangeObject);
            MLog.d(TAG, json);

            // gson deserialization
            NetworkExchangeObject n1 = gson.fromJson(json, NetworkExchangeObject.class);
            MLog.d(TAG, n1.project.getName() + " " + n1.project.getPath());
        }

        // list examples folders
        if (false) {
            ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder("./examples", 0);
            String jsonFiles = gson.toJson(files);
            MLog.d(TAG, "list examples folders -> " + jsonFiles);
        }

        // list all examples 30min
        if (false) {
            ArrayList<ProtoFile> files1 = ProtoScriptHelper.listFilesInFolder("./examples", 1);
            String jsonFiles1 = gson.toJson(files1);
            MLog.d(TAG, "list all examples -> " + jsonFiles1);
        }

        // list all files with 10 levels
        if (false) {
            ArrayList<ProtoFile> files2 = ProtoScriptHelper.listFilesInFolder(".", 10);
            String jsonFiles2 = gson.toJson(files2);
            MLog.d(TAG, "list all files with 10 levels -> " + jsonFiles2);
        }

        // run project
        if (false) {
            ProtoAppHelper.launchScript(this, new Project("examples/Media", "Sound"));
        }

        // run settings
        if (false) {
            ProtoAppHelper.launchSettings(this);
        }

        // run settings
        if (false) {
            ProtoAppHelper.launchLicense(this);
        }

        // run editor
        if (false) {
            ProtoAppHelper.launchEditor(this, new Project("examples/Media", "Sound"));
        }

        // stop project 1h
        if (true) {
            startServers();
        }

        // start servers 1h
        if (true) {

        }

        // stop servers 1h

        // list running projects 1h


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        startBroadCastReceiver();

        try {
            protocoderHttpServer2 = new ProtocoderHttpServer2(this, ProtocoderSettings.HTTP_PORT);
        } catch (IOException e) {
            MLog.e(TAG, "http server not initialized");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        protocoderHttpServer2.close();
        unregisterReceiver(adbBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
     * This broadcast will receive JS commands if is in debug mode, useful to debug the app through adb
     */
    BroadcastReceiver adbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getStringExtra("cmd");
            MLog.d(TAG, "executing >> " + cmd);
            appRunner.interp.eval(cmd);
        }
    };

    private void startBroadCastReceiver() {
        if (ProtocoderSettings.DEBUG) {
            //execute commands from intents
            //ie: adb shell am broadcast -a org.protocoder.intent.EXECUTE --es cmd "device.vibrate(100)"

            IntentFilter filterSend = new IntentFilter();
            filterSend.addAction("org.protocoder.intent.EXECUTE");
            registerReceiver(adbBroadcastReceiver, filterSend);
        }
    }

    /*
     * Server
     */
    private void startServers() {
        MLog.d(TAG, "starting servers");
        Intent serverIntent = new Intent(this, ProtocoderServerService.class);
        //serverIntent.putExtra(Project.FOLDER, folder);
        startService(serverIntent);
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /*
    * UI Stuff
    */
    private void setupActivity() {
        // Make the app take as much space as it can
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Create the action bar programmatically
        if (!AndroidUtils.isWear(this)) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);

            // Set the padding to match the Status Bar height
            mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        // project folder menu
        btnFolderChooser = (Button) findViewById(R.id.selectFolderButton);
        btnFolderChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderChooserDialog myDialog = FolderChooserDialog.newInstance();
                getSupportFragmentManager().beginTransaction().add(myDialog, "12345").commit();
            }
        });

    }

    // Project folder chooser
    private void addProjectFolderChooser(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mFolderChooserFragment = FolderChooserFragment.newInstance(ProtocoderSettings.EXAMPLES_FOLDER, true);
            addFragment(mFolderChooserFragment, R.id.fragmentFolderChooser);
        }
    }

    // add the project list fragment
    private void addProjectListFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mListFragmentBase = ProjectListFragment.newInstance("", true);

            addFragment(mListFragmentBase, R.id.fragmentScriptList);
        } else {
            // mProtocoder.protoScripts.reinitScriptList();
        }

        EventBus.getDefault().post(new Events.FolderChosen("Examples", "Media"));
    }

    private void showIntroduction(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            IntroductionFragment introductionFragment = IntroductionFragment.newInstance();
            addFragment(introductionFragment, R.id.fragmentIntroduction);
        } else {
            // mProtocoder.protoScripts.reinitScriptList();
        }
    }

    private void addFragment(Fragment f, int id) {
        FrameLayout fl = (FrameLayout) findViewById(id);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), f, String.valueOf(fl.getId()));
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // TODO
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            return true;
        } else if (itemId == R.id.menu_new) {
            //createProjectDialog();
            return true;
        } else if (itemId == R.id.menu_help) {
            //this.showHelp(true);
            return true;
        } else if (itemId == R.id.menu_settings) {
            ProtoAppHelper.launchSettings(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    */

    // execute lines
    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        String code = e.getCode();
        MLog.d(TAG, "event -> " + code);

        if (ProtocoderSettings.DEBUG) {
             appRunner.interp.eval(code);
        }
    }

    // folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, "< Event (folderChosen)");
        String folder = e.getFullFolder();
        btnFolderChooser.setText(folder);
    }

    // Run project
    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getAction());
        ProtoAppHelper.launchScript(this, e.getProject());
    }

}
