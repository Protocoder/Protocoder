package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

public class PAbsoluteLayout extends FixedLayout {

	public PAbsoluteLayout(Context context) {
		super(context);
	}

    @ProtocoderScript
    @APIMethod(description = "Sets the background color", example = "")
    @APIParam(params = { "colorHex" })
	public void backgroundColor(String c) {
		this.setBackgroundColor(Color.parseColor(c));
	}

    @ProtocoderScript
    @APIMethod(description = "Adds a view", example = "")
    @APIParam(params = { "view", "x", "y", "w", "h" })
	public void addView(View v, int x, int y, int w, int h) {
		//positionView(v, x, y, w, h);

        if (w == -1) w = LayoutParams.WRAP_CONTENT;
        if (h == -1) h = LayoutParams.WRAP_CONTENT;
		addView(v, new LayoutParams(w, h, x, y));
	}


    /**
	 * This is what we use to actually position and size the views
	 */
    /*
	protected void positionView(View v, int x, int y, int w, int h) {
		if (w == -1) {
			w = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		if (h == -1) {
			h = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
		params.leftMargin = x;
		params.topMargin = y;
		v.setLayoutParams(params);
	}
	*/

}
