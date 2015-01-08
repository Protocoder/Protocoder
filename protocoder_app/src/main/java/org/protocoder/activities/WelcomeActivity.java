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

package org.protocoder.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.protocoder.MainActivity;
import org.protocoder.R;
import org.protocoder.fragments.SettingsFragment;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.project.ProjectManager.InstallListener;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressLint("NewApi")
public class WelcomeActivity extends BaseActivity {

	private static final String TAG = "WelcomeActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// Create the action bar programmatically
		if (!AndroidUtils.isWear(this)) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.welcome_activity_name);
        }

		// Set copyright
		TextView copyright = (TextView) findViewById(R.id.copyright);
		copyright.setText(readFile(R.raw.copyright_notice));

		// first time id
		SettingsFragment.setId(this, StrUtils.generateRandomString());
	}

	/**
	 * onResume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MLog.d(TAG, "onResume");
	}

	/**
	 * onPause
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// do something here
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void onAcceptClick(View v) {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setTitle("Installing examples");
		progress.setMessage("You can start creating with Protocoder in just a second");
		progress.show();
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);

		// install examples
		ProjectManager.getInstance().install(this, ProjectManager.getInstance().FOLDER_EXAMPLES, new InstallListener() {

			@Override
			public void onReady() {
				progress.dismiss();
				// Write mContext shared pref to never come back here
				SharedPreferences userDetails = getSharedPreferences("org.protocoder", MODE_PRIVATE);
				userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), false).commit();
				// Start the activity
				Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});

	}

    //TODO remove and use fileIO methods
	/**
	 * Returns mContext string from mContext txt file resource
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
