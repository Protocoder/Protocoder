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

package com.makewithmoto;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makewithmoto.base.BaseNotification;
import com.makewithmoto.events.ProjectManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PrefsFragment extends PreferenceFragment {

	protected static final String TAG = "PrefsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		view.setBackgroundResource(R.drawable.gradient);

		final EditTextPreference prefId = (EditTextPreference) findPreference("pref_id");
		prefId.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				prefId.setText((String) newValue);
				return false;
			}
		});

		prefId.setText(getId(getActivity()));

		Preference button = (Preference) findPreference("licenses_detail");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				startActivity(new Intent(getActivity(), LicenseActivity.class));
				return true;
			}
		});

		Preference button2 = (Preference) findPreference("reinstall_examples");
		button2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {

				new AlertDialog.Builder(getActivity())
						.setMessage(
								"Do you really want to reinstall the examples?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Perform Your Task Here--When Yes Is
										// Pressed.
										ProjectManager.getInstance().install(
												getActivity());
										dialog.cancel();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Perform Your Task Here--When No is
										// pressed
										dialog.cancel();
									}
								}).show();

				return true;
			}
		});

		// Show curtain notification
		final TwoStatePreference curtainPreference = (TwoStatePreference) findPreference(getString(R.string.pref_curtain_notifications));
		if (curtainPreference != null) {
			curtainPreference
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(
								Preference preference, Object o) {
							boolean isChecked = (Boolean) o;
							SharedPreferences prefs = getActivity()
									.getSharedPreferences("com.makewithmoto",
											Context.MODE_PRIVATE);
							prefs.edit()
									.putBoolean(
											getActivity()
													.getResources()
													.getString(
															R.string.pref_curtain_notifications),
											isChecked).commit();
							// if start
							if (isChecked) {
								// Do nothing as the server will restart on
								// resume of MainActivity.
								// If we don't have the server automatically
								// restart, then this is a separate issue.
							} else {
								// Kill all notifications
								BaseNotification.killAll(getActivity());
							}
							return true;
						}
					});
		} else {
			// something
		}

		return view;
	}

	public static String getId(Context c) {
		// get apprunner settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		String id = sharedPrefs.getString("pref_id", "-1");

		return id;
	}

	public static void setId(Context c, String id) {
		// get apprunner settings
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		Editor editor = sharedPrefs.edit();
		editor.putString("pref_id", id);
		editor.commit();
	}

}
