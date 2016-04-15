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

package org.protocoderrunner.api.network;


import android.os.Handler;
import android.os.Looper;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.api.ProtoBase;

import java.net.InetSocketAddress;
import java.util.Collections;

public class PWebSocketServer extends ProtoBase {

    public Handler mHandler = new Handler(Looper.getMainLooper());
    WebSocketServer websocketServer;
    private startWebSocketServerCB mCallbackfn;

    // --------- webSocket Server ---------//
    interface startWebSocketServerCB {
        void event(String string, WebSocket socket, String arg1);
    }

    public PWebSocketServer(AppRunner appRunner, int port) {
        super(appRunner);

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
    }

    public PWebSocketServer onNewData(final startWebSocketServerCB callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    @Override
    public void __stop() {

    }
}
