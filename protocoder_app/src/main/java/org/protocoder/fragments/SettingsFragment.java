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

package org.protocoder.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.protocoder.R;
import org.protocoder.activities.LicenseActivity;
import org.protocoder.appApi.EditorManager;
import org.protocoder.appApi.Protocoder;
import org.protocoder.appApi.Settings;
import org.protocoderrunner.base.BaseNotification;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.project.ProjectManager.InstallListener;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SettingsFragment extends PreferenceFragment {

	protected static final String TAG = "PrefsFragment";
    private Context mContext;
    private SharedPreferences mPrefs;
    private Settings mSettings;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}

    //twostatepreference(boolean)->action/action edittextpreference(text)->action preference->action
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSettings = Protocoder.getInstance(mContext).settings;

        TwoStatePreference qq = new TwoStatePreference(getActivity()) {
            @Override
            protected void onClick() {
                super.onClick();
            }
        };

		final EditTextPreference prefId = (EditTextPreference) findPreference("pref_id");
		prefId.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				prefId.setText((String) newValue);
				return false;
			}
		});

		prefId.setText(mSettings.getId());

		Preference btnShowLicenses = findPreference("licenses_detail");
		btnShowLicenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                startActivity(new Intent(getActivity(), LicenseActivity.class));
                return true;
            }
        });

		Preference btnReinstall = findPreference("reinstall_examples");
		btnReinstall.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                final ProgressDialog progress = new ProgressDialog(getActivity());
                progress.setTitle("Reinstalling examples");
                progress.setMessage("Your examples are getting restored, wait a sec!");
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(false);

                new AlertDialog.Builder(getActivity()).setMessage("Do you really want to reinstall the examples?")
                        .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progress.show();

                        ProjectManager.getInstance().install(getActivity(), ProjectManager.FOLDER_EXAMPLES,
                                new InstallListener() {

                                    @Override
                                    public void onReady() {
                                        progress.dismiss();
                                    }
                                });
                        dialog.cancel();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
			curtainPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object o) {
					boolean isChecked = (Boolean) o;
					mPrefs.edit()
							.putBoolean(getActivity().getResources().getString(R.string.pref_curtain_notifications),
									isChecked).commit();
					// if start
					if (isChecked) {
						// Do nothing as the server will restart on
						// resume of MainActivity.
						// If we don't have the server automatically
						// restart, then this is mContext separate issue.
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


        // Screen always on mode
        final TwoStatePreference screenOnPreference = (TwoStatePreference) findPreference("pref_screen_on");
        if (screenOnPreference != null) {
            screenOnPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isChecked = (Boolean) o;
                    mPrefs.edit().putBoolean("pref_screen_on", isChecked).commit();

                    return true;
                }
            });
        }

        // Column mode
        final TwoStatePreference columnModePreference = (TwoStatePreference) findPreference("pref_list_mode");
        if (columnModePreference != null) {
            columnModePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isChecked = (Boolean) o;
                    mPrefs.edit().putBoolean("pref_list_mode", isChecked).commit();

                    return true;
                }
            });
        }

        // Background mode
        final TwoStatePreference backgroundModePreference = (TwoStatePreference) findPreference("pref_background_mode");
        if (backgroundModePreference != null) {
            backgroundModePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isChecked = (Boolean) o;
                    mPrefs.edit().putBoolean("pref_background_mode", isChecked).commit();
                    return true;
                }
            });
        }

        // Connection alert mode
        final TwoStatePreference connectionAlertPreference = (TwoStatePreference) findPreference("pref_connection_alert");
        if (connectionAlertPreference != null) {
            connectionAlertPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isChecked = (Boolean) o;
                    mSettings.setConnectionAlert(isChecked);
                    return true;
                }
            });
        }

        //load webIDE
        final ListPreference loadEditorPreference = (ListPreference) findPreference("pref_change_editor");
        String[] editors = EditorManager.getInstance().listEditors();
        loadEditorPreference.setEntries(editors);
        loadEditorPreference.setEntryValues(editors);
        loadEditorPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MLog.d(TAG, "" + newValue);
                mPrefs.edit().putString("pref_change_editor", (String) newValue).commit();
                return true;
            }
        });


        // Notify and download
        final TwoStatePreference notifyNewVersionPreference = (TwoStatePreference) findPreference("pref_notify_new_version");
        if (notifyNewVersionPreference != null) {
            notifyNewVersionPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    boolean isChecked = (Boolean) o;
                    mPrefs.edit().putBoolean("pref_notify_new_version", isChecked).commit();
                    return true;
                }
            });
        }

        Preference btnFtp = findPreference("pref_ftp");
        btnFtp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("FTP settings");
                final View view = getActivity().getLayoutInflater().inflate(R.layout.view_ftp_settings_dialog, null);
                //alertDialog.setView(R.layout.view_ftp_settings_dialog);


                final EditText userName = (EditText) view.findViewById(R.id.ftp_username);
                final EditText userPassword = (EditText) view.findViewById(R.id.ftp_userpassword);
                final CheckBox check = (CheckBox) view.findViewById(R.id.ftp_enable);

                final boolean[] checked = {mSettings.getFtpChecked()};
                final String[] userNameText = {mSettings.getFtpUserName()};
                final String[] userPasswordText = {mSettings.getFtpUserPassword()};

                userName.setText(userNameText[0]);
                userPassword.setText(userPasswordText[0]);

                check.setChecked(checked[0]);

                alertDialog.setView(view);

                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("Save",  new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        checked[0] = check.isChecked();
                        userNameText[0] = userName.getText().toString();
                        userPasswordText[0] = userPassword.getText().toString();

                        //sha-1 the userPassword to store it
                       // String saltedPassword = null;
                       // try {
                         //   saltedPassword = AndroidUtils.sha1(userPasswordText[0]);
                        //    MLog.d(TAG, " qq " + saltedPassword);

                            mSettings.setFtp(checked[0], userNameText[0], userPasswordText[0]);
                       // } catch (NoSuchAlgorithmException e) {
                       //     e.printStackTrace();
                       // } catch (UnsupportedEncodingException e) {
                       //     e.printStackTrace();
                       // }

                    }
                });

                alertDialog.show();

                return true;
            }
        });

        return view;

	}

}
