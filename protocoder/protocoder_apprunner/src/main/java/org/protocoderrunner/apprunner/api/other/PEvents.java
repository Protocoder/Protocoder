package org.protocoderrunner.apprunner.api.other;

import android.content.Context;

import org.mozilla.javascript.NativeObject;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.utils.StrUtils;

import java.util.ArrayList;

/**
 * Created by victormanueldiazbarrales on 13/10/14.
 */
public class PEvents extends PInterface {
    ArrayList<EventItem> eventsList;

    public interface EventCB {
        public void event(NativeObject obj);

    }

    public PEvents(Context appActivity) {
        super(appActivity);
        eventsList = new ArrayList<EventItem>();
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
        MLog.d(TAG, "eventList " + eventsList);

        for (int i = 0; i < eventsList.size(); i++) {
            if (name.equals(eventsList.get(i).name)) {
                MLog.d(TAG, "sending event to " + eventsList.get(i).name);
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

}
