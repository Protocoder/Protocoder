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


import android.content.Context;


import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.net.URISyntaxException;

public class PMqtt extends PInterface {

    private final String TAG = PMqtt.class.getSimpleName();

    //String content      = "Message from MqttPublishSample";
    //String host         = "messagesight.demos.ibm.com";
    //int port            = 1883;
    String clientId     = "ProtoClient";
    private MQTT mMqtt;
    private CallbackConnection mConnection;
    private boolean mConnected = false;
    private OnNewDataCallback mCallbackData;


    public PMqtt(Context c) {
        super(c);

        WhatIsRunning.getInstance().add(this);
    }

    public interface ConnectCallback {
        void event(boolean mConnected);
    }

    public PMqtt connect(String host, int port, final ConnectCallback callback) {
        MLog.d(TAG, "connect 1");
        mMqtt = new MQTT();

        try {
            mMqtt.setHost(host, port);
            mMqtt.setClientId(clientId);
            mMqtt.setCleanSession(true);

            mConnection = mMqtt.callbackConnection();
            mConnection.listener(new Listener() {
                @Override
                public void onConnected() {
                    MLog.d(TAG, "mconnection onConnected");
                    //callback.event(true);
                }

                @Override
                public void onDisconnected() {
                    MLog.d(TAG, "mconnection onDisconnected");
                    //callback.event(false);
                }

                @Override
                public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Runnable runnable) {
                    MLog.d(TAG, "mconnection onPublish " + utf8Buffer.toString() + " " + buffer.toString());
                    if(mCallbackData != null) mCallbackData.event(utf8Buffer.toString(), buffer.toString());
                }

                @Override
                public void onFailure(Throwable throwable) {
                    MLog.d(TAG, "mconnection onFailure");
                    //callback.event(false);
                }
            });

            mConnection.connect(new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mConnected = true;
                    MLog.d(TAG, "mconnection onSuccess");
                    callback.event(mConnected);

                }

                @Override
                public void onFailure(Throwable throwable) {
                    mConnected = false;
                    MLog.d(TAG, "mconnection onFailure");
                    callback.event(mConnected);
                }
            });

            MLog.d(TAG, "connect 2");


        } catch (URISyntaxException e) {
            e.printStackTrace();
            MLog.d(TAG, "connect :( 1");

        } catch (Exception e) {
            e.printStackTrace();
            MLog.d(TAG, "connect :( 2");
        }

        return this;
    }

    public interface SubscribeCallback {
        void event(String data);
    }

    public PMqtt subscribe(String topicStr, final SubscribeCallback callback) {

        Topic topic = new Topic(topicStr, QoS.AT_MOST_ONCE);
        Topic[] topics = new Topic[]{topic};

        mConnection.subscribe(topics, new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String dataString = bytes.toString();
                MLog.d(TAG, "subscribe onSuccess byte " + dataString);
                callback.event(dataString);
            }

            @Override
            public void onFailure(Throwable throwable) {
                MLog.d(TAG, "subscribe onFailure");

            }
        });

        return this;
    }


    public interface OnNewDataCallback {
        void event(String topic, String data);
    }

    public PMqtt onNewData(OnNewDataCallback callback) {
        mCallbackData = callback;

        return this;
    }

    public PMqtt publish(String topic, String data) {
        boolean retain = false;

        mConnection.publish(topic, data.getBytes(), QoS.AT_MOST_ONCE, retain, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MLog.d(TAG, "publish onSuccess");

            }

            @Override
            public void onFailure(Throwable throwable) {
                MLog.d(TAG, "publish onFailure");
            }
        });

        return this;
    }

    public PMqtt disconnect() {
        MLog.d(TAG, "disconnect");
        mConnection.disconnect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                MLog.d(TAG, "disconnect onSuccess");
            }

            @Override
            public void onFailure(Throwable value) {
                MLog.d(TAG, "failure onFailure");
            }
        });

        return this;
    }

    public void stop() {
        disconnect();
    }

}
