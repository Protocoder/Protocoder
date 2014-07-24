package org.protocoderrunner.network.bt;

import java.util.ArrayList;
import java.util.TreeSet;

import org.protocoderrunner.R;
import org.protocoderrunner.utils.MLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint("ShowToast")
public class DebugFragment extends Fragment {

	private final String TAG = "qq";
	private FragmentActivity c;
	ListView lv;
	View v;
	ArrayList<Info> list = new ArrayList<Info>();
	public CustomAdapter adapter;

	public DebugFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		v = inflater.inflate(R.layout.fragment_debug, container, false);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		c = getActivity();
		adapter = new CustomAdapter(getActivity(), list);

		setRetainInstance(true);

		lv = (ListView) v.findViewById(R.id.logWindow);
		lv.setAdapter(adapter);
		lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		lv.setStackFromBottom(true);

		final EditText cmdEditText = (EditText) v.findViewById(R.id.pdName);

		Button btn = (Button) v.findViewById(R.id.pdSend);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String cmd = cmdEditText.getText().toString();
				MLog.d(TAG, cmd);

				// TOFIX write to serial
				// ((ActivityMAKr) c).makr.writeSerial(cmd);
				adapter.addLeftItem(cmd);

			}
		});

		ToggleButton toggleButton = (ToggleButton) v.findViewById(R.id.lockList);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				} else {
					lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
				}

			}
		});
		toggleButton.setChecked(true);

	}

	public void bindButton(int resource, final int value) {
		Button btn = (Button) v.findViewById(resource);
		btn.setSoundEffectsEnabled(false);

		btn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// MLog.d("qq", "action_down");

				} else {

				}
				return false;
			}
		});

	}

	private Toast toast = null;

	private void toast(final String msg) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(getActivity().getApplicationContext(), "", Toast.LENGTH_SHORT);
				}
				toast.setText(TAG + ": " + msg);
				toast.show();
			}
		});
	}

	public void clear() {
		list.clear();
		adapter.notifyDataSetChanged();

	}

	public class CustomAdapter extends BaseAdapter {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

		ArrayList<Info> list_;
		private final TreeSet mSeparatorsSet = new TreeSet();

		public CustomAdapter(Context context, ArrayList<Info> list) {
			super();

			list_ = list;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			// L.d("view", "getting view");
			int type = getItemViewType(position);
			// MLog.d("view", "" + type);

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				holder = new ViewHolder();

				switch (type) {
				case TYPE_ITEM:
					v = vi.inflate(R.layout.view_textview_receive, null);

					break;

				case TYPE_SEPARATOR:
					v = vi.inflate(R.layout.view_textview_send, null);

					break;

				default:
					break;
				}

				holder.content = (TextView) v.findViewById(R.id.content);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			Info info = list_.get(position);
			if (info != null) {
				holder.content.setText(info.content);
				// holder.item2.setText(custom.getSecond());
			}

			return v;
		}

		public void addLeftItem(final String text) {
			if (getActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						list.add(new Info(text));
						adapter.notifyDataSetChanged();
					}
				});
			}
		}

		public void addRightItem(final String item) {
			if (getActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						list_.add(new Info(item));
						// save separator position
						mSeparatorsSet.add(list_.size() - 1);
						notifyDataSetChanged();
					}
				});
			}
		}

		@Override
		public int getItemViewType(int position) {
			return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getCount() {
			return list_.size();
		}

		@Override
		public Object getItem(int position) {
			return list_.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	class Info {
		String content;

		public Info(String soundName) {
			this.content = soundName;

		}
	}

	private class ViewHolder {
		TextView content;
	}

}
