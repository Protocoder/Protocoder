package com.makewithmoto;

import ioio.lib.spi.Log;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.makewithmoto.animation.AnimUtils;
import com.makewithmoto.app.utils.NetworkUtils;
import com.makewithmoto.base.AppSettings;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.base.BaseNotification;
import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.fragments.HelpFragment;
import com.makewithmoto.fragments.NewProjectDialog;
import com.makewithmoto.network.IWebSocketService;
import com.makewithmoto.network.MyHTTPServer;
import com.makewithmoto.projectlist.ProjectsListFragment;
import com.makewithmoto.utils.ALog;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements
		NewProjectDialog.NewProjectDialogListener {

	Context c;
	private int projectRequestCode = 1;
	private static final int MENU_NEW_PROJECT = 0;
	private static final int MENU_TOGGLE_HELP = 1;
	private static final String TAG = "FragmentHolder";
	public boolean ledON = true;
	protected float servoVal; 
	Handler handler; 

	MyHTTPServer httpServer;

	public HelpFragment helpFragment;
	private ProjectsListFragment projectListFragment;
	private Boolean showingHelp = false;

	private TextView textIP;
	protected int textIPHeight;

	private Intent currentProjectApplicationIntent;

	private IWebSocketService wsServiceInterface;
	private Boolean isConnectedToWebsockets = false;
	private Intent wsIntent;
	private BaseNotification baseNotification;
	
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isConnectedToWebsockets = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			isConnectedToWebsockets = true;
			Log.d(TAG, "connected to websockets");
			wsServiceInterface = IWebSocketService.Stub
					.asInterface((IBinder) service);
			try {
				Log.d(TAG, "Starting...................");
				wsServiceInterface.start();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				wsServiceInterface.sendToSockets("Connected");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forfragments);
		c = this;

		ActionBar actionBar = getActionBar();
		// actionBar.hide();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setTitle("");
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.mwmgreen)));

		baseNotification = new BaseNotification(this);
		baseNotification.show(MainActivity.class, R.drawable.logo, "http:/"
				+ NetworkUtils.getLocalIpAddress().toString() + ":"
				+ AppSettings.httpPort, "MWM Server Running");

		textIP = (TextView) findViewById(R.id.ip);

		projectListFragment = new ProjectsListFragment();
		addFragment(projectListFragment, R.id.f1, false);

		helpFragment = new HelpFragment();
		addFragment(helpFragment, R.id.helpFragment, false);

		httpServer = MyHTTPServer.getInstance(AppSettings.httpPort, getApplicationContext());
		textIP.setText("Open your browser \nhttp:/"
				+ NetworkUtils.getLocalIpAddress().toString() + ":"
				+ AppSettings.httpPort);

		textIP.setVisibility(View.VISIBLE);
		
		// STARTING REMOTE SERVICE CONNECTION
		wsIntent = new Intent(IWebSocketService.class.getName());
		this.bindService(wsIntent, conn, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "WebSocketService bound");

		ViewTreeObserver vto = textIP.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				ViewTreeObserver obs = textIP.getViewTreeObserver();

				textIPHeight = textIP.getHeight();
				textIP.setTranslationY(textIPHeight);

				textIP.setAlpha(0);
				ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(textIP,
						View.ALPHA, 1); //
				alphaAnimator.setDuration(2000); //

				final ObjectAnimator shiftAnimator = ObjectAnimator.ofFloat(
						textIP, View.TRANSLATION_Y, 0); // shiftAnimator.setRepeatCount(1);
														// //

				shiftAnimator.setRepeatMode(ValueAnimator.REVERSE);
				shiftAnimator.setDuration(2000);
				shiftAnimator.setInterpolator(new DecelerateInterpolator());

				final AnimatorSet setAnimation = new AnimatorSet();

				setAnimation.play(alphaAnimator).with(shiftAnimator);
				setAnimation.start();

				obs.removeOnGlobalLayoutListener(this);
			}

		});

	}

	
	private void stopServices(){
	
		/*
		try {
			Log.d(TAG, "Stoping...................");
			wsServiceInterface.stop();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		/*
		if (isConnectedToWebsockets) {
			unbindService(conn);
		}
		*/
		
		ViewGroup vg = (ViewGroup) findViewById(R.layout.activity_forfragments);
		if (vg != null) {
			vg.invalidate();
			vg.removeAllViews();
		}
		
		if(httpServer != null){
		    httpServer.stop();
		    httpServer = null;
		}
		
		finish();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Intent receivedIntent = getIntent();
		String receivedAction = receivedIntent.getAction();
		
		if(receivedAction == "STOP"){
			Log.d(TAG, "Stoping Services!!!");
			baseNotification.hide();
			stopServices();
		}
		else{

			Log.d(TAG, "Registering as an EventBus listener in MainActivity");
			EventBus.getDefault().register(this);
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// TODO enable this at some point
		
		if (isConnectedToWebsockets) {
			unbindService(conn);
		}
		ViewGroup vg = (ViewGroup) findViewById(R.layout.activity_forfragments);
		if (vg != null) {
			vg.invalidate();
			vg.removeAllViews();
		}
		
		if(httpServer != null){
		    httpServer.stop();
		    httpServer = null;
		}
		
		System.exit(0);
	}

	// TODO call intent and kill it in an appropiate way
	// TODO kill the previous app if one is running
	public void onEventMainThread(ProjectEvent evt) {
		// Using transaction so the view blocks

		if (evt.getAction() == "run") {
			if (currentProjectApplicationIntent != null) { 
				finishActivity(projectRequestCode);
				currentProjectApplicationIntent = null;
			}
			Log.d("ProjectEvent/MainActivity", evt.getProject().getName());
			projectListFragment.projectLaunch(evt.getProject().getName());

			currentProjectApplicationIntent = new Intent(
					"com.makewithmoto.apprunner.MWMActivity");

			currentProjectApplicationIntent.putExtra("project_name", evt
					.getProject().getName());

			// check if the apprunner is installed
			// TODO add handling
			final PackageManager mgr = this.getPackageManager();
			List<ResolveInfo> list = mgr.queryIntentActivities(
					currentProjectApplicationIntent,
					PackageManager.MATCH_DEFAULT_ONLY);

			Log.d(TAG, "intent available " + list.size());

			startActivityForResult(currentProjectApplicationIntent, projectRequestCode);
		} else if (evt.getAction() == "save") {
			Log.d(TAG, "saving project " + evt.getProject().getName());
			projectListFragment.projectRefresh(evt.getProject().getName());

		}
	}

	// MUST IMPLEMENT THIS
	public void onEventAsync(LogEvent evt) {
		Log.d(TAG, "LogEvent ---> onEventAsync " + evt.getMessage());
		String msg = evt.getMessage();
		JSONObject res = new JSONObject();
		try {
			res.put("type", "log_event");
			res.put("tag", evt.getTag());
			res.put("msg", msg);
			try {
				if (isConnectedToWebsockets) {
					wsServiceInterface.logToSockets(evt.getTag(), msg);
				} else {
					Log.e(TAG,
							"Not connected to webSockets. Cannot send message: "
									+ msg);
				}
			} catch (RemoteException e) {
				Log.d(TAG, "ERROR SENDING TO SOCKETS");
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Show source editor view
	private void showHelpView() {
		showingHelp = true;
		//textIP.setVisibility(View.INVISIBLE);
		AnimUtils.showHelp(this);
	}

	private void hideHelpView() {
		AnimUtils.hideHelp(this);
		showingHelp = false;
		//textIP.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.project_list, menu);
		menu.add(0, MENU_NEW_PROJECT, 0, "New").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, MENU_TOGGLE_HELP, 0, "Help").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	//	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	//			.getMenuInfo();

		switch (item.getItemId()) {

		case android.R.id.home:
			hideHelpView();

			return true;
		case MENU_NEW_PROJECT:
			showEditDialog();

			return true;
		case MENU_TOGGLE_HELP:
			if (showingHelp) {
				hideHelpView();
			} else {
				showHelpView();
			}
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
		Toast.makeText(this, "Creating " + inputText, Toast.LENGTH_SHORT)
				.show();

//		Project newProject = ProjectManager.getInstance().addNewProject(c,
//				inputText, inputText);
	}

	/*
	 * Key management
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent evt) {
		super.onKeyDown(keyCode, evt);

		ALog.d("BUTTON PRESSED ON MAINACTIVITY", "Key: " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ALog.d("BACK BUTTON", "Back button was pressed");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			
		}
		return false;
	}

	public ProjectsListFragment getProjectListFragment() {
		return projectListFragment;
	}
}
