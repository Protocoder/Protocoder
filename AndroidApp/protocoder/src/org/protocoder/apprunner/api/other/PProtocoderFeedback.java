package org.protocoder.apprunner.api.other;

import org.protocoder.R;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.utils.MLog;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PProtocoderFeedback {

	private static final String TAG = "PProtocoderFeedback";

	protected Activity a;

	private LinearLayout liveRLayout;
	float liveTextY = 0;

	private TextView liveText;

	private boolean show = true; //
	private int bgColor = 0x55000000; // OK
	private int textColor = 0xFFFFFFFF;
	private int textSize = 12; // OK
	private boolean autoHide = false; // OK
	boolean hidingLiveText = true;

	private final Handler h;
	private final Runnable r;

	private final Typeface fontCode;

	public boolean enable = false;

	public PProtocoderFeedback(Activity a) {
		this.a = a;
		fontCode = Typeface.createFromAsset(a.getAssets(), "Inconsolata.otf");

		h = new Handler();
		r = new Runnable() {

			@Override
			public void run() {
				liveRLayout.animate().alpha(0.0f).setListener(new AnimatorListener() {

					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						// liveRLayout.setVisibility(View.GONE);
						// MLog.d("qq", "anim stop");
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
			}
		};

	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PProtocoderFeedback show(boolean b) {
		this.show = b;

		if (b) {
			liveRLayout.setVisibility(View.VISIBLE);
		} else {
			liveRLayout.setVisibility(View.GONE);
		}

		return this;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PProtocoderFeedback autoHide(boolean b) {
		this.autoHide = b;
		return this;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PProtocoderFeedback backgroundColor(String c) {
		new Color();
		this.bgColor = Color.parseColor(c);
		liveRLayout.setBackgroundColor(this.bgColor);

		return this;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PProtocoderFeedback textColor(String c) {
		new Color();
		this.textColor = Color.parseColor(c);
		liveText.setTextColor(this.textColor);

		return this;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PProtocoderFeedback textSize(int textSize) {
		this.textSize = textSize;
		MLog.d(TAG, "textsize " + textSize);
		// liveText.setTextSize(this.textSize);
		return this;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public PProtocoderFeedback write(String text) {
		// this.text = text;

		if (enable) {
			// showing layout and content and removing previous callbacks
			if (hidingLiveText) {
				liveRLayout.setVisibility(View.VISIBLE);
				liveRLayout.animate().alpha(1.0f);
				h.removeCallbacks(r);
				if (this.autoHide) {
					h.postDelayed(r, 3000);
				}

			}

			// if there is a textview then remove it
			if (liveText != null) {
				liveRLayout.removeView(liveText);
			}

			// add text view
			liveText = new TextView(a);
			RelativeLayout.LayoutParams liveTextLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			liveTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			liveTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			liveText.setLayoutParams(liveTextLayoutParams);
			liveText.setBackgroundColor(0x00000000);
			liveText.setPadding(5, 5, 2, 2);
			liveText.setTextColor(0xFFFFFFFF);
			liveText.setTextSize(this.textSize);
			liveText.setTextColor(this.textColor);
			liveText.setShadowLayer(2, 1, 1, 0x55000000);
			liveText.setTypeface(fontCode);

			liveText.setText(text);
			liveRLayout.addView(liveText);
		}
		return this;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public View add() {
		// live execution layout
		liveRLayout = new LinearLayout(a);
		RelativeLayout.LayoutParams liveLayoutParams = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		liveLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		liveLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		liveRLayout.setLayoutParams(liveLayoutParams);
		liveRLayout.setGravity(Gravity.BOTTOM);
		liveRLayout.setBackgroundColor(bgColor);
		liveRLayout.setPadding(10, 10, 10, 10);
		liveRLayout.setVisibility(View.GONE);
		// liveRLayout.setLayoutTransition(transition)

		// Animation spinin = AnimationUtils.loadAnimation(this,
		// R.anim.slide_in_left);
		// liveRLayout.setLayoutAnimation(new
		// LayoutAnimationController(spinin));

		Animator appearingAnimation = ObjectAnimator.ofFloat(null, "translationY", 20, 0);
		appearingAnimation.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setTranslationX(0f);
			}
		});

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			LayoutTransition l = new LayoutTransition();
			l.enableTransitionType(LayoutTransition.CHANGING);

			AnimatorSet as = (AnimatorSet) AnimatorInflater.loadAnimator(a, R.animator.live_code);
			// as.se
			l.setAnimator(LayoutTransition.APPEARING, as);
			l.setDuration(LayoutTransition.APPEARING, 300);
			l.setStartDelay(LayoutTransition.APPEARING, 0);

			liveRLayout.setLayoutTransition(l);
		}
		return liveRLayout;

	}

}
