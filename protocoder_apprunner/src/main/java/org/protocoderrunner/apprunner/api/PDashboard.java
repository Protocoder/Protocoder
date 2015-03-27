/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.apprunner.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
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


    @ProtoMethod(description = "add a plot in the dashboad", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "minLimit", "maxLimit"})
    public PDashboardPlot addPlot(String name, int x, int y, int w, int h, float minLimit, float maxLimit)
            throws UnknownHostException, JSONException {

        PDashboardPlot pWebAppPlot = new PDashboardPlot(getContext());
        pWebAppPlot.add(name, x, y, w, h, minLimit, maxLimit);

        return pWebAppPlot;
    }


    @ProtoMethod(description = "add a HTML content in the dashboard", example = "")
    @ProtoMethodParam(params = {"htmlFile", "x", "y"})
    public PDashboardHTML addHtml(String html, int x, int y) throws UnknownHostException, JSONException {

        PDashboardHTML pWebAppHTML = new PDashboardHTML(getContext());
        pWebAppHTML.add(html, x, y);

        return pWebAppHTML;
    }


    @ProtoMethod(description = "add a button in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "function()"})
    public PDashboardButton addButton(String name, int x, int y, int w, int h) throws UnknownHostException, JSONException {

        PDashboardButton pWebAppButton = new PDashboardButton(getContext());
        pWebAppButton.add(name, x, y, w, h);

        return pWebAppButton;
    }


    @ProtoMethod(description = "add a slider in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "min", "max", "function(num)"})
    public PDashboardSlider addSlider(String name, int x, int y, int w, int h, int min, int max) throws UnknownHostException, JSONException {

        PDashboardSlider pWebAppSlider = new PDashboardSlider(getContext());
        pWebAppSlider.add(name, x, y, w, h, min, max);

        return pWebAppSlider;
    }


    @ProtoMethod(description = "add a input box in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "w", "h", "function(text)"})
    public PDashboardInput addInput(String name, int x, int y, int w, int h)
            throws UnknownHostException, JSONException {

        PDashboardInput pWebAppInput = new PDashboardInput(getContext());
        pWebAppInput.add(name, x, y, w, h);

        return pWebAppInput;
    }


    @ProtoMethod(description = "add a text in the dashboard", example = "")
    @ProtoMethodParam(params = {"name", "x", "y", "size", "hexColor"})
    public PDashboardText addText(String name, int x, int y, int width, int height, int size, String color)
            throws UnknownHostException, JSONException {

        PDashboardText pWebAppText = new PDashboardText(getContext());
        pWebAppText.add(name, x, y, width, height, size, color);

        return pWebAppText;
    }


    @ProtoMethod(description = "add an image in the dashboard", example = "")
    @ProtoMethodParam(params = {"url", "x", "y", "w", "h"})
    public PDashboardImage addImage(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {

        PDashboardImage pWebAppImage = new PDashboardImage(getContext());
        pWebAppImage.add(url, x, y, w, h);

        return pWebAppImage;
    }


    @ProtoMethod(description = "add a camera preview in the dashboard", example = "")
    @ProtoMethodParam(params = {"url", "x", "y", "w", "h"})
    public PDashboardVideoCamera addCameraPreview(int x, int y, int w, int h) throws UnknownHostException, JSONException {

        PDashboardVideoCamera pWebAppVideoCamera = new PDashboardVideoCamera(getContext());
        pWebAppVideoCamera.add(x, y, w, h);

        return pWebAppVideoCamera;
    }


    @ProtoMethod(description = "add a custom widget in the dashboard", example = "")
    @ProtoMethodParam(params = {"url", "x", "y", "w", "h", "function(obj)"})
    public PDashboardCustomWidget addCustomWidget(String url, int x, int y, int w, int h, PDashboardCustomWidget.jDashboardAddCB callback) throws UnknownHostException, JSONException {

        PDashboardCustomWidget pWebAppCustom = new PDashboardCustomWidget(getContext());
        pWebAppCustom.add(url, x, y, w, h, callback);

        return pWebAppCustom;
    }


    @ProtoMethod(description = "change the background color (HEX format) of the dashboard", example = "")
    @ProtoMethodParam(params = {"hexColor"})
    public PDashboardBackground backgroundColor(String hex) throws JSONException, UnknownHostException {
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


    @ProtoMethod(description = "show/hide the dashboard", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void onKeyPressed(jDashboardKeyPressed callback) throws UnknownHostException, JSONException {
        initKeyEvents(callback);
    }


    @ProtoMethod(description = "show/hide the dashboard", example = "")
    @ProtoMethodParam(params = {"boolean"})
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
