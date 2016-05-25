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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.appinterpreter.AppRunnerCustom;
import org.protocoder.appinterpreter.ProtocoderApp;
import org.protocoder.events.Events;
import org.protocoder.gui.IntroductionFragment;
import org.protocoder.gui._components.NewProjectDialogFragment;
import org.protocoder.gui.editor.FileManagerDialog;
import org.protocoder.gui.folderchooser.FolderChooserFragment;
import org.protocoder.gui.projectlist.ProjectListFragment;
import org.protocoder.gui.settings.ProtocoderSettings;
import org.protocoder.helpers.ProtoAppHelper;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.ProtocoderServerService;
import org.protocoder.server.model.ProtoFile;
import org.protocoder.server.networkexchangeobjects.NEOProject;
import org.protocoderrunner.api.widgets.PPopupDialogFragment;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // custom app runner
    protected AppRunnerCustom appRunner;

    // ui
    private ProjectListFragment mListFragmentBase;
    private FolderChooserFragment mFolderChooserFragment;
    // private TextView mTxtIp;

    private Intent mServerIntent;
    private boolean isTablet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTablet = getResources().getBoolean(R.bool.isTablet);
        MLog.d(TAG, "isTablet " + isTablet);

        /*
         * Setup the ui
         */
        setContentView(R.layout.main_activity);
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
            NEOProject networkExchangeObject = new NEOProject();
            networkExchangeObject.cmd = "run";
            networkExchangeObject.project = new Project("name", "folder");

            ProtoFile codefile = new ProtoFile("name", "path");
            codefile.code = "qq";
            networkExchangeObject.files.add(codefile);
            networkExchangeObject.files.add(codefile);

            String json = gson.toJson(networkExchangeObject);
            MLog.d(TAG, json);

            // gson deserialization
            NEOProject n1 = gson.fromJson(json, NEOProject.class);
            MLog.d(TAG, n1.project.getName() + " " + n1.project.getPath());
        }

        // list examples & projects subfolders
        if (false) {
            ArrayList<ProtoFile> files = ProtoScriptHelper.listFilesInFolder("./", 0);
            String jsonFiles = gson.toJson(files);
            MLog.d(TAG, "list examples folders -> " + jsonFiles);
        }

        // list all examples 30min
        if (false) {
            ArrayList<ProtoFile> files1 = ProtoScriptHelper.listFilesInFolder("./examples", 1);
            String jsonFiles1 = gson.toJson(files1);
            MLog.d(TAG, "list all examples -> " + jsonFiles1);
        }

        // list all mCurrentFileList with 10 levels
        if (false) {
            ArrayList<ProtoFile> files2 = ProtoScriptHelper.listFilesInFolder(".", 10);
            String jsonFiles2 = gson.toJson(files2);
            MLog.d(TAG, "list all mCurrentFileList with 10 levels -> " + jsonFiles2);
        }

        // run project
        if (false) {
            ProtoAppHelper.launchScript(this, new Project("user_projects/User Projects", "f10"));
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

        // load filemanager in fragment
        // currentFolder
        // mCurrentFileList
        //
        if (false) {
            FragmentManager fm = getSupportFragmentManager();
            PPopupDialogFragment pPopupCustomFragment = PPopupDialogFragment.newInstance(fm);
            TextView txt = new TextView(this);
            txt.setText("hola");

            MLog.d(TAG, pPopupCustomFragment + " " + txt + " ");
            pPopupCustomFragment.addView(txt);
            pPopupCustomFragment.show();
        }

        if (false) {
            FragmentManager fm = getSupportFragmentManager();
            FileManagerDialog fmd = FileManagerDialog.newInstance();
            fm.beginTransaction().add(fmd, "qqa").commit();
        }

        // start servers
        if (true) {
            startServers();
        }

        // stop servers 1h
        if (false) {
            stopServers();
        }

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

        unregisterReceiver(adbBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopServers();
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
        mServerIntent = new Intent(this, ProtocoderServerService.class);
        //serverIntent.putExtra(Project.FOLDER, folder);
        startService(mServerIntent);
    }

    private void stopServers() {
        stopService(mServerIntent);
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
    @Override
    protected void setupActivity() {
        super.setupActivity();

        final TextView title = (TextView) findViewById(R.id.toolbar2_title);
        title.animate().translationY(0).withStartAction(new Runnable(){
            public void run(){
                title.setTranslationY(500 - title.getY());
            }
        }).setDuration(1000).start();

        // FileManagerDialog myDialog = FileManagerDialog.newInstance();
        // getSupportFragmentManager().beginTransaction().add(myDialog, "12345").commit();

        /*
        mTxtIp = (TextView) findViewById(R.id.ip);
        Button btnIp = (Button) findViewById(R.id.button11);
        btnIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView(findViewById(R.id.button13));
            }
        });
        */

    }

    public void toggleView(View v) {
        if (v.getVisibility() == View.VISIBLE) v.setVisibility(View.GONE);
        else v.setVisibility(View.VISIBLE);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            return true;
        } else if (itemId == R.id.menu_new) {
            createProjectDialog();
            return true;
        } else if (itemId == R.id.menu_help) {
            ProtoAppHelper.launchLicense(this);
            return true;
        } else if (itemId == R.id.menu_settings) {
            ProtoAppHelper.launchSettings(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void createProjectDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");

        newProjectDialog.setListener(new NewProjectDialogFragment.NewProjectDialogListener() {
            @Override
            public void onFinishEditDialog(String inputText) {
                Toast.makeText(MainActivity.this, "Creating " + inputText, Toast.LENGTH_SHORT).show();
                ProtoScriptHelper.createNewProject(MainActivity.this, "", inputText);
            }
        });
    }

    // execute lines
    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        String code = e.getCode();
        MLog.d(TAG, "event -> " + code);

        if (ProtocoderSettings.DEBUG && e.getProject() != null) {
             appRunner.interp.eval(code);
        }
    }

    // network notification
    @Subscribe
    public void onEventMainThread(Events.Connection e) {
        String type = e.getType();
        String address = e.getAddress();
        // mTxtIp.setText(type + " " + address);

        MLog.d(TAG, " got event "); // No WIFI, still you can hack via USB using the adb command");
        //MLog.d(TAG, "Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(ProtocoderServerService.this) + ":" + ProtocoderSettings.HTTP_PORT);
    }

}
