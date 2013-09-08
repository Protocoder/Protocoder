package com.makewithmoto;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.base.BaseMainApp;
import com.makewithmoto.utils.FileIO;

@SuppressLint("NewApi")
public class WelcomeActivity extends BaseActivity {

    private static final String TAG = "WelcomeActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        //Create the action bar programmatically
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.welcome_activity_name);
        
    	new Runnable() {

			@Override
			public void run() {
				File dir = new File(BaseMainApp.baseDir + "/" + "examples");
				FileIO.deleteDir(dir);
				FileIO.copyFileOrDir(getApplicationContext(), "examples");
			}
		}.run();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        super.onPause();
        //do something here
    }

    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {

        case android.R.id.home:
            // Up button pressed
            Intent intentHome = new Intent(this, MainActivity.class);
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHome);
            overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);

    }
    
    public void onAcceptClick(View v){
        //Write a shared pref to never come back here
        SharedPreferences userDetails = getSharedPreferences("com.makewithmoto", MODE_PRIVATE);
        userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), false).commit();
        //Start the activity
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
