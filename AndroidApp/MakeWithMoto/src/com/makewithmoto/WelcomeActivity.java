package com.makewithmoto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.makewithmoto.R;
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

        //Set copyright
        TextView copyright = (TextView)findViewById(R.id.copyright);
        copyright.setText(readFile(R.raw.copyright_notice));

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
    
	/**
	 * Returns a string from a txt file resource
	 * 
	 * @return
	 */
	private String readFile(int resource) {
		InputStream inputStream = getResources().openRawResource(resource);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return byteArrayOutputStream.toString();
	}

}
