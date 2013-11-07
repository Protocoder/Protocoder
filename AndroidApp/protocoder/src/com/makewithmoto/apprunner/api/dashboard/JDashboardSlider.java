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

package com.makewithmoto.apprunner.api.dashboard;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apprunner.JInterface;
import com.makewithmoto.apprunner.JavascriptInterface;
import com.makewithmoto.network.CustomWebsocketServer;
import com.makewithmoto.network.CustomWebsocketServer.WebSocketListener;
import com.makewithmoto.utils.StrUtils;

public class JDashboardSlider extends JInterface {

	private static final String TAG = "JDashboardSlider";
	String id;

	public JDashboardSlider(Activity a) {
		super(a);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void add(String name, int x, int y, int w, int h, int min, int max,
			final String callbackfn) throws UnknownHostException, JSONException {
		this.id = StrUtils.generateRandomString();
		JSONObject msg = new JSONObject();

		msg.put("type", "widget");
		msg.put("action", "add");

		JSONObject values = new JSONObject();
		values.put("id", id);
		values.put("name", name);
		values.put("type", "slider");
		values.put("x", x);
		values.put("y", y);
		values.put("w", w);
		values.put("h", h);
		values.put("min", min);
		values.put("max", max);

		msg.put("values", values);

		CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
		ws.send(msg);

		CustomWebsocketServer.getInstance(a.get()).addListener(id,
				new WebSocketListener() {
					@Override
					public void onUpdated(JSONObject jsonObject) {
						try {
							Log.d(TAG, "" + jsonObject.toString(2));
							double val = jsonObject.getDouble("val");
							Log.d(TAG, "" + val);
							callback(callbackfn, val);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void setPosition(float position) throws UnknownHostException,
			JSONException {
		JSONObject msg = new JSONObject();

		msg.put("type", "widget");
		msg.put("action", "setPosition");

		JSONObject values = new JSONObject();
		values.put("id", id);
		values.put("type", "label");
		values.put("val", position);
		msg.put("values", values);

		CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
		ws.send(msg);

	}
}
