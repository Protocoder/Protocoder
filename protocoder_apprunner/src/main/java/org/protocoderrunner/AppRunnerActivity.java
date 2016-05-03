/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
*
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner;

import android.animation.Animator;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoderrunner.api.PMedia;
import org.protocoderrunner.api.PUI;
import org.protocoderrunner.api.network.PBluetooth;
import org.protocoderrunner.api.sensors.PNfc;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.base.gui.DebugFragment;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.base.utils.StrUtils;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.models.Project;

import java.util.ArrayList;

public class AppRunnerActivity extends BaseActivity {

    private static final String TAG = AppRunnerActivity.class.getSimpleName();

    private Context mContext;
    private AppRunnerFragment mAppRunnerFragment;

    /*
     * Events
     */
    private PNfc.onNFCListener                  onNFCListener;
    private PNfc.onNFCWrittenListener           onNFCWrittenListener;
    private PBluetooth.onBluetoothListener      onBluetoothListener;
    private PMedia.onVoiceRecognitionListener   onVoiceRecognitionListener;

    // ui fragment dependent
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 55;

    /*
     * Keyboard handling
     */
    private PUI.onKeyListener   onKeyListener;
    public boolean              keyVolumeEnabled = true;
    public boolean              keyBackEnabled = true;

    /*
     * UI stuff
     */
    private DebugFragment   mDebugFragment;
    private RelativeLayout consoleRLayout;
    private TextView consoleText;

    // project settings
    private boolean mSettingScreenAlwaysOn;
    private boolean mSettingWakeUpScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Intent intent = getIntent();

        // if intent is empty => finish
        if (intent == null) finish();

        // if is a service with start it and finish this activity
        if (intent.getBooleanExtra("isService", false)) {
            Intent i = new Intent(this, AppRunnerService.class);
            i.putExtras(intent);
            this.startService(i);
            finish();
        }

        // settings
        mSettingScreenAlwaysOn   = intent.getBooleanExtra(Project.SETTINGS_SCREEN_ALWAYS_ON, false);
        mSettingWakeUpScreen     = intent.getBooleanExtra(Project.SETTINGS_SCREEN_WAKEUP, false);

        // the actual code
        String prefix   = intent.getStringExtra(Project.PREFIX);
        String code     = intent.getStringExtra(Project.INTENTCODE);
        String postfix  = intent.getStringExtra(Project.POSTFIX);

        // send bundle to the fragment
        Bundle bundle = new Bundle();
        bundle.putString(Project.NAME, intent.getStringExtra(Project.NAME));
        bundle.putString(Project.FOLDER, intent.getStringExtra(Project.FOLDER));
        bundle.putString(Project.PREFIX, prefix);
        bundle.putString(Project.INTENTCODE, code);
        bundle.putString(Project.POSTFIX, postfix);

        // Set the Activity UI
        setContentView(R.layout.apprunner_activity);
        setupActivity();

        // add AppRunnerFragment
        mAppRunnerFragment = AppRunnerFragment.newInstance(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FrameLayout fl = (FrameLayout) findViewById(R.id.apprunner_fragment);
        ft.add(fl.getId(), mAppRunnerFragment, String.valueOf(fl.getId()));
        ft.commit();

        // Add debug fragment
        if (AppRunnerSettings.DEBUG) addDebugFragment();

        //TODO change to events
        //IDEcommunication.getInstance(this).ready(true);
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        // wake up the device if intent says so
        if (mSettingWakeUpScreen) {
            final Window win = getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        setScreenAlwaysOn(mSettingScreenAlwaysOn);

    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        // NFC
        if (nfcSupported) mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

        // broadcast to start/stop the activity
        startStopActivityBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

        if (nfcSupported) mAdapter.disableForegroundDispatch(this);
        unregisterReceiver(stopActivitiyBroadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // TODO change to events
        //IDEcommunication.getInstance(this).ready(false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void addDebugFragment() {
        mDebugFragment = DebugFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FrameLayout fl = (FrameLayout) findViewById(R.id.debug_fragment);
        fl.setVisibility(View.VISIBLE);
        ft.add(fl.getId(), mDebugFragment, String.valueOf(fl.getId()));
        ft.commit();
    }

    /**
     * NFC stuf
	 */
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private boolean nfcSupported;
    private boolean nfcInit = false;
    public boolean isCodeExecutedShown;

    public void initializeNFC() {

        if (nfcInit == false) {
            PackageManager pm = getPackageManager();
            nfcSupported = pm.hasSystemFeature(PackageManager.FEATURE_NFC);

            if (nfcSupported == false) {
                return;
            }

            // when is in foreground
            MLog.d(TAG, "starting NFC");
            mAdapter = NfcAdapter.getDefaultAdapter(this);

            // PedingIntent will be delivered to this activity
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            // Setup an intent filter for all MIME based dispatches
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            mFilters = new IntentFilter[]{ ndef, };

            // Setup a tech list for all NfcF tags
            mTechLists = new String[][]{new String[]{NfcF.class.getName()}};
            nfcInit = true;
        }
    }


    /**
     * Listen to NFC incomming data
     */
    @Override
    public void onNewIntent(Intent intent) {
        MLog.d(TAG, "New intent " + intent);

        if (intent.getAction() != null) {
            MLog.d(TAG, "Discovered tag with intent: " + intent);

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String nfcID = StrUtils.bytetostring(tag.getId());

            // if there is a message waiting to be written
            if (PNfc.nfcMsg != null) {
                MLog.d(TAG, "->" + PNfc.nfcMsg);
                PNfc.writeTag(this, tag, PNfc.nfcMsg);
                onNFCWrittenListener.onNewTag();
                onNFCWrittenListener = null;
                PNfc.nfcMsg = null;

            // read the nfc tag info
            } else {
                // get NDEF tag details
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    return;
                }

                int size = ndefTag.getMaxSize(); // tag size
                boolean writable = ndefTag.isWritable(); // is tag writable?
                String type = ndefTag.getType(); // tag type

                String nfcMessage = "";

                // get NDEF message details
                NdefMessage ndefMesg = ndefTag.getCachedNdefMessage();
                if (ndefMesg != null) {
                    NdefRecord[] ndefRecords = ndefMesg.getRecords();
                    int len = ndefRecords.length;
                    String[] recTypes = new String[len]; // will contain the
                    // NDEF record types
                    String[] recPayloads = new String[len]; // will contain the
                    // NDEF record types
                    for (int i = 0; i < len; i++) {
                        recTypes[i] = new String(ndefRecords[i].getType());
                        recPayloads[i] = new String(ndefRecords[i].getPayload());
                        MLog.d(TAG, "qq " + i + " " + recTypes[i] + " " + recPayloads[i]);

                    }
                    nfcMessage = recPayloads[0];
                }
                onNFCListener.onNewTag(nfcID, nfcMessage);
            }
        }
    }

    /**
     * key listeners
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyListener != null) onKeyListener.onKeyDown(keyCode);
        if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) return super.onKeyDown(keyCode, event);

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (onKeyListener != null) onKeyListener.onKeyUp(keyCode);
        if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) return super.onKeyUp(keyCode, event);

        return true;
    }

    /**
     * Menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(R.anim.splash_slide_in_anim_reverse_set, R.anim.splash_slide_out_anim_reverse_set);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            for (String _string : matches) {
                MLog.d(TAG, "" + _string);

            }
            onVoiceRecognitionListener.onNewResult(matches.get(0));

            //TODO disabled
        }

        if (onBluetoothListener != null) {
            onBluetoothListener.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addOnKeyListener(PUI.onKeyListener onKeyListener2) { onKeyListener = onKeyListener2; }

    public void addNFCReadListener(PNfc.onNFCListener onNFCListener2) { onNFCListener = onNFCListener2; }

    public void addNFCWrittenListener(PNfc.onNFCWrittenListener onNFCWrittenListener2) { onNFCWrittenListener = onNFCWrittenListener2; }

    public void addBluetoothListener(PBluetooth.onBluetoothListener onBluetoothListener2) { onBluetoothListener = onBluetoothListener2; }

    public void addVoiceRecognitionListener(PMedia.onVoiceRecognitionListener onVoiceRecognitionListener2) { onVoiceRecognitionListener = onVoiceRecognitionListener2; }

    public boolean checkBackKey(int keyCode) {
        if (keyBackEnabled && keyCode == KeyEvent.KEYCODE_BACK) return true;
        else return false;
    }

    public boolean checkVolumeKeys(int keyCode) {
        if (keyVolumeEnabled && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            return true;
        } else {
            return false;
        }
    }

    public void showConsole(boolean visible) {

        if (visible) {
            consoleRLayout.setAlpha(0);
            consoleRLayout.setTranslationY(50);
            consoleRLayout.setVisibility(View.VISIBLE);
            consoleRLayout.animate().alpha(1).translationYBy(-50).setDuration(500);
        } else {
            consoleRLayout.animate().alpha(0).translationYBy(50).setDuration(500).setListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    consoleRLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
        }
    }

    public void showConsole(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MLog.d(TAG, "showing console");
                showConsole(true);
                consoleText.setText(message);
                MLog.d(TAG, "msg text");
            }
        });
    }

    /**
     * Activity dependent events
     */

    // folder choose
    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "");
    }

    @Subscribe
    public void onEventMainThread(Events.LogEvent e) {
        String logMsg = e.getLog();
        MLog.d(TAG, logMsg);

        Intent i = new Intent("org.protocoder.intent.CONSOLE");
        i.putExtra("log", logMsg);
        sendBroadcast(i);
    }

    /**
     * Receiving order to close the activity
     */
    public void startStopActivityBroadcastReceiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoderrunner.intent.CLOSE");
        registerReceiver(stopActivitiyBroadcastReceiver, filterSend);
    }

    BroadcastReceiver stopActivitiyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "stop_all 2");
            finish();
        }
    };

}
