package com.makewithmoto;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.TwoStatePreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makewithmoto.base.BaseNotification;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PrefsFragment extends PreferenceFragment {

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

		Preference button = (Preference) findPreference("button");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override 
			public boolean onPreferenceClick(Preference arg0) {
				startActivity(new Intent(getActivity(), LicenseActivity.class));
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

}
