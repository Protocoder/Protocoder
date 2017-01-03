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
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apidoc.annotation.ProtoObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.AppRunnerInterpreter;
import org.protocoderrunner.events.Events;

import java.text.SimpleDateFormat;
import java.util.Date;

@ProtoObject
public class PConsole extends ProtoBase {

    private final Gson gson;
    private boolean showTime = false;
    private SimpleDateFormat s = new SimpleDateFormat("hh:mm:ss");


    public PConsole(AppRunner appRunner) {
        super(appRunner);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
        gsonBuilder.setPrettyPrinting();

        gson = gsonBuilder.create();
    }

    @ProtoMethod(description = "shows any HTML text in the webIde console", example = "")
    @ProtoMethodParam(params = {"text", "text", "..."})
    public PConsole log(Object... outputs) {
        base_log("log", outputs);
        return this;
    }

    @ProtoMethod(description = "shows any HTML text in the webIde console marked as error", example = "")
    @ProtoMethodParam(params = {"text", "text", "..."})
    public PConsole error(Object outputs) {
        base_log("log_error", outputs);

        return this;
    }

    public PConsole p_error(int type, Object outputs) {

        switch (type) {
            case AppRunnerInterpreter.RESULT_ERROR:
                base_log("log_error", outputs);

                break;
            case AppRunnerInterpreter.RESULT_PERMISSION_ERROR:
                base_log("log_permission_error", outputs);

                break;
        }

        return this;
    }



    @ProtoMethod(description = "clear the console", example = "")
    @ProtoMethodParam(params = {""})
    public PConsole clear() {
        send("clear", "");
        return this;
    }

    @ProtoMethod(description = "show/hide the console", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PConsole show(boolean b) {
        if (b) send("show", "");
        else send("hide", "");

        return this;
    }


    /*
    @ProtoMethod(description = "Change the background color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PConsole backgroundColor(String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("color", color);
            msg = new JSONObject().put("type", "console").put("action", "backgroundColor").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Log using a defined colorHex", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PConsole logC(String text, String colorHex) {
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

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Changes the console text size", example = "")
    @ProtoMethodParam(params = {"size"})
    public PConsole textSize(int textSize) {

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textSize", textSize + "px");
            msg = new JSONObject().put("type", "console").put("action", "textSize").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Changes the console text color", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PConsole textColor(String colorHex) {
        String color = AndroidUtils.colorHexToHtmlRgba(colorHex);

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textColor", color);
            msg = new JSONObject().put("type", "console").put("action", "textColor").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // send(msg);

        return this;
    }

    @ProtoMethod(description = "Enable/Disable time in the log", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PConsole showTime(boolean b) {
        showTime = b;

        return this;
    }
    */

    private void base_log(String action, Object... outputs) {
        StringBuilder builder = new StringBuilder();
        if (showTime) builder.append(getCurrentTime());
        for (Object output : outputs) {
            // format the objects to json output if the object is a ReturnObject
            String out = "";
            if (output instanceof ReturnObject)out = gson.toJson(output);
            else out = output.toString();
            builder.append(" ").append(out);
        }
        String log = builder.toString();

        send(action, log);
    }

    private void send(String action, String data) {
        EventBus.getDefault().postSticky(new Events.LogEvent(action, getCurrentTime(), data));
    }

    private String getCurrentTime() {
        return s.format(new Date());
    }

    @Override
    public void __stop() {

    }

}

