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

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardButton;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardHTML;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardImage;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardInput;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardLabel;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardPlot;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardSlider;
import org.protocoderrunner.network.CustomWebsocketServer;

import android.app.Activity;

public class PDashboard extends PInterface {

	String TAG = "JDashboard";

	public PDashboard(Activity a) {
		super(a);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "minLimit", "maxLimit" })
	public PDashboardPlot addPlot(String name, int x, int y, int w, int h, float minLimit, float maxLimit)
			throws UnknownHostException, JSONException {

		PDashboardPlot jWebAppPlot = new PDashboardPlot(a.get());
		jWebAppPlot.add(name, x, y, w, h, minLimit, maxLimit);

		return jWebAppPlot;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "html", "x", "y" })
	public PDashboardHTML addHTML(String html, int x, int y) throws UnknownHostException, JSONException {

		PDashboardHTML jWebAppHTML = new PDashboardHTML(a.get());
		jWebAppHTML.add(html, x, y);

		return jWebAppHTML;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "function()" })
	public PDashboardButton addButton(String name, int x, int y, int w, int h,
			final PDashboardButton.jDashboardAddCB callbackfn) throws UnknownHostException, JSONException {
		// MLog.d(TAG, "callback " + callbackfn);

		PDashboardButton jWebAppButton = new PDashboardButton(a.get());
		jWebAppButton.add(name, x, y, w, h, callbackfn);

		return jWebAppButton;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "min", "max", "callback(num)" })
	public PDashboardSlider addSlider(String name, int x, int y, int w, int h, int min, int max,
			final PDashboardSlider.jDashboardSliderAddCB callbackfn) throws UnknownHostException, JSONException {
		// MLog.d(TAG, "callback " + callbackfn);

		PDashboardSlider jWebAppSlider = new PDashboardSlider(a.get());
		jWebAppSlider.add(name, x, y, w, h, min, max, callbackfn);

		return jWebAppSlider;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "callback(text)" })
	public PDashboardInput addInput(String name, int x, int y, int w, int h, final PDashboardInput.jDashboardInputCB callbackfn)
			throws UnknownHostException, JSONException {
		// MLog.d(TAG, "callback " + callbackfn);

		PDashboardInput jWebAppInput = new PDashboardInput(a.get());
		jWebAppInput.add(name, x, y, w, h, callbackfn);

		return jWebAppInput;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "name", "x", "y", "size", "hexColor" })
	public PDashboardLabel addLabel(String name, int x, int y, int width, int height, int size, String color)
			throws UnknownHostException, JSONException {

		PDashboardLabel jWebAppLabel = new PDashboardLabel(a.get());
		jWebAppLabel.add(name, x, y, width, height, size, color);

		return jWebAppLabel;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "url", "x", "y", "w", "h" })
	public PDashboardImage addImage(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {

		PDashboardImage jWebAppImage = new PDashboardImage(a.get());
		jWebAppImage.add(url, x, y, w, h);

		return jWebAppImage;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "r", "g", "b", "alpha" })
	public void setBackgroundColor(int r, int g, int b, float alpha) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "setBackgroundColor");

			JSONObject values = new JSONObject();
			values.put("r", r);
			values.put("g", g);
			values.put("b", b);
			values.put("alpha", alpha);
			msg.put("values", values);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void show(boolean b) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("type", "widget");
			msg.put("action", "showDashboard");

			JSONObject values = new JSONObject();
			values.put("val", b);
			msg.put("values", values);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			CustomWebsocketServer ws = CustomWebsocketServer.getInstance(a.get());
			ws.send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
}
