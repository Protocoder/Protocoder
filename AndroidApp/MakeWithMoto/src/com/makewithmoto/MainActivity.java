package com.makewithmoto;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.base.AppSettings;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.base.BaseNotification;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.events.ProjectManager;
import com.makewithmoto.fragments.NewProjectDialog;
import com.makewithmoto.network.ALog;
import com.makewithmoto.network.CustomWebsocketServer;
import com.makewithmoto.network.MyHTTPServer;
import com.makewithmoto.network.NetworkUtils;
import com.makewithmoto.projectlist.ListFragmentExamples;
import com.makewithmoto.projectlist.ListFragmentUserProjects;
import com.makewithmoto.sensors.AccelerometerManager;
import com.makewithmoto.sensors.AccelerometerManager.AccelerometerListener;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements
		NewProjectDialog.NewProjectDialogListener {

	private static final String TAG = "FragmentHolder";

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
		//addFragment(projectListFragment, R.id.f1, false);

		
		mProjectPagerAdapter = new ProjectsPagerAdapter(getSupportFragmentManager());
		mProjectPagerAdapter.setExamplesFragment(exampleListFragment);
		mProjectPagerAdapter.setProjectsFragment(userProjectListFragment);
       
		// Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mProjectPagerAdapter);
        
        

		// Start the servers
		startServers();

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
				ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(
						mIpContainer, View.ALPHA, 1); //
				alphaAnimator.setDuration(1200); //

				final ObjectAnimator shiftAnimator = ObjectAnimator.ofFloat(
						mIpContainer, View.TRANSLATION_Y, 0); // shiftAnimator.setRepeatCount(1);
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
		IntentFilter filterSend = new IntentFilter();
		filterSend.addAction("com.makewithmoto.intent.action.STOP_SERVER");
		registerReceiver(mStopServerReceiver, filterSend);
	}

	/**
	 * onPause
	 */
	@Override
	protected void onPause() {
		super.onPause();

		EventBus.getDefault().unregister(this);
		unregisterReceiver(mStopServerReceiver);
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
	private void startServers() {

		// Show the notification
		SharedPreferences prefs = getSharedPreferences("com.makewithmoto",
				MODE_PRIVATE);
		boolean showNotification = prefs.getBoolean(
				getResources().getString(R.string.pref_curtain_notifications),
				true);
		if (showNotification) {
			mNotification = new BaseNotification(this);
			mNotification.show(MainActivity.class, R.drawable.ic_stat_logo,
					"http:/" + NetworkUtils.getLocalIpAddress().toString()
							+ ":" + AppSettings.httpPort, "MWM Server Running",
					R.drawable.ic_navigation_cancel);
		}

		// Create the IP text view
		textIP = (TextView) findViewById(R.id.ip);
		textIP.setOnClickListener(null);// Remove the old listener explicitly
		textIP.setBackgroundResource(0);
		mIpContainer = (LinearLayout) findViewById(R.id.ip_container);
		updateStartStopActionbarItem();

		// start webserver
		httpServer = MyHTTPServer.getInstance(AppSettings.httpPort,
				getApplicationContext());

		// websocket
		try {
			ws = CustomWebsocketServer.getInstance(this, 8081, new Draft_17());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		AccelerometerManager accelerometerManager = new AccelerometerManager(
				this);
		accelerometerManager.addListener(new AccelerometerListener() {

			@Override
			public void onShake(float force) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAccelerometerChanged(float x, float y, float z) {

				// Log.d(TAG, " " + x);
				JSONObject obj = new JSONObject();
				try {
					obj.put("acc_x", x);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//ws.send(obj);

			}
		});
		accelerometerManager.start();
		
		
		

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
		handler.postDelayed(r, 0);

		textIP.setText("Hack via your browser @ http:/"
				+ NetworkUtils.getLocalIpAddress().toString() + ":"
				+ AppSettings.httpPort);
		if (httpServer != null) {// If no instance of HTTPServer, we set the IP
									// address view to gone.
			textIP.setVisibility(View.VISIBLE);
		} else {
			textIP.setVisibility(View.GONE);
		}

	}



    /**
     * Unbinds service and stops the http server
     */
    private void killConnections() {
        // TODO enable this at some point
        //TODO add websocket

        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
        //Hide the notification
        SharedPreferences prefs = getSharedPreferences("com.makewithmoto", MODE_PRIVATE);
        boolean showNotification = prefs.getBoolean(getResources().getString(R.string.pref_curtain_notifications), true);
        if (showNotification) {
            if (mNotification != null)
                mNotification.hide();
        }
        textIP.setText(getResources().getString(R.string.start_the_server));
        textIP.setOnClickListener(null);//Remove the old listener explicitly
        textIP.setBackgroundResource(0);
    }

    /**
     * Explicitly kills connections, with UI impact
     */
    private void hardKillConnections() {
        // TODO enable this at some point
        //TODO add here websocket 

        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
        textIP.setText(getResources().getString(R.string.start_the_server));
        updateStartStopActionbarItem();
        textIP.setOnClickListener(null);//Remove the old listener explicitly
        textIP.setBackgroundResource(R.drawable.transparent_blue_button);
        textIP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startServers();
                updateStartStopActionbarItem();

            }
        });

        //Hide the notification
        SharedPreferences prefs = getSharedPreferences("com.makewithmoto", MODE_PRIVATE);
        boolean showNotification = prefs.getBoolean(getResources().getString(R.string.pref_curtain_notifications), true);
        if (showNotification) {
            if (mNotification != null)
                mNotification.hide();
        }
    }

    private void updateStartStopActionbarItem() {
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
    // TODO kill the previous app if one is running
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
                // TODO add handling
                final PackageManager mgr = this.getPackageManager();
                List<ResolveInfo> list = mgr.queryIntentActivities(currentProjectApplicationIntent, PackageManager.MATCH_DEFAULT_ONLY);

                Log.d(TAG, "intent available " + list.size());

                startActivityForResult(currentProjectApplicationIntent, mProjectRequestCode);
            } catch (Exception e) {
                Log.d(TAG, "Error launching script");
            }

        } else if (evt.getAction() == "save") {
            Log.d(TAG, "saving project " + evt.getProject().getName());
            userProjectListFragment.projectRefresh(evt.getProject().getName());

        } else if (evt.getAction() == "new") {
            //projectListFragment.addProject(evt.getProject().getName(), evt.getProject().getUrl()); 
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        //menu.add(0, MENU_NEW_PROJECT, 0, "New").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(0, MENU_TOGGLE_HELP, 0, "Help").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

        case android.R.id.home:
            //We've changed this from a fragment
            //hideHelpView();

            return true;
        case R.id.menu_new:
            showEditDialog();

            return true;
        case R.id.menu_help:
            Intent aboutActivityIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutActivityIntent);
            overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
            //Using a fragment here makes it really difficult to handle Activity lifecycle in relation to TSB...
            /*if (showingHelp) {
                hideHelpView();
            } else {
                showHelpView();
            }*/
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
        Project newProject = ProjectManager.getInstance().addNewProject(c, inputText, inputText, ProjectManager.PROJECT_USER_MADE);
    
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

}
