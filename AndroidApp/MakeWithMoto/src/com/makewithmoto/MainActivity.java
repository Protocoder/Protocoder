package com.makewithmoto;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makewithmoto.animation.AnimUtils;
import com.makewithmoto.app.utils.NetworkUtils;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.base.AppSettings;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.base.BaseNotification;
import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.fragments.HelpFragment;
import com.makewithmoto.fragments.NewProjectDialog;
import com.makewithmoto.network.IWebSocketService;
import com.makewithmoto.network.MyHTTPServer;
import com.makewithmoto.projectlist.ProjectManager;
import com.makewithmoto.projectlist.ProjectsListFragment;
import com.makewithmoto.utils.ALog;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements NewProjectDialog.NewProjectDialogListener {

    private static final String TAG = "FragmentHolder";
    
    Context c;
    private int mProjectRequestCode = 1;
    public boolean ledON = true;
    protected float servoVal;
    Handler handler;

    MyHTTPServer httpServer; 
    
    BaseNotification baseNotification;

    public HelpFragment helpFragment;
    private ProjectsListFragment projectListFragment;
    private Boolean showingHelp = false;

    private TextView textIP;
    private LinearLayout mIpContainer;
    protected int textIPHeight;

    private Intent currentProjectApplicationIntent;

    private IWebSocketService wsServiceInterface;
    private Boolean isConnectedToWebsockets = false;
    private Intent wsIntent;
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnectedToWebsockets = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isConnectedToWebsockets = true;
            Log.d(TAG, "connected to websockets");
            wsServiceInterface = IWebSocketService.Stub.asInterface((IBinder) service);
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
        
        //Set the content view and get the context
        //FIXME: Store Context field as mContext
        setContentView(R.layout.activity_forfragments);
        c = this;

        //Create the action bar programmatically
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        //Show the notification
        baseNotification = new BaseNotification(this);
        baseNotification.show(MainActivity.class, R.drawable.ic_stat_logo, "http:/" + NetworkUtils.getLocalIpAddress().toString() + ":" + AppSettings.httpPort, "MWM Server Running", R.drawable.ic_navigation_cancel);

        //Create the IP text view
        textIP = (TextView) findViewById(R.id.ip);
        mIpContainer = (LinearLayout) findViewById(R.id.ip_container);

        //Instantiate fragments
        projectListFragment = new ProjectsListFragment();
        addFragment(projectListFragment, R.id.f1, false);
        helpFragment = new HelpFragment();
        addFragment(helpFragment, R.id.helpFragment, false);

        httpServer = MyHTTPServer.getInstance(AppSettings.httpPort, getApplicationContext());
        textIP.setText("Hack via your browser @ http:/" + NetworkUtils.getLocalIpAddress().toString() + ":" + AppSettings.httpPort);
        textIP.setVisibility(View.VISIBLE);

        // Start the remote service connection
        wsIntent = new Intent(IWebSocketService.class.getName());
        this.bindService(wsIntent, conn, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "WebSocketService bound");

        ViewTreeObserver vto = mIpContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = mIpContainer.getViewTreeObserver();

                textIPHeight = mIpContainer.getHeight();
                mIpContainer.setTranslationY(textIPHeight);

                //FIXME: This animation should be done with an xml file
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

                obs.removeOnGlobalLayoutListener(this);
            }

        });

    }

    /**
     * onResume
     */
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

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    /**
     * onDestroy
     */
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
        
        baseNotification.hide();
        System.exit(0);
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

    // TODO call intent and kill it in an appropiate way
    // TODO kill the previous app if one is running
    public void onEventMainThread(ProjectEvent evt) {
        // Using transaction so the view blocks

        if (evt.getAction() == "run") {
            if (currentProjectApplicationIntent != null) {
                finishActivity(mProjectRequestCode);
                currentProjectApplicationIntent = null;
            }
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + 
    				AppSettings.appFolder + File.separator;
            
            Log.d("ProjectEvent/MainActivity", baseDir+ evt.getProject().getName() + File.separator + "script.js");
            projectListFragment.projectLaunch(evt.getProject().getName());



            try{
        
            currentProjectApplicationIntent = new Intent(MainActivity.this, AppRunnerActivity.class); 
            String script = evt.getProject().getCode();
            Log.d("MainActivity", script);
            currentProjectApplicationIntent.putExtra("Script", script);

            // check if the apprunner is installed
            // TODO add handling
            final PackageManager mgr = this.getPackageManager();
            List<ResolveInfo> list = mgr.queryIntentActivities(currentProjectApplicationIntent, PackageManager.MATCH_DEFAULT_ONLY);

            Log.d(TAG, "intent available " + list.size());

            startActivityForResult(currentProjectApplicationIntent, mProjectRequestCode);
            }catch(Exception e){
            	Log.d(TAG, "Error launching script");
            }
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
                    Log.e(TAG, "Not connected to webSockets. Cannot send message: " + msg);
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
    // Recommend deprecation...
    private void showHelpView() {
        showingHelp = true;
        //textIP.setVisibility(View.INVISIBLE);
        AnimUtils.showHelp(this);
    }

    // Recommend deprecation...
    private void hideHelpView() {
        AnimUtils.hideHelp(this);
        showingHelp = false;
        //textIP.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        //menu.add(0, MENU_NEW_PROJECT, 0, "New").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //menu.add(0, MENU_TOGGLE_HELP, 0, "Help").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
        Project newProject = ProjectManager.getInstance().addNewProject(c, inputText, inputText);
    }

    /*
     * Key management
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent evt) {
        ALog.d("BACK BUTTON", "Back button was pressed");
        if (keyCode == 4){
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

    public ProjectsListFragment getProjectListFragment() {
        return projectListFragment;
    }
}
