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

package org.protocoder.apprunner.api.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.protocoder.R;
import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;
import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.ProtocoderScript;
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
import org.protocoder.base.BaseActivity;
import org.protocoder.fragments.CameraFragment;
import org.protocoder.fragments.CustomVideoTextureView;
import org.protocoder.utils.AndroidUtils;
import org.protocoder.utils.FileIO;
import org.protocoder.utils.MLog;
import org.protocoder.views.HoloCircleSeekBar;
import org.protocoder.views.HoloCircleSeekBar.OnCircleSeekBarChangeListener;
import org.protocoder.views.PadView;
import org.protocoder.views.PadView.TouchEvent;
import org.protocoder.views.TouchAreaView;
import org.protocoder.views.TouchAreaView.OnTouchAreaListener;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class JUIGeneric extends JInterface {

	String TAG = "JUIGeneric";
	// layouts
	final static int MAXVIEW = 2000;
	View viewArray[] = new View[MAXVIEW];
	int viewCount = 0;
	boolean isMainLayoutSetup = false;
	protected FrameLayout uiAbsoluteLayout;
	protected LinearLayout uiLinearLayout;
	protected RelativeLayout holderLayout;
	protected ImageView bgImageView;

	// properties
	public int canvasWidth;
	public int canvasHeight;
	public int cw;
	public int ch;
	private ScrollView sv;
	public int screenWidth;
	public int screenHeight;
	public int sw;
	public int sh;
	protected int theme;
	protected boolean absoluteLayout = true;
	protected boolean noActionBarAllowed = false;
	protected boolean isScrollLayout = true;

	public JUIGeneric(Activity a) {
		super(a);

		screenWidth = ((BaseActivity) a).screenWidth;
		screenHeight = ((BaseActivity) a).screenHeight;

		sw = ((BaseActivity) a).screenWidth;
		sh = ((BaseActivity) a).screenHeight;
	}

	protected void initializeLayout() {
		if (!isMainLayoutSetup) {
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			// this is the structure of the layout
			// parentLayout
			// holderLayout (background color)
			// bgImage (background image)
			// [scrollview] if (isScrollLayout)
			// [uiAbsoluteLayout] if (!isScrollLayout)

			// set the parent
			RelativeLayout parentLayout = new RelativeLayout(a.get());
			parentLayout.setLayoutParams(layoutParams);
			parentLayout.setGravity(Gravity.BOTTOM);
			parentLayout.setBackgroundColor(a.get().getResources().getColor(R.color.transparent));

			// set the holder
			holderLayout = new RelativeLayout(a.get());
			holderLayout.setLayoutParams(layoutParams);
			holderLayout.setBackgroundColor(a.get().getResources().getColor(R.color.transparent));
			parentLayout.addView(holderLayout);

			// We need to let the view scroll, so we're creating a scroll
			// view
			sv = new ScrollView(a.get());
			sv.setLayoutParams(layoutParams);
			sv.setBackgroundColor(a.get().getResources().getColor(R.color.transparent));
			sv.setFillViewport(true);
			// sv.setEnabled(false);
			allowScroll(isScrollLayout);

			if (absoluteLayout) {
				// Create the main layout. This is where all the items actually
				// go
				uiAbsoluteLayout = new FrameLayout(a.get());
				uiAbsoluteLayout.setLayoutParams(layoutParams);
				uiAbsoluteLayout.setBackgroundColor(a.get().getResources().getColor(R.color.transparent));
				sv.addView(uiAbsoluteLayout);
			} else {
				uiLinearLayout = new LinearLayout(a.get());
				uiLinearLayout.setLayoutParams(layoutParams);
				uiLinearLayout.setOrientation(LinearLayout.VERTICAL);
				uiLinearLayout.setBackgroundColor(a.get().getResources().getColor(R.color.transparent));
				uiLinearLayout.setLayoutTransition(new LayoutTransition());
				sv.addView(uiLinearLayout);
				holderLayout.setPadding(AndroidUtils.pixelsToDp(a.get(), 5), 0, AndroidUtils.pixelsToDp(a.get(), 5), 0);
			}

			// background image
			bgImageView = new ImageView(a.get());
			holderLayout.addView(bgImageView, layoutParams);

			// set the layout
			a.get().initLayout();
			a.get().addScriptedLayout(parentLayout);
			holderLayout.addView(sv);

			isMainLayoutSetup = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "boolean" })
	public void setAbsoluteLayout(boolean absoluteLayout) {
		this.absoluteLayout = absoluteLayout;
	}

	@ProtocoderScript
	@APIMethod(description = "Creates a button ", example = "ui.button(\"button\"); ")
	@APIParam(params = { "boolean" })
	public void allowScroll(boolean scroll) {
		if (scroll) {
			sv.setOnTouchListener(null);
		} else {
			sv.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					return true;
				}
			});
		}
		isScrollLayout = scroll;
	}

	protected void addViewAbsolute(View v, int x, int y, int w, int h) {
		positionView(v, x, y, w, h);
		addViewGeneric(v);
		uiAbsoluteLayout.addView(v);
	}

	protected void addViewLinear(View v) {
		addViewGeneric(v);
		uiLinearLayout.addView(v);
	}

	protected void addViewGeneric(View v) {
		v.setAlpha(0);
		v.setRotationX(-30);
		viewArray[viewCount++] = v;
		v.animate().alpha(1).rotationX(0).setDuration(500).setStartDelay(100 * (1 + viewCount));
	}

	public void removeAll() {
		uiAbsoluteLayout.removeAllViews();
		uiLinearLayout.removeAllViews();
	}

	/**
	 * This is what we use to actually position and size the views
	 */
	protected void positionView(View v, int x, int y, int w, int h) {
		if (w == -1) {
			w = LayoutParams.WRAP_CONTENT;
		}
		if (h == -1) {
			h = LayoutParams.WRAP_CONTENT;
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
		params.leftMargin = x;
		params.topMargin = y;
		v.setLayoutParams(params);
	}

	@ProtocoderScript
	@APIMethod(description = "Uses a DARK / BLUE / NONE theme for some widgets", example = "ui.setTheme(\"DARK\"); ")
	@APIParam(params = { "themeName" })
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

	/**
	 * Adds a card holder
	 * 
	 */
	public JCard addGenericCard() {
		initializeLayout();

		JCard card = new JCard(a.get());
		return card;
	}

	// --------- addGenericButton ---------//
	public interface addGenericButtonCB {
		void event();
	}

	public JButton addGenericButton(String label, final addGenericButtonCB callbackfn) {
		initializeLayout();

		// Create the button
		JButton b = new JButton(a.get());
		b.setText(label);

		// Set on click behavior
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callbackfn.event();
			}
		});

		return b;
	}

	// --------- TouchAreaView ---------//
	public interface addGenericTouchAreaCB {
		void event(boolean touching, float x, float y);
	}

	public TouchAreaView addGenericTouchArea(boolean showArea, final addGenericTouchAreaCB callbackfn) {
		initializeLayout();

		TouchAreaView taV = new TouchAreaView(a.get(), showArea);
		taV.setTouchAreaListener(new OnTouchAreaListener() {

			@Override
			public void onTouch(TouchAreaView touchAreaView, boolean touching, float x, float y) {
				callbackfn.event(touching, x, y);
			}
		});

		return taV;
	}

	// --------- addPad (Touch Area) ---------//
	public interface addPadCB {
		void event(JSONArray array);
	}

	public PadView addPad(final addPadCB callbackfn) {
		initializeLayout();

		PadView taV = new PadView(a.get());
		taV.setTouchAreaListener(new PadView.OnTouchAreaListener() {

			@Override
			public void onGenericTouch(HashMap<Integer, TouchEvent> t) {

				JSONArray array = new JSONArray();

				for (Map.Entry<Integer, PadView.TouchEvent> t1 : t.entrySet()) {
					int key = t1.getKey();
					TouchEvent value = t1.getValue();

					JSONObject o = new JSONObject();
					try {
						o.put("id", value.id);
						o.put("x", value.x);
						o.put("y", value.y);
						o.put("action", value.action);
						o.put("type", value.type);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					array.put(o);
				}

				callbackfn.event(array);
			}
		});

		return taV;
	}

	// --------- HoloCircleSeekBar ---------//
	public interface addGenericKnobCB {
		void eval(int progress);
	}

	public HoloCircleSeekBar addGenericKnob(final addGenericKnobCB callbackfn) {
		initializeLayout();

		HoloCircleSeekBar pkr = new HoloCircleSeekBar(a.get());

		// Add the change listener
		pkr.setOnSeekBarChangeListener(new OnCircleSeekBarChangeListener() {

			@Override
			public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {

				// TODO Callback should capture the checked state
				callbackfn.eval(progress);
			}
		});

		return pkr;
	}

	// --------- seekbar ---------//
	public interface addGenericSliderCB {
		void eval(int progress);
	}

	// We'll add in the circular view as a nice to have later once all the other
	// widgets are handled.
	public JSeekBar addGenericSlider(int max, int progress, final addGenericSliderCB callbackfn) {

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
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				callbackfn.eval(progress);
			}
		});

		return sb;

	}

	/**
	 * Adds a TextView. Note that the user doesn't specify font size
	 * 
	 */
	public JTextView addLabelGeneric(String label) {
		// int defaultTextSize = 16;
		// tv.setTextSize((float) textSize);
		JTextView tv = new JTextView(a.get());
		initializeLayout();

		tv.setText(label);
		themeWidget(tv);

		return tv;
	}

	// --------- getRequest ---------//
	public interface addGenericInputCB {
		void event();
	}

	public JEditText addGenericInput(String label, final addGenericInputCB callbackfn) {

		initializeLayout();
		// Create view
		JEditText et = new JEditText(a.get());
		et.setHint(label);

		// On focus lost, we need to call the callback function
		et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					callbackfn.event();
				}
			}
		});

		// Add the view
		themeWidget(et);

		return et;

	}

	// --------- Toggle ---------//
	public interface addGenericToggleCB {
		void event(boolean isChecked);
	}

	public JToggleButton addGenericToggle(final String label, boolean initstate, final addGenericToggleCB callbackfn) {
		initializeLayout();
		// Create the view
		JToggleButton tb = new JToggleButton(a.get());
		tb.setChecked(initstate);
		tb.setText(label);

		// Add change listener
		tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				callbackfn.event(new Boolean(isChecked));
			}
		});

		return tb;
	}

	// --------- checkbox ---------//
	public interface addGenericCheckboxCB {
		void event(boolean isChecked);
	}

	public JCheckBox addGenericCheckbox(String label, boolean initstate, final addGenericCheckboxCB callbackfn) {

		initializeLayout();
		// Adds a checkbox and set the initial state as initstate. if the button
		// state changes, call the callbackfn
		JCheckBox cb = new JCheckBox(a.get());
		cb.setChecked(initstate);
		cb.setText(label);

		// Add the click callback
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				callbackfn.event(isChecked);
			}
		});

		// Add the view
		themeWidget(cb);

		return cb;

	}

	/**
	 * Adds a switch
	 * 
	 */
	public interface addGenericSwitchCB {
		void event(boolean isChecked);
	}

	public JSwitch addGenericSwitch(boolean initstate, final addGenericSwitchCB callbackfn) {

		initializeLayout();
		// Adds a switch. If the state changes, we'll call the callback function
		JSwitch s = new JSwitch(a.get());
		s.setChecked(initstate);

		// Add the click callback
		s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				callbackfn.event(isChecked);
			}
		});

		return s;
	}

	/**
	 * Adds a radiobutton
	 * 
	 */
	// --------- getRequest ---------//
	public interface addGenericRadioButtonCB {
		void event(boolean isChecked);
	}

	public JRadioButton addGenericRadioButton(String label, boolean initstate, final addGenericRadioButtonCB callbackfn) {

		initializeLayout();
		// Create and position the radio button
		JRadioButton rb = new JRadioButton(a.get());
		rb.setChecked(initstate);
		rb.setText(label);

		// Add the click callback
		rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				callbackfn.event(isChecked);
			}
		});

		// Add the view
		themeWidget(rb);

		return rb;
	}

	/**
	 * Adds an imageview
	 * 
	 */
	public JImageView addGenericImage(String imagePath) {

		initializeLayout();
		// Create and position the image view
		final JImageView iv = new JImageView(a.get());
		iv.setImage(imagePath);

		return iv;

	}

	public JPlotView addGenericPlot(int min, int max) {
		initializeLayout();
		JPlotView jPlotView = new JPlotView(a.get());
		jPlotView.setLimits(min, max);

		return jPlotView;
	}

	/*
	 * ---------- aqui
	 */

	/**
	 * Adds an image button with the default background
	 * 
	 */
	public JImageButton addImageButton(int x, int y, int w, int h, String imagePath, final addImageButtonCB callbackfn) {
		return addImageButton(x, y, w, h, imagePath, "", false, callbackfn);
	}

	public JImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final addImageButtonCB callbackfn) {
		return addImageButton(x, y, w, h, imgNotPressed, imgPressed, false, callbackfn);
	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 */

	public// --------- getRequest ---------//
	interface addImageButtonCB {
		void event();
	}

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

	public JCanvasView addCanvas(int x, int y, int w, int h) {
		initializeLayout();

		JCanvasView sv = new JCanvasView(a.get(), w, h);
		positionView(sv, x, y, w, h);
		// Add the view
		addViewAbsolute(sv, x, y, w, h);

		return sv;
	}

	/* ------------------------------ */

	public JMap addGenericMap() {
		initializeLayout();
		JMap mapView = new JMap(a.get(), 256);

		mapView.setMapListener(new DelayedMapListener(new MapListener() {
			@Override
			public boolean onZoom(final ZoomEvent e) {
				// do something
				MLog.d("map", "zoom " + e.getZoomLevel());
				return true;
			}

			@Override
			public boolean onScroll(final ScrollEvent e) {
				Log.i("zoom", e.getX() + " " + e.getY());
				return true;
			}
		}, 1000));

		return mapView;
	}

	public JVideo addGenericVideo(final String videoFile) {
		initializeLayout();
		final JVideo video = new JVideo(a.get());

		video.addListener(new CustomVideoTextureView.VideoListener() {

			@Override
			public void onTimeUpdate(int ms, int totalDuration) {
				// callback(fn, args)
			}

			@Override
			public void onReady(boolean ready) {
				video.loadExternalVideo(AppRunnerSettings.get().project.getStoragePath() + File.separator + videoFile);
			}

			@Override
			public void onFinish(boolean finished) {

			}
		});

		return video;
	}

	// transform fragment into view
	public JCamera addGenericCamera(int type, int x, int y, int w, int h) {
		// initializeLayout();
		//
		// if (type == 0) {
		// type = CustomCameraView.MODE_CAMERA_FRONT;
		// } else {
		// type = CustomCameraView.MODE_CAMERA_BACK;
		// }
		// JCamera camera = new JCamera(a.get(), type);

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fl.setId(12345);

		// Add the view
		addViewAbsolute(fl, x, y, w, h);

		CameraFragment cameraFragment = new CameraFragment();
		Bundle bundle = new Bundle();
		if (type == 1) {
			bundle.putInt("camera", CameraFragment.MODE_CAMERA_FRONT);
		} else {
			bundle.putInt("camera", CameraFragment.MODE_CAMERA_BACK);

		}
		cameraFragment.setArguments(bundle);

		FragmentTransaction ft = a.get().getSupportFragmentManager().beginTransaction();
		ft.add(fl.getId(), cameraFragment, String.valueOf(fl.getId()));
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.addToBackStack(null);
		ft.commit();

		JCamera jCamera = new JCamera(a.get(), cameraFragment);

		return jCamera;
	}

	/**
	 * This class lets us download an image asynchronously without blocking the
	 * UI thread
	 * 
	 * @author ncbq76
	 * 
	 */
	public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
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

		@Override
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
	public static class SetImageTask extends AsyncTask<String, Void, Object> {
		ImageView bgImage;
		String imagePath;
		private String fileExtension;

		public SetImageTask(ImageView bmImage) {
			this.bgImage = bmImage;
		}

		@Override
		protected Object doInBackground(String... paths) {
			imagePath = paths[0];
			File imgFile = new File(imagePath);
			MLog.d("svg", "imagePath " + imagePath);
			if (imgFile.exists()) {
				fileExtension = FileIO.getFileExtension(imagePath);
				MLog.d("svg", "fileExtension " + fileExtension);
				if (fileExtension.equals("svg")) {
					try {
						MLog.d("svg", "is SVG 1");
						File file = new File(imagePath);
						FileInputStream fileInputStream = new FileInputStream(file);
						MLog.d("svg", "input " + fileInputStream);

						SVG svg = SVG.getFromInputStream(fileInputStream);
						MLog.d("svg", "svg " + svg);
						Drawable drawable = new PictureDrawable(svg.renderToPicture());
						MLog.d("svg", "drawable " + drawable);

						return drawable;
					} catch (SVGParseException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					// Get the bitmap with appropriate options
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPurgeable = true;
					Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
					return bmp;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (fileExtension.equals("svg")) {
				MLog.d("svg", "is SVG 2 " + result);
				bgImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				bgImage.setImageDrawable((Drawable) result);
			} else {
				bgImage.setImageBitmap((Bitmap) result);
			}
		}
	}

	/**
	 * This class lets us set the background asynchronously
	 * 
	 * @author ncbq76
	 * 
	 */
	// We need to set the bitmap image asynchronously
	protected class SetBgImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView fl;

		public SetBgImageTask(ImageView bgImageView) {
			this.fl = bgImageView;
		}

		@Override
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

		@Override
		protected void onPostExecute(Bitmap result) {
			fl.setImageBitmap(result);
			fl.setScaleType(ScaleType.CENTER_INSIDE);
			// fl.setBackgroundDrawable(d);
		}
	}

}