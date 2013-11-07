/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
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

package com.makewithmoto.apprunner.api;

import java.io.File;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.makewithmoto.R;
import com.makewithmoto.apidoc.annotation.APIMethod;
import com.makewithmoto.apidoc.annotation.APIParam;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.apprunner.AppRunnerSettings;
import com.makewithmoto.apprunner.JInterface;
import com.makewithmoto.apprunner.JavascriptInterface;
import com.makewithmoto.apprunner.api.widgets.JButton;
import com.makewithmoto.apprunner.api.widgets.JCanvasView;
import com.makewithmoto.apprunner.api.widgets.JCheckBox;
import com.makewithmoto.apprunner.api.widgets.JEditText;
import com.makewithmoto.apprunner.api.widgets.JImageButton;
import com.makewithmoto.apprunner.api.widgets.JImageView;
import com.makewithmoto.apprunner.api.widgets.JPlotView;
import com.makewithmoto.apprunner.api.widgets.JRadioButton;
import com.makewithmoto.apprunner.api.widgets.JSeekBar;
import com.makewithmoto.apprunner.api.widgets.JSwitch;
import com.makewithmoto.apprunner.api.widgets.JTextView;
import com.makewithmoto.apprunner.api.widgets.JToggleButton;
import com.makewithmoto.apprunner.api.widgets.JWebView;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.fragments.CameraFragment;
import com.makewithmoto.fragments.VideoPlayerFragment;
import com.makewithmoto.fragments.VideoPlayerFragment.VideoListener;
import com.makewithmoto.fragments.VideoTextureFragment;
import com.makewithmoto.utils.AndroidUtils;
import com.makewithmoto.views.HoloCircleSeekBar;
import com.makewithmoto.views.HoloCircleSeekBar.OnCircleSeekBarChangeListener;
import com.makewithmoto.views.PlotView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class JUI extends JInterface {

	String TAG = "JUI";
	final static int MAXVIEW = 1000;
	FrameLayout mMainLayout;
	Boolean isMainLayoutSetup = false;
	int viewCount = 0;
	View viewArray[] = new View[MAXVIEW];

	public int canvasWidth;
	public int canvasHeight;
	private ScrollView sv;
	public int screenWidth;
	public int screenHeight;
	private int theme;
	private RelativeLayout bgRelativeLayout;
	private ImageView bgImageView;
	private int w;
	private int h;

	public JUI(Activity a) {
		super(a);

		screenWidth = ((BaseActivity) a).screenWidth;
		screenHeight = ((BaseActivity) a).screenHeight;
		
		w = ((BaseActivity) a).screenWidth;
		h = ((BaseActivity) a).screenHeight;
	}

	private void initializeLayout() {
		if (!isMainLayoutSetup) {
			// We need to let the view scroll, so we're creating a scroll view
			sv = new ScrollView(a.get());

			sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			sv.setFillViewport(true);

			// Create the main layout. This is where all the items actually go
			mMainLayout = new FrameLayout(a.get());
			mMainLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			sv.addView(mMainLayout);

			// Set the content view
			bgRelativeLayout = (RelativeLayout) a.get().findViewById(
					R.id.user_ui);
			bgImageView = (ImageView) a.get().findViewById(R.id.bg);
			bgRelativeLayout.addView(sv);
			isMainLayoutSetup = true;
		}
	}

	private void addView(View v, int x, int y, int w, int h) {
		positionView(v, x, y, w, h);

		v.setAlpha(0);
		v.setRotationX(-30);
		viewArray[viewCount++] = v;
		v.animate().alpha(1).rotationX(0).setDuration(500)
				.setStartDelay((long) (100 * (1 + viewCount)));
		mMainLayout.addView(v);

		/*
		 * final ViewTreeObserver vto = sv.getViewTreeObserver();
		 * OnGlobalLayoutListener globalLayoutListener = new
		 * OnGlobalLayoutListener() {
		 * 
		 * @SuppressWarnings("deprecation")
		 * 
		 * @Override public void onGlobalLayout() { final ViewTreeObserver obs =
		 * sv.getViewTreeObserver();
		 * 
		 * canvasWidth = mMainLayout.getWidth(); canvasHeight =
		 * mMainLayout.getHeight(); //
		 * observer.removeGlobalOnLayoutListener(this);
		 * 
		 * if (AppSettings.CURRENT_VERSION > Build.VERSION.SDK_INT) {
		 * obs.removeOnGlobalLayoutListener(this); } else {
		 * obs.removeGlobalOnLayoutListener(this); } } };
		 * vto.addOnGlobalLayoutListener(globalLayoutListener);
		 */
	}

	public void removeAll() {
		mMainLayout.removeAllViews();
	}

	/**
	 * This is what we use to actually position and size the views
	 */
	private void positionView(View v, int x, int y, int w, int h) {
		if (w == -1)
			w = LayoutParams.WRAP_CONTENT;
		if (h == -1)
			h = LayoutParams.WRAP_CONTENT;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
		params.leftMargin = x;
		params.topMargin = y;
		v.setLayoutParams(params);
	}

	@JavascriptInterface
	@APIMethod(description = "Uses a DARK / BLUE / NONE theme for some widgets", example = "ui.setTheme(\"DARK\"); ")
	@APIParam( params = {"themeName"} )
	public void setTheme(String theme) {
		if (theme.equals("DARK")) {
			this.theme = R.drawable.theme_rounded_rect_dark;
		} else if (theme.equals("BLUE")) {
			this.theme = R.drawable.theme_rounded_rect_blue;
		} else if (theme.equals("NONE")) {
			theme = null;
		}

	}

	public void themeWidget(View v) {
		v.setBackgroundResource(theme);
	}
	
	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam( params = {"function(x, y)"} )
	public void onTouch(final String callbackfn) {
		FrameLayout fl = (FrameLayout) ((AppRunnerActivity) a.get()).findViewById(R.id.touchOverlay);
		
		fl.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// MainActivity.mMapIsTouched = true;
					break;
				case MotionEvent.ACTION_UP:
					// MainActivity.mMapIsTouched = false;
					break;

				case MotionEvent.ACTION_MOVE:
					int x = (int) event.getX();
					int y = (int) event.getY();
					Log.d(TAG, "" + x + " " + y);

					callback(callbackfn, x, y);
					//Point point = new Point(x, y);
					//LatLng latLng = map.getProjection().fromScreenLocation(point);
					//Point pixels = map.getProjection().toScreenLocation(latLng);;
					//mapCustomFragment.setTouch(latLng);
					
					//Log.d("qq2", x + " " + y + " " + latLng.latitude + " " + latLng.longitude);
					break;
				}

				return true; //a.get().dispatchTouchEvent(event);
			}
		});
	}

	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam( params = {"titleName"} )
	public void setTitle(String title) {
		((AppRunnerActivity) a.get()).actionBar.setTitle(title);
	}

	@JavascriptInterface
	@APIMethod(description = " ", example = "")
	@APIParam( params = {"subtitleName"} )
	public void setSubtitle(String title) {
		((AppRunnerActivity) a.get()).actionBar.setSubtitle(title);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"boolean"} )
	public void showTitleBar(Boolean b) {
		if (b)
			((AppRunnerActivity) a.get()).actionBar.show();
		else {
			((AppRunnerActivity) a.get()).actionBar.hide();
		}
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"r", "g", "b"} )
	public void setTitleBgColor(int r, int g, int b) {
		ColorDrawable d = new ColorDrawable();
		int c = Color.rgb(r, g, b);
		d.setColor(c);
		((AppRunnerActivity) a.get()).actionBar.setBackgroundDrawable(d);
	} 
	
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"r", "g", "b"} )
	public void setTitleTextColor(int r, int g, int b) {
		ColorDrawable d = new ColorDrawable();
		int c = Color.rgb(r, g, b);
		d.setColor(c);
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView yourTextView = (TextView)a.get().findViewById(titleId);
		yourTextView.setTextColor(c);
	} 
	
	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"imageName"} )
	public void setTitleImage(String imagePath) {
		Bitmap myBitmap = BitmapFactory.decodeFile(AppRunnerSettings.get().project.getFolder() + imagePath);
		Drawable icon = new BitmapDrawable(a.get().getResources(), myBitmap);

		((AppRunnerActivity) a.get()).actionBar.setIcon(icon);
	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"boolean"} )
	public void showHomeBar(boolean b) {
		((AppRunnerActivity) a.get()).showHomeBar(b);
	}

//
//	@JavascriptInterface
//	@APIMethod(description = "", example = "")
//	public void setOrientation() {
//		// ((AppRunnerActivity) a.get()).setOr();
//	}

	@JavascriptInterface
	@APIMethod(description = "", example = "")
	@APIParam( params = {"text", "duration"} )
	public void toast(String text, int duration) {
		Toast.makeText(a.get(), text, duration).show();
	}

	/**
	 * Adds a button to the view
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param callbackfn
	 */
	@JavascriptInterface
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam( params = {"label", "x", "y", "w", "h", "function(progress)"} )
	public JButton addButton(String label, int x, int y, int w, int h,
			final String callbackfn) {
		initializeLayout();

		// Create the button
		JButton b = new JButton(a.get());
		b.setText(label);

		// Set on click behavior
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Callback should capture the checked state
				callback(callbackfn);
			}
		});

		// Add the view to the layout
		addView(b, x, y, w, h);

		return b;
	}

	/**
	 * Adds a circular seekbar or picker
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param callbackfn
	 */
	@JavascriptInterface
	@APIParam( params = {"x", "y", "w", "h, function(progress)"} )
	public HoloCircleSeekBar addKnob(int x, int y, int w, int h,
			final String callbackfn) {
		initializeLayout();

		HoloCircleSeekBar pkr = new HoloCircleSeekBar(a.get());

		// Add the change listener
		pkr.setOnSeekBarChangeListener(new OnCircleSeekBarChangeListener() {

			@Override
			public void onProgressChanged(HoloCircleSeekBar seekBar,
					int progress, boolean fromUser) {

				// TODO Callback should capture the checked state
				callback(callbackfn, progress);
			}
		});

		// Add the view
		addView(pkr, x, y, w, h);

		return pkr;
	}

	/**
	 * Adds a seekbar with a callback function
	 * 
	 * @param max
	 * @param progress
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param callbackfn
	 */
	// We'll add in the circular view as a nice to have later once all the other
	// widgets are handled.
	@JavascriptInterface
	@APIParam( params = {"x", "y", "w", "h", "max", "progress", "function(progress)"} )
	public JSeekBar addSlider(int x, int y, int w, int h, int max,
			int progress, final String callbackfn) {

		initializeLayout();
		// Create the position the view
		JSeekBar sb = new JSeekBar(a.get());
		sb.setMax(max);
		sb.setProgress(progress);

		// Add the change listener
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				callback(callbackfn, progress);
			}
		});

		// Add the view
		addView(sb, x, y, w, -1);

		return sb;

	}

	/**
	 * Adds a TextView. Note that the user doesn't specify font size
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	@JavascriptInterface
	@APIParam( params = {"label", "x", "y", "w", "h"} )
	public JTextView addLabel(String label, int x, int y, int w, int h) {
		int defaultTextSize = 16;
		return addLabel(label, x, y, w, h, defaultTextSize);
	}

	/**
	 * Adds a label, allowing the user to specify font size
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param textSize
	 */
	@JavascriptInterface
	@APIParam( params = {"label", "x", "y", "w", "h", "textSize"} )
	public JTextView addLabel(String label, int x, int y, int w, int h,
			int textSize) {

		initializeLayout();
		// Create the TextView
		JTextView tv = new JTextView(a.get());
		tv.setText(label);
		tv.setTextSize((float) textSize);
		// tv.setTypeface(null, Typeface.BOLD);
		// tv.setGravity(Gravity.CENTER_VERTICAL);
		// tv.setPadding(2, 0, 0, 0);
		// tv.setTextColor(a.get().getResources().getColor(R.color.theme_text_white));

		// Add the view
		themeWidget(tv);
		addView(tv, x, y, w, h);

		return tv;
	}

	/**
	 * Adds an Input dialog
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param callbackfn
	 */
	@APIParam( params = {"label", "x", "y", "w", "h", "function()"} )
	public JEditText addInput(String label, int x, int y, int w, int h,
			final String callbackfn) {

		initializeLayout();
		// Create view
		JEditText et = new JEditText(a.get());
		et.setHint(label);

		// On focus lost, we need to call the callback function
		et.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					callback(callbackfn);
				}
			}
		});

		// Add the view
		themeWidget(et);
		addView(et, x, y, w, h);

		return et;

	}

	/**
	 * Adds a toggle button
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param initstate
	 * @param callbackfn
	 */
	@JavascriptInterface
	@APIParam( params = {"label", "x", "y", "w", "h", "checked", "function(checked)"} )
	public JToggleButton addToggle(final String label, int x, int y, int w,
			int h, boolean initstate, final String callbackfn) {
		initializeLayout();
		// Create the view
		JToggleButton tb = new JToggleButton(a.get());
		tb.setChecked(initstate);
		tb.setText(label);

		// Add change listener
		tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				callback(callbackfn, isChecked);
			}
		});

		// Add the view
		addView(tb, x, y, w, h);

		return tb;
	}

	/**
	 * Adds a checkbox
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param initstate
	 * @param callbackfn
	 */
	@APIParam( params = {"label", "x", "y", "w", "h", "checked", "function(checked)"} )
	public JCheckBox addCheckbox(String label, int x, int y, int w, int h,
			boolean initstate, final String callbackfn) {

		initializeLayout();
		// Adds a checkbox and set the initial state as initstate. if the button
		// state changes, call the callbackfn
		JCheckBox cb = new JCheckBox(a.get());
		cb.setChecked(initstate);
		cb.setText(label);

		// Add the click callback
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn, isChecked);
			}
		});

		// Add the view
		themeWidget(cb);
		addView(cb, x, y, w, h);

		return cb;

	}

	/**
	 * Adds a switch
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param initstate
	 * @param callbackfn
	 */
	@APIParam( params = {"x", "y", "w", "h", "checked", "function(checked)"} )
	public JSwitch addSwitch(int x, int y, int w, int h, boolean initstate,
			final String callbackfn) {

		initializeLayout();
		// Adds a switch. If the state changes, we'll call the callback function
		JSwitch s = new JSwitch(a.get());
		s.setChecked(initstate);

		// Add the click callback
		s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn, isChecked);
			}
		});

		// Add the view
		addView(s, x, y, w, h);

		return s;
	}

	/**
	 * Adds a radiobutton
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param initstate
	 * @param callbackfn
	 */
	@APIParam( params = {"label", "x", "y", "w", "h", "checked", "function(checked)"} )
	public JRadioButton addRadioButton(String label, int x, int y, int w,
			int h, boolean initstate, final String callbackfn) {

		initializeLayout();
		// Create and position the radio button
		JRadioButton rb = new JRadioButton(a.get());
		rb.setChecked(initstate);
		rb.setText(label);

		// Add the click callback
		rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn, isChecked);
			}
		});

		// Add the view
		themeWidget(rb);
		addView(rb, x, y, w, h);

		return rb;
	}

	/**
	 * Adds an imageview
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imagePath
	 */
	@APIParam( params = {"x", "y", "w", "h", "imageName"} )
	public JImageView addImage(int x, int y, int w, int h, String imagePath) {

		initializeLayout();
		// Create and position the image view
		final JImageView iv = new JImageView(a.get());

		// Add the image from file
		new SetImageTask(iv).execute(AppRunnerSettings.get().project
				.getFolder() + File.separator + imagePath);

		// Add the view
		iv.setBackgroundColor(0x33b5e5);
		addView(iv, x, y, w, h);

		return iv;

	}

	/**
	 * Adds an image from a URL
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param address
	 * @return
	 */
	//TODO change to a normal
	@APIParam( params = {"x", "y", "w", "h", "url"} )
	public JImageView addWebImage(int x, int y, int w, int h, String address) {

		initializeLayout();
		// Create and position the image view
		final JImageView iv = new JImageView(a.get());

		// Add image asynchronously
		new DownloadImageTask(iv).execute(address);

		// Add the view
		addView(iv, x, y, w, h);

		return iv;
	}

	/**
	 * Adds an image button with the default background
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imagePath
	 * @param callbackfn
	 */
	@APIParam( params = {"x", "y", "w", "h", "imageName", "function()"} )
	public JImageButton addImageButton(int x, int y, int w, int h,
			String imagePath, final String callbackfn) {
		return addImageButton(x, y, w, h, imagePath, "", false, callbackfn);
	}

	@APIParam( params = {"x", "y", "w", "h", "imageNameNotPressed", "imageNamePressed", "function()"} )
	public JImageButton addImageButton(int x, int y, int w, int h,
			String imgNotPressed, String imgPressed, final String callbackfn) {
		return addImageButton(x, y, w, h, imgNotPressed, imgPressed, false,
				callbackfn);
	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imgNotPressed
	 * @param hideBackground
	 * @param callbackfn
	 */
	@APIParam( params = {"x", "w", "h", "imageNameNotPressed", "imagePressed", "boolean", "function()"} )
	public JImageButton addImageButton(int x, int y, int w, int h,
			String imgNotPressed, String imgPressed,
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
		new SetImageTask(ib).execute(AppRunnerSettings.get().project
				.getFolder() + File.separator + imgNotPressed);

		// Set on click behavior
		ib.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "" + event.getAction());
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					Log.d(TAG, "down");
					if (hideBackground) {
						ib.getDrawable().setColorFilter(0xDD00CCFC,
								PorterDuff.Mode.MULTIPLY);

					}
					callback(callbackfn);

				} else if (action == MotionEvent.ACTION_UP
						|| action == MotionEvent.ACTION_CANCEL) {
					Log.d(TAG, "up");
					if (hideBackground) {
						ib.getDrawable().setColorFilter(0xFFFFFFFF,
								PorterDuff.Mode.MULTIPLY);

					}
				}

				return true;
			}
		});

		// Add the view
		addView(ib, x, y, w, h);

		return ib;

	}

	public void addTabs() {

	}

	/**
	 * Set padding on the entire view
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	@APIParam( params = {"left", "top", "right", "bottom"} )
	public void setPadding(int left, int top, int right, int bottom) {
		initializeLayout();
		mMainLayout.setPadding(left, top, right, bottom);
	}

	/**
	 * Set background color for the main layout via int
	 * 
	 * @param color
	 */
	@APIParam( params = {"hexColor"} )
	public void backgroundColor(int color) {
		initializeLayout();
		bgRelativeLayout.setBackgroundColor(color);
	}

	/**
	 * The more common way to set background color, set bg color via RGB
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 */
	@APIParam( params = {"r", "g", "b"} )
	public void backgroundColor(int red, int green, int blue) {
		initializeLayout();
		bgRelativeLayout.setBackgroundColor(Color.rgb(red, green, blue));
	}

	/**
	 * Set a background image
	 * 
	 * @param imagePath
	 */
	@APIParam( params = {"imageName"} )
	public void backgroundImage(String imagePath) {
		initializeLayout();
		// Add the bg image asynchronously
		new SetBgImageTask(bgImageView).execute(AppRunnerSettings.get().project
				.getFolder() + File.separator + imagePath);

	}

	@JavascriptInterface
	@APIParam( params = {"x", "y", "w", "h", "min", "max"} )
	public JPlotView addPlot(int x, int y, int w, int h, int min, int max) {
		initializeLayout();
		PlotView plotView = new PlotView(a.get());
		positionView(plotView, x, y, w, h);

		// Add the view
		addView(plotView, x, y, w, h);

		JPlotView jPlotView = new JPlotView(a.get(), plotView, min, max);

		return jPlotView;
	}

	@JavascriptInterface
	@APIParam( params = {"x", "y", "w", "h"} )
	public JCanvasView addCanvas(int x, int y, int w, int h) {
		initializeLayout();

		JCanvasView sv = new JCanvasView(a.get(), w, h);
		positionView(sv, x, y, w, h);
		// Add the view
		addView(sv, x, y, w, h);

		return sv;
	}

	// private PApplet papplet;

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 * @author victordiaz
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * 
	 *            public PApplet addProcessing(PApplet papplet, int x, int y,
	 *            int w, int h) {
	 * 
	 *            initializeLayout();
	 * 
	 *            // Create the main layout. This is where all the items
	 *            actually go FrameLayout fl = new FrameLayout(a.get());
	 *            fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
	 *            LayoutParams.MATCH_PARENT)); fl.setId(12);
	 * 
	 *            // Add the view positionView(fl, x, y, w, h); addView(fl);
	 * 
	 *            //papplet = new PApplet(); Log.d("processing", "" + papplet);
	 * 
	 *            FragmentTransaction ft = a.get().getSupportFragmentManager()
	 *            .beginTransaction(); ft.add(fl.getId(), papplet,
	 *            String.valueOf(fl.getId()));
	 *            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	 *            ft.setCustomAnimations(android.R.anim.fade_in,
	 *            android.R.anim.fade_out); ft.addToBackStack(null);
	 *            ft.commit();
	 * 
	 * 
	 *            return papplet;
	 * 
	 *            }
	 */

	@APIParam( params = {"x", "y", "w", "h"} )
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

		addView(webView, x, y, w, h);
		// webview.loadData(content, "text/html", "utf-8");

		return webView;

	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 * @author victordiaz
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	@APIParam( params = {"type", "x", "y", "w", "h"} )
	public JCamera addCameraView(int type, /* int filter, */int x, int y,
			int w, int h) {

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		fl.setId(12345);

		// Add the view
		addView(fl, x, y, w, h);

		CameraFragment cameraFragment = new CameraFragment();
		Bundle bundle = new Bundle();
		if (type == 1) {
			bundle.putInt("camera", CameraFragment.MODE_CAMERA_FRONT);
		} else {
			bundle.putInt("camera", CameraFragment.MODE_CAMERA_BACK);
		}

		// if (filter == 1) {
		// bundle.putInt("color", CameraFragment.MODE_COLOR_BW);
		// } else {
		// bundle.putInt("color", CameraFragment.MODE_COLOR_COLOR);
		// }
		cameraFragment.setArguments(bundle);

		FragmentTransaction ft = a.get().getSupportFragmentManager()
				.beginTransaction(); // FIXME: Because we have no tagging
										// system we need to use the int as
										// a // tag, which may cause
										// collisions
		ft.add(fl.getId(), cameraFragment, String.valueOf(fl.getId()));
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.addToBackStack(null);
		ft.commit();

		JCamera jcamera = new JCamera(a.get(), cameraFragment);

		return jcamera;

	}

	/**
	 * yesnoDialog
	 * 
	 * @author victordiaz
	 * 
	 * @param msg
	 */
	@APIParam( params = {"title", "function(boolean)"} )
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
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
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
	@APIParam( params = {"title", "function(text)"} )
	public void inputDialog(String title, final String callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		// Set up the input
		final EditText input = new EditText(a.get());
		// Specify the type of input expected; this, for example, sets the input
		// as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				callback(callbackfn, text);
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
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
	@APIParam( params = {"title", "arrayStrings", "function(text)"} )
	public void choiceDialog(String title, final String[] choices, final String callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);
		
		// Set up the input
		final EditText input = new EditText(a.get());
		// Specify the type of input expected; this, for example, sets the input
		// as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		
		// Set up the buttons
		builder.setItems(choices, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback(callbackfn,  "\"" + choices[which] +  "\"");
				
			}
		});
		
		builder.show();
	}

	/**
	 * Adds a video
	 * 
	 * @author victordiaz
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	@APIParam( params = {"videoFileName", "x", "y", "w", "h"} )
	public JVideo addVideoView(final String videoFile, int x, int y, int w,
			int h) {

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		fl.setId(12345678);

		// Add the view
		addView(fl, x, y, w, h);

		final VideoPlayerFragment fragment = new VideoPlayerFragment();
		fragment.addListener(new VideoListener() {

			@Override
			public void onTimeUpdate(int ms, int totalDuration) {

			}

			@Override
			public void onReady(boolean ready) {
				// fragment.loadResourceVideo("/raw/cityfireflies");
				fragment.loadExternalVideo(AppRunnerSettings.get().project
						.getFolder() + File.separator + videoFile);
				// fragment.setLoop(true);
			}

			@Override
			public void onFinish(boolean finished) {

			}
		});

		FragmentTransaction ft = a.get().getSupportFragmentManager()
				.beginTransaction(); // FIXME: Because we have no tagging
										// system we need to use the int as
										// a // tag, which may cause
										// collisions
		ft.add(fl.getId(), fragment, String.valueOf(fl.getId()));
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.addToBackStack(null);
		ft.commit();

		JVideo jvideo = new JVideo(a.get(), fragment);

		return jvideo;

	}
	/**
	 * Adds a video
	 * 
	 * @author victordiaz
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	@APIParam( params = {"videoFileName", "x", "y", "w", "h"} )
	public JVideo2 addVideoView2(final String videoFile, int x, int y, int w,
			int h) {
		
		initializeLayout();
		
		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		fl.setId(12345678);
		
		// Add the view
		addView(fl, x, y, w, h);
		
		final VideoTextureFragment fragment = new VideoTextureFragment();
		fragment.addListener(new VideoTextureFragment.VideoListener() {
			
			@Override
			public void onTimeUpdate(int ms, int totalDuration) {		
				
			}
			
			@Override
			public void onReady(boolean ready) {
				fragment.loadExternalVideo(AppRunnerSettings.get().project
						.getFolder() + File.separator + videoFile);				
			}
			
			@Override
			public void onFinish(boolean finished) {
				
			}
		});
		
		FragmentTransaction ft = a.get().getSupportFragmentManager()
				.beginTransaction(); // FIXME: Because we have no tagging
		// system we need to use the int as
		// a // tag, which may cause
		// collisions
		ft.add(fl.getId(), fragment, String.valueOf(fl.getId()));
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.addToBackStack(null);
		ft.commit();
		
		JVideo2 jvideo = new JVideo2(a.get(), fragment);
		
		return jvideo;
		
	}

	@APIParam( params = {"imageName"} )
	public void takeScreenshot(String imagePath) {
		AndroidUtils.takeScreenshot(
				AppRunnerSettings.get().project.getFolder(), imagePath,
				mMainLayout);
	}

	//@JavascriptInterface
	//@APIParam( params = {"milliseconds", "function()"} )
	//public void startTrackingTouches(String b) {
	//}

	/**
	 * This class lets us download an image asynchronously without blocking the
	 * UI thread
	 * 
	 * @author ncbq76
	 * 
	 */
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap bmp = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				bmp = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	/**
	 * This class lets us set images from file asynchronously
	 * 
	 * @author ncbq76
	 * 
	 */
	private class SetImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bgImage;

		public SetImageTask(ImageView bmImage) {
			this.bgImage = bmImage;
		}

		protected Bitmap doInBackground(String... paths) {
			String imagePath = paths[0];
			File imgFile = new File(imagePath);
			if (imgFile.exists()) {
				// Get the bitmap with appropriate options
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
				return bmp;
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			bgImage.setImageBitmap(result);
		}
	}

	/**
	 * This class lets us set the background asynchronously
	 * 
	 * @author ncbq76
	 * 
	 */
	// We need to set the bitmap image asynchronously
	private class SetBgImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView fl;

		public SetBgImageTask(ImageView bgImageView) {
			this.fl = bgImageView;
		}

		protected Bitmap doInBackground(String... paths) {
			String imagePath = paths[0];
			File imgFile = new File(imagePath);
			if (imgFile.exists()) {
				// Get the bitmap with appropriate options
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPurgeable = true;
				Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
				return bmp;
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			fl.setImageBitmap(result);
			fl.setScaleType(ScaleType.CENTER_INSIDE);
			// fl.setBackgroundDrawable(d);
		}
	}

}