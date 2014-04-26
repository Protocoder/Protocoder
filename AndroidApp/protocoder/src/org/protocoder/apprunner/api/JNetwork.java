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

package org.protocoder.apprunner.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.network.NetworkUtils;
import org.protocoder.network.NetworkUtils.DownloadTask.DownloadListener;
import org.protocoder.network.OSC;
import org.protocoder.network.OSC.Client;
import org.protocoder.network.OSC.OSCServerListener;
import org.protocoder.network.OSC.Server;
import org.protocoder.network.bt.DeviceListActivity;
import org.protocoder.network.bt.SimpleBT;
import org.protocoder.network.bt.SimpleBT.SimpleBTListener;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.MLog;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.codebutler.android_websockets.SocketIOClient;

import de.sciss.net.OSCMessage;

public class JNetwork extends JInterface {

	private final String TAG = "JNetwork";

	public JNetwork(Activity a) {
		super(a);

	}

	// --------- download file ---------//
	interface downloadFileCB {
		void event(int eventType);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "url", "fileName", "function(progress)" })
	public void downloadFile(String url, String fileName, final downloadFileCB callbackfn) {

		NetworkUtils.DownloadTask downloadTask = new NetworkUtils.DownloadTask(a.get(), fileName);
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
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public String getIP() {
		return NetworkUtils.getLocalIpAddress(a.get());
	}

	// --------- OSC Server ---------//
	interface startOSCServerCB {
		void event(String string, JSONArray jsonArray);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "port", "function(jsonData)" })
	public OSC.Server startOSCServer(String port, final startOSCServerCB callbackfn) {
		OSC osc = new OSC();
		Server server = osc.new Server();

		server.addListener(new OSCServerListener() {

			@Override
			public void onMessage(OSCMessage msg) {
				MLog.d(TAG, "message received " + msg);

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
					MLog.d(TAG, msg.getName() + " " + jsonArray.toString(2));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// callback(callbackfn, "\"" + msg.getName() + "\"", str);
				callbackfn.event(msg.getName(), jsonArray);
			}

		});

		server.start(port);
		WhatIsRunning.getInstance().add(server);

		return server;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "address", "port" })
	public OSC.Client connectOSC(String address, int port) {
		OSC osc = new OSC();
		Client client = osc.new Client(address, port);
		WhatIsRunning.getInstance().add(client);

		return client;
	}

	// --------- webSocket Server ---------//
	interface startWebSocketServerCB {
		void event(String string, String string2, String arg1);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "port", "function(status, remoteAddress, jsonData)" })
	public WebSocketServer startWebsocketServer(int port, final startWebSocketServerCB callbackfn) {

		InetSocketAddress inetSocket = new InetSocketAddress(port);
		Draft d = new Draft_17();
		WebSocketServer websocketServer = new WebSocketServer(inetSocket, Collections.singletonList(d)) {

			@Override
			public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
				callbackfn.event("close", arg0.getRemoteSocketAddress().toString(), "");
			}

			@Override
			public void onError(WebSocket arg0, Exception arg1) {
				callbackfn.event("error", arg0.getRemoteSocketAddress().toString(), "");
			}

			@Override
			public void onMessage(WebSocket arg0, String arg1) {
				callbackfn.event("message", arg0.getRemoteSocketAddress().toString(), arg1);
			}

			@Override
			public void onOpen(WebSocket arg0, ClientHandshake arg1) {
				callbackfn.event("open", arg0.getRemoteSocketAddress().toString(), "");
			}
		};
		websocketServer.start();

		return websocketServer;

	}

	// --------- connect websocket ---------//
	interface connectWebsocketCB {
		void event(String string, String string2);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "uri", "function(type, data)" })
	public org.java_websocket.client.WebSocketClient connectWebsocket(String uri, final connectWebsocketCB callbackfn) {

		org.java_websocket.client.WebSocketClient webSocketClient = null;
		try {
			webSocketClient = new org.java_websocket.client.WebSocketClient(new URI(uri)) {

				@Override
				public void onOpen(ServerHandshake arg0) {
					callbackfn.event("open", "");
				}

				@Override
				public void onMessage(String arg0) {
					callbackfn.event("message", arg0);
				}

				@Override
				public void onError(Exception arg0) {
					callbackfn.event("error", "");
				}

				@Override
				public void onClose(int arg0, String arg1, boolean arg2) {
					callbackfn.event("close", "");
				}
			};
		} catch (URISyntaxException e) {
			callbackfn.event("error ", e.toString());
			e.printStackTrace();
		}
		return webSocketClient;
	}

	// --------- connectSocketIO ---------//
	interface connectSocketIOCB {
		void event(String string, String reason, String string2);

		void event(String string, String event, JSONArray arguments);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "uri", "function(type, data)" })
	public SocketIOClient connectSocketIO(String uri, final connectSocketIOCB callbackfn) {
		SocketIOClient socketIOClient = new SocketIOClient(URI.create(uri), new SocketIOClient.Handler() {

			@Override
			public void onMessage(String message) {

			}

			@Override
			public void onJSON(JSONObject json) {

			}

			@Override
			public void onError(Exception error) {
				callbackfn.event("error", "", "");
			}

			@Override
			public void onDisconnect(int code, String reason) {
				callbackfn.event("disconnect", reason, "");
			}

			@Override
			public void onConnect() {
				callbackfn.event("connected", "", "");

			}

			@Override
			public void on(String event, JSONArray arguments) {
				callbackfn.event("onmessage", event, arguments);

			}
		});

		return socketIOClient;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "url", "function(data)" })
	public void sendEmail() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NetworkUtils.sendEmail();
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
	interface getRequestCB {
		void event(int eventType, String responseString);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "url", "function(eventType, responseString)" })
	public void getRequest(String url, final getRequestCB callbackfn) {

		class RequestTask extends AsyncTask<String, String, String> {
			String responseString = null;

			@Override
			protected String doInBackground(String... uri) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response;
				try {
					response = httpclient.execute(new HttpGet(uri[0]));
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

					a.get().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							callbackfn.event(statusLine.getStatusCode(), responseString);
						}
					});

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

		MLog.d(TAG, "" + new RequestTask().execute(url));
	}

	// --------- Bluetooth ---------//
	private scanBTNetworksCB onBluetoothfn;
	private SimpleBT simpleBT;

	public interface onBluetoothListener {
		public void onDeviceFound(String name, String macAddress, float strength);

		public void onActivityResult(int requestCode, int resultCode, Intent data);
	}

	interface scanBTNetworksCB {
		void event(String name, String macAddress, float strength);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(name, macAddress, strength)" })
	public void scanBTNetworks(final scanBTNetworksCB callbackfn) {
		onBluetoothfn = callbackfn;

		simpleBT.scanBluetooth();

		simpleBT.addBluetoothScanListener(new onBluetoothListener() {

			@Override
			public void onDeviceFound(String name, String macAddress, float strength) {
				onBluetoothfn.event(name, macAddress, strength);
			}

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {

			}
		});

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(name, macAddress, strength)" })
	public SimpleBT startBluetooth() {
		simpleBT = new SimpleBT(a.get());
		simpleBT.start();
		(a.get()).addBluetoothListener(new onBluetoothListener() {

			@Override
			public void onDeviceFound(String name, String macAddress, float strength) {
			}

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				simpleBT.onActivityResult(requestCode, resultCode, data);

				switch (requestCode) {
				case SimpleBT.REQUEST_CONNECT_DEVICE:
					// When DeviceListActivity returns with a device to connect
					if (resultCode == Activity.RESULT_OK) {
						// Get the device MAC address
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						// Get the BLuetoothDevice object
						BluetoothDevice device = simpleBT.getAdapter().getRemoteDevice(address);
						// Attempt to connect to the device
						simpleBT.getSerialService().connect(device);
						MLog.d(TAG, "connected");
					}
					break;
				case SimpleBT.REQUEST_ENABLE_BT:
					// When the request to enable Bluetooth returns
					if (resultCode == Activity.RESULT_OK) {
						MLog.d(TAG, "enabling BT");
						// Bluetooth is now enabled, so set up a Bluetooth
						// session
						simpleBT.startBTService();
					} else {
						// User did not enable Bluetooth or an error occurred
						MLog.d(TAG, "BT not enabled");
						Toast.makeText(a.get().getApplicationContext(), "BT not enabled, leaving", Toast.LENGTH_SHORT)
								.show();

						// TODO show error
						// finish();
					}
				}
			}
		});

		WhatIsRunning.getInstance().add(simpleBT);
		return simpleBT;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(name, macAddress, strength)" })
	public void connectBluetoothByUI(final String callbackfn) {
		simpleBT.startDeviceListActivity();
	}

	// --------- connectBluetooth ---------//
	interface connectBluetoothCB {
		void event(String data);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(name, macAddress, strength)" })
	public void connectBluetoothByMac(String mac, final connectBluetoothCB callbackfn) {
		simpleBT.connectByMac(mac);
		simpleBT.addListener(new SimpleBTListener() {

			@Override
			public void onRawDataReceived(byte[] buffer, int size) {
			}

			@Override
			public void onMessageReceived(final String data) {
				if (data != "") {
					a.get().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MLog.d(TAG, "Got data: " + data);
							callbackfn.event(data);
						}
					});
				}
			}

			@Override
			public void onConnected() {
			}
		});
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name, function()" })
	public void connectBluetoothByName(String name, final String callbackfn) {
		simpleBT.connectByName(name);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "string" })
	public void sendBluetoothSerial(String string) {
		simpleBT.send(string);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void disconnectBluetooth() {
		simpleBT.disconnect();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void disableBluetooth() {
		simpleBT.disable();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void enableBluetooth() {
		simpleBT.start();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void enableWifi(boolean enabled) {
		WifiManager wifiManager = (WifiManager) a.get().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = {})
	public boolean isWifiEnabled() {
		WifiManager wifiManager = (WifiManager) a.get().getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}

	// http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "ssidName", "type", "password" })
	public void connectWifi(String networkSSID, String type, String networkPass) {

		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes. String
												// should contain ssid in quotes

		if (type.equals("wep")) {
			// wep
			conf.wepKeys[0] = "\"" + networkPass + "\"";
			conf.wepTxKeyIndex = 0;
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		} else if (type.equals("wpa")) {
			// wpa
			conf.preSharedKey = "\"" + networkPass + "\"";
		} else if (type.equals("open")) {
			// open
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		}

		WifiManager wifiManager = (WifiManager) a.get().getSystemService(Context.WIFI_SERVICE);
		wifiManager.addNetwork(conf);

		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration i : list) {
			if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
				wifiManager.disconnect();
				wifiManager.enableNetwork(i.networkId, true);
				wifiManager.reconnect();

				break;
			}
		}

	}

	private Object mIsWifiAPEnabled = true;

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean, apName" })
	public void wifiAP(boolean enabled, String wifiName) {

		WifiManager wifi = (WifiManager) a.get().getSystemService(a.get().WIFI_SERVICE);
		Method[] wmMethods = wifi.getClass().getDeclaredMethods();
		Log.d(TAG, "enableMobileAP methods " + wmMethods.length);
		for (Method method : wmMethods) {
			Log.d(TAG, "enableMobileAP method.getName() " + method.getName());
			if (method.getName().equals("setWifiApEnabled")) {
				WifiConfiguration netConfig = new WifiConfiguration();
				netConfig.SSID = wifiName;
				netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
				netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

				//
				try {
					Log.d(TAG, "enableMobileAP try: ");
					method.invoke(wifi, netConfig, enabled);
					if (netConfig.wepKeys != null && netConfig.wepKeys.length >= 1) {
						Log.d(TAG, "enableMobileAP key : " + netConfig.wepKeys[0]);
					}
					Log.d(TAG, "enableMobileAP enabled: ");
					mIsWifiAPEnabled = enabled;
				} catch (Exception e) {
					Log.e(TAG, "enableMobileAP failed: ", e);
				}
			}
		}
	}

}