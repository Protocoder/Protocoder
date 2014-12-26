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

package org.protocoderrunner.apprunner.api.widgets;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Space;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;
import com.larvalabs.svgandroid.SVGParser;

import org.json.JSONArray;
import org.mozilla.javascript.NativeArray;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.protocoderrunner.R;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PCamera;
import org.protocoderrunner.apprunner.api.other.PVideo;
import org.protocoderrunner.apprunner.api.other.ProtocoderNativeObject;
import org.protocoderrunner.apprunner.api.widgets.PPadView.TouchEvent;
import org.protocoderrunner.fragments.CameraNew;
import org.protocoderrunner.fragments.CustomVideoTextureView;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.FileIO;
import org.protocoderrunner.utils.Image;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.views.TouchAreaView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PUIGeneric extends PInterface {

    String TAG = "PUIGeneric";

	// layouts
	final static int MAXVIEW = 2000;
	View viewArray[] = new View[MAXVIEW];
	int viewCount = 0;
	boolean isMainLayoutSetup = false;
	protected PAbsoluteLayout uiAbsoluteLayout;
	protected LinearLayout uiLinearLayout;
	protected RelativeLayout holderLayout;
	protected PImageView bgImageView;

	// properties
	public int canvasWidth;
	public int canvasHeight;
	public int cw;
	public int ch;
	private PScrollView sv;
	public int screenWidth;
	public int screenHeight;
	public int sw;
	public int sh;
    public boolean isFullscreenMode = false;
    public boolean isImmersiveMode = false;
	protected int theme;
	protected boolean absoluteLayout = true;
	protected boolean noActionBarAllowed = false;
	protected boolean isScrollLayout = true;

	public PUIGeneric(Context a) {
		super(a);
	}

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        updateScreenSizes();
    }

    public void updateScreenSizes() {

        if (mActivity != null) {
            screenWidth = mActivity.getScrenSize().x;
            screenHeight = mActivity.getScrenSize().y;

            MLog.d("qq", " " + screenWidth + " " + screenHeight);

            //if in immersive mode then add the navigation bar height
            if (isImmersiveMode) {
                screenHeight += mActivity.getNavigationBarHeight();
            }

            sw = screenWidth;
            sh = screenHeight;
        }
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

			// set the holder
			holderLayout = new RelativeLayout(mContext);
			holderLayout.setLayoutParams(layoutParams);
			holderLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

			// We need to let the view scroll, so we're creating mContext scroll
			// view
			sv = new PScrollView(mContext, true);
			sv.setLayoutParams(layoutParams);
			sv.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
			sv.setFillViewport(true);
			// sv.setEnabled(false);
			allowScroll(isScrollLayout);

			if (absoluteLayout) {
				// Create the main layout. This is where all the items actually
				// go
				uiAbsoluteLayout = new PAbsoluteLayout(mContext);
				uiAbsoluteLayout.setLayoutParams(layoutParams);
				uiAbsoluteLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
				sv.addView(uiAbsoluteLayout);
			} else {
				uiLinearLayout = new LinearLayout(mContext);
				uiLinearLayout.setLayoutParams(layoutParams);
				uiLinearLayout.setOrientation(LinearLayout.VERTICAL);
				uiLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
				uiLinearLayout.setLayoutTransition(new LayoutTransition());
				sv.addView(uiLinearLayout);
				holderLayout.setPadding(AndroidUtils.pixelsToDp(mContext, 5), 0, AndroidUtils.pixelsToDp(mContext, 5), 0);
			}

			// background image
			bgImageView = new PImageView(mContext);
			holderLayout.addView(bgImageView, layoutParams);

			// set the layout

			//mFragment.initLayout(); old init
			mFragment.addScriptedLayout(holderLayout);
			holderLayout.addView(sv);

			isMainLayoutSetup = true;
		}
	}

	@ProtocoderScript
	@APIMethod(description = "Enables/Disables the absolute layout", example = "")
	@APIParam(params = { "boolean" })
	public void setAbsoluteLayout(boolean absoluteLayout) {
		this.absoluteLayout = absoluteLayout;
	}

	@ProtocoderScript
	@APIMethod(description = "Allows the main interface to scroll", example = "")
	@APIParam(params = { "boolean" })
	public void allowScroll(boolean scroll) {
		if (sv != null) {
			if (scroll) {
                sv.setScrollingEnabled(true);
               // sv.requestDisallowInterceptTouchEvent(false);
               // sv.setOnTouchListener(null);
			} else {
                sv.setScrollingEnabled(false);
//                sv.requestDisallowInterceptTouchEvent(true);
//				sv.setOnTouchListener(new OnTouchListener() {
//
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//
//						return true;
//					}
//				});
			}
		}
		isScrollLayout = scroll;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds the given view to the layout", example = "")
    @APIParam(params = { "x", "y", "w", "h" })
	protected void addViewAbsolute(View v, int x, int y, int w, int h) {
		addViewGeneric(v);
      //  if (true) {
           // x = AndroidUtils.dpToPixels(mContext, x);
           // y = AndroidUtils.dpToPixels(mContext, y);
           // w = AndroidUtils.dpToPixels(mContext, w);
           // h = AndroidUtils.dpToPixels(mContext, h);
      //  }
		uiAbsoluteLayout.addView(v, x, y, w, h);
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


    @ProtocoderScript
    @APIMethod(description = "Creates an absolute layout", example = "")
    @APIParam(params = { "" })
    public PAbsoluteLayout newAbsoluteLayout() {
        PAbsoluteLayout al = new PAbsoluteLayout(mContext);

        return al;
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a card view holder", example = "")
    @APIParam(params = { "" })
    public PCard newCard() {
		initializeLayout();

		PCard card = new PCard(mContext);
		return card;
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new window", example = "")
    @APIParam(params = { "" })
	public PWindow newWindow() {
		initializeLayout();

		PWindow w = new PWindow(mContext);
		return w;
	}

	// --------- newButton ---------//
	public interface addGenericButtonCB {
		void event();
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new button", example = "")
    @APIParam(params = { "label", "function()" })
	public PButton newButton(String label, final addGenericButtonCB callbackfn) {
		initializeLayout();

		// Create the button
		PButton b = new PButton(mContext);
		b.setText(label);

		// Set on click behavior
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (callbackfn != null) {
					callbackfn.event();
				}
			}
		});

		return b;
	}

	// --------- TouchAreaView ---------//
	public interface addGenericTouchAreaCB {
		void event(boolean touching, float x, float y);
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new touch area", example = "")
    @APIParam(params = { "boolean", "function(touching, x, y)" })
	public TouchAreaView newTouchArea(boolean showArea, final addGenericTouchAreaCB callbackfn) {
		initializeLayout();

		TouchAreaView taV = new TouchAreaView(mContext, showArea);
		taV.setTouchAreaListener(new TouchAreaView.OnTouchAreaListener() {

			@Override
			public void onTouch(TouchAreaView touchAreaView, boolean touching, float x, float y) {
				callbackfn.event(touching, x, y);
			}
		});

		return taV;
	}

	// --------- newTouchPad (Touch Area) ---------//
	public interface addPadCB {
		void event(PadXYReturn[] q2);
	}

	public class PadXYReturn {
		public int id;
		public int x;
		public int y;
		public String action;
		public String type;
	}

	PadXYReturn[] q2;

    @ProtocoderScript
    @APIMethod(description = "Creates a new touch pad", example = "")
    @APIParam(params = { "function(data)" })
	public PPadView newTouchPad(final addPadCB callbackfn) {
		initializeLayout();

		final ArrayList<PadXYReturn> m = new ArrayList<PUIGeneric.PadXYReturn>();

		PPadView taV = new PPadView(mContext);
		taV.setTouchAreaListener(new PPadView.OnTouchAreaListener() {

			@Override
			public void onGenericTouch(HashMap<Integer, TouchEvent> t) {

				JSONArray array = new JSONArray();
				q2 = new PadXYReturn[t.size()];

				int num = 0;
				for (Map.Entry<Integer, PPadView.TouchEvent> t1 : t.entrySet()) {
					int key = t1.getKey();
					TouchEvent value = t1.getValue();

					/*
					 * JSONObject o = new JSONObject(); try { o.put("id",
					 * value.id); o.put("x", value.x); o.put("y", value.y);
					 * o.put("action", value.action); o.put("type", value.type);
					 * 
					 * } catch (JSONException e) { e.printStackTrace(); }
					 */

					PadXYReturn q = new PadXYReturn();
					q.id = value.id;
					q.x = value.x;
					q.y = value.y;
					q.action = value.action;
					q.type = value.type;

					// m.addPE(q);
					q2[num++] = q;

					// array.put(o);
				}

				callbackfn.event(q2);
			}
		});

		return taV;
	}

	// --------- HoloCircleSeekBar ---------//
	public interface addGenericKnobCB {
		void eval(int progress);
	}

	public void addGenericKnob(final addGenericKnobCB callbackfn) {
		initializeLayout();

	}

	// --------- seekbar ---------//
	public interface addGenericSliderCB {
		void event(float progress);
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new slider", example = "")
    @APIParam(params = { "max", "progress", "function(progress)" })
	public PSlider newSlider(float min, float max, final addGenericSliderCB callbackfn) {

		initializeLayout();
		// Create the position the view
		final PSlider sb = new PSlider(mContext);
        sb.setMin(min);
		sb.setMax(max);

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
				callbackfn.event(sb.valueToFloat(progress));
			}
		});

		return sb;

	}

    // --------- seekbar ---------//
    public interface addGenericSpinnerCB {
        void event(String result);
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a new choice box", example = "")
    @APIParam(params = { "array", "function(selected)" })
    public PSpinner newChoiceBox(final String[] array, final addGenericSpinnerCB callbackfn) {
        initializeLayout();

        PSpinner spinner = new PSpinner(mContext);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callbackfn.event(array[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return spinner;
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a progress bar of n units", example = "")
    @APIParam(params = { "units" })
	public PProgressBar newProgress(int max) {

		initializeLayout();
		// Create the position the view
		PProgressBar pb = new PProgressBar(mContext, android.R.attr.progressBarStyleHorizontal);

		return pb;
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new text", example = "")
    @APIParam(params = { "text" })
	public PTextView newText(String label) {
		// int defaultTextSize = 16;
		// tv.setTextSize((float) textSize);
		PTextView tv = new PTextView(mContext);
		initializeLayout();

		tv.setText(label);
		themeWidget(tv);

		return tv;
	}

	// --------- getRequest ---------//
	public interface addGenericInputCB {
		void event(String txt);
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new input", example = "")
    @APIParam(params = { "label, function(data)" })
	public PEditText newInput(String label, final addGenericInputCB callbackfn) {

		initializeLayout();
		// Create view
		final PEditText et = new PEditText(mContext);
		et.setHint(label);

        if (callbackfn != null) {
            // On focus lost, we need to call the callback function
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    callbackfn.event(et.getText().toString());
                }
            });

            et.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                    }
                }
            });
        }

		// Add the view
		themeWidget(et);

		return et;
	}


    // --------- getRequest ---------//
    public interface NewGenericNumberPickerCB {
        void event(int val);
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a new number picker", example = "")
    @APIParam(params = { "from", "to", "function(data)" })
    public PNumberPicker newNumberPicker(int from, int to, final NewGenericNumberPickerCB callback) {
        initializeLayout();
        PNumberPicker pNumberPicker = new PNumberPicker(mContext);
        pNumberPicker.setMinValue(from);
        pNumberPicker.setMaxValue(to);
        pNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                callback.event(newVal);
            }
        });

        return pNumberPicker;
    }


	// --------- Toggle ---------//
	public interface addGenericToggleCB {
		void event(boolean isChecked);
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new toggle", example = "")
    @APIParam(params = { "name", "boolean", "function(b)" })
	public PToggleButton newToggle(final String label, boolean initstate, final addGenericToggleCB callbackfn) {
		initializeLayout();
		// Create the view
		PToggleButton tb = new PToggleButton(mContext);
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

    @ProtocoderScript
    @APIMethod(description = "Creates a new checkbox", example = "")
    @APIParam(params = { "name", "boolean", "function(boolean)" })
	public PCheckBox newCheckbox(String label, boolean initstate, final addGenericCheckboxCB callbackfn) {

		initializeLayout();
		// Adds mContext checkbox and set the initial state as initstate. if the button
		// state changes, call the callbackfn
		PCheckBox cb = new PCheckBox(mContext);
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
	 * Adds mContext switch
	 * 
	 */
	public interface addGenericSwitchCB {
		void event(boolean isChecked);
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a new switch", example = "")
    @APIParam(params = { "boolean", "function(b)" })
	public PSwitch newSwitch(boolean initstate, final addGenericSwitchCB callbackfn) {

		initializeLayout();
		// Adds mContext switch. If the state changes, we'll call the callback function
		PSwitch s = new PSwitch(mContext);
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
	 * Adds mContext radiobutton
	 * 
	 */
	// --------- getRequest ---------//
	public interface addGenericRadioButtonCB {
		void event(boolean isChecked);
	}

    //TODO change this, it doesnt make sense to have just one
    @ProtocoderScript
    @APIMethod(description = "Creates a new radio button", example = "")
    @APIParam(params = { "thickness" })
	public PRadioButton newRadioButton(String label, boolean initstate, final addGenericRadioButtonCB callbackfn) {

		initializeLayout();
		// Create and position the radio button
		PRadioButton rb = new PRadioButton(mContext);
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

    @ProtocoderScript
    @APIMethod(description = "Creates a new image view", example = "")
    @APIParam(params = { "imageName" })
	public PImageView newImage(String imagePath) {

		initializeLayout();
		// Create and position the image view
		final PImageView iv = new PImageView(mContext);
        if (imagePath != null) {
            iv.setImage(imagePath);
        }

		return iv;

	}


    @ProtocoderScript
    @APIMethod(description = "Creates a new plot", example = "")
    @APIParam(params = { "min", "max" })
	public PPlotView newPlot(int min, int max) {
		initializeLayout();
		PPlotView jPlotView = new PPlotView(mContext);
		jPlotView.setLimits(min, max);

		return jPlotView;
	}

	/**
	 * Adds an image with the option to hide the default background
	 * 
	 */

	public// --------- getRequest ---------//
	interface addImageButtonCB {
		void event();
	}


    @ProtocoderScript
    @APIMethod(description = "Creates a new image button", example = "")
    @APIParam(params = { "x", "y", "w", "h", "imgNameNotPressed", "imgNamePressed", "hideBackground", "function()" })
	public PImageButton newImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final boolean hideBackground, final addImageButtonCB callbackfn) {

		initializeLayout();
		// Create and position the image button
		final PImageButton ib = new PImageButton(mContext);

		ib.setScaleType(ScaleType.FIT_XY);
		// Hide the background if desired
		if (hideBackground) {
			ib.setBackgroundResource(0);
		}

		// Add image asynchronously
		new SetImageTask(ib, false).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator + imgNotPressed);

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

    // --------- getRequest ---------//
    public interface addGridOfCB {
        void event(ProtocoderNativeObject json);
    }

    public PGrid newGridOf(String type, NativeArray array, int cols, final addGridOfCB callbackfn) {

        PGrid gridLayout = new PGrid(mContext);
        int counter = 0;
        int num = (int) array.getLength();
        int rows = (int) Math.ceil(num / 2);

       // try {

            //cols = obj.get("cols");
            //rows = obj.get("rows");
            //num = rows * cols;
            //name = obj.getString("name");
            //prefix = obj.getString("prefix");
            //postfix = obj.getString("postfix");

        //} catch (JSONException e) {
        //    e.printStackTrace();
        //}

        PGridRow ll2 = null;
        int i = 0;
        for (int j = 0; j < cols * rows; j++) {
            if (j % cols == 0) {
                ll2 = gridLayout.addRow(cols);
                i++;
                Log.d(TAG, "added new row");
            }

            Log.d(TAG, "counter/num " + counter + " " + num + " " + i + " " + cols + " " + rows);
            final ProtocoderNativeObject cbData = new ProtocoderNativeObject();

            if (counter >= num) {
                Log.d(TAG, "this space");
                ll2.addViewInRow(new Space(mContext));
            //   break;
            } else {
                String name = (String) array.get(counter);
                cbData.addPE("name", name);
                cbData.addPE("name", name);
                cbData.addPE("i", i);
                cbData.addPE("j", j);
                cbData.addPE("count", counter);

                //button
                if (type.equals("button")) {
                    PButton btn = null;
                    btn = newButton(name, new addGenericButtonCB() {
                        @Override
                        public void event() {
                            cbData.addPE("data", "");
                            callbackfn.event(cbData);
                        }
                    });

                    cbData.addPE("view", btn);

                    ll2.addViewInRow(btn);

                    //imagebutton
                } else if (type.equals("imagebutton")) {
                    PImageButton btn = new PImageButton(mContext);


                    //toggle
                } else if (type.equals("toggle")) {
                    PToggleButton toggle = newToggle(name, false, new addGenericToggleCB() {
                        @Override
                        public void event(boolean isChecked) {

                            cbData.addPE("data", isChecked);
                            callbackfn.event(cbData);

                        }
                    });
                    ll2.addViewInRow(toggle);

                    //hslider
                } else if (type.equals("hslider")) {
                    PSlider slider = newSlider(1024, 0, new addGenericSliderCB() {
                        @Override
                        public void event(float progress) {
                            cbData.addPE("data", progress);
                            callbackfn.event(cbData);

                        }
                    });
                    ll2.addViewInRow(slider);

                    //vslider
                } else if (type.equals("vslider")) {

                    //knob
                } else if (type.equals("knob")) {

                }
                counter++;
            }
        }

        //MLog.network(mContext, "qq", "" + gridLayout);
        return gridLayout;
    }

	/* ------------------------------ */

    @ProtocoderScript
    @APIMethod(description = "Creates a new map", example = "")
    @APIParam(params = { "" })
	public PMap newMap() {
		initializeLayout();
		PMap mapView = new PMap(mContext, 256);

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

	public PVideo newVideo(final String videoFile) {
		initializeLayout();
		final PVideo video = new PVideo(mContext);

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

    @ProtocoderScript
    @APIMethod(description = "Creates a new drawing canvas", example = "")
    @APIParam(params = { "width", "height" })
    public PCanvasView newCanvas(int w, int h) {
        initializeLayout();
        PCanvasView canvasView = new PCanvasView(mActivity, w, h);

        return canvasView;
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a new web view", example = "")
    @APIParam(params = { "" })
    public PWebView newWebview() {
        initializeLayout();
        PWebView webView = new PWebView(mContext);

        return webView;
    }

    @ProtocoderScript
    @APIMethod(description = "Creates a new camera view", example = "")
    @APIParam(params = { "type={0,1}" })
	public PCamera newCamera(String type) {
        initializeLayout();

        int camNum = -1;
        if (type.equals("front")) {
            camNum = CameraNew.MODE_CAMERA_FRONT;
        } else if (type.equals("back")) {
            camNum = CameraNew.MODE_CAMERA_BACK;
        }

		PCamera pCamera = new PCamera(mActivity, camNum, PCamera.MODE_COLOR_COLOR);

		return pCamera;
	}

	/**
	 * This class lets us download an image asynchronously without blocking the
	 * UI thread
	 * 
	 * @author ncbq76
	 * 
	 */
	public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		PImageView bmImage;
        boolean isTiled = false;

		public DownloadImageTask(PImageView bmImage, boolean isTiled) {
			this.bmImage = bmImage;
            this.isTiled = isTiled;
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

            if (isTiled == true) {
                bmImage.setRepeat();
            }
		}
	}

	/**
	 * This class lets us set images from file asynchronously
	 * 
	 * @author ncbq76
	 * 
	 */
	public static class SetImageTask extends AsyncTask<String, Void, Object> {
		PImageView bgImage;
		String imagePath;
		private String fileExtension;
        boolean isTiled = false;

		public SetImageTask(PImageView bmImage, boolean isTiled) {
			this.bgImage = bmImage;
            this.isTiled = isTiled;
		}

		@Override
		protected Object doInBackground(String... paths) {
			imagePath = paths[0];
			File imgFile = new File(imagePath);
			//MLog.d("svg", "imagePath " + imagePath);
			if (imgFile.exists()) {
				fileExtension = FileIO.getFileExtension(imagePath);
				//MLog.d("svg", "fileExtension " + fileExtension);
				if (fileExtension.equals("svg")) {

                    File file = new File(imagePath);
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                        SVG svg = new SVGBuilder().readFromInputStream(fileInputStream).build();
                        SVGParser svgParser = new SVGParser();
                        //new SVGBuilder().
                        //       SVGParser.


                        //svg.
                        return svg.getDrawable();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


//                    try {
//						//MLog.d("svg", "is SVG 1");
//						File file = new File(imagePath);
//						FileInputStream fileInputStream = new FileInputStream(file);
//
//						SVG svg = SVG.getFromInputStream(fileInputStream);
//						Drawable drawable = new PictureDrawable(svg.renderToPicture());
//
//						return drawable;
//					} catch (SVGParseException e) {
//						e.printStackTrace();
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}
				} else {
				    Bitmap bmp = Image.loadBitmap(imagePath);
					return bmp;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
            bgImage.setScaleType(ScaleType.FIT_XY);

            if (fileExtension.equals("svg")) {
				MLog.d("svg", "is SVG 2 " + result);
				bgImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				bgImage.setImageDrawable((Drawable) result);
			} else {
				bgImage.setImageBitmap((Bitmap) result);

                if (isTiled == true) {
                    bgImage.setRepeat();
                }
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
		PImageView fl;
        boolean isTiled;

		public SetBgImageTask(PImageView bgImageView, boolean isTiled) {
			this.fl = bgImageView;
            this.isTiled = isTiled;
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

            if (isTiled == true) {
                fl.setRepeat();
            }
		}
	}

}