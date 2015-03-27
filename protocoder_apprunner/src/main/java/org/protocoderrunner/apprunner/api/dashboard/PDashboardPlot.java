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

package org.protocoderrunner.apprunner.api.dashboard;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.utils.StrUtils;

import java.net.UnknownHostException;

public class PDashboardPlot extends PInterface {

    private static final String TAG = "PDashboardPlot";
    String id;

    public PDashboardPlot(Context a) {
        super(a);
    }

    public void add(String name, int x, int y, int w, int h, float minLimit, float maxLimit)
            throws UnknownHostException, JSONException {
        this.id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("type", "plot")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h)
                .put("minLimit", minLimit)
                .put("maxLimit", maxLimit);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
    }


    @ProtoMethod(description = "update the plot with a given value", example = "")
    @ProtoMethodParam(params = {"value"})
    public void update(float val) throws UnknownHostException, JSONException {

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "plot")
                .put("val", val);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "update")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
    }
}
