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
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.network.CustomWebsocketServer;
import org.protocoderrunner.utils.AndroidUtils;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class PConsole extends PInterface {

	String TAG = "PConsole";
    private boolean showTime = false;

    public PConsole(Context a) {
		super(a);
	}


	@ProtoMethod(description = "shows any HTML text in the webIde console", example = "")
	@ProtoMethodParam(params = { "text","text","..." })
	public void log(String... outputs) {

		StringBuilder builder = new StringBuilder();
        builder.append(getCurrentTime());

		for (String output : outputs) {
			builder.append(" ").append(output);
		}


        JSONObject values = null;
        JSONObject msg = null;
        try {
           values = new JSONObject().put("val", builder.toString());
           msg = new JSONObject().put("type", "console").put("action", "log").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);
	}


	@ProtoMethod(description = "clear the webIde console", example = "")
	@ProtoMethodParam(params = { "" })
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
    @ProtoMethodParam(params = { "boolean" })
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
    @ProtoMethodParam(params = { "colorHex" })
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
    @ProtoMethodParam(params = { "colorHex" })
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
    @ProtoMethodParam(params = { "size" })
    public void textSize(int textSize) {

        JSONObject values = null;
        JSONObject msg = null;
        try {
            values = new JSONObject().put("textSize", textSize+"px");
            msg = new JSONObject().put("type", "console").put("action", "textSize").put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        send(msg);
    }




    @ProtoMethod(description = "Changes the console text color", example = "")
    @ProtoMethodParam(params = { "colorHex" })
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
    @ProtoMethodParam(params = { "boolean" })
    public void showTime(boolean b) {
        showTime = b;

    }


    private void send(JSONObject msg) {
        try {
            CustomWebsocketServer.getInstance(getContext()).send(msg);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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

}

