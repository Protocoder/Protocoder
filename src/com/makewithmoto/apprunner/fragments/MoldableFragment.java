package com.makewithmoto.apprunner.fragments;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.makewithmoto.apprunner.views.PlotView;
import com.makewithmoto.base.BaseFragment;
import com.makewithmoto.utils.ALog;

@SuppressLint("NewApi")
public class MoldableFragment extends BaseFragment {
	private static String TAG = "Moto";
	private Point size = new Point();
	private int screenWidth, screenHeight;
	private LinearLayout v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		v = new LinearLayout(getActivity());

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);

		v.setOrientation(LinearLayout.VERTICAL);
		v.setGravity(Gravity.TOP);
		v.setLayoutParams(params);

		return v;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// listener.onReady();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void log(String msg) {
		ALog.i(msg);
	}

	// //////////////////////////////////////
	// Visual methods
	// //////////////////////////////////////
	// BUTTONS
	// //////////////////////////////////////
	public Button addButton(final String text, OnClickListener onClickListener) {
		final Button aNewButton = new Button(getActivity());
		aNewButton.setText(text);

		// Setup the button
		aNewButton.setTextAppearance(getActivity(),
				android.R.style.TextAppearance_Large);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		aNewButton.setGravity(Gravity.CENTER);
		// params.setMargins(100, 0, 0, 500);
		aNewButton.setLayoutParams(params);

		addToView(aNewButton);
		aNewButton.setOnClickListener(onClickListener);
		return aNewButton;
	}

	// //////////////////////////////////////
	// SeekBar
	// //////////////////////////////////////
	public SeekBar addSeekBar(String text, String max, String start,
			OnSeekBarChangeListener callback) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		TextView label = new TextView(getActivity());

		label.setText(text);
		label.setTextAppearance(getActivity(),
				android.R.style.TextAppearance_Large);

		label.setLayoutParams(params);
		addToView(label);

		SeekBar aSeekBar = new SeekBar(getActivity());
		aSeekBar.setMax(Integer.parseInt(max));
		aSeekBar.setProgress(Integer.parseInt(start));

		aSeekBar.setLayoutParams(params);
		aSeekBar.setOnSeekBarChangeListener(callback);

		addToView(aSeekBar);
		return aSeekBar;
	}

	// //////////////////////////////////////
	// TextView
	// //////////////////////////////////////
	public TextView addTextView(final String text) {
		TextView tv = new TextView(getActivity());

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		tv.setLayoutParams(params);

		tv.setText(text);
		tv.setTextAppearance(getActivity(),
				android.R.style.TextAppearance_Large);
		addToView(tv);

		return tv;
	}

	// //////////////////////////////////////
	// TextView
	// //////////////////////////////////////
	public CheckBox addACheckbox(final String label, final String initial,
			OnCheckedChangeListener listener) {
		RelativeLayout rl = new RelativeLayout(getActivity());

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		rl.setLayoutParams(params);
		addToView(rl);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.leftMargin = 107;

		TextView tv = new TextView(getActivity());
		tv.setLayoutParams(lp);

		tv.setText(label);
		tv.setTextAppearance(getActivity(),
				android.R.style.TextAppearance_Large);
		rl.addView(tv);

		CheckBox cb = new CheckBox(getActivity());
		cb.setLayoutParams(params);
		cb.setChecked(Boolean.valueOf(initial));
		cb.setOnCheckedChangeListener(listener);

		lp.leftMargin = 107;
		rl.addView(cb);

		// addToView(rl);
		return cb;
	}

	// //////////////////////////////////////
	// ToggleBox
	// //////////////////////////////////////
	public ToggleButton addAToggleButton(final String text,
			OnCheckedChangeListener onCheckedChangeListener) {
		final ToggleButton aNewButton = new ToggleButton(getActivity());
		aNewButton.setText(text);

		// Setup the button
		aNewButton.setTextAppearance(getActivity(),
				android.R.style.TextAppearance_Large);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		aNewButton.setGravity(Gravity.CENTER);
		// params.setMargins(100, 0, 0, 500);
		aNewButton.setLayoutParams(params);

		addToView(aNewButton);
		aNewButton.setOnCheckedChangeListener(onCheckedChangeListener);
		return aNewButton;
	}

	// //////////////////////////////////////
	// Toast
	// //////////////////////////////////////
	public Toast showToast(final String text, final String strDuration) {
		int duration = Integer.parseInt(strDuration);

		Toast toast = Toast.makeText(getActivity(), text, duration);
		toast.show();

		return toast;
	}

	// //////////////////////////////////////
	// RadioBox
	// //////////////////////////////////////

	// //////////////////////////////////////
	// Plot
	// //////////////////////////////////////
	public PlotView addPlot() {
		Log.d("qq", "adding a plot");
		PlotView plotView = new PlotView(getActivity()); 

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		//plotView.setGravity(Gravity.CENTER);
		// params.setMargins(100, 0, 0, 500);
		plotView.setLayoutParams(params);

		addToView(plotView); 
		
		return plotView; 
	}
	// //////////////////////////////////////
	// Helpers
	// //////////////////////////////////////
	private void addToView(final View aView) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				v.addView(aView);
			}
		});
	}


}
