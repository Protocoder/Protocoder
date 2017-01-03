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

package org.protocoderrunner.api.dashboard;

import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.api.ProtoBase;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.StrUtils;

import java.net.UnknownHostException;

public class PDashboardImage extends ProtoBase {

    private static final String TAG = "PDashboardImage";
    String id;

    public PDashboardImage(AppRunner appRunner) {
        super(appRunner);
    }


    @ProtoMethod(description = "", example = "")
    public void add(String url, int x, int y, int w, int h) throws UnknownHostException, JSONException {
        this.id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("url", url)
                .put("type", "image")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }


    @ProtoMethod(description = "change image with a provided url", example = "")
    @ProtoMethodParam(params = {"url"})
    public void changeImage(String url) throws JSONException, UnknownHostException {

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "label")
                .put("url", url);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "changeImage")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    @Override
    public void __stop() {

    }
}
