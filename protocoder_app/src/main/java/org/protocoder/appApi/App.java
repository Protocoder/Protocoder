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
package org.protocoder.appApi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.java_websocket.drafts.Draft_17;
import org.protocoder.R;
import org.protocoder.activities.AboutActivity;
import org.protocoder.activities.SetPreferenceActivity;
import org.protocoder.network.ProtocoderFtpServer;
import org.protocoder.network.ProtocoderHttpServer;
import org.protocoder.views.Overlay;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.network.NetworkUtils;
import org.protocoderrunner.utils.AndroidUtils;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;

public class App {

    final String TAG = "App";
    private final Protocoder protocoder;

    //Servers
    private ProtocoderHttpServer httpServer;
    private CustomWebsocketServer ws;
    private ProtocoderFtpServer mFtpServer;

    //Views
    private RelativeLayout mainAppView;
    private TextView textIP;
    private LinearLayout mIpContainer;
    protected int textIPHeight;
    public Overlay overlay;

    public Editor editor;

    int usbEnabled = 0;

    App(Protocoder protocoder) {
        editor = new Editor(protocoder);
        this.protocoder = protocoder;

        init();
    }


    public void init() {

        mainAppView = (RelativeLayout) protocoder.mActivityContext.findViewById(R.id.contentHolder);
        mainAppView.setBackgroundColor(Color.parseColor(protocoder.settings.getColor()));
        // Create the IP text view
        textIP = (TextView) protocoder.mActivityContext.findViewById(R.id.ip);
        textIP.setOnClickListener(null);// Remove the old listener explicitly
        textIP.setBackgroundResource(0);
        mIpContainer = (LinearLayout) protocoder.mActivityContext.findViewById(R.id.ip_container);

        // Add animations
        ViewTreeObserver vto = mIpContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = mIpContainer.getViewTreeObserver();

                textIPHeight = mIpContainer.getHeight();
                mIpContainer.setTranslationY(textIPHeight);

                // FIXME: This animation should be done with an xml file
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

                if (AndroidUtils.isVersionMinSupported()) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });


//        overlay = new Overlay(protocoder.mContext);
//        overlay.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT));
//        mainAppView.addView(overlay);
//
//        try {
//            overlay.setFrame();
//        } catch (Exception e) {
//            e.printStackTrace();
//            MLog.d("qq", e.getStackTrace().toString());
//        }
    }


    public void showHelp(boolean show) {

        if (show) {
            Intent aboutActivityIntent = new Intent(protocoder.mActivityContext, AboutActivity.class);
            protocoder.mActivityContext.startActivity(aboutActivityIntent);
            //protocoder.mActivityContext.overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);

            //HelpFragment helpFragment = new HelpFragment();
            //Bundle bundle = new Bundle();
            //bundle.putString(Project.NAME, project.getName());
            //bundle.putString(Project.URL, project.getStoragePath());
            //bundle.putString(Project.FOLDER, project.getFolder());

            //helpFragment.setArguments(bundle);
            //MainActivity ma = (MainActivity) (protocoder.mContext);
            //ma.addFragment(helpFragment, R.id.fragmentEditor, "helpFragment", true);
        } else {

        }
    }

    public void showSettings(boolean b) {
        Intent preferencesIntent = new Intent(protocoder.mActivityContext, SetPreferenceActivity.class);
        protocoder.mActivityContext.startActivity(preferencesIntent);
        //protocoder.mActivityContext.overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
    }

    public void showNumberConections() {

    }

    public void showNetworkBottomInfo(boolean show) {
        if (show) {
            textIP.setVisibility(View.VISIBLE);
        } else {
            textIP.setVisibility(View.GONE);
        }
    }

    public void showNetworkDetails(boolean show) {
        if (show) {

        } else {

        }
    }

    //when there is mContext some data transfer
    public void showNetworkProgress(boolean show) {
        if (show) {

        } else {

        }
    }

    public void showLibrariesRepo(boolean show) {
        if (show) {

        } else {

        }
    }

    public void showLibaries(boolean show) {
        if (show) {

        } else {

        }
    }

    public void highlight(String color) {
        overlay.setFrame();
    }

    public void vibrate(int time) {
        //protocoder.pDevice.vibrate(time);
    }

    public void shake() {
        View v = (View) mainAppView.getParent().getParent();
        v.animate().rotation(10).translationX(100).setDuration(1000).setInterpolator(new CycleInterpolator(1)).start();
    }

    //"noise", "blipy", "hipster", "color"
    public void mode(String mode) {

    }

    public void close() {
        protocoder.mActivityContext.superMegaForceKill();
    }

    public void restart() {

    }

    public void setIp(String s) {
        textIP.setText(s);
    }


    /**
     * Starts the remote service connection
     */
    public boolean startServers() {

        // check if usb is enabled
        usbEnabled = Settings.Secure.getInt(protocoder.mActivityContext.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);

        // start webserver
        httpServer = ProtocoderHttpServer.getInstance(protocoder.mActivityContext.getApplicationContext(), AppSettings.HTTP_PORT);

        // websocket
        try {
            ws = CustomWebsocketServer.getInstance(protocoder.mActivityContext, AppSettings.WEBSOCKET_PORT, new Draft_17());
            IDEcommunication.getInstance(protocoder.mActivityContext).ready(false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // check if there is mContext WIFI connection or we can connect via USB
        if (NetworkUtils.getLocalIpAddress(protocoder.mActivityContext).equals("-1")) {
            setIp("No WIFI, still you can hack via USB using the adb command");
        } else {
            setIp("Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(protocoder.mActivityContext) + ":"
                    + AppSettings.HTTP_PORT);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //MLog.d(TAG, "qq" + NetworkUtils.getLocalIpAddress(protocoder.mContext));

        if (httpServer != null) {// If no instance of HTTPServer, we set the IP
            // address view to gone.
            showNetworkBottomInfo(true);
        } else {
            showNetworkBottomInfo(false);
        }

        if (protocoder.settings.getFtpChecked()) {
            mFtpServer = ProtocoderFtpServer.getInstance(protocoder.mActivityContext, AppSettings.FTP_PORT);
            if (!mFtpServer.isStarted()) {
                mFtpServer.startServer();
            }
        }

        return true;

    }

    /**
     * Unbinds service and stops the servers
     */
    // TODO add stop websocket
    public void killConnections() {
        if (httpServer != null) {
            httpServer.close();
            httpServer = null;
        }
        setIp(protocoder.mActivityContext.getResources().getString(R.string.start_the_server));

        if (mFtpServer != null) {
            mFtpServer.stopServer();
        }
    }

    //showPopUp={true, false}
    public void checkNewVersion() {

    }

    //JSON
    public void sendDeviceStats() {

    }

    //JSON
    public void sendCrashStats() {

    }

    public void getListLibraries() {

    }

    public void getListCommunityLibraries() {

    }


}
