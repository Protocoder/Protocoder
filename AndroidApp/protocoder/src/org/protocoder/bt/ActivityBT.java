package org.protocoder.bt;

import org.protocoder.R;
import org.protocoder.bt.SimpleBT.SimpleBTListener;
import org.protocoder.views.PlotView;
import org.protocoder.views.PlotView.Plot;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

/*
 * Example using a normal Bluetooth module 
 * 
 * 
 */

@SuppressLint("NewApi")
public class ActivityBT extends FragmentActivity {

	private static final String TAG = "ExAPP";
	private DebugFragment df;
	private boolean f2V = true;

	ActionBar actionBar;

	SimpleBT simpleBT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bt);

		actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setLogo(null);
		actionBar.setTitle("MakeWithMoto");

		df = new DebugFragment();
		addFragment(df, R.id.f1);

		final PlotView graphView = (PlotView) findViewById(R.id.plotView1);
		final Plot p1 = graphView.new Plot(Color.RED);
		// graphView.addPlot(p1);

		simpleBT = new SimpleBT(this);
		simpleBT.startDeviceListActivity();
		simpleBT.addListener(new SimpleBTListener() {

			@Override
			public void onRawDataReceived(byte[] buffer, int size) {

			}

			@Override
			public void onConnected() {
				simpleBT.send("hello");

			}

			@Override
			public void onMessageReceived(String cmd, String value) {

				df.adapter.addRightItem(cmd + " " + value);
				float val = Float.parseFloat(value);
				// graphView.setValue(p1, val);

			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		simpleBT.destroy();

	}

	public void addFragment(Fragment f, int fragmentPosition) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(fragmentPosition, f);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FrameLayout layout = (FrameLayout) findViewById(R.id.f2);

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			if (f2V) {
				layout.setVisibility(View.GONE);
				f2V = false;
			} else {
				layout.setVisibility(View.VISIBLE);
				f2V = true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onStart() {
		super.onStart();
		simpleBT.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + resultCode);
		simpleBT.result(requestCode, resultCode, data);
	}

}
