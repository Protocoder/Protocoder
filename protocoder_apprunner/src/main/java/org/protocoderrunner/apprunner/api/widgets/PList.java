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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

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

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void setItems(ArrayList<PListItem> items) {
        plistAdapter = new PListAdapter(c, items);
        lv.setAdapter(plistAdapter);
    }

    @Override
    //TODO place holder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void addView(View v) {
        lv.addView(v);
    }

    //TODO place holder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {
        lv.removeAllViews();
        plistAdapter.notifyDataSetChanged();
    }

    //TODO place holder

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void notifyAddedProject() {
        plistAdapter.notifyDataSetChanged();
        lv.invalidateViews();
    }

}
