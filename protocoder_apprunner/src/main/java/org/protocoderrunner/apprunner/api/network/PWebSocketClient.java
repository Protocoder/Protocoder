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

package org.protocoderrunner.apprunner.api.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;

import java.net.URI;
import java.net.URISyntaxException;

public class PWebSocketClient {

    private static final String TAG = "PWebSocketClient";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private connectWebsocketCB mCallbackfn;
    private WebSocketClient mWebSocketClient = null;
    private boolean mIsConnected = false;

    // --------- connect websocket ---------//
    interface connectWebsocketCB {
        void event(String string, String string2);
    }

    public PWebSocketClient(String uri) {
        WhatIsRunning.getInstance().add(this);

        Draft d = new Draft_17();

        try {
            mWebSocketClient = new WebSocketClient(new URI(uri), d) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    mIsConnected = true;
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbackfn.event("onOpen", "");
                        }
                    });
                    //Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String arg0) {
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbackfn.event("onMessage", arg0);
                        }
                    });

                    //Log.d(TAG, "onMessage client");

                }

                @Override
                public void onError(Exception arg0) {
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbackfn.event("onError", "");

                        }
                    });

                    //Log.d(TAG, "onError");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    if (mCallbackfn == null) return;

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbackfn.event("onClose", "");
                        }
                    });

                    //Log.d(TAG, "onClose");

                }
            };
            mWebSocketClient.connect();

        } catch (URISyntaxException e) {
            Log.d(TAG, "error");

            mCallbackfn.event("error ", e.toString());
            e.printStackTrace();
        }
    }

    public PWebSocketClient onNewData(final connectWebsocketCB callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    public PWebSocketClient send(String msg) {
        if (mIsConnected) {
            mWebSocketClient.send(msg);
        }

        return this;
    }


    public boolean isConnected() {
        return mIsConnected;
    }


    public void stop() {
        mWebSocketClient.close();
    }
}
