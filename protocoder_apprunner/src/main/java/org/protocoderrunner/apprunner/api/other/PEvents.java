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
