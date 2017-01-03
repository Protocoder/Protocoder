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

package org.protocoderrunner.api.widgets;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.api.common.ReturnInterfaceWithReturn;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.StyleProperties;
import org.protocoderrunner.base.views.FitRecyclerView;

public class PList extends FitRecyclerView {

    private final Context mContext;
    private PViewItemAdapter mViewAdapter;

    public StyleProperties props = new StyleProperties();
    public Styler styler;

    public PList(AppRunner appRunner, int numCols, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        super(appRunner.getAppContext());
        mContext = appRunner.getAppContext();

        styler = new Styler(appRunner, this, props);
        styler.apply();

        setLayoutManager(new GridLayoutManager(mContext, numCols));
        // setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
        mViewAdapter = new PViewItemAdapter(mContext, data, creating, binding);

        // Get GridView and set adapter
        setHasFixedSize(true);

        setAdapter(mViewAdapter);
        notifyDataChanged();

        setItemAnimator(new DefaultItemAnimator());
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void setItems(NativeArray data) {
        mViewAdapter.setArray(data);
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void clear() {

    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void notifyDataChanged() {
        mViewAdapter.notifyDataSetChanged();
    }

}
