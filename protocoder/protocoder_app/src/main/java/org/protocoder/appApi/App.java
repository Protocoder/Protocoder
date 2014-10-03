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
package org.protocoder.appApi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.java_websocket.drafts.Draft_17;
import org.protocoder.AboutActivity;
import org.protocoder.R;
import org.protocoder.SetPreferenceActivity;
import org.protocoder.network.ProtocoderHttpServer;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.network.IDEcommunication;
import org.protocoderrunner.network.NetworkUtils;
import org.protocoderrunner.utils.AndroidUtils;

import java.net.UnknownHostException;

public class App {

    private final Protocoder protocoder;
    public Editor editor;

    private ProtocoderHttpServer httpServer;
    private CustomWebsocketServer ws;

    private TextView textIP;
    private LinearLayout mIpContainer;
    protected int textIPHeight;

    int usbEnabled = 0;


    App(Protocoder protocoder) {
        editor = new Editor(protocoder);
        this.protocoder = protocoder;

        init();
    }


    public void init() {
        // Create the IP text view
        textIP = (TextView) protocoder.a.findViewById(R.id.ip);
        textIP.setOnClickListener(null);// Remove the old listener explicitly
        textIP.setBackgroundResource(0);
        mIpContainer = (LinearLayout) protocoder.a.findViewById(R.id.ip_container);

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
    }


    public void showHelp(boolean show) {

        if (show) {
            Intent aboutActivityIntent = new Intent(protocoder.a, AboutActivity.class);
            protocoder.a.startActivity(aboutActivityIntent);
            protocoder.a.overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
        } else {

        }
    }


    public void showAbout(boolean show) {
        if (show) {

        } else {

        }

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

    //when there is a some data transfer
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

    }
    public void vibrate(int time) {
        protocoder.pDevice.vibrate(time);
    }

    public void shake(int time) {

    }

    //"noise", "blipy", "hipster", "color"
    public void mode(String mode) {

    }
    public void close() {

    }
    public void restart() {

    }

    public void showSettings(boolean b) {
        Intent preferencesIntent = new Intent(protocoder.a, SetPreferenceActivity.class);
        protocoder.a.startActivity(preferencesIntent);
        protocoder.a.overridePendingTransition(R.anim.splash_slide_in_anim_set, R.anim.splash_slide_out_anim_set);
    }

    public void setIp(String s) {
        textIP.setText(s);
    }


    /**
     * Starts the remote service connection
     */
    public int startServers() {

        // check if usb is enabled
        usbEnabled = Settings.Secure.getInt(protocoder.a.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);

        // start webserver
        httpServer = ProtocoderHttpServer.getInstance(protocoder.a.getApplicationContext(), AppSettings.HTTP_PORT);

        // websocket
        try {
            ws = CustomWebsocketServer.getInstance(protocoder.a, AppSettings.WEBSOCKET_PORT, new Draft_17());
            IDEcommunication.getInstance(protocoder.a).ready(false);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // check if there is a WIFI connection or we can connect via USB
        if (NetworkUtils.getLocalIpAddress(protocoder.a).equals("-1")) {
            protocoder.app.setIp("No WIFI, still you can hack via USB using the companion app");
        } else {
            protocoder.app.setIp("Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(protocoder.a) + ":"
                    + AppSettings.HTTP_PORT);
        }

        if (httpServer != null) {// If no instance of HTTPServer, we set the IP
            // address view to gone.
            protocoder.app.showNetworkBottomInfo(true);
        } else {
            protocoder.app.showNetworkBottomInfo(false);
        }

        return 1;
    }

    /**
     * Unbinds service and stops the http server
     */
    // TODO add stop websocket
    public void killConnections() {
        if (httpServer != null) {
            httpServer.close();
            httpServer = null;
        }
        protocoder.app.setIp(protocoder.a.getResources().getString(R.string.start_the_server));
    }

    /**
     * Explicitly kills connections, with UI impact
     */
    public void hardKillConnections() {
        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
        protocoder.app.setIp(protocoder.a.getResources().getString(R.string.start_the_server));
    }

}
