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
