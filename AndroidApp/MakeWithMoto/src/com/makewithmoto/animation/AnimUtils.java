package com.makewithmoto.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.makewithmoto.MainActivity;
import com.makewithmoto.R;

@SuppressLint("NewApi")
public class AnimUtils {

	public static  void showHelp(Context c) {
		final FrameLayout f2 = (FrameLayout) (((MainActivity) c).findViewById(R.id.helpFragment));
	
		f2.setVisibility(View.VISIBLE);
		f2.setY(-200);
		f2.animate().alpha(1).setDuration(500).translationY(0).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		}).start();
	}
	
	public static void hideHelp(Context c) {
		final FrameLayout f2 = (FrameLayout) (((MainActivity) c).findViewById(R.id.helpFragment));
		
		f2.setVisibility(View.VISIBLE);
		f2.setY(0);
		f2.animate().alpha(0).setDuration(500).translationY(-200).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {

				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				f2.setVisibility(View.GONE);				
				
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {

				
			}
		}).start();


	}
}
