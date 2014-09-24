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

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.network.CustomWebsocketServer;

import java.net.UnknownHostException;

public class PConsole extends PInterface {

	String TAG = "PConsole";

	public PConsole(Activity a) {
		super(a);
	}

	@ProtocoderScript
	@APIMethod(description = "shows any HTML text in the webIde console", example = "")
	@APIParam(params = { "text","text","..." })
	public void log(String... outputs) throws JSONException, UnknownHostException {

		StringBuilder builder = new StringBuilder();

		for (String output : outputs) {
			builder.append(" ").append(output);
		}

		JSONObject values = new JSONObject().put("val", builder.toString());
        JSONObject msg = new JSONObject().put("type", "console").put("action", "log").put("values", values);

		CustomWebsocketServer.getInstance(a.get()).send(msg);
	}

	@ProtocoderScript
	@APIMethod(description = "clear the webIde console", example = "")
	@APIParam(params = { "" })
	public void clear() throws JSONException, UnknownHostException {
		JSONObject msg = new JSONObject().put("type", "console").put("action", "clear");

		CustomWebsocketServer.getInstance(a.get()).send(msg);
	}

    //TODO
    public void show(boolean b) {

    }

    //TODO
    public void size(int textSize) {

    }
}

