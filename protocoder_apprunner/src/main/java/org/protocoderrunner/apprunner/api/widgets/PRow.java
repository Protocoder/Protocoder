package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

public class PRow {
    int n;

    LinearLayout ll;
    LinearLayout.LayoutParams lParams;

    public PRow(Context c, LinearLayout cardLl, int n) {
        this.n = n;
        float t = 100f;
        float tt = t / n;

        ll = new LinearLayout(c);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setWeightSum(t);
        cardLl.addView(ll);

        lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, tt);
    }


    @ProtoMethod(description = "Add a new view to the row", example = "")
    @ProtoMethodParam(params = {"view"})
    public void addView(View v) {
        v.setLayoutParams(lParams);
        ll.addView(v);
    }

}