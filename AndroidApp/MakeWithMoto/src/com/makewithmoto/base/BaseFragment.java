package com.makewithmoto.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

@SuppressLint("NewApi")
public class BaseFragment extends Fragment {

	private static final String TAG = "BaseFragment";

	public interface FragmentListener {
		public void onReady();
		public void onFinish(boolean finished);
	}
	
	FragmentListener fragmentListener; 
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	} 
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (fragmentListener != null) { 
			fragmentListener.onReady();
		} 
	}

}
