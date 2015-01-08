package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

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

    @ProtocoderScript
    @APIMethod(description = "Add a new view to the row", example = "")
    @APIParam(params = { "view" })
	public void addView(View v) {
		v.setLayoutParams(lParams);
		ll.addView(v);
	}

}