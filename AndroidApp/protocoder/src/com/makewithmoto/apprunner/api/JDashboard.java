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

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.JavascriptInterface;
import com.makewithmoto.apprunner.api.dashbwidgets.JWebAppButton;
import com.makewithmoto.apprunner.api.dashbwidgets.JWebAppHTML;
import com.makewithmoto.apprunner.api.dashbwidgets.JWebAppImage;
import com.makewithmoto.apprunner.api.dashbwidgets.JWebAppLabel;
import com.makewithmoto.apprunner.api.dashbwidgets.JWebAppPlot;
import com.makewithmoto.network.CustomWebsocketServer;
import com.makewithmoto.network.CustomWebsocketServer.WebSocketListener;
import com.makewithmoto.utils.StrUtils;

public class JDashboard extends JInterface {

	String TAG = "JDashboard";

	public JDashboard(Activity a) {
        super(a); 
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public JWebAppPlot addPlot(String name, int x, int y, int w, int h, float minLimit, float maxLimit) throws UnknownHostException, JSONException {
		
		JWebAppPlot jWebAppPlot = new JWebAppPlot(a.get());
		jWebAppPlot.add(StrUtils.generateRandomString(), name, x, y, w, h, minLimit, maxLimit);
		
		return jWebAppPlot;
	}
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public JWebAppHTML addHTML(String html, int posx, int posy) throws UnknownHostException, JSONException {
		
		JWebAppHTML jWebAppHTML = new JWebAppHTML(a.get());
		jWebAppHTML.add(StrUtils.generateRandomString(), html, posx, posy);
		
		
		return jWebAppHTML;
	}
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public JWebAppButton addButton(String name, int x, int y, int w, int h, final String callbackfn) throws UnknownHostException, JSONException {
		Log.d(TAG, "callback " + callbackfn);
		
		String id = StrUtils.generateRandomString();
		JWebAppButton jWebAppButton = new JWebAppButton(a.get());
		jWebAppButton.add(id, name, x, y, w, h);
		
		CustomWebsocketServer.getInstance(a.get()).addListener(id, new WebSocketListener() {
			
			@Override
			public void onUpdated(JSONObject jsonObject) {
				callback(callbackfn);
			}
		});
		

		return jWebAppButton;
	}
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public JWebAppLabel addLabel(String name, int x, int y, int size, String color) throws UnknownHostException, JSONException {
		String id = StrUtils.generateRandomString();

		JWebAppLabel jWebAppLabel = new JWebAppLabel(a.get());
		jWebAppLabel.add(id, name, x, y, size, color);
				
		return jWebAppLabel;
	}
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public JWebAppImage addImage(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {
		String id = StrUtils.generateRandomString();

		JWebAppImage jWebAppImage = new JWebAppImage(a.get());
		jWebAppImage.add(id, url, x, y, w, h);
		
		return jWebAppImage;
	}
	
	

	@JavascriptInterface
	@APIMethod(description = "", example = "")
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

	@JavascriptInterface
	@APIMethod(description = "", example = "")
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
