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

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardBackground;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardButton;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardCustomWidget;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardHTML;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardImage;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardInput;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardPlot;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardSlider;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardText;
import org.protocoderrunner.apprunner.api.dashboard.PDashboardVideoCamera;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.utils.StrUtils;

import java.net.UnknownHostException;

public class PDashboard extends PInterface {

	String TAG = "PDashboard";

	public PDashboard(Context a) {
		super(a);
    }

	@ProtocoderScript
	@APIMethod(description = "add a plot in the dashboad", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "minLimit", "maxLimit" })
	public PDashboardPlot addPlot(String name, int x, int y, int w, int h, float minLimit, float maxLimit)
			throws UnknownHostException, JSONException {

		PDashboardPlot pWebAppPlot = new PDashboardPlot(getContext());
		pWebAppPlot.add(name, x, y, w, h, minLimit, maxLimit);

		return pWebAppPlot;
	}

	@ProtocoderScript
	@APIMethod(description = "add a HTML content in the dashboard", example = "")
	@APIParam(params = { "htmlFile", "x", "y" })
	public PDashboardHTML addHtml(String html, int x, int y) throws UnknownHostException, JSONException {

		PDashboardHTML pWebAppHTML = new PDashboardHTML(getContext());
		pWebAppHTML.add(html, x, y);

		return pWebAppHTML;
	}

	@ProtocoderScript
	@APIMethod(description = "add a button in the dashboard", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "function()" })
	public PDashboardButton addButton(String name, int x, int y, int w, int h,
			final PDashboardButton.jDashboardAddCB callbackfn) throws UnknownHostException, JSONException {

		PDashboardButton pWebAppButton = new PDashboardButton(getContext());
		pWebAppButton.add(name, x, y, w, h, callbackfn);

		return pWebAppButton;
	}

	@ProtocoderScript
	@APIMethod(description = "add a slider in the dashboard", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "min", "max", "function(num)" })
	public PDashboardSlider addSlider(String name, int x, int y, int w, int h, int min, int max,
			final PDashboardSlider.jDashboardSliderAddCB callbackfn) throws UnknownHostException, JSONException {

		PDashboardSlider pWebAppSlider = new PDashboardSlider(getContext());
		pWebAppSlider.add(name, x, y, w, h, min, max, callbackfn);

		return pWebAppSlider;
	}

	@ProtocoderScript
	@APIMethod(description = "add a input box in the dashboard", example = "")
	@APIParam(params = { "name", "x", "y", "w", "h", "function(text)" })
	public PDashboardInput addInput(String name, int x, int y, int w, int h, final PDashboardInput.jDashboardInputCB callbackfn)
			throws UnknownHostException, JSONException {

		PDashboardInput pWebAppInput = new PDashboardInput(getContext());
		pWebAppInput.add(name, x, y, w, h, callbackfn);

		return pWebAppInput;
	}

	@ProtocoderScript
	@APIMethod(description = "add a text in the dashboard", example = "")
	@APIParam(params = { "name", "x", "y", "size", "hexColor" })
	public PDashboardText addText(String name, int x, int y, int width, int height, int size, String color)
			throws UnknownHostException, JSONException {

		PDashboardText pWebAppText = new PDashboardText(getContext());
		pWebAppText.add(name, x, y, width, height, size, color);

		return pWebAppText;
	}

	@ProtocoderScript
	@APIMethod(description = "add an image in the dashboard", example = "")
	@APIParam(params = { "url", "x", "y", "w", "h" })
	public PDashboardImage addImage(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {

		PDashboardImage pWebAppImage = new PDashboardImage(getContext());
		pWebAppImage.add(url, x, y, w, h);

		return pWebAppImage;
	}

	@ProtocoderScript
	@APIMethod(description = "add a camera preview in the dashboard", example = "")
	@APIParam(params = { "url", "x", "y", "w", "h" })
	public PDashboardVideoCamera addCameraPreview(int x, int y, int w, int h) throws UnknownHostException, JSONException {

		PDashboardVideoCamera pWebAppVideoCamera = new PDashboardVideoCamera(getContext());
		pWebAppVideoCamera.add(x, y, w, h);

		return pWebAppVideoCamera;
	}

	@ProtocoderScript
	@APIMethod(description = "add a custom widget in the dashboard", example = "")
	@APIParam(params = { "url", "x", "y", "w", "h", "function(obj)" })
	public PDashboardCustomWidget addCustomWidget(String url, int x, int y, int w, int h, PDashboardCustomWidget.jDashboardAddCB callback) throws UnknownHostException, JSONException {

		PDashboardCustomWidget pWebAppCustom = new PDashboardCustomWidget(getContext());
		pWebAppCustom.add(url, x, y, w, h, callback);

		return pWebAppCustom;
	}

	@ProtocoderScript
	@APIMethod(description = "change the background color (HEX format) of the dashboard", example = "")
	@APIParam(params = { "hexColor" })
	public PDashboardBackground setBackgroundColor(String hex) throws JSONException, UnknownHostException {
        PDashboardBackground pDashboardBackground = new PDashboardBackground(getContext());
        pDashboardBackground.updateColor(hex);

        return pDashboardBackground;
	}

    private void initKeyEvents(final jDashboardKeyPressed callbackfn) throws UnknownHostException, JSONException {

        String id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "keyevent")
                .put("enabled", true);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
        CustomWebsocketServer.getInstance(getContext()).addListener(id, new CustomWebsocketServer.WebSocketListener() {

            @Override
            public void onUpdated(final JSONObject jsonObject) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            callbackfn.event(jsonObject.getInt("val"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    // --------- JDashboard add ---------//
    public interface jDashboardKeyPressed {
        void event(int val);
    }

    @ProtocoderScript
    @APIMethod(description = "show/hide the dashboard", example = "")
    @APIParam(params = { "boolean" })
    public void onKeyPressed(jDashboardKeyPressed callback) throws UnknownHostException, JSONException {
        initKeyEvents(callback);
    }

	@ProtocoderScript
	@APIMethod(description = "show/hide the dashboard", example = "")
	@APIParam(params = { "boolean" })
	public void show(boolean b) {
		JSONObject msg = new JSONObject();
		try {
			JSONObject values = new JSONObject().put("val", b);
            msg.put("type", "widget").put("action", "showDashboard").put("values", values);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			CustomWebsocketServer.getInstance(getContext()).send(msg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
