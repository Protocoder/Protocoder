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

package org.protocoder;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;

import org.protocoder.appApi.Protocoder;
import org.protocoder.fragments.SettingsFragment;
import org.protocoderrunner.apprunner.AppRunnerInterpreter;
import org.protocoderrunner.apprunner.api.PFileIO;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PNetwork;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.PUtil;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.events.Events.ProjectEvent;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;

import java.lang.reflect.Field;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {

	private static final String TAG = "MainActivity";

	MainActivity c;
	Menu mMenu;

	private FileObserver observer;

	// connection
	BroadcastReceiver mStopServerReceiver;

	private ConnectivityChangeReceiver connectivityChangeReceiver;
    private Protocoder mProtocoder;
    private PUtil mPUtil;
    private PUI mPUi;
    public AppRunnerInterpreter interp;
    private PNetwork mPNetwork;
    private PFileIO mPFileIO;
    private PMedia mPMedia;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forfragments);
		c = this;

		// Create the action bar programmatically
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

        mProtocoder = Protocoder.getInstance(this);
        mProtocoder.init();
        mPUtil = new PUtil(this);
        mPUi = new PUI(this);
        mPNetwork = new PNetwork(this);
        mPFileIO = new PFileIO(this);
        mPMedia = new PMedia(this);


       /*
        *  Views
        */
        addFragments();

        // Check when a file is changed in the protocoder dir
		observer = new FileObserver(ProjectManager.FOLDER_USER_PROJECTS, FileObserver.CREATE | FileObserver.DELETE) {

			@Override
			public void onEvent(int event, String file) {
				if ((FileObserver.CREATE & event) != 0) {

					MLog.d(TAG, "File created [" + ProjectManager.FOLDER_USER_PROJECTS + "/" + file + "]");

					// check if its a "create" and not equal to probe because
					// thats created every time camera is
					// launched
				} else if ((FileObserver.DELETE & event) != 0) {
					MLog.d(TAG, "File deleted [" + ProjectManager.FOLDER_USER_PROJECTS + "/" + file + "]");

				}
			}
		};

        connectivityChangeReceiver = new ConnectivityChangeReceiver();


        interp = new AppRunnerInterpreter(this);
        interp.createInterpreter(true);

        interp.interpreter.addObjectToInterface("ui", mPUi);
        interp.interpreter.addObjectToInterface("util", mPUtil);
        interp.interpreter.addObjectToInterface("protocoder", mProtocoder);

    }

    private void addFragments() {
        MLog.d(TAG, "fragments adding ");

        //colors
        final int c0 = getResources().getColor(R.color.project_user_color);
        final int c1 = getResources().getColor(R.color.project_example_color);

        mProtocoder.protoScripts.addScriptList(R.drawable.ic_script, "projects", c0, false);
        mProtocoder.protoScripts.addScriptList(R.drawable.ic_script_example, "examples", c1, true);

        MLog.d(TAG, "fragments added ");
    }

    /**
	 * onResume
	 */
	@Override
	protected void onResume() {
		super.onResume();

        //set settings
        setScreenAlwaysOn(SettingsFragment.getScreenOn(this));

        MLog.d(TAG, "Registering as an EventBus listener in MainActivity");
		EventBus.getDefault().register(this);

		mStopServerReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
                mProtocoder.app.hardKillConnections();
			}
		};

		registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mProtocoder.app.startServers();
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction("org.protocoder.intent.action.STOP_SERVER");
		registerReceiver(mStopServerReceiver, filterSend);
		observer.startWatching();

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			// presumably, not relevant
		}

        IDEcommunication.getInstance(this).ready(false);
    }

	/**
	 * onPause
	 */
	@Override
	protected void onPause() {
		super.onPause();

		EventBus.getDefault().unregister(this);
		unregisterReceiver(mStopServerReceiver);
		observer.stopWatching();
		unregisterReceiver(connectivityChangeReceiver);

	}

	/**
	 * onDestroy
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ViewGroup vg = (ViewGroup) findViewById(R.layout.activity_forfragments);
		if (vg != null) {
			vg.invalidate();
			vg.removeAllViews();
		}
		mProtocoder.app.killConnections();
	}


	// TODO call intent and kill it in an appropiate way
	public void onEventMainThread(ProjectEvent evt) {
		// Using transaction so the view blocks
		MLog.d(TAG, "event -> " + evt.getAction());

		if (evt.getAction() == "run") {
            Project p = evt.getProject();
            mProtocoder.protoScripts.run(p.getFolder(), p.getName());
		} else if (evt.getAction() == "save") {
            Project p = evt.getProject();
            mProtocoder.protoScripts.refresh(p.getFolder(), p.getName());
		} else if (evt.getAction() == "new") {
			MLog.d(TAG, "creating new project " + evt.getProject().getName());
            mProtocoder.protoScripts.createProject("projects", evt.getProject().getName());
		} else if (evt.getAction() == "update") {
            mProtocoder.protoScripts.listRefresh();
		}
	}


    // execute lines
    public void onEventMainThread(Events.ExecuteCodeEvent evt) {
        String code = evt.getCode();
        MLog.d(TAG, "event -> " + code);

        interp.eval(code);
    }

    public void onEventMainThread(Events.SelectedProjectEvent evt) {
        String folder = evt.getFolder();
        String name = evt.getName();

        mProtocoder.protoScripts.goTo(folder, name);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);

		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			return true;
		} else if (itemId == R.id.menu_new) {
			Protocoder.getInstance(this).protoScripts.createProjectDialog();
			return true;
		} else if (itemId == R.id.menu_help) {
            Protocoder.getInstance(this).app.showHelp(true);
			return true;
		} else if (itemId == R.id.menu_settings) {
		    Protocoder.getInstance(this).app.showSettings(true);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * Key management
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent evt) {
		MLog.d("BACK BUTTON", "Back button was pressed");
		if (keyCode == 4) {
			Fragment fragment = getSupportFragmentManager().findFragmentByTag("editorFragment");
			if (fragment != null && fragment.isVisible()) {
				MLog.d(TAG, "Removing editor");
				removeFragment(fragment);
				return true;
			} else {
				finish();
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN && event.isCtrlPressed()) {

			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.KEYCODE_R:
				MLog.d(TAG, "run app");
				break;

			default:
				break;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	// check if connection has changed
	public class ConnectivityChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			AndroidUtils.debugIntent("connectivityChangerReceiver", intent);
            mProtocoder.app.startServers();
		}
	}
}
