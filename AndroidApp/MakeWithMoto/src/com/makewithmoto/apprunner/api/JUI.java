package com.makewithmoto.apprunner.api;

import java.io.File;
import java.io.InputStream;

import processing.core.PApplet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makewithmoto.R;
import com.makewithmoto.apidoc.APIAnnotation;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.base.BaseActivity;
import com.makewithmoto.fragments.CameraFragment;
import com.makewithmoto.fragments.VideoPlayerFragment;
import com.makewithmoto.fragments.VideoPlayerFragment.VideoListener;
import com.makewithmoto.views.HoloCircleSeekBar;
import com.makewithmoto.views.HoloCircleSeekBar.OnCircleSeekBarChangeListener;
import com.makewithmoto.views.PlotView;
import com.makewithmoto.views.PlotView.Plot;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class JUI extends JInterface {

	final static int MAXVIEW = 100;
	FrameLayout mMainLayout;
	Boolean isMainLayoutSetup = false;
	int viewCount = 0;
	View viewArray[] = new View[MAXVIEW];

	//TODO add variable support 
	// @APIAnnotationField(description = "Creates a button ", example =
	// "ui.button(\"button\"); ")
	public int canvasWidth;
	public int canvasHeight;
	private ScrollView sv;
	public int screenWidth;
	public int screenHeight;
	private int theme;

	public JUI(Activity a) {
		super(a);
		
		screenWidth = ((BaseActivity) a ).screenWidth; 
		screenHeight = ((BaseActivity) a ).screenHeight; 
	}	

	private void initializeLayout() {
		if (!isMainLayoutSetup) {
			// We need to let the view scroll, so we're creating a scroll view
			sv = new ScrollView(a.get());
			sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			// Create the main layout. This is where all the items actually go
			mMainLayout = new FrameLayout(a.get());
			mMainLayout.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			sv.addView(mMainLayout);

			// Set the content view
			RelativeLayout rl = (RelativeLayout) a.get().findViewById(R.id.user_ui);
			rl.addView(sv);
			isMainLayoutSetup = true;
		}
	}

	private void addView(View v) {
		mMainLayout.addView(v);
/*
		final ViewTreeObserver vto = sv.getViewTreeObserver();
		OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				final ViewTreeObserver obs = sv.getViewTreeObserver();

				canvasWidth = mMainLayout.getWidth();
				canvasHeight = mMainLayout.getHeight();
				// observer.removeGlobalOnLayoutListener(this);

				if (AppSettings.CURRENT_VERSION > Build.VERSION.SDK_INT) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
			}
		};
		vto.addOnGlobalLayoutListener(globalLayoutListener);
*/
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
	@APIAnnotation(description = "Uses a DARK / BLUE / NONE theme for some widgets", example = "ui.setTheme(\"DARK\"); ")
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
	@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void title(String title) {
		((AppRunnerActivity) a.get()).changeTitle(title);
	}
	
	
	@JavascriptInterface
	@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void fullscreen() {
		((AppRunnerActivity) a.get()).setFullScreen();
	}
	
	@JavascriptInterface
	@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void hideHomeBar() {
		((AppRunnerActivity) a.get()).setHideHomeBar();
	}
	
	
	
	@JavascriptInterface
	@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void setOrientation() {
	//	((AppRunnerActivity) a.get()).setOr();
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
	@APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
	public void button(String label, int x, int y, int w, int h,
			final String callbackfn) {
		initializeLayout();

		// Create the button
		Button b = new Button(a.get());
		b.setText(label);
		positionView(b, x, y, w, h);

		// Set on click behavior
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Callback should capture the checked state
				callback(callbackfn);
			}
		});

		// Add the view to the layout
		mMainLayout.addView(b);
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
	public void picker(final String callbackfn) {
		// initializeLayout();

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
		a.get().setContentView(pkr);

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
	public void seekbar(int max, int progress, int x, int y, int w, int h,
			final String callbackfn) {

		initializeLayout();
		// Create the position the view
		SeekBar sb = new SeekBar(a.get());
		sb.setMax(max);
		sb.setProgress(progress);
		positionView(sb, x, y, w, -1);// height must always wrap

		// Add the change listener
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// @GOPI: Should we do something here? Any callback?
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// @GOPI: Should we do something here? Any callback?
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Callback should capture the checked state
				callback(callbackfn);
			}
		});

		// Add the view
		addView(sb);

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
	public int label(String label, int x, int y, int w, int h) {
		int defaultTextSize = 16;
		return label(label, x, y, w, h, defaultTextSize);
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
	public int label(String label, int x, int y, int w, int h, int textSize) {

		initializeLayout();
		// Create the TextView
		TextView tv = new TextView(a.get());
		tv.setText(label);
		tv.setTextSize((float) textSize);
		//tv.setTypeface(null, Typeface.BOLD);
		//tv.setGravity(Gravity.CENTER_VERTICAL);
		//tv.setPadding(2, 0, 0, 0);
		//tv.setTextColor(a.get().getResources().getColor(R.color.theme_text_white));
		positionView(tv, x, y, w, h);

		// Add the view
		themeWidget(tv);
		addView(tv);

		viewArray[viewCount] = tv;

		viewCount += 1;

		return (viewCount - 1);
	}

	@JavascriptInterface
	public void labelSetText(int view, String text) {
		TextView tv = (TextView) viewArray[view];
		tv.setText(text);
	}

	/**
	 * Adds an EditText view
	 * 
	 * @param label
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param callbackfn
	 */
	public void input(String label, int x, int y, int w, int h,
			final String callbackfn) {

		initializeLayout();
		// Create view
		EditText et = new EditText(a.get());
		et.setHint(label);
		positionView(et, x, y, w, h);

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
		addView(et);

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
	public void toggleButton(final String label, int x, int y, int w, int h,
			boolean initstate, final String callbackfn) {
		initializeLayout();
		// Create the view
		ToggleButton tb = new ToggleButton(a.get());
		tb.setChecked(initstate);
		tb.setText(label);
		positionView(tb, x, y, w, h);

		// Add change listener
		tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn, isChecked);
			}
		});

		// Add the view
		addView(tb);
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
	public void checkbox(String label, int x, int y, int w, int h,
			boolean initstate, final String callbackfn) {

		initializeLayout();
		// Adds a checkbox and set the initial state as initstate. if the button
		// state changes, call the callbackfn
		CheckBox cb = new CheckBox(a.get());
		cb.setChecked(initstate);
		cb.setText(label);
		positionView(cb, x, y, w, h);

		// Add the click callback
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn);
			}
		});

		// Add the view
		themeWidget(cb);
		addView(cb);

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
	public void toggleswitch(int x, int y, int w, int h, boolean initstate,
			final String callbackfn) {

		initializeLayout();
		// Adds a switch. If the state changes, we'll call the callback function
		Switch s = new Switch(a.get());
		s.setChecked(initstate);
		positionView(s, x, y, w, h);

		// Add the click callback
		s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn);
			}
		});

		// Add the view
		addView(s);

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
	public void radiobutton(String label, int x, int y, int w, int h,
			boolean initstate, final String callbackfn) {

		initializeLayout();
		// Create and position the radio button
		RadioButton rb = new RadioButton(a.get());
		rb.setChecked(initstate);
		rb.setText(label);
		positionView(rb, x, y, w, h);

		// Add the click callback
		rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Callback should capture the checked state
				callback(callbackfn);
			}
		});

		// Add the view
		themeWidget(rb);
		addView(rb);

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
	public void image(int x, int y, int w, int h, String imagePath) {

		initializeLayout();
		// Create and position the image view
		final ImageView iv = new ImageView(a.get());
		positionView(iv, x, y, w, h);

		// Add the image from file
		new SetImageTask(iv).execute(((AppRunnerActivity) a.get())
				.getCurrentDir() + File.separator + imagePath);

		// Add the view
		iv.setBackgroundColor(0x33b5e5);
		addView(iv);

	}

	/**
	 * Adds an image from a URL
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param address
	 */
	public void webimage(int x, int y, int w, int h, String address) {

		initializeLayout();
		// Create and position the image view
		final ImageView iv = new ImageView(a.get());
		positionView(iv, x, y, w, h);

		// Add image asynchronously
		new DownloadImageTask(iv).execute(address);

		// Add the view
		addView(iv);

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
	public void imagebutton(int x, int y, int w, int h, String imagePath,
			final String callbackfn) {
		imagebutton(x, y, w, h, imagePath, false, callbackfn);
	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imagePath
	 * @param hideBackground
	 * @param callbackfn
	 */
	public void imagebutton(int x, int y, int w, int h, String imagePath,
			boolean hideBackground, final String callbackfn) {

		initializeLayout();
		// Create and position the image button
		ImageButton ib = new ImageButton(a.get());
		positionView(ib, x, y, w, h);

		ib.setScaleType(ScaleType.FIT_XY);
		// Hide the background if desired
		if (hideBackground) {
			ib.setBackgroundResource(0);
		}

		// Add image asynchronously
		new SetImageTask(ib).execute(((AppRunnerActivity) a.get())
				.getCurrentDir() + File.separator + imagePath);

		// Set on click behavior
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				callback(callbackfn);
			}
		});

		// Add the view
		addView(ib);

	}

	/**
	 * Set padding on the entire view
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setPadding(int left, int top, int right, int bottom) {
		initializeLayout();
		mMainLayout.setPadding(left, top, right, bottom);
	}

	/**
	 * Set background color for the main layout via int
	 * 
	 * @param color
	 */
	public void backgroundColor(int color) {
		initializeLayout();
		mMainLayout.setBackgroundColor(color);
	}

	/**
	 * The more common way to set background color, set bg color via RGB
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void backgroundColor(int red, int green, int blue) {
		initializeLayout();
		mMainLayout.setBackgroundColor(Color.rgb(red, green, blue));
	}

	/**
	 * Set a background image
	 * 
	 * @param imagePath
	 */
	public void backgroundImage(String imagePath) {
		initializeLayout();
		// Add the bg image asynchronously
		new SetBgImageTask(mMainLayout).execute(((AppRunnerActivity) a.get())
				.getCurrentDir() + File.separator + imagePath);

	}

	PlotView plotView;
	Plot plot1;

	@JavascriptInterface
	public void addPlot(int x, int y, int w, int h) {
		initializeLayout();
		plotView = new PlotView(a.get());
		plot1 = plotView.new Plot(Color.RED);
		plotView.addPlot(plot1);

		positionView(plotView, x, y, w, h);

		// Add the view
		addView(plotView);

		// return p1;
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
	public PApplet addProcessing(int x, int y, int w, int h) {
		
		initializeLayout();
		
		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fl.setId(12);
		
		// Add the view
		positionView(fl, x, y, w, h);
		addView(fl);
		
		PApplet papplet = new PApplet();
		
		FragmentTransaction ft = a.get().getSupportFragmentManager()
				.beginTransaction();  
		ft.add(fl.getId(), papplet, String.valueOf(fl.getId()));
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		ft.addToBackStack(null);
		ft.commit();
		
		
		return papplet;
		
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
	public void addCameraView(int x, int y, int w, int h) {

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fl.setId(12345);
		
		// Add the view
		positionView(fl, x, y, w, h);
		addView(fl);

		CameraFragment cameraFragment = new CameraFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("color", CameraFragment.MODE_COLOR_COLOR);
		bundle.putInt("camera", CameraFragment.MODE_CAMERA_BACK);

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
	public void addVideoView(int x, int y, int w, int h) {

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fl.setId(12345678);
		
		// Add the view
		positionView(fl, x, y, w, h);
		addView(fl);

		final VideoPlayerFragment fragment = new VideoPlayerFragment();
		fragment.addListener(new VideoListener() {

			@Override
			public void onTimeUpdate(int ms, int totalDuration) {

			}

			@Override
			public void onReady(boolean ready) {
				fragment.loadResourceVideo("/raw/cityfireflies");
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
	
	}

	


	@JavascriptInterface
	public void setPlotValue(float value) {
		plotView.setValue(plot1, value);
	}

	@JavascriptInterface
	public void startTrackingTouches(String b) {
	}

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
		ImageView bmImage;

		public SetImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
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
			bmImage.setImageBitmap(result);
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
		FrameLayout fl;

		public SetBgImageTask(FrameLayout fl) {
			this.fl = fl;
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

		@SuppressWarnings("deprecation")
		protected void onPostExecute(Bitmap result) {
			Drawable d = new BitmapDrawable(a.get().getResources(), result);
			fl.setBackgroundDrawable(d);
		}
	}

}