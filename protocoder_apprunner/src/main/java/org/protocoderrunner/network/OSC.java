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

package org.protocoderrunner.network;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.protocoderrunner.utils.MLog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.Vector;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;

public class OSC {

    protected static final String TAG = "OSC";

    public interface OSCServerListener {

        public void onMessage(OSCMessage msg);

    }

    // --------- OSC Server ---------//
    interface startOSCServerCB {
        void event(String string, JSONArray jsonArray);
    }


    public class Server {
        public Handler mHandler = new Handler(Looper.getMainLooper());

        // OSC server
        OSCReceiver rcv;
        OSCTransmitter trns;
        DatagramChannel dch;
        int n = 0;

        SocketAddress inPort = null;
        Vector<OSCServerListener> listeners = new Vector<OSCServerListener>();

        public void start(String port) {

            rcv = null;
            dch = null;

            try {
                inPort = new InetSocketAddress(Integer.parseInt(port));

                dch = DatagramChannel.open();
                dch.socket().bind(inPort); // assigns an automatic local socket
                // address
                rcv = OSCReceiver.newUsing(dch);

                rcv.addOSCListener(new OSCListener() {

                    @Override
                    public void messageReceived(OSCMessage msg, SocketAddress sender, long time) {

                        for (OSCServerListener l : listeners) {
                            l.onMessage(msg);
                        }

                    }
                });

                rcv.startListening();

            } catch (IOException e2) {
                MLog.d(TAG, e2.getLocalizedMessage());
            }
        }

        public void onNewData(final startOSCServerCB callbackfn) {
            this.addListener(new OSC.OSCServerListener() {

                @Override
                public void onMessage(final OSCMessage msg) {
                    MLog.d(TAG, "message received " + msg);

                    final JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < msg.getArgCount(); i++) {
                        jsonArray.put(msg.getArg(i));
                    }

                    try {
                        MLog.d(TAG, msg.getName() + " " + jsonArray.toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // callback(callbackfn, "\"" + msg.getName() + "\"", str);
                    // Log.d(TAG, msg.g)
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // MLog.d(TAG, "receiver");
                            callbackfn.event(msg.getName(), jsonArray);
                        }
                    });
                }

            });
        }

        public void stop() {
            stopOSCServer();
        }

        public void stopOSCServer() {
            try {
                dch.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rcv.dispose();
        }

        public void addListener(OSCServerListener listener) {
            listeners.add(listener);
        }

        public void removeListener(OSCServerListener listener) {
            listeners.remove(listener);
        }

    }

    public class Client {

        // OSC client
        SocketAddress addr2;
        DatagramChannel dch2;
        OSCTransmitter trns2;
        boolean oscConnected = false;

        public Client(String address, int port) {
            connectOSC(address, port);
        }

        public void connectOSC(String address, int port) {

            MLog.d(TAG, "connecting to " + address + " in " + port);
            try {
                addr2 = new InetSocketAddress(InetAddress.getByName(address), port);
                dch2 = DatagramChannel.open();
                dch2.socket().bind(null);
                trns2 = OSCTransmitter.newUsing(dch2);
                MLog.d(TAG, "connected to " + address + " in " + port);
                oscConnected = true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public boolean isOSCConnected() {
            return oscConnected;
        }

        public void send(final String msg, final Object[] o) {

            if (oscConnected == true) {
                // send

                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // Object[] o = new Object[1];
                        // o[0] = content;
                        MLog.d(TAG, "sending");
                        try {
                            MLog.d(TAG, "sent");
                            trns2.send(new OSCMessage(msg, o), addr2);
                        } catch (IOException e) {
                            MLog.d(TAG, "not sent");
                            e.printStackTrace();
                        }

                    }
                });
                t.start();
            }
        }

        public void disconnectOSC() {
            try {
                dch2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            trns2.dispose();

        }

        public void stop() {
            disconnectOSC();

        }
    }

}
