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

package org.protocoderrunner.apprunner.api.other;

import android.content.Context;

import org.mozilla.javascript.NativeObject;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.utils.StrUtils;

import java.util.ArrayList;

public class PEvents extends PInterface {
    ArrayList<EventItem> eventsList;

    public interface EventCB {
        public void event(NativeObject obj);

    }

    public PEvents(Context appActivity) {
        super(appActivity);
        eventsList = new ArrayList<EventItem>();
        WhatIsRunning.getInstance().add(this);
    }

    public String add(String name, EventCB callback) {
        String id = StrUtils.generateRandomString();
        eventsList.add(new EventItem(id, name, callback));

        return id;
    }

    public void remove(String id) {
        for (int i = 0; i < eventsList.size(); i++) {
            if (id.equals(eventsList.get(i).id)) {
                eventsList.remove(i);
                break;
            }
        }
    }

    public void sendEvent(String name, NativeObject obj) {
        //get all matching listeners and send event

        for (int i = 0; i < eventsList.size(); i++) {
            if (name.equals(eventsList.get(i).name)) {
                eventsList.get(i).cb.event(obj);
            }
        }
    } 
    
    class EventItem {
        public final String id;
        public final String name;
        public final PEvents.EventCB cb;

        EventItem(String id, String name, PEvents.EventCB cb) {
            this.id = id;
            this.name = name;
            this.cb = cb;
        }
    }

    public void stop() {
        for (int i = 0; i < eventsList.size(); i++) {
            eventsList.remove(i);
        }
    }

}
