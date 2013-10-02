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
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.codebutler.android_websockets.SocketIOClient;
import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.network.OSC;
import com.makewithmoto.network.OSC.Client;
import com.makewithmoto.network.OSC.OSCServerListener;
import com.makewithmoto.network.OSC.Server;

import de.sciss.net.OSCMessage;

public class JNetwork extends JInterface {

	private String TAG = "JNetwork";

	private String callbackfn;

	public JNetwork(Activity a) {
		super(a);

	}

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public OSC.Server startOSCServer(String port, final String callbackfn) { 
		OSC osc = new OSC();
		Server server = osc.new Server();
		
		server.addListener(new OSCServerListener() {

			@Override
			public void onMessage(OSCMessage msg) {
				//callback(callbackfn, "\"" + msg.getName() + "\"");	
				callback(callbackfn, msg);	
			}		

		});

		return server;
	}
	
	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public OSC.Client connectOSC(String uri) { 
		OSC osc = new OSC();
		Client client = osc.new Client();

		return client;
	}
	
	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public SocketIOClient connectSocketIO(String uri, final String callbackfn) { 
		SocketIOClient socketIOClient = new SocketIOClient(URI.create(uri), new SocketIOClient.Handler() {
			
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
				callback(callbackfn, "disconnect", reason);				
			}
			
			@Override
			public void onConnect() {
				callback(callbackfn, "connected");
				
			}
			
			@Override
			public void on(String event, JSONArray arguments) {
				callback(callbackfn, "onmessage", event, arguments);
				
			}
		});
		
		return socketIOClient;
	} 
	
	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
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
					callback(callbackfn, statusLine.getStatusCode(),  "\"" + responseString + "\"");
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

	// previous callback callback("OnSerialRead("+receivedData+");");
	// callback(callbackfn, "\"" + receivedData + "\"");

}