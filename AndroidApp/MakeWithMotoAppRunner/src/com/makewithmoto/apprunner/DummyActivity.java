package com.makewithmoto.apprunner;

import java.lang.ref.WeakReference;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.makewithmoto.base.BaseActivity;


@SuppressLint("NewApi")
public class DummyActivity extends BaseActivity {

	WeakReference<Context> c;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dummy);
		c = new WeakReference<Context>(this);

		Button btn = (Button) findViewById(R.id.btbLaunchMWM); 
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.makewithmoto.LauncherActivity");
				startActivity(intent);
				
			}
		});
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

	}

}
