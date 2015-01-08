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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

import java.util.ArrayList;

public class PList extends LinearLayout implements PViewInterface {

	private final ListView lv;
	private final Context c;
	private PListAdapter plistAdapter;

	public PList(Context context) {
		super(context);
		c = context;

		lv = new ListView(c);

	}

    //TODO place holder
    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
	public void setItems(ArrayList<PListItem> items) {
		plistAdapter = new PListAdapter(c, items);
		lv.setAdapter(plistAdapter);
	}

	@Override
    //TODO place holder
    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
	public void addView(View v) {
		lv.addView(v);
	}

    //TODO place holder
    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
	public void clear() {
		lv.removeAllViews();
		plistAdapter.notifyDataSetChanged();
	}

    //TODO place holder
    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "" })
	public void notifyAddedProject() {
		plistAdapter.notifyDataSetChanged();
		lv.invalidateViews();
	}

}
