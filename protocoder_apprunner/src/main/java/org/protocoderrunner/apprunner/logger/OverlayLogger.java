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

package org.protocoderrunner.apprunner.logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.protocoderrunner.R;
import org.protocoderrunner.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("NewApi")
public class OverlayLogger extends BaseFragment {

    View v;
    ArrayList<String> list = new ArrayList<String>();
    CustomAdapter adapter;
    ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_logger_overlay, container, false);
        return v;

    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listview = (ListView) v.findViewById(R.id.logger_listview);
        String[] values = new String[]{"Android", "iPhone", "WindowsMobile", "Blackberry", "WebOS", "Ubuntu",
                "Windows7", "Max OS X", "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2", "Ubuntu",
                "Windows7", "Max OS X", "Linux", "OS/2", "Android", "iPhone", "WindowsMobile"};

		/*
         * for (int i = 0; i < values.length; ++i) { list.add(values[i]); }
		 */

        adapter = new CustomAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listview.setStackFromBottom(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

				/*
				 * view.animate().setDuration(2000).alpha(0).withEndAction(new
				 * Runnable() {
				 * 
				 * @Override public void run() { list.remove(item);
				 * adapter.notifyDataSetChanged(); view.setAlpha(1); } });
				 */
            }

        });

        listview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    public void addItem(String text) {
        if (getActivity() != null) {
            list.add(text);
            adapter.notifyDataSetChanged();
            listview.invalidateViews();
            // listview.scrollBy(0, 0);
        }

    }

    public void clear() {
        list.clear();
        adapter.notifyDataSetChanged();

    }

    private class CustomAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public CustomAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);

            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }

        }

		/*
		 * @Override public long getItemId(int position) { //String item =
		 * getItem(position);
		 * 
		 * return mIdMap.get(item); }
		 */

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}