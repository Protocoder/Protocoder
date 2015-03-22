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

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;

import java.net.InetSocketAddress;
import java.util.Collections;

public class PWebSocketServer {

    public Handler mHandler = new Handler(Looper.getMainLooper());
    WebSocketServer websocketServer;
    private startWebSocketServerCB mCallbackfn;

    // --------- webSocket Server ---------//
    interface startWebSocketServerCB {
        void event(String string, WebSocket socket, String arg1);
    }

    public PWebSocketServer(int port) {

        InetSocketAddress inetSocket = new InetSocketAddress(port);
        Draft d = new Draft_17();

        websocketServer = new WebSocketServer(inetSocket, Collections.singletonList(d)) {

            @Override
            public void onClose(final WebSocket arg0, int arg1, String arg2, boolean arg3) {
                if (mCallbackfn == null) return;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallbackfn.event("onClose", arg0, "");
                    }
                });
                //MLog.d(TAG, "onClose");
            }

            @Override
            public void onError(final WebSocket arg0, Exception arg1) {
                if (mCallbackfn == null) return;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallbackfn.event("onError", arg0, "");
                    }
                });
                //MLog.d(TAG, "onError");
            }

            @Override
            public void onMessage(final WebSocket arg0, final String arg1) {
                if (mCallbackfn == null) return;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallbackfn.event("onMessage", arg0, arg1);
                    }
                });
                //MLog.d(TAG, "onMessage server");

            }

            @Override
            public void onOpen(final WebSocket arg0, ClientHandshake arg1) {
                if (mCallbackfn == null) return;

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallbackfn.event("onOpen", arg0, "");
                    }
                });
                //MLog.d(TAG, "onOpen");
            }

        };
        websocketServer.start();
        WhatIsRunning.getInstance().add(websocketServer);

    }

    public PWebSocketServer onNewData(final startWebSocketServerCB callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

}
