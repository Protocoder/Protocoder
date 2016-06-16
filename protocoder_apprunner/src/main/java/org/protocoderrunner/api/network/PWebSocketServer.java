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
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.api.ProtoBase;
import org.protocoderrunner.base.utils.MLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

public class PWebSocketServer extends ProtoBase {

    public Handler mHandler = new Handler(Looper.getMainLooper());
    WebSocketServer websocketServer;
    private ReturnInterface mCallbackfn;

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
                        ReturnObject o = new ReturnObject();
                        o.put("status", "close");
                        o.put("socket", null);
                        mCallbackfn.event(o);
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
                        ReturnObject o = new ReturnObject();
                        o.put("status", "error");
                        o.put("socket", null);
                        mCallbackfn.event(o);
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
                        ReturnObject o = new ReturnObject();
                        o.put("status", "message");
                        o.put("socket", arg0);
                        o.put("data", arg1);
                        mCallbackfn.event(o);
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
                        ReturnObject o = new ReturnObject();
                        o.put("status", "open");
                        o.put("socket", null);
                        mCallbackfn.event(o);
                    }
                });
                //MLog.d(TAG, "onOpen");
            }

        };
        websocketServer.start();
    }

    public PWebSocketServer onNewData(final ReturnInterface callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    @Override
    public void __stop() {
        try {
            websocketServer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
