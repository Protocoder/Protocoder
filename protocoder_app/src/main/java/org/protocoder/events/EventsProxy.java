package org.protocoder.events;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoderrunner.base.utils.MLog;

/**
 * Created by victornomad on 25/02/16.
 */
public class EventsProxy {

    String TAG = EventsProxy.class.getSimpleName();

    public EventsProxy() {
        EventBus.getDefault().register(this);
    }

    public void stop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getAction());
    }

    @Subscribe
    public void onEventMainThread(Events.HTTPServerEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getWhat());
    }

    // execute lines
    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getCode());
    }

    //folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, e.getClass().getSimpleName() + " -> " + e.getFullFolder());
    }

}
