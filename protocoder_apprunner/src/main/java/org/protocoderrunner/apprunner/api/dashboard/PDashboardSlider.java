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

package org.protocoderrunner.apprunner.api.dashboard;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.network.CustomWebsocketServer.WebSocketListener;
import org.protocoderrunner.utils.StrUtils;

import java.net.UnknownHostException;

public class PDashboardSlider extends PInterface {

    private static final String TAG = "PDashboardSlider";
    String id;

    public PDashboardSlider(Context a) {
        super(a);
    }

    // --------- JDashboardSlider add ---------//
    public interface jDashboardSliderAddCB {
        void event(double val);
    }

    public void add(String name, int x, int y, int w, int h, int min, int max)
            throws UnknownHostException, JSONException {
        this.id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("type", "slider")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h)
                .put("min", min)
                .put("max", max);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    public void onClick(final jDashboardSliderAddCB callbackfn) throws UnknownHostException {
        CustomWebsocketServer.getInstance(getContext()).addListener(id, new WebSocketListener() {
            @Override
            public void onUpdated(JSONObject jsonObject) {
                try {
                    final double val = jsonObject.getDouble("val");
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            callbackfn.event(val);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //TODO this method doesnt work yet
    //
    //@APIMethod(description = "change the slider value", example = "")
    //@APIParam(params = { "value" })
    public void position(float position) throws UnknownHostException, JSONException {
        JSONObject msg = new JSONObject();

        msg.put("type", "widget");
        msg.put("action", "setValue");

        JSONObject values = new JSONObject();
        values.put("id", id);
        values.put("type", "label");
        values.put("val", position);
        msg.put("values", values);

        CustomWebsocketServer ws = CustomWebsocketServer.getInstance(getContext());
        ws.send(msg);

    }
}
