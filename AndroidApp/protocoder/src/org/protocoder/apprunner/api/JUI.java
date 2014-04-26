/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.apprunner.api;

import java.io.File;

import org.protocoder.AppSettings;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.ProtocoderScript;
import org.protocoder.apprunner.api.other.JCamera;
import org.protocoder.apprunner.api.other.JUIGeneric;
import org.protocoder.apprunner.api.other.JVideo;
import org.protocoder.apprunner.api.other.QQ;
import org.protocoder.apprunner.api.widgets.JButton;
import org.protocoder.apprunner.api.widgets.JCanvasView;
import org.protocoder.apprunner.api.widgets.JCard;
import org.protocoder.apprunner.api.widgets.JCheckBox;
import org.protocoder.apprunner.api.widgets.JEditText;
import org.protocoder.apprunner.api.widgets.JImageButton;
import org.protocoder.apprunner.api.widgets.JImageView;
import org.protocoder.apprunner.api.widgets.JMap;
import org.protocoder.apprunner.api.widgets.JPlotView;
import org.protocoder.apprunner.api.widgets.JRadioButton;
import org.protocoder.apprunner.api.widgets.JSeekBar;
import org.protocoder.apprunner.api.widgets.JSwitch;
import org.protocoder.apprunner.api.widgets.JTextView;
import org.protocoder.apprunner.api.widgets.JToggleButton;
import org.protocoder.apprunner.api.widgets.JWebView;
import org.protocoder.sensors.WhatIsRunning;
import org.protocoder.utils.AndroidUtils;
import org.protocoder.utils.MLog;
import org.protocoder.views.HoloCircleSeekBar;
import org.protocoder.views.PadView;
import org.protocoder.views.TouchAreaView;

import processing.core.PApplet;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.CycleInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class JUI extends JUIGeneric {

	String TAG = "JUI";
	private String onNFCfn;

	private onKeyDownCB onKeyDownfn;
	private onKeyUpCB onKeyUpfn;

	public JUI(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

		((AppRunnerActivity) a).addOnKeyListener(new onKeyListener() {

			@Override
			public void onKeyUp(int keyCode) {
				if (onKeyDownfn != null) {
					onKeyDownfn.event(keyCode);
				}
			}

			@Override
			public void onKeyDown(int keyCode) {
				if (onKeyUpfn != null) {
					onKeyUpfn.event(keyCode);
				}
			}
		});

	}

	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "titleName" })
	public void setTitle(String title) {
		if (noActionBarAllowed) {
			return;
		}

		a.get().setActionBar(null, null);
		a.get().actionBar.setTitle(title);
	}

	@ProtocoderScript
	@APIMethod(description = " ", example = "")
	@APIParam(params = { "subtitleName" })
	public void setSubtitle(String title) {
		if (noActionBarAllowed) {
			return;
		}

		a.get().setActionBar(null, null);
		a.get().actionBar.setSubtitle(title);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void showTitleBar(Boolean b) {
		if (noActionBarAllowed) {
			return;
		}

		a.get().setActionBar(null, null);
		if (b) {
			a.get().actionBar.show();
		} else {
			a.get().actionBar.hide();
		}
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "r", "g", "b" })
	public void setTitleBgColor(int r, int g, int b) {
		if (noActionBarAllowed) {
			return;
		}
		int c = Color.rgb(r, g, b);
		a.get().setActionBar(c, null);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "r", "g", "b" })
	public void setTitleTextColor(int r, int g, int b) {
		if (noActionBarAllowed) {
			return;
		}

		int c = Color.rgb(r, g, b);
		a.get().setActionBar(null, c);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "imageName" })
	public void setTitleImage(String imagePath) {
		if (noActionBarAllowed) {
			return;
		}

		Bitmap myBitmap = BitmapFactory.decodeFile(AppRunnerSettings.get().project.getStoragePath() + imagePath);
		Drawable icon = new BitmapDrawable(a.get().getResources(), myBitmap);

		a.get().actionBar.setIcon(icon);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void showHomeBar(boolean b) {
		a.get().showHomeBar(b);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setFullscreen() {
		noActionBarAllowed = true;
		a.get().setFullScreen();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setImmersive() {
		noActionBarAllowed = true;
		a.get().setImmersive();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setLightsOut() {
		a.get().lightsOutMode();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setLandscape() {
		a.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void setPortrait() {
		a.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text" })
	public void toast(String text) {
		Toast.makeText(a.get(), text, Toast.LENGTH_SHORT).show();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text", "duration" })
	public void toast(String text, int duration) {
		Toast.makeText(a.get(), text, duration).show();
	}

	/**
	 * Set padding on the entire view
	 * 
	 */
	@ProtocoderScript
	@APIParam(params = { "left", "top", "right", "bottom" })
	public void setPadding(int left, int top, int right, int bottom) {
		initializeLayout();
		uiAbsoluteLayout.setPadding(left, top, right, bottom);
	}

	@ProtocoderScript
	@APIParam(params = { "View", "height" })
	public void resizeView(final View v, int h) {
		boolean animated = false;

		if (!animated) {
			v.getLayoutParams().height = h;
			v.setLayoutParams(v.getLayoutParams());
		} else {

			int initHeight = v.getLayoutParams().height;
			// v.setLayoutParams(v.getLayoutParams());

			ValueAnimator anim = ValueAnimator.ofInt(initHeight, h);
			anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					int val = (Integer) valueAnimator.getAnimatedValue();
					v.getLayoutParams().height = val;
					v.setLayoutParams(v.getLayoutParams());

				}
			});
			anim.setDuration(200);
			anim.start();
		}
	}

	@ProtocoderScript
	@APIParam(params = { "View", "x", "y" })
	public void move(View v, float x, float y) {
		v.animate().x(x).setDuration(AppSettings.animSpeed);
		v.animate().y(y).setDuration(AppSettings.animSpeed);

	}

	@ProtocoderScript
	@APIParam(params = { "View" })
	public void jump(View v) {

		ValueAnimator w = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.9f, 1.2f, 1f);
		w.setDuration(500);

		ValueAnimator h = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.9f, 1.2f, 1f);
		h.setDuration(500);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(w).with(h);
		animatorSet.start();
	}

	@ProtocoderScript
	@APIParam(params = { "View", "num" })
	public void blink(View v, int num) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
		anim.setDuration(1000);
		anim.setInterpolator(new CycleInterpolator(1));
		anim.setRepeatCount(num);
		anim.start();
	}

	@ProtocoderScript
	@APIParam(params = { "View", "degrees" })
	public void rotate(View v, float x) {
		v.animate().rotation(x).setDuration(AppSettings.animSpeed);
	}

	@ProtocoderScript
	@APIParam(params = { "View", "degrees", "degrees", "degrees" })
	public void rotate(View v, float x, float y, float z) {
		v.animate().rotation(x).setDuration(AppSettings.animSpeed);
		// looks weird but it works more consistent
		v.animate().rotationX(y).setDuration(AppSettings.animSpeed);
		v.animate().rotationY(z).setDuration(AppSettings.animSpeed);
	}

	@ProtocoderScript
	@APIParam(params = { "View", "float" })
	public void alpha(View v, float deg) {
		v.animate().alpha(deg).setDuration(AppSettings.animSpeed);
	}

	@ProtocoderScript
	@APIParam(params = { "View", "float" })
	public void scale(View v, float x, float y) {
		v.animate().scaleX(x).setDuration(AppSettings.animSpeed);
		v.animate().scaleY(y).setDuration(AppSettings.animSpeed);
	}

	@ProtocoderScript
	@APIParam(params = { "View", "float" })
	public void scaleBy(View v, float x, float y) {
		v.animate().scaleXBy(x).setDuration(AppSettings.animSpeed);
		v.animate().scaleYBy(y).setDuration(AppSettings.animSpeed);
	}

	/**
	 * Set background color for the main layout via int
	 * 
	 * 
	 @APIParam(params = { "hexColor" }) public void backgroundColor(int color) {
	 *                  initializeLayout();
	 *                  holderLayout.setBackgroundColor(color); }
	 * 
	 *                  /** The more common way to set background color, set bg
	 *                  color via RGB
	 * 
	 */
	@APIParam(params = { "r", "g", "b" })
	public void backgroundColor(int red, int green, int blue) {
		initializeLayout();
		holderLayout.setBackgroundColor(Color.rgb(red, green, blue));
	}

	/**
	 * Set a background image
	 */
	@APIParam(params = { "imageName" })
	public void backgroundImage(String imagePath) {
		initializeLayout();
		// Add the bg image asynchronously
		new SetBgImageTask(bgImageView).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator
				+ imagePath);

	}

	/**
	 * Adds a card holder
	 * 
	 */
	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label" })
	public JCard addView(View v) {
		JCard c = addGenericCard();
		addViewLinear(v);

		return c;
	}

	/**
	 * Adds a card holder
	 * 
	 */
	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label" })
	public JCard addCard() {
		JCard c = addGenericCard();
		addViewLinear(c);

		return c;
	}

	/**
	 * Adds a card holder
	 * 
	 */
	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label", "x", "y", "w", "h" })
	public JCard addCard(String label, int x, int y, int w, int h) {
		JCard c = addGenericCard();
		addViewAbsolute(c, x, y, w, h);
		return c;
	}

	/**
	 * Adds a button to the view
	 * 
	 */
	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public JButton addButton(String label, final addGenericButtonCB callbackfn) {
		JButton b = addGenericButton(label, callbackfn);
		return b;
	}

	/**
	 * Adds a button to the view
	 * 
	 */
	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public JButton addButton(String label, int x, int y, int w, int h, final addGenericButtonCB callbackfn) {
		JButton b = addGenericButton(label, callbackfn);
		addViewAbsolute(b, x, y, w, h);
		return b;
	}

	/**
	 * Adds a touch area
	 * 
	 */
	@ProtocoderScript
	@APIParam(params = { "bShowArea", "function(touching, x, y)" })
	public TouchAreaView addTouchArea(boolean showArea, final addGenericTouchAreaCB callbackfn) {
		TouchAreaView taV = addGenericTouchArea(showArea, callbackfn);

		return taV;
	}

	@ProtocoderScript
	@APIParam(params = { "x", "y", "w", "h", "bShowArea", "function(touching, x, y)" })
	public TouchAreaView addTouchArea(int x, int y, int w, int h, boolean showArea,
			final addGenericTouchAreaCB callbackfn) {
		TouchAreaView taV = addGenericTouchArea(showArea, callbackfn);
		addViewAbsolute(taV, x, y, w, h);

		return taV;
	}

	@ProtocoderScript
	@APIParam(params = { "x", "y", "w", "h", "function(touching, x, y)" })
	public PadView addXYPad(int x, int y, int w, int h, final addPadCB callbackfn) {
		PadView taV = addPad(callbackfn);
		addViewAbsolute(taV, x, y, w, h);

		return taV;
	}

	@ProtocoderScript
	@APIParam(params = { "function(touching, x, y)" })
	public PadView addXYPad(final addPadCB callbackfn) {
		PadView taV = addPad(callbackfn);

		return taV;
	}

	/**
	 * Adds a circular seekbar
	 * 
	 */

	@ProtocoderScript
	@APIParam(params = { "function(progress)" })
	public HoloCircleSeekBar addKnob(final addGenericKnobCB callbackfn) {
		HoloCircleSeekBar pkr = addGenericKnob(callbackfn);

		return pkr;
	}

	@ProtocoderScript
	@APIParam(params = { "x", "y", "w", "h, function(progress)" })
	public HoloCircleSeekBar addKnob(int x, int y, int w, int h, final addGenericKnobCB callbackfn) {
		HoloCircleSeekBar pkr = addGenericKnob(callbackfn);
		addViewAbsolute(pkr, x, y, w, h);

		return pkr;
	}

	/**
	 * Adds a seekbar with a callback function
	 * 
	 */
	@ProtocoderScript
	@APIParam(params = { "max", "progress", "function(progress)" })
	public JSeekBar addSlider(int max, int progress, final addGenericSliderCB callbackfn) {
		JSeekBar sb = addGenericSlider(max, progress, callbackfn);
		return sb;

	}

	@ProtocoderScript
	@APIParam(params = { "x", "y", "w", "h", "max", "progress", "function(progress)" })
	public JSeekBar addSlider(int x, int y, int w, int h, int max, int progress, final addGenericSliderCB callbackfn) {
		JSeekBar sb = addGenericSlider(max, progress, callbackfn);
		addViewAbsolute(sb, x, y, w, -1);
		return sb;

	}

	/**
	 * Adds a TextView. Note that the user doesn't specify font size
	 * 
	 */
	@ProtocoderScript
	@APIParam(params = { "label" })
	public JTextView addLabel(String label) {
		JTextView tv = addLabelGeneric(label);

		return tv;
	}

	@ProtocoderScript
	@APIParam(params = { "label", "x", "y", "w", "h" })
	public JTextView addLabel(String label, int x, int y, int w, int h) {
		JTextView tv = addLabelGeneric(label);
		addViewAbsolute(tv, x, y, w, h);

		return tv;
	}

	/**
	 * Adds an Input dialog
	 */
	@APIParam(params = { "label", "function()" })
	public JEditText addInput(String label, final addGenericInputCB callbackfn) {
		JEditText et = addGenericInput(label, callbackfn);

		return et;
	}

	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public JEditText addInput(String label, int x, int y, int w, int h, final addGenericInputCB callbackfn) {
		JEditText et = addGenericInput(label, callbackfn);
		addViewAbsolute(et, x, y, w, h);

		return et;
	}

	/**
	 * Adds a toggle button
	 * 
	 */
	@ProtocoderScript
	@APIParam(params = { "label", "checked", "function(checked)" })
	public JToggleButton addToggle(final String label, boolean initstate, final addGenericToggleCB callbackfn) {

		JToggleButton tb = addGenericToggle(label, initstate, callbackfn);

		return tb;
	}

	@ProtocoderScript
	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public JToggleButton addToggle(final String label, int x, int y, int w, int h, boolean initstate,
			final addGenericToggleCB callbackfn) {

		JToggleButton tb = addGenericToggle(label, initstate, callbackfn);
		addViewAbsolute(tb, x, y, w, h);

		return tb;
	}

	/**
	 * Adds a checkbox
	 * 
	 */
	@APIParam(params = { "label", "checked", "function(checked)" })
	public JCheckBox addCheckbox(String label, boolean initstate, final addGenericCheckboxCB callbackfn) {
		JCheckBox cb = addGenericCheckbox(label, initstate, callbackfn);

		return cb;
	}

	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public JCheckBox addCheckbox(String label, int x, int y, int w, int h, boolean initstate,
			final addGenericCheckboxCB callbackfn) {
		JCheckBox cb = addGenericCheckbox(label, initstate, callbackfn);
		addViewAbsolute(cb, x, y, w, h);

		return cb;
	}

	/**
	 * Adds a switch
	 * 
	 */
	@APIParam(params = { "checked", "function(checked)" })
	public JSwitch addSwitch(boolean initstate, final addGenericSwitchCB callbackfn) {
		JSwitch s = addGenericSwitch(initstate, callbackfn);

		return s;
	}

	@APIParam(params = { "x", "y", "w", "h", "checked", "function(checked)" })
	public JSwitch addSwitch(int x, int y, int w, int h, boolean initstate, final addGenericSwitchCB callbackfn) {

		JSwitch s = addGenericSwitch(initstate, callbackfn);
		addViewAbsolute(s, x, y, w, h);

		return s;
	}

	/**
	 * Adds a radiobutton
	 * 
	 */
	@APIParam(params = { "label", "checked", "function(checked)" })
	public JRadioButton addRadioButton(String label, boolean initstate, final addGenericRadioButtonCB callbackfn) {
		JRadioButton rb = addGenericRadioButton(label, initstate, callbackfn);

		return rb;
	}

	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public JRadioButton addRadioButton(String label, int x, int y, int w, int h, boolean initstate,
			final addGenericRadioButtonCB callbackfn) {

		JRadioButton rb = addGenericRadioButton(label, initstate, callbackfn);
		addViewAbsolute(rb, x, y, w, h);

		return rb;
	}

	/**
	 * Adds an imageview
	 * 
	 */
	@APIParam(params = { "imagePath" })
	public JImageView addImage(String imagePath) {
		final JImageView iv = addGenericImage(imagePath);

		return iv;

	}

	@APIParam(params = { "x", "y", "w", "h", "imagePath" })
	public JImageView addImage(int x, int y, int w, int h, String imagePath) {

		final JImageView iv = addGenericImage(imagePath);
		addViewAbsolute(iv, x, y, w, h);

		return iv;

	}

	@ProtocoderScript
	@APIParam(params = { "min", "max" })
	public JPlotView addPlot(int min, int max) {
		JPlotView jPlotView = addGenericPlot(min, max);

		return jPlotView;
	}

	@ProtocoderScript
	@APIParam(params = { "x", "y", "w", "h", "min", "max" })
	public JPlotView addPlot(int x, int y, int w, int h, int min, int max) {
		JPlotView jPlotView = addGenericPlot(min, max);
		addViewAbsolute(jPlotView, x, y, w, h);

		return jPlotView;
	}

	/*
	 * ---------- aqui
	 */

	/**
	 * Adds an image button with the default background
	 * 
	 */
	@Override
	@APIParam(params = { "x", "y", "w", "h", "imageName", "function()" })
	public JImageButton addImageButton(int x, int y, int w, int h, String imagePath, final addImageButtonCB callbackfn) {
		return addImageButton(x, y, w, h, imagePath, "", false, callbackfn);
	}

	@Override
	@APIParam(params = { "x", "y", "w", "h", "imageNameNotPressed", "imageNamePressed", "function()" })
	public JImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final addImageButtonCB callbackfn) {
		return addImageButton(x, y, w, h, imgNotPressed, imgPressed, false, callbackfn);
	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 */
	@Override
	@APIParam(params = { "x", "w", "h", "imageNameNotPressed", "imagePressed", "boolean", "function()" })
	public JImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final boolean hideBackground, final addImageButtonCB callbackfn) {

		initializeLayout();
		// Create and position the image button
		final JImageButton ib = new JImageButton(a.get());

		ib.setScaleType(ScaleType.FIT_XY);
		// Hide the background if desired
		if (hideBackground) {
			ib.setBackgroundResource(0);
		}

		// Add image asynchronously
		new SetImageTask(ib).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator + imgNotPressed);

		// Set on click behavior
		ib.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				MLog.d(TAG, "" + event.getAction());
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					MLog.d(TAG, "down");
					if (hideBackground) {
						ib.getDrawable().setColorFilter(0xDD00CCFC, PorterDuff.Mode.MULTIPLY);

					}
					callbackfn.event();

				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					MLog.d(TAG, "up");
					if (hideBackground) {
						ib.getDrawable().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);

					}
				}

				return true;
			}
		});

		// Add the view
		addViewAbsolute(ib, x, y, w, h);

		return ib;

	}

	@Override
	@ProtocoderScript
	@APIParam(params = { "x", "y", "w", "h" })
	public JCanvasView addCanvas(int x, int y, int w, int h) {
		initializeLayout();

		JCanvasView sv = new JCanvasView(a.get(), w, h);
		// Add the view
		addViewAbsolute(sv, x, y, w, h);

		return sv;
	}

	// private PApplet papplet;

	public PApplet addProcessing(int x, int y, int w, int h) {

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fl.setId(122);

		// Add the view
		addViewAbsolute(fl, x, y, w, h);

		QQ p = new QQ();
		p.setContext(this);

		// ProcessingSketchDefaultRenderer1 p = new
		// ProcessingSketchDefaultRenderer1();

		FragmentTransaction ft = a.get().getSupportFragmentManager().beginTransaction();
		ft.add(fl.getId(), p, String.valueOf(fl.getId()));
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.addToBackStack(null);
		ft.commit();

		return p;

	}

	@APIParam(params = { "x", "y", "w", "h" })
	public JWebView addWebView(int x, int y, int w, int h) {
		initializeLayout();
		JWebView webView = new JWebView(a.get());
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setFocusable(true);
		webView.setFocusableInTouchMode(true);

		webView.clearCache(false);
		webView.setBackgroundColor(0x00000000);

		webView.requestFocus(View.FOCUS_DOWN);
		webView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}
				return false;
			}
		});

		webView.addJavascriptInterface(new JApp(a.get()), "app");

		addViewAbsolute(webView, x, y, w, h);
		// webview.loadData(content, "text/html", "utf-8");

		return webView;

	}

	/**
	 * Adds an image with the option to hide the default background
	 */
	// @APIParam(params = { "type" })
	// public JCamera addCameraView(int type) {
	// // JCamera camera = addGenericCamera(type);
	//
	// return camera;
	// }

	/**
	 * Adds an image with the option to hide the default background
	 */
	// @APIParam(params = { "type", "x", "y", "w", "h" })
	// public JCamera addCameraView(int type, int x, int y, int w, int h) {
	// // JCamera camera = addGenericCamera(type);
	// FrameLayout fl = addGenericCamera(type);
	//
	// // Add the view
	// addViewAbsolute(fl, x, y, w, h);
	//
	// JCamera jCamera = new JCamera(a.get(), cameraFragment);
	//
	// return camera;
	// }
	//
	/**
	 * Adds an image with the option to hide the default background
	 */
	@APIParam(params = { "type", "x", "y", "w", "h" })
	public JCamera addCameraView(int type, int x, int y, int w, int h) {

		JCamera jCamera = addGenericCamera(type, x, y, w, h);

		return jCamera;
	}

	/**
	 * Adds a video
	 * 
	 * @author victordiaz
	 */
	@APIParam(params = { "videoFileName" })
	public JVideo addVideoView(final String videoFile) {
		JVideo video = addGenericVideo(videoFile);

		return video;
	}

	/**
	 * Adds a video
	 * 
	 * @author victordiaz
	 */
	@APIParam(params = { "videoFileName", "x", "y", "w", "h" })
	public JVideo addVideoView(final String videoFile, int x, int y, int w, int h) {
		JVideo video = addGenericVideo(videoFile);
		addViewAbsolute(video, x, y, w, h);

		return video;
	}

	/**
	 * Adds a map
	 * 
	 * @author victordiaz
	 */
	@APIParam(params = { "" })
	public JMap addMap() {
		JMap mapView = addGenericMap();
		return mapView;
	}

	/**
	 * Adds a map
	 * 
	 * @author victordiaz
	 */
	@APIParam(params = { "x", "y", "w", "h" })
	public JMap addMap(int x, int y, int w, int h) {
		JMap mapView = addGenericMap();

		addViewAbsolute(mapView, x, y, w, h);
		return mapView;
	}

	/**
	 * yesnoDialog
	 * 
	 * @author victordiaz
	 * 
	 * @param msg
	 */

	// --------- yesno dialog ---------//
	interface yesnoDialogCB {
		void event(boolean b);
	}

	@APIParam(params = { "title", "function(boolean)" })
	public void yesnoDialog(String title, final yesnoDialogCB callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callbackfn.event(true);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				callbackfn.event(false);
			}
		});

		builder.show();
	}

	/**
	 * inputDialog
	 * 
	 * @author victordiaz
	 * 
	 * @param title
	 */

	// --------- inputDialog ---------//
	interface inputDialogCB {
		void event(String text);
	}

	@APIParam(params = { "title", "function(text)" })
	public void inputDialog(String title, final inputDialogCB callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		final EditText input = new EditText(a.get());

		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				callbackfn.event(text);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		builder.show();
	}

	/**
	 * choiceDialog
	 * 
	 * @author victordiaz
	 * 
	 * @param title
	 * @param choices
	 */

	// --------- choiceDialog ---------//
	interface choiceDialogCB {
		void event(String string);
	}

	@APIParam(params = { "title", "arrayStrings", "function(text)" })
	public void choiceDialog(String title, final String[] choices, final choiceDialogCB callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		// Set up the buttons
		builder.setItems(choices, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callbackfn.event(choices[which]);

			}
		});

		builder.show();
	}

	@APIParam(params = { "imageName" })
	public void takeScreenshot(String imagePath) {
		AndroidUtils.takeScreenshot(AppRunnerSettings.get().project.getStoragePath(), imagePath, uiAbsoluteLayout);
	}

	@APIParam(params = { "imageName", "view" })
	public void takeScreenshot(String imagePath, View v) {
		AndroidUtils.takeScreenshotView(AppRunnerSettings.get().project.getStoragePath(), imagePath, v);
	}

	@APIParam(params = { "fontFile" })
	public Typeface loadFont(String fontName) {
		return Typeface.createFromFile(AppRunnerSettings.get().project.getStoragePath() + File.separator + fontName);
	}

	@APIParam(params = { "View", "Typeface" })
	public void setFont(View v, Typeface f) {

		if (v instanceof JButton) {
			((JButton) v).setTypeface(f);
		} else if (v instanceof JTextView) {
			((JTextView) v).setTypeface(f);
		}
	}

	// it only works with absolute layout and only when
	// a layout is been used
	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void showVirtualKeys(boolean show) {
		InputMethodManager imm = (InputMethodManager) a.get().getSystemService(a.get().INPUT_METHOD_SERVICE);

		if (show) {
			imm.showSoftInput(a.get().getCurrentFocus(), InputMethodManager.SHOW_FORCED);
			uiAbsoluteLayout.setFocusable(true);
			uiAbsoluteLayout.setFocusableInTouchMode(true);

		} else {
			imm.hideSoftInputFromWindow(a.get().getCurrentFocus().getWindowToken(), 0);
		}
	}

	// --------- onKeyDown ---------//
	interface onKeyDownCB {
		void event(int eventType);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(keyNumber)" })
	public void onKeyDown(final onKeyDownCB fn) {
		onKeyDownfn = fn;
	}

	// --------- onKeyUp ---------//
	interface onKeyUpCB {
		void event(int eventType);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(keyNumber)" })
	public void onKeyUp(final onKeyUpCB fn) {
		onKeyUpfn = fn;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void enableVolumeKeys(boolean b) {
		a.get().keyVolumeEnabled = b;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void enableBackKey(boolean b) {
		a.get().keyBackEnabled = b;
	}

	public interface onKeyListener {
		public void onKeyDown(int keyCode);

		public void onKeyUp(int keyCode);
	}

	public void stop() {

	}

	// @JavascriptInterface
	// @APIParam( params = {"milliseconds", "function()"} )
	// public void startTrackingTouches(String b) {
	// }

}