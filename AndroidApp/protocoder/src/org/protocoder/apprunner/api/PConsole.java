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

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.PInterface;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.network.CustomWebsocketServer;

import android.support.v4.app.FragmentActivity;

public class PConsole extends PInterface {

	String TAG = "JConsole";

	public PConsole(FragmentActivity mwmActivity) {
		super(mwmActivity);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text" })
	public void log(String... outputs) throws JSONException, UnknownHostException {

		StringBuilder builder = new StringBuilder();

		for (String output : outputs) {
			builder.append(" ");
			builder.append(output);
		}

		JSONObject msg = new JSONObject();
		msg.put("type", "console");
		msg.put("action", "log");
		JSONObject values = new JSONObject();
		values.put("val", builder.toString());
		msg.put("values", values);

		CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
		ws.send(msg);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text" })
	public void log(String output) throws JSONException, UnknownHostException {
		JSONObject msg = new JSONObject();
		msg.put("type", "console");
		msg.put("action", "log");
		JSONObject values = new JSONObject();
		values.put("val", output);
		msg.put("values", values);

		CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
		ws.send(msg);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "" })
	public void clear() throws JSONException, UnknownHostException {
		JSONObject msg = new JSONObject();
		msg.put("type", "console");
		msg.put("action", "clear");

		CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
		ws.send(msg);
	}

}
