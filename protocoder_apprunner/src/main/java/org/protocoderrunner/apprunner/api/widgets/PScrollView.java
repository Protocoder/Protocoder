package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by victormanueldiazbarrales on 25/07/14.
 */

//http://stackoverflow.com/questions/5763304/disable-scrollview-programmatically
public class PScrollView extends ScrollView {

    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private boolean mScrollable = true;


    public PScrollView(Context context, boolean mScrollable) {
        super(context);
        this.mScrollable = mScrollable;
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!mScrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }


}
