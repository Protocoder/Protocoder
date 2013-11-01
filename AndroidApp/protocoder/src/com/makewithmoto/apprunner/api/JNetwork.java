/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

package com.makewithmoto.apprunner.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;
import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.network.NetworkUtils;
import com.makewithmoto.network.NetworkUtils.DownloadTask.DownloadListener;
import com.makewithmoto.network.OSC;
import com.makewithmoto.network.OSC.Client;
import com.makewithmoto.network.OSC.OSCServerListener;
import com.makewithmoto.network.OSC.Server;
import com.makewithmoto.sensors.WhatIsRunning;

import de.sciss.net.OSCMessage;

public class JNetwork extends JInterface {

	private String TAG = "JNetwork";
	private String onBluetoothfn;

	public JNetwork(Activity a) {
		super(a);

	}

	public interface onBluetoothListener {
		public void onDeviceFound(String name, String macAddress, float strength);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void downloadFile(String url, String fileName, final String callbackfn) {

		NetworkUtils.DownloadTask downloadTask = new NetworkUtils.DownloadTask(
				(Context) a.get(), fileName);
		downloadTask.execute(url);
		downloadTask.addListener(new DownloadListener() {

			@Override
			public void onUpdate(int progress) {
				callback(callbackfn, progress);
			}
		});

	}

	// @JavascriptInterface
	// @APIMethod(description = "", example = "")
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

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public String getIP() {
		return NetworkUtils.getLocalIpAddress(a.get());
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public OSC.Server startOSCServer(String port, final String callbackfn) {
		OSC osc = new OSC();
		Server server = osc.new Server();

		server.addListener(new OSCServerListener() {

			@Override
			public void onMessage(OSCMessage msg) {
				Log.d(TAG, "message received " + msg);

				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < msg.getArgCount(); i++) {
					jsonArray.put(msg.getArg(i));
				}

				// String[] str = null;
				// try {
				// str = new String[msg.getSize()];
				// for (int i = 0; i < msg.getArgCount(); i++) {
				// str[i] = "" + msg.getArg(i);
				// }
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				//

				try {
					Log.d(TAG, msg.getName() + " " + jsonArray.toString(2));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// callback(callbackfn, "\"" + msg.getName() + "\"", str);
				callback(callbackfn, "\"" + msg.getName() + "\"", jsonArray);
			}

		});

		server.start(port);
		WhatIsRunning.getInstance().add(server);

		return server;
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public OSC.Client connectOSC(String address, int port) {
		OSC osc = new OSC();
		Client client = osc.new Client(address, port);
		WhatIsRunning.getInstance().add(client);

		return client;
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public WebSocketServer startWebsocketServer(int port,
			final String callbackfn) {

		InetSocketAddress inetSocket = new InetSocketAddress(port);
		Draft d = new Draft_17();
		WebSocketServer websocketServer = new WebSocketServer(inetSocket,
				Collections.singletonList(d)) {

			@Override
			public void onClose(WebSocket arg0, int arg1, String arg2,
					boolean arg3) {
				callback(callbackfn, "close");
			}

			@Override
			public void onError(WebSocket arg0, Exception arg1) {
				callback(callbackfn, "error");
			}

			@Override
			public void onMessage(WebSocket arg0, String arg1) {
				callback(callbackfn, "message", "\"" + arg0 + "\"");
			}

			@Override
			public void onOpen(WebSocket arg0, ClientHandshake arg1) {
				callback(callbackfn, "open", "\"" + arg0 + "\"");
			}
		};
		websocketServer.start();

		return websocketServer;

	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public org.java_websocket.client.WebSocketClient connectWebsocket(
			String uri, final String callbackfn) {

		org.java_websocket.client.WebSocketClient webSocketClient = null;
		try {
			webSocketClient = new org.java_websocket.client.WebSocketClient(
					new URI(uri)) {

				@Override
				public void onOpen(ServerHandshake arg0) {
					callback(callbackfn, "open", "\"" + arg0 + "\"");
				}

				@Override
				public void onMessage(String arg0) {
					callback(callbackfn, "message", "\"" + arg0 + "\"");
				}

				@Override
				public void onError(Exception arg0) {
					callback(callbackfn, "error");
				}

				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					callback(callbackfn, "close");
				}
			};
		} catch (URISyntaxException e) {
			callback(callbackfn, "error " + "\"" + e.toString() + "\"");
			e.printStackTrace();
		}
		return webSocketClient;
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public SocketIOClient connectSocketIO(String uri, final String callbackfn) {
		SocketIOClient socketIOClient = new SocketIOClient(URI.create(uri),
				new SocketIOClient.Handler() {

					@Override
					public void onMessage(String message) {

					}

					@Override
					public void onJSON(JSONObject json) {

					}

					@Override
					public void onError(Exception error) {
						callback(callbackfn, "error");
					}

					@Override
					public void onDisconnect(int code, String reason) {
						callback(callbackfn, "disconnect", "\"" + reason + "\"");
					}

					@Override
					public void onConnect() {
						callback(callbackfn, "connected");

					}

					@Override
					public void on(String event, JSONArray arguments) {
						callback(callbackfn, "onmessage", event, "\""
								+ arguments + "\"");

					}
				});

		return socketIOClient;
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void getRequest(String url, final String callbackfn) {
		class RequestTask extends AsyncTask<String, String, String> {

			@Override
			protected String doInBackground(String... uri) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response;
				String responseString = null;
				try {
					response = httpclient.execute(new HttpGet(uri[0]));
					StatusLine statusLine = response.getStatusLine();
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
					callback(callbackfn, statusLine.getStatusCode(), "\""
							+ responseString + "\"");
				} catch (ClientProtocolException e) {

				} catch (IOException e) {

				}
				return responseString;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				// Do anything with response..
			}
		}

		Log.d(TAG, "" + new RequestTask().execute(url));
		callback(callbackfn);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void scanBTNetworks(final String callbackfn) {
		onBluetoothfn = callbackfn;

		a.get().scanBluetooth();

		((AppRunnerActivity) a.get())
				.addBluetoothListener(new onBluetoothListener() {

					@Override
					public void onDeviceFound(String name, String macAddress,
							float strength) {
						callback(onBluetoothfn, "\"" + name + "\"", "\""
								+ macAddress + "\"", "\"" + strength + "\"");
					}
				});

	}
}