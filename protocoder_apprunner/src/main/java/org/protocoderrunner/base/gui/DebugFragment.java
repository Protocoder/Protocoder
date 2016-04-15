package org.protocoderrunner.base.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.protocoderrunner.R;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.events.Events;

import java.util.ArrayList;

public class DebugFragment extends Fragment {

    private static final String TAG = DebugFragment.class.getSimpleName();

    private View v;
    private RecyclerView mListView;
    private ArrayList<String> mLogArray;
    private MyAdapter mArrayAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean isLockPosition = false;

    public static DebugFragment newInstance() {
        DebugFragment myFragment = new DebugFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_debug, container, false);

        mLogArray = new ArrayList<>();
        mListView = (RecyclerView) v.findViewById(R.id.logwrapper);

        mArrayAdapter = new MyAdapter();
        mListView.setAdapter(mArrayAdapter);
        mListView.setItemAnimator(null);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        ToggleButton toggleLock = (ToggleButton) v.findViewById(R.id.toogleLockList);
        toggleLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLockPosition = isChecked;
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);

        mListView.setLayoutManager(mLayoutManager);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    public void addText(String log) {
        mLogArray.add(log);

        if (isLockPosition == false) {
            mArrayAdapter.notifyItemInserted(mLogArray.size());
            mListView.scrollToPosition(mLogArray.size() - 1);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return true;
    }

    @Subscribe
    public void onEventMainThread(Events.LogEvent e) {
        String logMsg = e.getLog();
        MLog.d(TAG, logMsg);

        addText(logMsg);
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        public MyAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            DebugFragment.ViewHolder vh = new DebugFragment.ViewHolder(textView);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String txt = mLogArray.get(position);
            MLog.d(TAG, txt);
            holder.textView.setText(txt);
        }

        @Override
        public int getItemCount() {
            return mLogArray.size();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }
}
