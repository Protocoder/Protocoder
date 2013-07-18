package com.makewithmoto.projectlist;

import com.makewithmoto.R;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.GridView;


//CHECK http://developer.android.com/reference/android/view/animation/GridLayoutAnimationController.html
@SuppressLint("NewApi")
public class ProjectAnimations {
	
	public static void projectRefresh(final View v) {
	    AnimatorSet animFlip;
        animFlip = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(), R.animator.flip_up);
        animFlip.setTarget(v);
        animFlip.start();
		/*
		v.animate().alpha(0).setDuration(200).setListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {						
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				v.animate().alpha(1);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});*/
	}

	public static void projectLaunch(final View v) {
	    AnimatorSet animSpin;
        animSpin = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(), R.animator.flip_up);
        animSpin.setTarget(v);
        animSpin.start();
		/*GridView gL = (GridView) v.getParent(); 
		final int x = (int) v.getX(); 
		final int y = (int) v.getY(); 

		v.animate().scaleX(5).scaleY(5).x(gL.getWidth() / 2 - v.getWidth() / 2).y(gL.getHeight() / 2 - v.getHeight()).alpha(0).setDuration(1000).setListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				v.setScaleX(1);
				v.setScaleY(1);
				v.setAlpha(1);
				v.setX(x);
				v.setY(y);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});*/

	}
}
