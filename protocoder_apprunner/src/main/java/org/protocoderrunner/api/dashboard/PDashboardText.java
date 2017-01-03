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

public class PDashboardText extends ProtoBase {

    private static final String TAG = "PDashboardText";
    String id;

    public PDashboardText(AppRunner appRunner) {
        super(appRunner);
    }

    /**
     *
     * @description Hola que tal
     * estamos por aqui
     *
     */
    public void add(String name, int x, int y, int width, int height, int size, String color)
            throws UnknownHostException, JSONException {
        this.id = StrUtils.generateRandomString();

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("type", "label")
                .put("x", x)
                .put("y", y)
                .put("w", width)
                .put("h", height)
                .put("size", size)
                .put("color", color);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "add")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }


    @ProtoMethod(description = "change the text", example = "")
    @ProtoMethodParam(params = {"text"})
    public void setText(String text) throws UnknownHostException, JSONException {

        JSONObject values = new JSONObject()
                .put("id", id)
                .put("type", "label")
                .put("val", text);

        JSONObject msg = new JSONObject()
                .put("type", "widget")
                .put("action", "setLabelText")
                .put("values", values);

        //TODO change to events
        //CustomWebsocketServer.getInstance(getContext()).send(msg);
    }

    @Override
    public void __stop() {

    }
}
