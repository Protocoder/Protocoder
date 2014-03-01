/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
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
import org.protocoder.apprunner.JavascriptInterface;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.CycleInterpolator;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class JUI extends JUIGeneric {

	String TAG = "JUI";
	private String onNFCfn;

	private String onKeyDownfn;
	private String onKeyUpfn;

	public JUI(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

		((AppRunnerActivity) a).addOnKeyListener(new onKeyListener() {

			@Override
			public void onKeyUp(int keyCode) {
				callback(onKeyDownfn, keyCode);
			}

			@Override
			public void onKeyDown(int keyCode) {
				callback(onKeyUpfn, keyCode);
			}
		});

	}

	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "titleName" })
	public void setTitle(String title) {
		if (noActionBarAllowed) {
			return;
		}

		a.get().setActionBar(null, null);
		a.get().actionBar.setTitle(title);
	}

	@JavascriptInterface
	@APIMethod(description = " ", example = "")
	@APIParam(params = { "subtitleName" })
	public void setSubtitle(String title) {
		if (noActionBarAllowed) {
			return;
		}

		a.get().setActionBar(null, null);
		a.get().actionBar.setSubtitle(title);
	}

	@JavascriptInterface
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

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "r", "g", "b" })
	public void setTitleBgColor(int r, int g, int b) {
		if (noActionBarAllowed) {
			return;
		}
		int c = Color.rgb(r, g, b);
		a.get().setActionBar(c, null);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "r", "g", "b" })
	public void setTitleTextColor(int r, int g, int b) {
		if (noActionBarAllowed) {
			return;
		}

		int c = Color.rgb(r, g, b);
		a.get().setActionBar(null, c);
	}

	@JavascriptInterface
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

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "boolean" })
	public void showHomeBar(boolean b) {
		a.get().showHomeBar(b);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void setFullscreen() {
		noActionBarAllowed = true;
		a.get().setFullScreen();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void setImmersive() {
		noActionBarAllowed = true;
		a.get().setImmersive();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void setLightsOut() {
		a.get().lightsOutMode();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void setLandscape() {
		a.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	public void setPortrait() {
		a.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text" })
	public void toast(String text) {
		Toast.makeText(a.get(), text, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "text", "duration" })
	public void toast(String text, int duration) {
		Toast.makeText(a.get(), text, duration).show();
	}

	/**
	 * Set padding on the entire view
	 * 
	 */
	@JavascriptInterface
	@APIParam(params = { "left", "top", "right", "bottom" })
	public void setPadding(int left, int top, int right, int bottom) {
		initializeLayout();
		uiAbsoluteLayout.setPadding(left, top, right, bottom);
	}

	@JavascriptInterface
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

	@JavascriptInterface
	@APIParam(params = { "View", "x", "y" })
	public void move(View v, float x, float y) {
		v.animate().x(x).setDuration(AppSettings.animSpeed);
		v.animate().y(y).setDuration(AppSettings.animSpeed);

	}

	@JavascriptInterface
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

	@JavascriptInterface
	@APIParam(params = { "View", "num" })
	public void blink(View v, int num) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
		anim.setDuration(1000);
		anim.setInterpolator(new CycleInterpolator(1));
		anim.setRepeatCount(num);
		anim.start();
	}

	@JavascriptInterface
	@APIParam(params = { "View", "degrees" })
	public void rotate(View v, float x) {
		v.animate().rotation(x).setDuration(AppSettings.animSpeed);
	}

	@JavascriptInterface
	@APIParam(params = { "View", "degrees", "degrees", "degrees" })
	public void rotate(View v, float x, float y, float z) {
		v.animate().rotation(x).setDuration(AppSettings.animSpeed);
		// looks weird but it works more consistent
		v.animate().rotationX(y).setDuration(AppSettings.animSpeed);
		v.animate().rotationY(z).setDuration(AppSettings.animSpeed);
	}

	@JavascriptInterface
	@APIParam(params = { "View", "float" })
	public void alpha(View v, float deg) {
		v.animate().alpha(deg).setDuration(AppSettings.animSpeed);
	}

	@JavascriptInterface
	@APIParam(params = { "View", "float" })
	public void scale(View v, float x, float y) {
		v.animate().scaleX(x).setDuration(AppSettings.animSpeed);
		v.animate().scaleY(y).setDuration(AppSettings.animSpeed);
	}

	@JavascriptInterface
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
	@JavascriptInterface
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
	@JavascriptInterface
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
	@JavascriptInterface
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
	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public JButton addButton(String label, final String callbackfn) {
		JButton b = addGenericButton(label, callbackfn);
		return b;
	}

	/**
	 * Adds a button to the view
	 * 
	 */
	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public JButton addButton(String label, int x, int y, int w, int h, final String callbackfn) {
		JButton b = addGenericButton(label, callbackfn);
		addViewAbsolute(b, x, y, w, h);
		return b;
	}

	/**
	 * Adds a touch area
	 * 
	 */
	@JavascriptInterface
	@APIParam(params = { "bShowArea", "function(touching, x, y)" })
	public TouchAreaView addTouchArea(boolean showArea, final String callbackfn) {
		TouchAreaView taV = addGenericTouchArea(showArea, callbackfn);

		return taV;
	}

	@JavascriptInterface
	@APIParam(params = { "x", "y", "w", "h", "bShowArea", "function(touching, x, y)" })
	public TouchAreaView addTouchArea(int x, int y, int w, int h, boolean showArea, final String callbackfn) {
		TouchAreaView taV = addGenericTouchArea(showArea, callbackfn);
		addViewAbsolute(taV, x, y, w, h);

		return taV;
	}

	@JavascriptInterface
	@APIParam(params = { "x", "y", "w", "h", "function(touching, x, y)" })
	public PadView addXYPad(int x, int y, int w, int h, final String callbackfn) {
		PadView taV = addPad(callbackfn);
		addViewAbsolute(taV, x, y, w, h);

		return taV;
	}

	@JavascriptInterface
	@APIParam(params = { "function(touching, x, y)" })
	public PadView addXYPad(final String callbackfn) {
		PadView taV = addPad(callbackfn);

		return taV;
	}

	/**
	 * Adds a circular seekbar
	 * 
	 */

	@JavascriptInterface
	@APIParam(params = { "function(progress)" })
	public HoloCircleSeekBar addKnob(final String callbackfn) {
		HoloCircleSeekBar pkr = addGenericKnob(callbackfn);

		return pkr;
	}

	@JavascriptInterface
	@APIParam(params = { "x", "y", "w", "h, function(progress)" })
	public HoloCircleSeekBar addKnob(int x, int y, int w, int h, final String callbackfn) {
		HoloCircleSeekBar pkr = addGenericKnob(callbackfn);
		addViewAbsolute(pkr, x, y, w, h);

		return pkr;
	}

	/**
	 * Adds a seekbar with a callback function
	 * 
	 */
	@JavascriptInterface
	@APIParam(params = { "max", "progress", "function(progress)" })
	public JSeekBar addSlider(int max, int progress, final String callbackfn) {
		JSeekBar sb = addGenericSlider(max, progress, callbackfn);
		return sb;

	}

	@JavascriptInterface
	@APIParam(params = { "x", "y", "w", "h", "max", "progress", "function(progress)" })
	public JSeekBar addSlider(int x, int y, int w, int h, int max, int progress, final String callbackfn) {
		JSeekBar sb = addGenericSlider(max, progress, callbackfn);
		addViewAbsolute(sb, x, y, w, -1);
		return sb;

	}

	/**
	 * Adds a TextView. Note that the user doesn't specify font size
	 * 
	 */
	@JavascriptInterface
	@APIParam(params = { "label" })
	public JTextView addLabel(String label) {
		JTextView tv = addLabelGeneric(label);

		return tv;
	}

	@JavascriptInterface
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
	public JEditText addInput(String label, final String callbackfn) {
		JEditText et = addGenericInput(label, callbackfn);

		return et;
	}

	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public JEditText addInput(String label, int x, int y, int w, int h, final String callbackfn) {
		JEditText et = addGenericInput(label, callbackfn);
		addViewAbsolute(et, x, y, w, h);

		return et;
	}

	/**
	 * Adds a toggle button
	 * 
	 */
	@JavascriptInterface
	@APIParam(params = { "label", "checked", "function(checked)" })
	public JToggleButton addToggle(final String label, boolean initstate, final String callbackfn) {

		JToggleButton tb = new JToggleButton(a.get());

		return tb;
	}

	@JavascriptInterface
	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public JToggleButton addToggle(final String label, int x, int y, int w, int h, boolean initstate,
			final String callbackfn) {

		JToggleButton tb = addGenericToggle(label, initstate, callbackfn);
		addViewAbsolute(tb, x, y, w, h);

		return tb;
	}

	/**
	 * Adds a checkbox
	 * 
	 */
	@APIParam(params = { "label", "checked", "function(checked)" })
	public JCheckBox addCheckbox(String label, boolean initstate, final String callbackfn) {
		JCheckBox cb = addGenericCheckbox(label, initstate, callbackfn);

		return cb;
	}

	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public JCheckBox addCheckbox(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {
		JCheckBox cb = addGenericCheckbox(label, initstate, callbackfn);
		addViewAbsolute(cb, x, y, w, h);

		return cb;
	}

	/**
	 * Adds a switch
	 * 
	 */
	@APIParam(params = { "checked", "function(checked)" })
	public JSwitch addSwitch(boolean initstate, final String callbackfn) {
		JSwitch s = addGenericSwitch(initstate, callbackfn);

		return s;
	}

	@APIParam(params = { "x", "y", "w", "h", "checked", "function(checked)" })
	public JSwitch addSwitch(int x, int y, int w, int h, boolean initstate, final String callbackfn) {

		JSwitch s = addGenericSwitch(initstate, callbackfn);
		addViewAbsolute(s, x, y, w, h);

		return s;
	}

	/**
	 * Adds a radiobutton
	 * 
	 */
	@APIParam(params = { "label", "checked", "function(checked)" })
	public JRadioButton addRadioButton(String label, boolean initstate, final String callbackfn) {
		JRadioButton rb = addGenericRadioButton(label, initstate, callbackfn);

		return rb;
	}

	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public JRadioButton addRadioButton(String label, int x, int y, int w, int h, boolean initstate,
			final String callbackfn) {

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

	@JavascriptInterface
	@APIParam(params = { "min", "max" })
	public JPlotView addPlot(int min, int max) {
		JPlotView jPlotView = addGenericPlot(min, max);

		return jPlotView;
	}

	@JavascriptInterface
	@APIParam(params = { "x", "y", "w", "h", "min", "max" })
	public JPlotView addPlot(int x, int y, int w, int h, int min, int max) {
		JPlotView jPlotView = addGenericPlot(min, max);
		addViewAbsolute(jPlotView.getView(), x, y, w, h);

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
	public JImageButton addImageButton(int x, int y, int w, int h, String imagePath, final String callbackfn) {
		return addImageButton(x, y, w, h, imagePath, "", false, callbackfn);
	}

	@Override
	@APIParam(params = { "x", "y", "w", "h", "imageNameNotPressed", "imageNamePressed", "function()" })
	public JImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final String callbackfn) {
		return addImageButton(x, y, w, h, imgNotPressed, imgPressed, false, callbackfn);
	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 */
	@Override
	@APIParam(params = { "x", "w", "h", "imageNameNotPressed", "imagePressed", "boolean", "function()" })
	public JImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final boolean hideBackground, final String callbackfn) {

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
				Log.d(TAG, "" + event.getAction());
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					Log.d(TAG, "down");
					if (hideBackground) {
						ib.getDrawable().setColorFilter(0xDD00CCFC, PorterDuff.Mode.MULTIPLY);

					}
					callback(callbackfn);

				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					Log.d(TAG, "up");
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
	@JavascriptInterface
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

		webView.addJavascriptInterface(new JProtocoder(a.get()), "protocoder");

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
	@APIParam(params = { "title", "function(boolean)" })
	public void yesnoDialog(String title, final String callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback(callbackfn, true);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				callback(callbackfn, false);
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
	@APIParam(params = { "title", "function(text)" })
	public void inputDialog(String title, final String callbackfn) {
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
				callback(callbackfn, "\"" + text + "\"");
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
	@APIParam(params = { "title", "arrayStrings", "function(text)" })
	public void choiceDialog(String title, final String[] choices, final String callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		// Set up the buttons
		builder.setItems(choices, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback(callbackfn, "\"" + choices[which] + "\"");

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

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(keyNumber)" })
	public void onKeyDown(final String fn) {
		onKeyDownfn = fn;
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam(params = { "function(keyNumber)" })
	public void onKeyUp(final String fn) {
		onKeyUpfn = fn;
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