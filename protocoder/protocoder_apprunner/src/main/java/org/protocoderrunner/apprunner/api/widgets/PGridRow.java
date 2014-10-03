package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by victormanueldiazbarrales on 29/07/14.
 */
public class PGridRow extends LinearLayout {

    public PGridRow(Context context, int cols) {
        super(context);

        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setWeightSum(cols);
    }

    public PGridRow addViewInRow(View v) {
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) getLayoutParams(); //or create new LayoutParams...

        if (v.getClass().equals(PSlider.class)) {
            lParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            lParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        lParams.weight = 1;
        lParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        v.setLayoutParams(lParams);
        addView(v);

        return this;
    }
}
