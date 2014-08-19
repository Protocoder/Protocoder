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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.BaseMainApp;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.project.ProjectManager.InstallListener;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

@SuppressLint("NewApi")
public class WelcomeActivity extends BaseActivity {

	private static final String TAG = "WelcomeActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		// Create the action bar programmatically
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.welcome_activity_name);

		// Set copyright
		TextView copyright = (TextView) findViewById(R.id.copyright);
		copyright.setText(readFile(R.raw.copyright_notice));

		// first time id
		PrefsFragment.setId(this, StrUtils.generateRandomString());
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

	/**
	 * onDestroy
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * switch (item.getItemId()) {
		 * 
		 * case android.R.id.home: // Up button pressed Intent intentHome = new
		 * Intent(this, MainActivity.class);
		 * intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intentHome);
		 * overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set,
		 * R.anim.splash_slide_out_anim_reverse_set); finish(); return true;
		 * default: return super.onOptionsItemSelected(item); }
		 */
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
		ProjectManager.getInstance().install(this, BaseMainApp.TYPE_EXAMPLE_STRING, new InstallListener() {

			@Override
			public void onReady() {
				progress.dismiss();
				// Write a shared pref to never come back here
				SharedPreferences userDetails = getSharedPreferences("org.protocoder", MODE_PRIVATE);
				userDetails.edit().putBoolean(getResources().getString(R.string.pref_is_first_launch), false).commit();
				// Start the activity
				Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		});

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
