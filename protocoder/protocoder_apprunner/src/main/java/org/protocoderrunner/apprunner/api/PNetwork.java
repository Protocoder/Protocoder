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

package org.protocoderrunner.apprunner.api;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PSimpleHttpServer;
import org.protocoderrunner.apprunner.api.other.PSocketIOClient;
import org.protocoderrunner.network.NetworkUtils;
import org.protocoderrunner.network.NetworkUtils.DownloadTask.DownloadListener;
import org.protocoderrunner.network.OSC;
import org.protocoderrunner.network.bt.SimpleBT;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import de.sciss.net.OSCMessage;

public class PNetwork extends PInterface {

	private final String TAG = "PNetwork";
    private boolean mBtStarted = false;

    public PNetwork(Context a) {
        super(a);

        WhatIsRunning.getInstance().add(this);
    }

    // --------- download file ---------//
    interface downloadFileCB {
        void event(int eventType);
    }

    @ProtocoderScript
    @APIMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    @APIParam(params = {"url", "fileName", "function(progress)"})
    public void downloadFile(String url, String fileName, final downloadFileCB callbackfn) {

        NetworkUtils.DownloadTask downloadTask = new NetworkUtils.DownloadTask(mContext, fileName);
        downloadTask.execute(url);
        downloadTask.addListener(new DownloadListener() {

            @Override
            public void onUpdate(int progress) {
                callbackfn.event(progress);
            }
        });

    }

    // @JavascriptInterface
    // @APIMethod(description = "", example = "")
    // @APIParam( params = {"file", "function()"} )
    public void isReachable(final String host, final String callbackfn) {

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                // doesnt work! isReachable
                //
                // try {
                // InetAddress in = InetAddress.getByName(host);
                // boolean isReacheable = in.isReachable(5000);
                // callback(callbackfn, isReacheable);
                // } catch (UnknownHostException e) {
                // e.printStackTrace();
                // } catch (IOException e) {
                // e.printStackTrace();
                // }

            }
        });
        t.start();

    }



    @ProtocoderScript
    @APIMethod(description = "Check if internet connection is available", example = "")
    @APIParam(params = {""})
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @ProtocoderScript
    @APIMethod(description = "Returns the current device Ip address", example = "")
    @APIParam(params = {""})
    public String getIp() {
        return NetworkUtils.getLocalIpAddress(mContext);
    }

    @ProtocoderScript
    @APIMethod(description = "Get the wifi ap information", example = "")
    @APIParam(params = {""})
    public WifiInfo getWifiInfo() {
        return NetworkUtils.getWifiInfo(mContext);
    }

    // --------- OSC Server ---------//
    interface startOSCServerCB {
        void event(String string, JSONArray jsonArray);
    }

    @ProtocoderScript
    @APIMethod(description = "Starts an OSC server", example = "")
    @APIParam(params = {"port", "function(jsonData)"})
    public OSC.Server startOSCServer(String port, final startOSCServerCB callbackfn) {
        OSC osc = new OSC();
        OSC.Server server = osc.new Server();

        server.addListener(new OSC.OSCServerListener() {

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
                    // TODO Auto-generated catch block
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

        server.start(port);
        WhatIsRunning.getInstance().add(server);

        return server;
    }

    @ProtocoderScript
    @APIMethod(description = "Connects to a OSC server. Returns an object that allow sending messages", example = "")
    @APIParam(params = {"address", "port"})
    public OSC.Client connectOSC(String address, int port) {
        OSC osc = new OSC();
        OSC.Client client = osc.new Client(address, port);
        WhatIsRunning.getInstance().add(client);

        return client;
    }

    // --------- webSocket Server ---------//
    interface startWebSocketServerCB {
        void event(String string, WebSocket socket, String arg1);
    }

    WifiManager.MulticastLock wifiLock;

    @ProtocoderScript
    @APIMethod(description = "Enable multicast networking", example = "")
    @APIParam(params = {"boolean"})
    public void setMulticast(boolean b) {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            if (b) {
                wifiLock = wifi.createMulticastLock("mylock");
                wifiLock.acquire();
            } else {
                wifiLock.release();
            }
        }
    }

    class MulticastEnabler {
        WifiManager.MulticastLock wifiLock;

        MulticastEnabler(boolean b) {
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                if (b) {
                    wifiLock = wifi.createMulticastLock("mylock");
                    wifiLock.acquire();
                    WhatIsRunning.getInstance().add(this);
                } else {
                    wifiLock.release();
                }
            }
        }

        public void stop() {
            if (wifiLock != null) {
                wifiLock.release();
            }
        }

    }

    @ProtocoderScript
    @APIMethod(description = "Start a websocket server", example = "")
    @APIParam(params = {"port", "function(status, socket, data)"})
    public WebSocketServer startWebsocketServer(int port, final startWebSocketServerCB callbackfn) {

        InetSocketAddress inetSocket = new InetSocketAddress(port);
        Draft d = new Draft_17();
        WebSocketServer websocketServer = new WebSocketServer(inetSocket, Collections.singletonList(d)) {

            @Override
            public void onClose(final WebSocket arg0, int arg1, String arg2, boolean arg3) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackfn.event("onClose", arg0, "");
                    }
                });
                //MLog.d(TAG, "onClose");
            }

            @Override
            public void onError(final WebSocket arg0, Exception arg1) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackfn.event("onError", arg0, "");
                    }
                });
                //MLog.d(TAG, "onError");
            }

            @Override
            public void onMessage(final WebSocket arg0, final String arg1) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackfn.event("onMessage", arg0, arg1);
                    }
                });
                //MLog.d(TAG, "onMessage server");

            }

            @Override
            public void onOpen(final WebSocket arg0, ClientHandshake arg1) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callbackfn.event("onOpen", arg0, "");
                    }
                });
                //MLog.d(TAG, "onOpen");
            }
        };
        websocketServer.start();
        WhatIsRunning.getInstance().add(websocketServer);
        return websocketServer;

    }

    // --------- connect websocket ---------//
    interface connectWebsocketCB {
        void event(String string, String string2);
    }

    @ProtocoderScript
    @APIMethod(description = "Connect to a websocket server", example = "")
    @APIParam(params = {"uri", "function(status, data)"})
    public WebSocketClient connectWebsocket(String uri, final connectWebsocketCB callbackfn) {

        Draft d = new Draft_17();

        WebSocketClient webSocketClient = null;
        try {
            webSocketClient = new WebSocketClient(new URI(uri), d) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbackfn.event("onOpen", "");
                        }
                    });
                    //Log.d(TAG, "onOpen");
                }

                @Override
                public void onMessage(final String arg0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbackfn.event("onMessage", arg0);
                        }
                    });

                    //Log.d(TAG, "onMessage client");

                }

                @Override
                public void onError(Exception arg0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbackfn.event("onError", "");

                        }
                    });

                    //Log.d(TAG, "onError");

                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbackfn.event("onClose", "");
                        }
                    });

                    //Log.d(TAG, "onClose");

                }
            };
            webSocketClient.connect();

        } catch (URISyntaxException e) {
            Log.d(TAG, "error");

            callbackfn.event("error ", e.toString());
            e.printStackTrace();
        }

        return webSocketClient;
    }

    // --------- connectSocketIO ---------//
    interface connectSocketIOCB {
        // void event(String string, String reason, String string2);
        void event(String string, String event, JSONArray arguments);
    }

    @ProtocoderScript
    @APIMethod(description = "Connect to a SocketIO server", example = "")
    @APIParam(params = {"uri", "function(status, message, data)"})
    public PSocketIOClient connectSocketIO(String uri, final connectSocketIOCB callbackfn) {

        PSocketIOClient socketIOClient = new PSocketIOClient(URI.create(uri), new SocketIOClient.Handler() {

            @Override
            public void onMessage(String message) {
                callbackfn.event("onMessage", null, null);
                //MLog.d("qq", "onMessage");
            }

            @Override
            public void onJSON(JSONObject json) {

            }

            @Override
            public void onError(Exception error) {
                callbackfn.event("error", null, null);
            }

            @Override
            public void onDisconnect(int code, String reason) {
                callbackfn.event("disconnect", reason, null);
                // MLog.d("qq", "disconnected");
            }

            @Override
            public void onConnect() {
                callbackfn.event("connected", null, null);
                // MLog.d("qq", "connected");
            }

            @Override
            public void onConnectToEndpoint(String s) {

            }

            @Override
            public void on(String event, JSONArray arguments) {
                callbackfn.event("on", event, arguments);
                // MLog.d("qq", "onmessage");

            }
        });
        socketIOClient.connect();

        return socketIOClient;
    }

    private class EmailConf {
        public String host;
        public String user;
        public String password;
        public String port;
        public String auth;
        public String ttl;
    }

    // public EmailConf emailSettings;

    @ProtocoderScript
    @APIMethod(description = "Creates an object where to set the e-mail sending settings", example = "")
    @APIParam(params = {"url", "function(data)"})
    public EmailConf createEmailSettings() {
		/*
		 * String host, String user, String pass, String iPort, String bAuth,
		 * String bTtl) {
		 * 
		 * emailSettings = new EmailConf(); emailSettings.host = host;
		 * emailSettings.user = user; emailSettings.password = pass;
		 * emailSettings.port = iPort; emailSettings.auth = bAuth;
		 * emailSettings.ttl = bTtl;
		 */
        return new EmailConf();
    }

    // http://mrbool.com/how-to-work-with-java-mail-api-in-android/27800#ixzz2tulYAG00
    @ProtocoderScript
    @APIMethod(description = "Send an E-mail. It requires passing a EmailConf object", example = "")
    @APIParam(params = {"url", "function(data)"})
    public void sendEmail(String from, String to, String subject, String text, final EmailConf emailSettings)
            throws AddressException, MessagingException {

        if (emailSettings == null) {
            return;
        }

        // final String host = "smtp.gmail.com";
        // final String address = "@gmail.com";
        // final String pass = "";

        Multipart multiPart;
        String finalString = "";

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", emailSettings.ttl);
        props.put("mail.smtp.host", emailSettings.host);
        props.put("mail.smtp.user", emailSettings.user);
        props.put("mail.smtp.password", emailSettings.password);
        props.put("mail.smtp.port", emailSettings.port);
        props.put("mail.smtp.auth", emailSettings.auth);

        Log.i("Check", "done pops");
        final Session session = Session.getDefaultInstance(props, null);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setDataHandler(handler);
        Log.i("Check", "done sessions");

        multiPart = new MimeMultipart();

        InternetAddress toAddress;
        toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        Log.i("Check", "added recipient");
        message.setSubject(subject);
        message.setContent(multiPart);
        message.setText(text);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MLog.i("check", "transport");
                    Transport transport = session.getTransport("smtp");
                    MLog.i("check", "connecting");
                    transport.connect(emailSettings.host, emailSettings.user, emailSettings.password);
                    MLog.i("check", "wana send");
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                    MLog.i("check", "sent");

                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();

    }

    // --------- getRequest ---------//
    public interface HttpGetCB {
        void event(int eventType, String responseString);
    }

    @ProtocoderScript
    @APIMethod(description = "Simple http get. It returns the data using the callback", example = "")
    @APIParam(params = {"url", "function(eventType, responseString)"})
    public void httpGet(String url, final HttpGetCB callbackfn) {

        class RequestTask extends AsyncTask<String, String, String> {
            String responseString = null;

            @Override
            protected String doInBackground(String... uri) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                try {
                    URL url = new URL(uri[0]);
                    response = httpclient.execute(new HttpGet(url.toString()));
                    final StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        // Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            callbackfn.event(statusLine.getStatusCode(), responseString);
                        }
                    });

                } catch (ClientProtocolException e) {
                    MLog.e(TAG, e.toString());
                } catch (IOException e) {
                    MLog.e(TAG, e.toString());
                } finally {
                    MLog.e(TAG, "error");
                }
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // Do anything with response..
            }
        }

        MLog.d(TAG, "" + new RequestTask().execute(url));
    }

    // --------- postRequest ---------//
    interface HttpPostCB {
        void event(String string);
    }

    @ProtocoderScript
    @APIMethod(description = "Simple http post request. It needs an object to be sent. If an element of the object contains the key file then it will try to upload the resource indicated in the value as Uri ", example = "")
    @APIParam(params = {"url", "params", "function(responseString)"})
    public void httpPost(String url, Object object, final HttpPostCB callbackfn) {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpContext localContext = new BasicHttpContext();
        final HttpPost httpPost = new HttpPost(url);

        Gson g = new Gson();
        JsonArray q = g.toJsonTree(object).getAsJsonArray();

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int i = 0; i < q.size(); i++) {
            Set<Entry<String, JsonElement>> set = q.get(i).getAsJsonObject().entrySet();

            // go through elements
            String name = "";
            String content = "";
            String type = "";
            for (Object element : set) {
                Entry<String, JsonElement> entry = (Entry<String, JsonElement>) element;
                if (entry.getKey().equals("name")) {
                    name = entry.getValue().getAsString();
                } else if (entry.getKey().equals("content")) {
                    content = entry.getValue().getAsString();
                } else if (entry.getKey().equals("type")) {
                    type = entry.getValue().getAsString();
                }
            }

            // create the multipart
            if (type.contains("file")) {
                File f = new File(ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + content);
                ContentBody cbFile = new FileBody(f);
                entity.addPart(name, cbFile);
            } else if (type.contains("text")) { // Normal string data
                try {
                    entity.addPart(name, new StringBody(content));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        // send
        httpPost.setEntity(entity);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpResponse response = httpClient.execute(httpPost, localContext);
                    callbackfn.event(response.getStatusLine().toString());
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //gives the url trying to access
    //if (url == "") {
    //} else {
    //server.serveFiles()
    //
    //}


	@ProtocoderScript
	@APIMethod(description = "Simple http server, serving the content of the project folder", example = "")
	@APIParam(params = { "port", "function(responseString)" })
	public PSimpleHttpServer startSimpleHttpServer(int port, final PSimpleHttpServer.HttpCB callbackfn) {
        PSimpleHttpServer httpServer = null;
        try {
			httpServer = new PSimpleHttpServer(mContext, port, callbackfn);
            WhatIsRunning.getInstance().add(httpServer);

		} catch (IOException e) {
			e.printStackTrace();
		}

        return httpServer;
	}



	//--------- Bluetooth ---------//
    //methods
    //scanBluetoothNetworks
    //connectBluetoothSerialByUi
    //connectBluetoothSerialByMac
    //connectBluetoothSerialByName
    //sendBluetoothSerial
    //disconnectBluetooth
    //enableBluetooth
    //isBluetoothConnected

	private scanBTNetworksCB onBluetoothfn;
	private SimpleBT simpleBT;

	public interface onBluetoothListener {
		public void onDeviceFound(String name, String macAddress, float strength);
		public void onActivityResult(int requestCode, int resultCode, Intent data);
	}

	interface scanBTNetworksCB {
		void event(String name, String macAddress, float strength);
	}
    //TODO reenable this
//
//	@ProtocoderScript
//	@APIMethod(description = "Scan bluetooth networks. Gives back the name, mac and signal strength", example = "")
//	@APIParam(params = { "function(name, macAddress, strength)" })
//	public void scanBluetoothNetworks(final scanBTNetworksCB callbackfn) {
//        startBluetooth();
//		onBluetoothfn = callbackfn;
//		simpleBT.scanBluetooth(new onBluetoothListener() {
//
//			@Override
//			public void onDeviceFound(String name, String macAddress, float strength) {
//				onBluetoothfn.event(name, macAddress, strength);
//			}
//
//			@Override
//			public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//			}
//		});
//
//	}
	//@ProtocoderScript
	//@APIMethod(description = "Start the bluetooth adapter", example = "")
	//@APIParam(params = { "" })
//	public SimpleBT startBluetooth() {
//        if (mBtStarted) {
//            return simpleBT;
//        }
//		simpleBT = new SimpleBT(contextUi.get());
//		simpleBT.start();
//		contextUi.addBluetoothListener(new onBluetoothListener() {
//
//			@Override
//			public void onDeviceFound(String name, String macAddress, float strength) {
//			}
//
//			@Override
//			public void onActivityResult(int requestCode, int resultCode, Intent data) {
//				simpleBT.onActivityResult(requestCode, resultCode, data);
//
//				switch (requestCode) {
//				case SimpleBT.REQUEST_ENABLE_BT:
//					// When the request to enable Bluetooth returns
//					if (resultCode == Activity.RESULT_OK) {
//						//MLog.d(TAG, "enabling BT");
//						// Bluetooth is now enabled, so set up mContext Bluetooth session
//                        mBtStarted = true;
//						simpleBT.startBtService();
//
//                    // User did not enable Bluetooth or an error occurred
//                    } else {
//					    //	MLog.d(TAG, "BT not enabled");
//						Toast.makeText(mContext.getApplicationContext(), "BT not enabled :(", Toast.LENGTH_SHORT)
//								.show();
//
//					}
//				}
//			}
//		});
//
//		WhatIsRunning.getInstance().add(simpleBT);
//		return simpleBT;
//	}
//
//    // --------- connectBluetooth ---------//
//    interface connectBluetoothCB {
//        void event(String what, String data);
//    }
//
//    //TODO removed new impl needed
//	@ProtocoderScript
//	@APIMethod(description = "Connects to mContext bluetooth device using mContext popup", example = "")
//	@APIParam(params = { "function(name, macAddress, strength)" })
//	public void connectBluetoothSerialByUi(final connectBluetoothCB callbackfn) {
//        startBluetooth();
//        NativeArray nativeArray = getBluetoothBondedDevices();
//        String[] arrayStrings = new String[(int) nativeArray.size()];
//        for (int i = 0; i < nativeArray.size(); i++) {
//            arrayStrings[i] = (String) nativeArray.get(i, null);
//        }
//
//        contextUi.get().pUi.popupChoice("Connect to device", arrayStrings, new PUI.choiceDialogCB() {
//            @Override
//            public void event(String string) {
//                connectBluetoothSerialByMac(string.split(" ")[1], callbackfn);
//            }
//        });
//		//simpleBT.startDeviceListActivity();
//	}
//
//	@ProtocoderScript
//	@APIMethod(description = "Connect to mContext bluetooth device using the mac address", example = "")
//	@APIParam(params = { "mac", "function(data)" })
//	public void connectBluetoothSerialByMac(String mac, final connectBluetoothCB callbackfn) {
//        startBluetooth();
//        simpleBT.connectByMac(mac);
//        addBTConnectionListener(callbackfn);
//
//	}
//
//	@ProtocoderScript
//	@APIMethod(description = "Connect to mContext bluetooth device using mContext name", example = "")
//	@APIParam(params = { "name, function(data)" })
//	public void connectBluetoothSerialByName(String name, final connectBluetoothCB callbackfn) {
//        startBluetooth();
//		simpleBT.connectByName(name);
//        addBTConnectionListener(callbackfn);
//    }
//
//    private void addBTConnectionListener(final connectBluetoothCB callbackfn) {
//        simpleBT.addListener(new SimpleBT.SimpleBTListener() {
//
//            @Override
//            public void onRawDataReceived(byte[] buffer, int size) {
//                //MLog.network(mContext, "Bluetooth", "1. got " + buffer.toString());
//            }
//
//            @Override
//            public void onMessageReceived(final String data) {
//                //MLog.network(mContext, "Bluetooth", "2. got " + data);
//
//                if (data != "") {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            //MLog.d(TAG, "Got data: " + data);
//                            callbackfn.event("data", data);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onConnected() {
//                callbackfn.event("connected", null);
//            }
//        });
//    }
//
//
//    @ProtocoderScript
//    @APIMethod(description = "Send mContext bluetooth serial message", example = "")
//    @APIParam(params = { "string" })
//    public NativeArray getBluetoothBondedDevices() {
//        startBluetooth();
//
//        Set<BluetoothDevice> listDevices = simpleBT.listBondedDevices();
//        MLog.d(TAG, "listDevices " + listDevices);
//        int listSize = listDevices.size();
//        ProtocoderNativeArray array = new ProtocoderNativeArray(listSize);
//        MLog.d(TAG, "array " + array);
//
//
//        int counter = 0;
//        for (BluetoothDevice b : listDevices) {
//            MLog.d(TAG, "b " + b);
//
//            String s = b.getName() + " " + b.getAddress();
//            array.addPE(counter++, s);
//        }
//
//        return array;
//    }
//
//	@ProtocoderScript
//	@APIMethod(description = "Send mContext bluetooth serial message", example = "")
//	@APIParam(params = { "string" })
//	public void sendBluetoothSerial(String string) {
//		if (simpleBT.isConnected()) {
//            simpleBT.send(string);
//        }
//	}
//
//	@ProtocoderScript
//	@APIMethod(description = "Disconnect the bluetooth", example = "")
//	@APIParam(params = { "" })
//	public void disconnectBluetooth() {
//        if (simpleBT.isConnected()) {
//            simpleBT.disconnect();
//        }
//	}
//
//	@ProtocoderScript
//	@APIMethod(description = "Enable/Disable the bluetooth adapter", example = "")
//	@APIParam(params = { "boolean" })
//	public void enableBluetooth(boolean b) {
//        if (b) {
//            simpleBT.start();
//        } else {
//            simpleBT.disable();
//        }
//	}
//
//    @ProtocoderScript
//    @APIMethod(description = "Enable/Disable the bluetooth adapter", example = "")
//    @APIParam(params = { "boolean" })
//    public boolean isBluetoothConnected() {
//        return simpleBT.isConnected();
//    }
//
//
//    @ProtocoderScript
//	@APIMethod(description = "Enable/Disable the Wifi adapter", example = "")
//	@APIParam(params = { "boolean" })
//	public void enableWifi(boolean enabled) {
//		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		wifiManager.setWifiEnabled(enabled);
//	}
//
//	@ProtocoderScript
//	@APIMethod(description = "Check if the Wifi adapter is enabled", example = "")
//	@APIParam(params = {})
//	public boolean isWifiEnabled() {
//		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		return wifiManager.isWifiEnabled();
//	}
//
//	// http://stackoverflow.com/questions/8818290/how-to-connect-to-mContext-specific-wifi-network-in-android-programmatically
//	@ProtocoderScript
//	@APIMethod(description = "Connect to mContext given Wifi network with mContext given 'wpa', 'wep', 'open' type and mContext password", example = "")
//	@APIParam(params = { "ssidName", "type", "password" })
//	public void connectWifi(String networkSSID, String type, String networkPass) {
//
//		WifiConfiguration conf = new WifiConfiguration();
//		conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes. String
//												// should contain ssid in quotes
//
//		if (type.equals("wep")) {
//			// wep
//			conf.wepKeys[0] = "\"" + networkPass + "\"";
//			conf.wepTxKeyIndex = 0;
//			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//		} else if (type.equals("wpa")) {
//			// wpa
//			conf.preSharedKey = "\"" + networkPass + "\"";
//		} else if (type.equals("open")) {
//			// open
//			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//		}
//
//		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		wifiManager.addNetwork(conf);
//
//		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
//		for (WifiConfiguration i : list) {
//			if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
//				wifiManager.disconnect();
//				wifiManager.enableNetwork(i.networkId, true);
//				wifiManager.reconnect();
//
//				break;
//			}
//		}
//
//	}
//
//	private Object mIsWifiAPEnabled = true;
//
//	@ProtocoderScript
//	@APIMethod(description = "Enable/Disable mContext Wifi access point", example = "")
//	@APIParam(params = { "boolean, apName" })
//	public void wifiAP(boolean enabled, String wifiName) {
//
//        WifiManager wifi = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
//        Method[] wmMethods = wifi.getClass().getDeclaredMethods();
//        Log.d(TAG, "enableMobileAP methods " + wmMethods.length);
//        for (Method method : wmMethods) {
//            Log.d(TAG, "enableMobileAP method.getName() " + method.getName());
//            if (method.getName().equals("setWifiApEnabled")) {
//                WifiConfiguration netConfig = new WifiConfiguration();
//                netConfig.SSID = wifiName;
//                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
//                //
//                try {
//                    //MLog.d(TAG, "enableMobileAP try: ");
//                    method.invoke(wifi, netConfig, enabled);
//                    if (netConfig.wepKeys != null && netConfig.wepKeys.length >= 1) {
//                        Log.d(TAG, "enableMobileAP key : " + netConfig.wepKeys[0]);
//                    }
//                    //MLog.d(TAG, "enableMobileAP enabled: ");
//                    mIsWifiAPEnabled = enabled;
//                } catch (Exception e) {
//                    //MLog.e(TAG, "enableMobileAP failed: ", e);
//                }
//            }
//        }
//    }
//
//
//    // --------- RegisterServiceCB ---------//
//    public interface RegisterServiceCB {
//        void event();
//    }
//
//    @ProtocoderScript
//    @APIMethod(description = "Register mContext discovery service", example = "")
//    @APIParam(params = { "serviceName, serviceType, port, function(name, status)" })
//    public void registerService(String serviceName, String serviceType, int port, ServiceDiscovery.CreateCB callbackfn) {
//        ServiceDiscovery.Create rD = new ServiceDiscovery().create(contextUi.get(), serviceName, serviceType, port, callbackfn);
//        WhatIsRunning.getInstance().add(rD);
//    }
//
//    @ProtocoderScript
//    @APIMethod(description = "Discover services in the current network", example = "")
//    @APIParam(params = { "serviceType, function(name, jsonData)" })
//    public void discoverServices(final String serviceType, ServiceDiscovery.DiscoverCB callbackfn) {
//        ServiceDiscovery.Discover sD = new ServiceDiscovery().discover(contextUi.get(), serviceType, callbackfn);
//        WhatIsRunning.getInstance().add(sD);
//
//    }
//
//
//    @ProtocoderScript
//    @APIMethod(description = "Ping mContext Ip address", example = "")
//    @APIParam(params = { "ip", "function(result)" })
//    public ExecuteCmd ping(final String where, final ExecuteCmd.ExecuteCommandCB callbackfn) {
////        mHandler.post(new Runnable() {
////            @Override
////            public void run() {
//               return new ExecuteCmd("/system/bin/ping -c 8 " + where, callbackfn);
//     //       }
//     //   });
//    }
//
//

    public void stop() {

	}



}