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

package org.protocoderrunner.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.events.Events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class PConsole extends ProtoBase {

    private final Gson gson;
    private boolean showTime = false;

    public PConsole(AppRunner appRunner) {
        super(appRunner);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
        gsonBuilder.setPrettyPrinting();

        gson = gsonBuilder.create();
    }

    @ProtoMethod(description = "shows any HTML text in the webIde console", example = "")
    @ProtoMethodParam(params = {"text", "text", "..."})
    public void log(Object... outputs) {

        StringBuilder builder = new StringBuilder();
        builder.append(getCurrentTime());

        for (Object output : outputs) {
            // format the objects to json output
            String out = gson.toJson(output);
            builder.append(" ").append(out);
        }
        String log = builder.toString();

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("val", log);
            msg = new JSONObject().put("type", "console").put("action", "log").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);

        EventBus.getDefault().post(new Events.LogEvent(log));
    }

    @ProtoMethod(description = "clear the webIde console", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {
        JSONObject msg = null;
        try {
            msg = new JSONObject().put("type", "console").put("action", "clear");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(msg);
    }

    @ProtoMethod(description = "show/hide the console", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void show(boolean b) {
        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("val", b);
            msg = new JSONObject().put("type", "console").put("action", "show").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(msg);
    }

    @ProtoMethod(description = "Change the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void backgroundColor(String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("color", color);
            msg = new JSONObject().put("type", "console").put("action", "backgroundColor").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);
    }

    @ProtoMethod(description = "Log using a defined colorHex", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void logC(String text, String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        text = getCurrentTime() + " " + text;
        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("val", text).put("color", color);
            msg = new JSONObject().put("type", "console").put("action", "logC").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);
    }

    @ProtoMethod(description = "Changes the console text size", example = "")
    @ProtoMethodParam(params = {"size"})
    public void textSize(int textSize) {

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textSize", textSize + "px");
            msg = new JSONObject().put("type", "console").put("action", "textSize").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);
    }

    @ProtoMethod(description = "Changes the console text color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public void textColor(String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textColor", color);
            msg = new JSONObject().put("type", "console").put("action", "textColor").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);
    }

    @ProtoMethod(description = "Enable/Disable time in the log", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void showTime(boolean b) {
        showTime = b;
    }

    //TODO migrate to events
    private void send(JSONObject msg) {
        /*
        try {
            CustomWebsocketServer.getInstance(getContext()).send(msg);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        */
    }

    private String getCurrentTime() {
        String time = "";

        if (showTime) {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            time = sdf.format(cal.getTime()).toString();
        }

        return time;
    }

    @Override
    public void __stop() {

    }
}

