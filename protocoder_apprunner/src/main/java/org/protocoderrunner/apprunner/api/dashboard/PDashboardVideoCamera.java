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
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.utils.StrUtils;

import java.net.UnknownHostException;

public class PDashboardVideoCamera extends PInterface {

    private static final String TAG = "PDashboardVideoCamera";
    String id;


    //TODO this is just mContext scaffold needs to be implemented
    public PDashboardVideoCamera(Context a) {
        super(a);
    }


    @ProtoMethod(description = "", example = "")
    public void add(int x, int y, int w, int h) throws UnknownHostException, JSONException {
        this.id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "camera")
                .put("x", x)
                .put("y", y)
                .put("w", w)
                .put("h", h);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);

    }


    @ProtoMethod(description = "", example = "")
    public void update(String encodedImage) throws JSONException, UnknownHostException {
        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "widget")
                .put("src", encodedImage);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "updateCamera")
                .put("values", values);

        CustomWebsocketServer.getInstance(getContext()).send(msg);
    }
}
