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

package org.protocoderrunner.apprunner;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.protocoderrunner.AppSettings;
import org.protocoderrunner.R;
import org.protocoderrunner.apprunner.api.PDevice;
import org.protocoderrunner.apprunner.api.PMedia;
import org.protocoderrunner.apprunner.api.PUI;
import org.protocoderrunner.apprunner.api.network.PBluetooth;
import org.protocoderrunner.apprunner.api.sensors.PNfc;
import org.protocoderrunner.apprunner.api.widgets.PPadView;
import org.protocoderrunner.base.BaseActivity;
import org.protocoderrunner.events.Events;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class AppRunnerActivity extends BaseActivity {

    private static final String TAG = "AppRunnerActivity";

    private BroadcastReceiver mIntentReceiver;

    private PDevice.onSmsReceivedListener onSmsReceivedListener;
    private PNfc.onNFCListener onNFCListener;
    private PNfc.onNFCWrittenListener onNFCWrittenListener;
    private PBluetooth.onBluetoothListener onBluetoothListener;
    private PMedia.onVoiceRecognitionListener onVoiceRecognitionListener;

    public AppRunnerFragment mAppRunnerFragment;
    public boolean mActionBarSet;

    //ui fragment dependent
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 55;

    private PUI.onKeyListener onKeyListener;
    public boolean keyVolumeEnabled = true;
    public boolean keyBackEnabled = true;
    private Toolbar mToolbar;
    private ActionBar mActionbar;

    private Project mProject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_apprunner_host);

        if (!AndroidUtils.isWear(this)) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            mActionbar = getSupportActionBar();
            mActionbar.setTitle("hola");
        }

        FrameLayout fl = (FrameLayout) findViewById(R.id.apprunner_fragment);

        // Read in the script given in the intent.
        Intent intent = getIntent();
        if (null != intent) {
            boolean isService = intent.getBooleanExtra("isService", false);

            if (isService) {
                Intent i = new Intent(this, AppRunnerService.class);
                i.putExtras(intent);
                // potentially add data to the intent
                // i.putExtra("KEY1", "Value to be used by the service");
                this.startService(i);
                finish();
            }

            // NOTE:
            // if mProject == null , Protocoder is running in "standard" mode
            // if mProject != null , Protocoder is running in "standalone" mode

            boolean settingScreenAlwaysOn = false;
            boolean settingWakeUpScreen = false;

            if (mProject == null) {

                mProject = new Project();

                // get projects intent
                //settings
                settingScreenAlwaysOn = intent.getBooleanExtra(Project.SETTINGS_SCREEN_ALWAYS_ON, false);
                settingWakeUpScreen = intent.getBooleanExtra(Project.SETTINGS_SCREEN_WAKEUP, false);

                //project info
                mProject.name = intent.getStringExtra(Project.NAME);
                mProject.folder = intent.getStringExtra(Project.FOLDER);
                mProject.prefix = intent.getStringExtra(Project.PREFIX);
                mProject.code = intent.getStringExtra(Project.CODE);
                mProject.postfix = intent.getStringExtra(Project.POSTFIX);
            }

            Bundle bundle = new Bundle();
            bundle.putString(Project.NAME, mProject.name);
            bundle.putString(Project.FOLDER, mProject.folder);
            //bundle.putInt(Project.COLOR, intent.getIntExtra("color", 0));
            bundle.putString(Project.PREFIX, mProject.prefix);
            bundle.putString(Project.CODE, mProject.code);
            bundle.putString(Project.POSTFIX, mProject.postfix);

            //MLog.d(TAG, "load " + projectName + " in " + projectFolder);

            // wake up if intent says so
            if (settingWakeUpScreen) {
                final Window win = getWindow();
                win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }

            //set screen always on if so
            setScreenAlwaysOn(settingScreenAlwaysOn);


            mAppRunnerFragment = AppRunnerFragment.newInstance(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fl.getId(), mAppRunnerFragment, String.valueOf(fl.getId()));

            // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            // ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            // ft.addToBackStack(null);
            ft.commit();

            IDEcommunication.getInstance(this).ready(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        if (nfcSupported) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }

        // sms receive
        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

                // Process the sms format and extract body &amp; phoneNumber
                msg = msg.replace("\n", "");
                String body = msg.substring(msg.lastIndexOf(":") + 1, msg.length());
                String pNumber = msg.substring(0, msg.lastIndexOf(":"));

                if (onSmsReceivedListener != null) {
                    onSmsReceivedListener.onSmsReceived(pNumber, body);
                }
                // Add it to the list or do whatever you wish to
            }
        };


        this.registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

        if (nfcSupported) {
            mAdapter.disableForegroundDispatch(this);
        }
        this.unregisterReceiver(this.mIntentReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IDEcommunication.getInstance(this).ready(false);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void onEventMainThread(Events.ProjectEvent evt) {
        MLog.d(TAG, "event -> " + evt.getAction());

        if (evt.getAction() == "run") {
            finish();
        }
    }


    public void setToolBar(String name, Integer colorBg, Integer colorText) {
        mActionBarSet = true;

        MLog.d("mocmoc", "setting tolbar for " + name + " " + mActionbar);

        if(mActionbar != null) {
            // home clickable if is running inside protocoderapp
            if (AppSettings.STANDALONE == false) {
                //mToolbar.setDisplayHomeAsUpEnabled(true);
                setToolbarBack();
            }

            // set color
            if (colorBg != null) {
                ColorDrawable d = new ColorDrawable();
                d.setColor(colorBg);
                mActionbar.setBackgroundDrawable(d);
            }

            // title
            if (name != null) {
                mActionbar.setTitle(name);
                MLog.d("mocmoc2", "setting tolbar for " + name + " " + mToolbar);

            }
            // set title color
            if (colorText != null) {
                int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
                TextView textTitleView = (TextView) findViewById(titleId);

                // apparently android-l doesnt have that resource
                if (textTitleView != null) {
                    textTitleView.setTextColor(colorText);
                }
            }
        }
    }

    public void setToolbarBack() {

        if (null != mToolbar) {
            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    /*
     * NFC
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

            // cuando esta en foreground
            MLog.d(TAG, "Starting NFC");
            mAdapter = NfcAdapter.getDefaultAdapter(this);
			/*
			 * mAdapter.setNdefPushMessageCallback(new
			 * NfcAdapter.CreateNdefMessageCallback() {
			 *
			 * @Override public NdefMessage createNdefMessage(NfcEvent event) {
			 * // TODO Auto-generated method stub return null; } }, this, null);
			 */

            // Create mContext generic PendingIntent that will be deliver to this
            // activity.
            // The NFC stack will fill in the intent with the details of the
            // discovered tag before
            // delivering to this activity.
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


            // Setup an intent filter for all MIME based dispatches
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            mFilters = new IntentFilter[]{ndef,};

            // Setup mContext tech list for all NfcF tags
            mTechLists = new String[][]{new String[]{NfcF.class.getName()}};
            nfcInit = true;
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        MLog.d(TAG, "New intent " + intent);

        if (intent.getAction() != null) {
            MLog.d(TAG, "Discovered tag with intent: " + intent);
            // mText.setText("Discovered tag " + ++mCount + " with intent: " +
            // intent);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyListener != null) {
            onKeyListener.onKeyDown(keyCode);
        }

        if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
            return super.onKeyDown(keyCode, event);
        }

        return true;
        //return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (onKeyListener != null) {
            onKeyListener.onKeyUp(keyCode);
        }

        if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
            return super.onKeyUp(keyCode, event);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //interp.callJsFunction("onOptionsItemSelected", item);
        switch (item.getItemId()) {
            case android.R.id.home:
                // Up button pressed
                //Intent intentHome = new Intent(this, AppRunnerActivity.class);
                //intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(intentHome);

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

        } else if (requestCode == 22 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("json");
            mAppRunnerFragment.interp.callJsFunction("onResult", result);
        }

        if (onBluetoothListener != null) {
            onBluetoothListener.onActivityResult(requestCode, resultCode, data);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onResult(String result) {

    }


    public void showCodeExecuted() {

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        int action = event.getAction();
        int actionCode = event.getActionMasked();

        ArrayList<PPadView.TouchEvent> t = new ArrayList<PPadView.TouchEvent>();

        // check finger if down or up
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {

        }

        if (event.getSource() == MotionEvent.TOOL_TYPE_MOUSE) {
            // if (event.getButtonState() == ac) {

            // }
        }

        // get positions per finger
        for (int i = 0; i < event.getPointerCount(); i++) {
            // TouchEvent o = new TouchEvent("finger", event.getPointerId(i),
            // action, (int) event.getX(),
            // (int) event.getY());
            // t.add(o);
        }

        //
        // FINGER 1 UP x y
        // FINGER 2 MOVE x y
        // MOUSE 3 MOVE x y

        // return touching;
        return super.onGenericMotionEvent(event);
    }

    public void addOnKeyListener(PUI.onKeyListener onKeyListener2) {
        onKeyListener = onKeyListener2;
    }

    public void addOnSmsReceivedListener(PDevice.onSmsReceivedListener onSmsReceivedListener2) {
        onSmsReceivedListener = onSmsReceivedListener2;
    }

    public void addNFCReadListener(PNfc.onNFCListener onNFCListener2) {
        onNFCListener = onNFCListener2;
    }

    public void addNFCWrittenListener(PNfc.onNFCWrittenListener onNFCWrittenListener2) {
        onNFCWrittenListener = onNFCWrittenListener2;
    }

    public void addBluetoothListener(PBluetooth.onBluetoothListener onBluetoothListener2) {
        onBluetoothListener = onBluetoothListener2;
    }

    public void addVoiceRecognitionListener(PMedia.onVoiceRecognitionListener onVoiceRecognitionListener2) {
        onVoiceRecognitionListener = onVoiceRecognitionListener2;
    }


/*
    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        if (onKeyListener != null) {
            onKeyListener.onKeyDown(keyCode);
        }

        if (checkBackKey(keyCode) || checkVolumeKeys(keyCode)) {
            return super.onKeyDown(keyCode, event);
        }

        return true;

        //return super.onKeyMultiple(keyCode, repeatCount, event);

    }
*/


//    @Override
//    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
//
//        MLog.d(TAG, "action " + ev.getAction());
//        if (onKeyListener != null) {
//            onKeyListener.onKeyDown(ev.getAction());
//        }
//
//        return super.dispatchGenericMotionEvent(ev);
//    }

    public boolean checkBackKey(int keyCode) {
        boolean r;

        if (keyBackEnabled && keyCode == KeyEvent.KEYCODE_BACK) {
            r = true;
        } else {
            r = false;
        }

        return r;
    }

    public boolean checkVolumeKeys(int keyCode) {
        boolean r;

        if (keyVolumeEnabled && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            r = true;
        } else {
            r = false;
        }

        return r;

    }

    public void setProject(Project project) {
        this.mProject = project;
    }

}
