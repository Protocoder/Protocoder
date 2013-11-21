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
import org.protocoder.base.BaseActivity;
import org.protocoder.base.BaseMainApp;
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
import org.protocoder.views.CanvasView;
import org.protocoder.views.ProjectSelectorStrip;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
    private Intent currentProjectApplicationIntent;

    // fragments that hold the projects
    private ListFragmentUserProjects userProjectListFragment;
    private ListFragmentExamples exampleListFragment;

    Menu mMenu;
    ProjectsPagerAdapter mProjectPagerAdapter;
    ViewPager mViewPager;
    private TextView textIP;
    private LinearLayout mIpContainer;
    protected int textIPHeight;

    private FileObserver observer;

    // connection
    BroadcastReceiver mStopServerReceiver;
    MyHTTPServer httpServer;
    private CustomWebsocketServer ws;
    private ConnectivityChangeReceiver connectivityChangeReceiver;
    int usbEnabled = 0;


    int calculateColor(float fraction, int startValue, int endValue) {

	int startInt = (Integer) startValue;
	int startA = (startInt >> 24) & 0xff;
	int startR = (startInt >> 16) & 0xff;
	int startG = (startInt >> 8) & 0xff;
	int startB = startInt & 0xff;

	int endInt = (Integer) endValue;
	int endA = (endInt >> 24) & 0xff;
	int endR = (endInt >> 16) & 0xff;
	int endG = (endInt >> 8) & 0xff;
	int endB = endInt & 0xff;

	return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
		| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
		| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
		| (int) ((startB + (int) (fraction * (endB - startB))));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_forfragments);
	c = this;

	// Create the action bar programmatically
	ActionBar actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);

	// Instantiate fragments
	userProjectListFragment = new ListFragmentUserProjects();
	exampleListFragment = new ListFragmentExamples();

	mProjectPagerAdapter = new ProjectsPagerAdapter(getSupportFragmentManager());
	mProjectPagerAdapter.setExamplesFragment(exampleListFragment);
	mProjectPagerAdapter.setProjectsFragment(userProjectListFragment);

	mViewPager = (ViewPager) findViewById(R.id.pager);
	mViewPager.setAdapter(mProjectPagerAdapter);
	
	//CanvasView c = (CanvasView) findViewById(R.id.cView);
	//Paint paint = new Paint();
	
	//paint.setColor(Color.argb(15, 255, 0, 0));
	//c.getCanvas().drawRect(0, 0, 500, 500, paint); 
	

	// final int c0 = Color.parseColor( getResources().getColor(R.color.project_user_color) );
	final int c0 = getResources().getColor(R.color.project_user_color);
	final int c1 = getResources().getColor(R.color.project_example_color);

	final ProjectSelectorStrip strip = (ProjectSelectorStrip) findViewById(R.id.pager_title_strip);
	mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

	    @Override
	    public void onPageSelected(int arg0) {
	    }

	    @Override
	    public void onPageScrolled(int arg0, float arg1, int arg2) {

		int c = calculateColor(arg0 + arg1, c0, c1);
		strip.setBackgroundColor(c);

	    }

	    @Override
	    public void onPageScrollStateChanged(int arg0) {
	    }
	});

	startServers();

	observer = new FileObserver(BaseMainApp.projectsDir,
	// set up a file observer to watch this directory on sd card
		FileObserver.CREATE | FileObserver.DELETE) {

	    @Override
	    public void onEvent(int event, String file) {
		if ((FileObserver.CREATE & event) != 0) {

		    Log.d(TAG, "File created [" + BaseMainApp.projectsDir + "/" + file + "]");

		    // check if its a "create" and not equal to probe because thats created every time camera is
		    // launched
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
	
	//check if usb is enabled 
	usbEnabled = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
	
	// Create the IP text view
	textIP = (TextView) findViewById(R.id.ip);
	textIP.setOnClickListener(null);// Remove the old listener explicitly
	textIP.setBackgroundResource(0);
	mIpContainer = (LinearLayout) findViewById(R.id.ip_container);
	updateStartStopActionbarItem();

	// start webserver
	httpServer = MyHTTPServer.getInstance(getApplicationContext(), AppSettings.httpPort);

	// websocket
	try {
	    ws = CustomWebsocketServer.getInstance(this, AppSettings.websocketPort, new Draft_17());
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

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

		if (AppSettings.MIN_SUPPORTED_VERSION > Build.VERSION.SDK_INT) {
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
	if (httpServer != null) {
	    httpServer.close();
	    httpServer = null;
	}
	textIP.setText(getResources().getString(R.string.start_the_server));
	textIP.setOnClickListener(null);// Remove the old listener explicitly
	textIP.setBackgroundResource(0);
    }

    /**
     * Explicitly kills connections, with UI impact
     */
    private void hardKillConnections() {
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

		Project p = evt.getProject();

		currentProjectApplicationIntent.putExtra(Project.NAME, p.getName());
		currentProjectApplicationIntent.putExtra(Project.TYPE, p.getType());

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

	int itemId = item.getItemId();
	if (itemId == android.R.id.home) {
	    return true;
	} else if (itemId == R.id.menu_new) {
	    showEditDialog();
	    return true;
	} else if (itemId == R.id.menu_help) {
	    Intent aboutActivityIntent = new Intent(this, AboutActivity.class);
	    startActivity(aboutActivityIntent);
	    overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
	    return true;
	} else if (itemId == R.id.menu_start_stop) {
	    if (httpServer != null) {
		hardKillConnections();
	    } else {
		startServers();
	    }
	    updateStartStopActionbarItem();
	    return true;
	} else if (itemId == R.id.menu_settings) {
	    Intent preferencesIntent = new Intent(this, SetPreferenceActivity.class);
	    startActivity(preferencesIntent);
	    overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
	    return true;
	} else {
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
	    debugIntent(intent, "");
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
