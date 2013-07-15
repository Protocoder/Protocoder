package com.makewithmoto.apprunner.api;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;

import com.makewithmoto.apprunner.fragments.MoldableFragment;
import com.makewithmoto.apprunner.views.PlotView;
import com.makewithmoto.apprunner.views.PlotView.Plot;
import com.makewithmoto.utils.ALog;

public class JUI extends JInterface {

	// Make this weakly connected
	protected WeakReference<MoldableFragment> moldableFragment;

	public JUI(FragmentActivity fragmentActivity) {
		super(fragmentActivity);
		this.moldableFragment = new WeakReference<MoldableFragment>(c.get().getMoldableFragment());
	}
	
	@Override
	public void destroy() {
		super.destroy();
		if (plotView != null) {
			plotView.destroy();
		}
	}

	@JavascriptInterface
	public void addButton(String label, final String callback) {
		ALog.d(TAG, callback + "()");
		Log.d(TAG, callback + "() [button with label " + label + "]");
		moldableFragment.get().addButton(label, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				applicationWebView
						.runJavascript("window['" + callback + "']()");
			}
		});
	}

	@JavascriptInterface
	public void addSeekBar(String label, String to, String start,
			final String callback) {
		Log.d(TAG, callback + "()");
		moldableFragment.get().addSeekBar(label, to, start,
				new android.widget.SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						applicationWebView.runJavascript("window['" + callback
								+ "']('" + progress + "');");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});
	}

	@JavascriptInterface
	public void addTextLabel(String label) {
		moldableFragment.get().addTextView(label);
	}

	@JavascriptInterface
	public void addACheckbox(final String label, final String selected,
			final String callback) {
		moldableFragment.get().addACheckbox(label, selected,
				new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						applicationWebView.runJavascript("window['" + callback
								+ "']('" + isChecked + "')");
					}

				});
	}

	@JavascriptInterface
	public void addAToggleButton(final String label, final String callback) {
		moldableFragment.get().addAToggleButton(label, new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("addAToggleButton", "is checked: " + isChecked);
				applicationWebView.runJavascript("window['" + callback + "']('"
						+ isChecked + "')");
			}
		});
	} 
	
	
	PlotView plotView;
	Plot plot;
	
	@JavascriptInterface
	public void addPlot(final String callback) {
		plotView = moldableFragment.get().addPlot();
		plot = plotView.new Plot(Color.RED);
		plotView.addPlot(plot);
	} 

	@JavascriptInterface 
	public void addPlotValue(float value) { 
		plotView.setValue(plot, value);
	}

	@JavascriptInterface
	public void showToast(final String text, final String duration) {
		moldableFragment.get().showToast(text, duration);
	}

	@JavascriptInterface
	public void startTrackingTouches(String b) {
	}

}
