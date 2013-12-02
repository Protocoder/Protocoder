/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

import java.net.UnknownHostException;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.base.AppSettings;
import org.protocoder.base.BaseActivity;
import org.protocoder.base.BaseMainApp;
import org.protocoder.base.BaseNotification;
import org.protocoder.events.Events.ProjectEvent;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.fragments.NewProjectDialog;
import org.protocoder.network.ALog;
import org.protocoder.network.CustomWebsocketServer;
import org.protocoder.network.MyHTTPServer;
import org.protocoder.network.NetworkUtils;
import org.protocoder.projectlist.ListFragmentExamples;
import org.protocoder.projectlist.ListFragmentUserProjects;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements NewProjectDialog.NewProjectDialogListener {

    private static final String TAG = "MainActivity";

    Context c;
    private int mProjectRequestCode = 1;
    public boolean ledON = true;
    protected float servoVal;
    Handler handler;
    BaseNotification mNotification;
    Menu mMenu;
    BroadcastReceiver mStopServerReceiver;

    MyHTTPServer httpServer;

    ProjectsPagerAdapter mProjectPagerAdapter;
    ViewPager mViewPager;

    private ListFragmentUserProjects userProjectListFragment;
    private ListFragmentExamples exampleListFragment;

    private Boolean showingHelp = false;

    private TextView textIP;
    private LinearLayout mIpContainer;
    protected int textIPHeight;

    private Intent currentProjectApplicationIntent;

    private CustomWebsocketServer ws;

    private FileObserver observer;

    private ConnectivityChangeReceiver connectivityChangeReceiver;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// Set the content view and get the context
	setContentView(R.layout.activity_forfragments);
	c = this;

	// Create the action bar programmatically
	ActionBar actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);

	// Instantiate fragments
	userProjectListFragment = new ListFragmentUserProjects();
	exampleListFragment = new ListFragmentExamples();
	// addFragment(projectListFragment, R.id.f1, false);

	mProjectPagerAdapter = new ProjectsPagerAdapter(getSupportFragmentManager());
	mProjectPagerAdapter.setExamplesFragment(exampleListFragment);
	mProjectPagerAdapter.setProjectsFragment(userProjectListFragment);

	// Set up the ViewPager, attaching the adapter.
	mViewPager = (ViewPager) findViewById(R.id.pager);
	mViewPager.setAdapter(mProjectPagerAdapter);

	// Start the servers
	startServers();

	observer = new FileObserver(BaseMainApp.projectsDir,
	// set up a file obs`erver to
	// watch this directory on sd card
		FileObserver.CREATE | FileObserver.DELETE) {

	    @Override
	    public void onEvent(int event, String file) {
		if ((FileObserver.CREATE & event) != 0) {

		    Log.d(TAG, "File created [" + BaseMainApp.projectsDir + "/" + file + "]");

		    // check if its a "create" and not
		    // equal to probe because thats created
		    // every time camera is launched
		} else if ((FileObserver.DELETE & event) != 0) {
		    Log.d(TAG, "File deleted [" + BaseMainApp.projectsDir + "/" + file + "]");

		}
	    }
	};

	connectivityChangeReceiver = new ConnectivityChangeReceiver();

    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
	super.onResume();
	Log.d(TAG, "Registering as an EventBus listener in MainActivity");
	EventBus.getDefault().register(this);

	// TODO do something with the webserver
	// Create broadcast receiver for if the user cancels from the curtain
	mStopServerReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
		hardKillConnections();
	    }
	};

	registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	startServers();
	IntentFilter filterSend = new IntentFilter();
	filterSend.addAction("com.makewithmoto.intent.action.STOP_SERVER");
	registerReceiver(mStopServerReceiver, filterSend);
	observer.startWatching();
	// startServers();
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
	killConnections();
	// TODO add stop websocket
    }

    /**
     * Starts the remote service connection
     */
    private int startServers() {

	/*
	 * IntentFilter intentFilter = new IntentFilter(); intentFilter.addAction
	 * (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION); registerReceiver(new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { final String action = intent.getAction();
	 * if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) { if (intent.getBooleanExtra(
	 * WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) { Log.d(TAG, "wifi connection on"); } else { Log.d(TAG,
	 * "wifi connection lost"); } }
	 * 
	 * } }, intentFilter);
	 */

	// Create the IP text view
	textIP = (TextView) findViewById(R.id.ip);
	textIP.setOnClickListener(null);// Remove the old listener explicitly
	textIP.setBackgroundResource(0);
	mIpContainer = (LinearLayout) findViewById(R.id.ip_container);
	updateStartStopActionbarItem();

	// check if wifi connection is available
	if (NetworkUtils.isNetworkAvailable(c) != true) {
	    Log.d(TAG, "There is no connection");
	    // textIP.setText("Connect to a network if you want to code via your computer");
	    hardKillConnections();
	    return -1;
	} else {

	}

	Log.d(TAG, "There is connection");
	// Show the notification
	SharedPreferences prefs = getSharedPreferences("com.makewithmoto", MODE_PRIVATE);
	boolean showNotification = prefs
		.getBoolean(getResources().getString(R.string.pref_curtain_notifications), true);
	if (showNotification) {
	    mNotification = new BaseNotification(this);
	    /*
	     * mNotification.show(MainActivity.class, R.drawable.ic_stat_logo, "http://" +
	     * NetworkUtils.getLocalIpAddress(this) + ":" + AppSettings.httpPort, "protocoder Running",
	     * R.drawable.ic_navigation_cancel);
	     */
	}

	// start webserver
	httpServer = MyHTTPServer.getInstance(getApplicationContext(), AppSettings.httpPort);

	// websocket
	try {
	    ws = CustomWebsocketServer.getInstance(this, AppSettings.websocketPort, new Draft_17());
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	final Handler handler = new Handler();
	Runnable r = new Runnable() {

	    @Override
	    public void run() {

		// Log.d(TAG, " " + x);
		JSONObject obj = new JSONObject();
		try {
		    obj.put("executeRemote", "addButton");
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		ws.send(obj);

		handler.postDelayed(this, 1000);
	    }
	};
	// handler.postDelayed(r, 0);

	// check if there is a WIFI connection or we can connect via USB
	if (NetworkUtils.getLocalIpAddress(this) == null) {
	    textIP.setText("No WIFI, still you can hack via USB using the companion app");
	} else {
	    textIP.setText("Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(this) + ":"
		    + AppSettings.httpPort);
	}

	if (httpServer != null) {// If no instance of HTTPServer, we set the IP
				 // address view to gone.
	    textIP.setVisibility(View.VISIBLE);
	} else {
	    textIP.setVisibility(View.GONE);
	}

	// Add animations
	ViewTreeObserver vto = mIpContainer.getViewTreeObserver();
	vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	    @Override
	    public void onGlobalLayout() {
		ViewTreeObserver obs = mIpContainer.getViewTreeObserver();

		textIPHeight = mIpContainer.getHeight();
		mIpContainer.setTranslationY(textIPHeight);

		// FIXME: This animation should be done with an xml file
		mIpContainer.setAlpha(0);
		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mIpContainer, View.ALPHA, 1); //
		alphaAnimator.setDuration(1200); //

		final ObjectAnimator shiftAnimator = ObjectAnimator.ofFloat(mIpContainer, View.TRANSLATION_Y, 0); // shiftAnimator.setRepeatCount(1);
		shiftAnimator.setRepeatMode(ValueAnimator.REVERSE);
		shiftAnimator.setDuration(1200);
		shiftAnimator.setInterpolator(new DecelerateInterpolator());

		final AnimatorSet setAnimation = new AnimatorSet();

		setAnimation.play(alphaAnimator).with(shiftAnimator);
		setAnimation.start();

		if (AppSettings.CURRENT_VERSION > Build.VERSION.SDK_INT) {
		    obs.removeOnGlobalLayoutListener(this);
		} else {
		    obs.removeGlobalOnLayoutListener(this);
		}
	    }

	});

	return 1;
    }

    /**
     * Unbinds service and stops the http server
     */
    private void killConnections() {
	// TODO enable this at some point
	// TODO add websocket

	if (httpServer != null) {
	    httpServer.close();
	    httpServer = null;
	}
	// Hide the notification
	SharedPreferences prefs = getSharedPreferences("com.makewithmoto", MODE_PRIVATE);
	boolean showNotification = prefs
		.getBoolean(getResources().getString(R.string.pref_curtain_notifications), true);
	if (showNotification) {
	    if (mNotification != null)
		mNotification.hide();
	}
	textIP.setText(getResources().getString(R.string.start_the_server));
	textIP.setOnClickListener(null);// Remove the old listener explicitly
	textIP.setBackgroundResource(0);
    }

    /**
     * Explicitly kills connections, with UI impact
     */
    private void hardKillConnections() {
	// TODO enable this at some point
	// TODO add here websocket

	if (httpServer != null) {
	    httpServer.stop();
	    httpServer = null;
	}
	textIP.setText(getResources().getString(R.string.start_the_server));
	updateStartStopActionbarItem();
	textIP.setOnClickListener(null);// Remove the old listener explicitly
	textIP.setBackgroundResource(R.drawable.transparent_blue_button);
	textIP.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		startServers();
		updateStartStopActionbarItem();
	    }
	});

	// Hide the notification
	SharedPreferences prefs = getSharedPreferences("com.makewithmoto", MODE_PRIVATE);
	boolean showNotification = prefs
		.getBoolean(getResources().getString(R.string.pref_curtain_notifications), true);
	if (showNotification) {
	    if (mNotification != null)
		mNotification.hide();
	}
    }

    private void updateStartStopActionbarItem() {
	getActionBar().show();
	if (mMenu != null) {
	    MenuItem stopServerAction = mMenu.findItem(R.id.menu_start_stop);
	    if (httpServer != null) {
		stopServerAction.setTitle(getResources().getString(R.string.menu_label_stop_server));
	    } else {
		stopServerAction.setTitle(getResources().getString(R.string.menu_label_start_server));
	    }
	}
    }

    // TODO call intent and kill it in an appropiate way
    public void onEventMainThread(ProjectEvent evt) {
	// Using transaction so the view blocks
	Log.d(TAG, "event -> " + evt.getAction());

	if (evt.getAction() == "run") {
	    if (currentProjectApplicationIntent != null) {
		finishActivity(mProjectRequestCode);
		currentProjectApplicationIntent = null;
	    }

	    try {
		currentProjectApplicationIntent = new Intent(MainActivity.this, AppRunnerActivity.class);
		String script = ProjectManager.getInstance().getCode(evt.getProject());

		Project p = evt.getProject();

		currentProjectApplicationIntent.putExtra("projectName", p.getName());
		currentProjectApplicationIntent.putExtra("projectType", p.getType());

		// check if the apprunner is installed
		// final PackageManager mgr = this.getPackageManager();
		// List<ResolveInfo> list = mgr.queryIntentActivities(
		// currentProjectApplicationIntent,
		// PackageManager.MATCH_DEFAULT_ONLY);

		// Log.d(TAG, "intent available " + list.size());

		startActivityForResult(currentProjectApplicationIntent, mProjectRequestCode);
	    } catch (Exception e) {
		Log.d(TAG, "Error launching script");
	    }

	} else if (evt.getAction() == "save") {
	    Log.d(TAG, "saving project " + evt.getProject().getName());

	    if (evt.getProject().getType() == ProjectManager.PROJECT_EXAMPLE) {
		exampleListFragment.projectRefresh(evt.getProject().getName());
	    } else if (evt.getProject().getType() == ProjectManager.PROJECT_USER_MADE) {
		userProjectListFragment.projectRefresh(evt.getProject().getName());
	    }

	} else if (evt.getAction() == "new") {
	    Log.d("qq", "creating new project " + evt.getProject().getName());
	    newProject(evt.getProject().getName());
	}

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

	switch (item.getItemId()) {

	case android.R.id.home:

	    return true;
	case R.id.menu_new:
	    showEditDialog();

	    return true;
	case R.id.menu_help:
	    Intent aboutActivityIntent = new Intent(this, AboutActivity.class);
	    startActivity(aboutActivityIntent);
	    overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);

	    return true;
	case R.id.menu_start_stop:
	    if (httpServer != null) {
		hardKillConnections();
	    } else {
		startServers();
	    }
	    updateStartStopActionbarItem();
	    return true;
	case R.id.menu_settings:
	    Intent preferencesIntent = new Intent(this, SetPreferenceActivity.class);
	    startActivity(preferencesIntent);
	    overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    /*
     * New project dialog
     */
    private void showEditDialog() {
	FragmentManager fm = getSupportFragmentManager();
	NewProjectDialog newProjectDialog = new NewProjectDialog();
	newProjectDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
	Toast.makeText(this, "Creating " + inputText, Toast.LENGTH_SHORT).show();

	newProject(inputText);
    }

    public void newProject(String inputText) {
	Project newProject = ProjectManager.getInstance().addNewProject(c, inputText, inputText,
		ProjectManager.PROJECT_USER_MADE);

	userProjectListFragment.projects.add(newProject);
	userProjectListFragment.notifyAddedProject();
    }

    /*
     * Key management
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent evt) {
	ALog.d("BACK BUTTON", "Back button was pressed");
	if (keyCode == 4) {
	    Fragment fragment = getSupportFragmentManager().findFragmentByTag("editorFragment");
	    if (fragment != null && fragment.isVisible()) {
		ALog.d("Removing editor");
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
		Log.d(TAG, "run app");
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
	    debugIntent(intent, "grokkingandroid");
	    startServers();
	}

	private void debugIntent(Intent intent, String tag) {
	    Log.v(tag, "action: " + intent.getAction());
	    Log.v(tag, "component: " + intent.getComponent());
	    Bundle extras = intent.getExtras();
	    if (extras != null) {
		for (String key : extras.keySet()) {
		    Log.v(tag, "key [" + key + "]: " + extras.get(key));
		}
	    } else {
		Log.v(tag, "no extras");
	    }
	}

    }
}
