/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.apprunner.api.widgets;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;

import org.json.JSONArray;
import org.mozilla.javascript.NativeArray;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.protocoderrunner.R;
import org.protocoderrunner.apidoc.annotation.ProtoField;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.media.PCamera;
import org.protocoderrunner.apprunner.api.other.ProtocoderNativeObject;
import org.protocoderrunner.apprunner.api.widgets.PPadView.TouchEvent;
import org.protocoderrunner.fragments.CameraNew;
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

import static org.protocoderrunner.apprunner.api.widgets.PSlider.addGenericSliderCB;

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
    protected boolean isScrollLayout = true;

    private Context mContext;

    @ProtoField(description = "Toolbar", example = "")
    public PToolbar toolbar;

    public PUIGeneric(Context a) {
        super(a);
        this.mContext = a;
    }

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        toolbar = new PToolbar(getActivity());

        updateScreenSizes();
    }

    public void updateScreenSizes() {

        if (getActivity() != null) {
            screenWidth = getActivity().getScrenSize().x;
            screenHeight = getActivity().getScrenSize().y;

            MLog.d("qq", " " + screenWidth + " " + screenHeight);

            //if in immersive mode then add the navigation bar height
            if (isImmersiveMode) {
                screenHeight += getActivity().getNavigationBarHeight();
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
            holderLayout = new RelativeLayout(getContext());
            holderLayout.setLayoutParams(layoutParams);
            holderLayout.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));

            // We need to let the view scroll, so we're creating mContext scroll
            // view
            sv = new PScrollView(getContext(), true);
            sv.setLayoutParams(layoutParams);
            sv.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
            sv.setFillViewport(true);
            // sv.setEnabled(false);
            allowScroll(isScrollLayout);

            if (absoluteLayout) {
                // Create the main layout. This is where all the items actually
                // go
                uiAbsoluteLayout = new PAbsoluteLayout(getContext());
                uiAbsoluteLayout.setLayoutParams(layoutParams);
                uiAbsoluteLayout.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                sv.addView(uiAbsoluteLayout);
            } else {
                uiLinearLayout = new LinearLayout(getContext());
                uiLinearLayout.setLayoutParams(layoutParams);
                uiLinearLayout.setOrientation(LinearLayout.VERTICAL);
                uiLinearLayout.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                uiLinearLayout.setLayoutTransition(new LayoutTransition());
                sv.addView(uiLinearLayout);
                holderLayout.setPadding(AndroidUtils.pixelsToDp(getContext(), 5), 0, AndroidUtils.pixelsToDp(getContext(), 5), 0);
            }

            // background image
            bgImageView = new PImageView(getContext());
            holderLayout.addView(bgImageView, layoutParams);

            // set the layout

            //mFragment.initLayout(); old init

            if (getFragment() != null) {
                getFragment().addScriptedLayout(holderLayout);
            } else if (getService() != null) {
                getService().addScriptedLayout(holderLayout);
            }
            holderLayout.addView(sv);

            isMainLayoutSetup = true;
        }
    }


    @ProtoMethod(description = "Enables/Disables the absolute layout", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void setAbsoluteLayout(boolean absoluteLayout) {
        this.absoluteLayout = absoluteLayout;
    }


    @ProtoMethod(description = "Allows the main interface to scroll", example = "")
    @ProtoMethodParam(params = {"boolean"})
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


    @ProtoMethod(description = "Adds the given view to the layout", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
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


//    @ProtoMethod(description = "Uses a DARK / BLUE / NONE theme for some widgets", example = "ui.setTheme(\"DARK\"); ")
//    @ProtoMethodParam(params = {"themeName"})
//    public void setTheme(String theme) {
//        if (theme.equals("DARK")) {
//            this.theme = R.drawable.theme_rounded_rect_dark;
//        } else if (theme.equals("BLUE")) {
//            this.theme = R.drawable.theme_rounded_rect_blue;
//        } else if (theme.equals("NONE")) {
//            theme = null;
//        }
//
//    }
//
//    public void themeWidget(View v) {
//       // v.setBackgroundResource(theme);
//    }


    @ProtoMethod(description = "Creates an absolute layout", example = "")
    @ProtoMethodParam(params = {""})
    public PAbsoluteLayout newAbsoluteLayout() {
        PAbsoluteLayout al = new PAbsoluteLayout(getContext());

        return al;
    }


    @ProtoMethod(description = "Creates a card view holder", example = "")
    @ProtoMethodParam(params = {""})
    public PCard newCard() {
        initializeLayout();

        PCard card = new PCard(getContext());
        return card;
    }


    @ProtoMethod(description = "Creates a new window", example = "")
    @ProtoMethodParam(params = {""})
    public PWindow newWindow() {
        initializeLayout();

        PWindow w = new PWindow(getContext());
        return w;
    }


    @ProtoMethod(description = "Creates a new button", example = "")
    @ProtoMethodParam(params = {"label", "function()"})
    public PButton newButton(String label) {
        initializeLayout();

        // Create the button
        PButton b = new PButton(getContext());
        b.setText(label);

        return b;
    }

    // --------- TouchAreaView ---------//
    public interface addGenericTouchAreaCB {
        void event(boolean touching, float x, float y);
    }


    @ProtoMethod(description = "Creates a new touch area", example = "")
    @ProtoMethodParam(params = {"boolean", "function(touching, x, y)"})
    public TouchAreaView newTouchArea(boolean showArea, final addGenericTouchAreaCB callbackfn) {
        initializeLayout();

        TouchAreaView taV = new TouchAreaView(getContext(), showArea);
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


    @ProtoMethod(description = "Creates a new touch pad", example = "")
    @ProtoMethodParam(params = {"function(data)"})
    public PPadView newTouchPad(final addPadCB callbackfn) {
        initializeLayout();

        final ArrayList<PadXYReturn> m = new ArrayList<PUIGeneric.PadXYReturn>();

        PPadView taV = new PPadView(getContext());
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


    @ProtoMethod(description = "Creates a new slider", example = "")
    @ProtoMethodParam(params = {"max", "progress", "function(progress)"})
    public PSlider newSlider(float min, float max) {

        initializeLayout();
        // Create the position the view
        final PSlider sb = new PSlider(getContext());
        sb.setMin(min);
        sb.setMax(max);

        return sb;
    }

    // --------- seekbar ---------//
    public interface addGenericSpinnerCB {
        void event(String result);
    }


    @ProtoMethod(description = "Creates a new choice box", example = "")
    @ProtoMethodParam(params = {"array", "function(selected)"})
    public PSpinner newChoiceBox(final String[] array, final addGenericSpinnerCB callbackfn) {
        initializeLayout();

        PSpinner spinner = new PSpinner(getContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, array);
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


    @ProtoMethod(description = "Creates a progress bar of n units", example = "")
    @ProtoMethodParam(params = {"units"})
    public PProgressBar newProgress(int max) {

        initializeLayout();
        // Create the position the view
        PProgressBar pb = new PProgressBar(getContext(), android.R.attr.progressBarStyleHorizontal);

        return pb;
    }


    @ProtoMethod(description = "Creates a new text", example = "")
    @ProtoMethodParam(params = {"text"})
    public PTextView newText(String label) {
        // int defaultTextSize = 16;
        // tv.setTextSize((float) textSize);
        PTextView tv = new PTextView(getContext());
        initializeLayout();

        tv.setText(label);
        //themeWidget(tv);

        return tv;
    }


    @ProtoMethod(description = "Creates a new input", example = "")
    @ProtoMethodParam(params = {"label, function(data)"})
    public PEditText newInput(String label) {

        initializeLayout();
        // Create view
        final PEditText et = new PEditText(getContext());
        et.setHint(label);


        // Add the view
        //themeWidget(et);

        return et;
    }


    // --------- getRequest ---------//
    public interface NewGenericNumberPickerCB {
        void event(int val);
    }


    @ProtoMethod(description = "Creates a new number picker", example = "")
    @ProtoMethodParam(params = {"from", "to", "function(data)"})
    public PNumberPicker newNumberPicker(int from, int to, final NewGenericNumberPickerCB callback) {
        initializeLayout();
        PNumberPicker pNumberPicker = new PNumberPicker(getContext());
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


    @ProtoMethod(description = "Creates a new toggle", example = "")
    @ProtoMethodParam(params = {"name", "boolean", "function(b)"})
    public PToggleButton newToggle(final String label, boolean initstate) {
        initializeLayout();
        // Create the view
        PToggleButton tb = new PToggleButton(getContext());
        tb.setChecked(initstate);
        tb.setText(label);

        return tb;
    }


    @ProtoMethod(description = "Creates a new checkbox", example = "")
    @ProtoMethodParam(params = {"name", "boolean", "function(boolean)"})
    public PCheckBox newCheckbox(String label, boolean initstate) {

        initializeLayout();
        // Adds mContext checkbox and set the initial state as initstate. if the button
        // state changes, call the callbackfn
        PCheckBox cb = new PCheckBox(getContext());
        cb.setChecked(initstate);
        cb.setText(label);

        // Add the view
        //themeWidget(cb);

        return cb;

    }


    @ProtoMethod(description = "Creates a new switch", example = "")
    @ProtoMethodParam(params = {"boolean", "function(b)"})
    public PSwitch newSwitch(boolean initstate) {

        initializeLayout();
        // Adds mContext switch. If the state changes, we'll call the callback function
        PSwitch s = new PSwitch(getContext());
        s.setChecked(initstate);

        return s;
    }

    /**
     * Adds mContext radiobutton
     */
    // --------- getRequest ---------//
    public interface addGenericRadioButtonCB {
        void event(boolean isChecked);
    }

    //TODO change this, it doesnt make sense to have just one

    @ProtoMethod(description = "Creates a new radio button", example = "")
    @ProtoMethodParam(params = {"thickness"})
    public PRadioButton newRadioButton(String label, boolean initstate, final addGenericRadioButtonCB callbackfn) {

        initializeLayout();
        // Create and position the radio button
        PRadioButton rb = new PRadioButton(getContext());
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
        //themeWidget(rb);

        return rb;
    }


    @ProtoMethod(description = "Creates a new image view", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PImageView newImage(String imagePath) {

        initializeLayout();
        // Create and position the image view
        final PImageView iv = new PImageView(getContext());
        if (imagePath != null) {
            iv.setImage(imagePath);
        }

        return iv;

    }


    @ProtoMethod(description = "Creates a new plot", example = "")
    @ProtoMethodParam(params = {"min", "max"})
    public PPlotView newPlot(int min, int max) {
        initializeLayout();
        PPlotView jPlotView = new PPlotView(getContext());
        jPlotView.setLimits(min, max);

        return jPlotView;
    }


    @ProtoMethod(description = "Creates a new image button", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "imgNameNotPressed", "imgNamePressed", "hideBackground", "function()"})
    public PImageButton newImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
                                       final boolean hideBackground) {

        initializeLayout();
        // Create and position the image button
        final PImageButton ib = new PImageButton(getContext());
        ib.hideBackground = hideBackground;

        ib.setScaleType(ScaleType.FIT_XY);
        // Hide the background if desired
        if (hideBackground) {
            ib.setBackgroundResource(0);
        }

        // Add image asynchronously
        new SetImageTask(ib, false).execute(
                AppRunnerSettings.get().project.getStoragePath()
                        + File.separator
                        + imgNotPressed);

        // Add the view
        addViewAbsolute(ib, x, y, w, h);

        return ib;

    }

    // --------- getRequest ---------//
    public interface addGridOfCB {
        void event(ProtocoderNativeObject json);
    }

    public PGrid newGridOf(String type, NativeArray array, int cols, final addGridOfCB callbackfn) {

        PGrid gridLayout = new PGrid(getContext());
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
                ll2.addViewInRow(new Space(getContext()));
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
                    btn = newButton(name);

                    btn.onClick(new PButton.addGenericButtonCB() {
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
                    PImageButton btn = new PImageButton(getContext());


                    //toggle
                } else if (type.equals("toggle")) {
                    PToggleButton toggle = newToggle(name, false).onChange(new PToggleButton.addGenericToggleCB() {
                        @Override
                        public void event(boolean isChecked) {

                            cbData.addPE("data", isChecked);
                            callbackfn.event(cbData);

                        }
                    });
                    ll2.addViewInRow(toggle);

                    //hslider
                } else if (type.equals("hslider")) {
                    PSlider slider = newSlider(1024, 0).onChange(new addGenericSliderCB() {
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

        return gridLayout;
    }

	/* ------------------------------ */


    @ProtoMethod(description = "Creates a new map", example = "")
    @ProtoMethodParam(params = {""})
    public PMap newMap() {
        initializeLayout();
        PMap mapView = new PMap(getContext(), 256);

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


    @ProtoMethod(description = "Adds a video view and starts playing the fileName", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public PVideo newVideo(final String videoFile) {
        initializeLayout();
        final PVideo video = new PVideo(getContext(), videoFile);

        return video;
    }


    @ProtoMethod(description = "Creates a new drawing canvas", example = "")
    @ProtoMethodParam(params = {"width", "height"})
    public PCanvas newCanvas(int w, int h) {
        initializeLayout();
        PCanvas canvasView = new PCanvas(getContext(), w, h);

        return canvasView;
    }


    @ProtoMethod(description = "Creates a new web view", example = "")
    @ProtoMethodParam(params = {""})
    public PWebView newWebview() {
        initializeLayout();
        PWebView webView = new PWebView(getContext());

        return webView;
    }


    @ProtoMethod(description = "Creates a new camera view", example = "")
    @ProtoMethodParam(params = {"type={0,1}"})
    public PCamera newCameraView(String type) {
        initializeLayout();

        int camNum = -1;
        if (type.equals("front")) {
            camNum = CameraNew.MODE_CAMERA_FRONT;
        } else if (type.equals("back")) {
            camNum = CameraNew.MODE_CAMERA_BACK;
        }

        PCamera pCamera = new PCamera(getActivity(), camNum, PCamera.MODE_COLOR_COLOR);

        return pCamera;
    }

    /**
     * This class lets us download an image asynchronously without blocking the
     * UI thread
     *
     * @author ncbq76
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
     * @author ncbq76 / Modifications by @josejuansanchez
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
            return loadImage(imagePath);
        }

        private Object loadImage(String imagePath) {
            File imgFile = new File(imagePath);

            if (imgFile.exists()) {
                fileExtension = FileIO.getFileExtension(imagePath);
                if (fileExtension.equals("svg")) {

                    File file = new File(imagePath);
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                        SVG svg = new SVGBuilder().readFromInputStream(fileInputStream).build();
                        return svg.getDrawable();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    return Image.loadBitmap(imagePath);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            bgImage.setScaleType(ImageView.ScaleType.FIT_XY);

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
	 * @author ncbq76 / Modifications by @josejuansanchez
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
            return loadImage(imagePath);
        }

        private Bitmap loadImage(String imagePath) {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                return bmp;
            } catch(final Throwable tx) {
                return null;
            }
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