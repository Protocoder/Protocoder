package org.protocoderrunner.api;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.protocoderrunner.AppRunnerFragment;
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.api.media.PCamera;
import org.protocoderrunner.api.media.PCamera2;
import org.protocoderrunner.api.other.PProcessing;
import org.protocoderrunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.api.widgets.PButton;
import org.protocoderrunner.api.widgets.PCanvas;
import org.protocoderrunner.api.widgets.PCheckBox;
import org.protocoderrunner.api.widgets.PInput;
import org.protocoderrunner.api.widgets.PImageButton;
import org.protocoderrunner.api.widgets.PImageView;
import org.protocoderrunner.api.widgets.PLinearLayout;
import org.protocoderrunner.api.widgets.PMap;
import org.protocoderrunner.api.widgets.PNumberPicker;
import org.protocoderrunner.api.widgets.PPadView;
import org.protocoderrunner.api.widgets.PPlotView;
import org.protocoderrunner.api.widgets.PPopupDialogFragment;
import org.protocoderrunner.api.widgets.PProgressBar;
import org.protocoderrunner.api.widgets.PRadioButtonGroup;
import org.protocoderrunner.api.widgets.PScrollView;
import org.protocoderrunner.api.widgets.PSlider;
import org.protocoderrunner.api.widgets.PSpinner;
import org.protocoderrunner.api.widgets.PSwitch;
import org.protocoderrunner.api.widgets.PTextView;
import org.protocoderrunner.api.widgets.PToggleButton;
import org.protocoderrunner.api.widgets.PToolbar;
import org.protocoderrunner.api.widgets.PVideo;
import org.protocoderrunner.api.widgets.PViewPager;
import org.protocoderrunner.api.widgets.PWebView;
import org.protocoderrunner.api.widgets.WidgetHelper;
import org.protocoderrunner.apidoc.annotation.ProtoField;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.FeatureNotAvailableException;
import org.protocoderrunner.apprunner.PermissionNotGrantedException;
import org.protocoderrunner.base.gui.CameraNew;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;

import processing.core.PApplet;

public class PUI extends ProtoBase {

    // contains a reference of all views added to the absolute layout
    private ArrayList<View> viewArray = new ArrayList<>();

    // UI
    private boolean isMainLayoutSetup = false;
    private boolean isScrollEnabled = false;
    protected PAbsoluteLayout uiAbsoluteLayout;
    private RelativeLayout uiHolderLayout;
    private PScrollView uiScrollView;

    private int screenWidth;
    private int screenHeight;

    public PUI(AppRunner appRunner) {
        super(appRunner);
    }

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        if (fragment != null) {
            toolbar = new PToolbar(getAppRunner(), getActivity().getSupportActionBar());
        }
        initializeLayout();
    }

    /**
     * This method creates the basic layout where the user created views will lay out
     */
    protected void initializeLayout() {
        if (!isMainLayoutSetup) {
            // View v = getActivity().getLayoutInflater().inflate(R.layout.apprunner_user_layout, null);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // uiHolderLayout = (RelativeLayout) v.findViewById(R.id.holder);
            // uiScrollView = (PScrollView) v.findViewById(R.id.scroll);
            // uiAbsoluteLayout = (PAbsoluteLayout) v.findViewById(R.id.absolute);


            // this is the structure of the layout
            // uiHolderLayout (background color)
            // [scrollview] if (isScrollEnabled)
            // [uiAbsoluteLayout] if (!isScrollEnabled)

            // set the holder
            uiHolderLayout = new RelativeLayout(getContext());
            uiHolderLayout.setLayoutParams(layoutParams);

            // We need to let the view scroll, so we're creating a scrollview
            uiScrollView = new PScrollView(getContext(), true);
            uiScrollView.setLayoutParams(layoutParams);
            uiScrollView.setFillViewport(true);
            allowScroll(isScrollEnabled);

            // Create the main layout. This is where all the items actually go
            uiAbsoluteLayout = new PAbsoluteLayout(getAppRunner());
            uiAbsoluteLayout.setLayoutParams(layoutParams);
            uiScrollView.addView(uiAbsoluteLayout);

            if (getFragment() != null) {
                getFragment().addScriptedLayout(uiHolderLayout);
            } else if (getService() != null) {
                getService().addScriptedLayout(uiHolderLayout);
            }
            uiHolderLayout.addView(uiScrollView);

            isMainLayoutSetup = true;
        }
    }

    @ProtoMethod(description = "Changes the position mode", example = "")
    @ProtoMethodParam(params = {"['pixels', 'dp', 'normalized']"})
    public void positionMode(String type) {
        uiAbsoluteLayout.mode(type);
    }


    @ProtoMethod(description = "Sets the fullscreen / immersive / dimBars mode", example = "")
    @ProtoMethodParam(params = {"mode={fullscreen, immersive, lightsout, normal}"})
    public void screenMode(String mode) {

        switch (mode) {
            case "fullscreen":
                getActivity().setFullScreen();
                break;

            case "lightsout":
                getActivity().lightsOutMode();
                break;

            case "immersive":
                getActivity().setImmersive();
                break;

            default:
                getActivity().setNormal();
        }
        
        updateScreenSizes();
    }

    private void updateScreenSizes() {
        screenWidth = uiAbsoluteLayout.width();
        screenHeight = uiAbsoluteLayout.height();
    }

    @ProtoMethod(description = "Forces landscape mode in the app", example = "")
    @ProtoMethodParam(params = {"mode={'landscape', 'portrait', 'auto'"})
    public void screenOrientation(String mode) {
        if (mode.equals("landscape")) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (mode.equals("portrait")) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        updateScreenSizes();
    }

    @ProtoMethod(description = "Adds the given view to the layout", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    protected void addViewAbsolute(View v, float x, float y, float w, float h) {
        addView(v);
        uiAbsoluteLayout.addView(v, x, y, w, h);
    }

    protected void addView(View v) {
        // v.setAlpha(0);
        // v.setRotationX(-30);
        // v.animate().alpha(1).setDuration(500).setStartDelay(100 * (1 + viewArray.size()));
        viewArray.add(v);
    }

    public void removeAllViews() {
        uiAbsoluteLayout.removeAllViews();
        viewArray.clear();
    }

    @ProtoMethod(description = "Allows the main interface to scroll up and down", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void allowScroll(boolean scroll) {
        uiScrollView.setScrollingEnabled(scroll);
        isScrollEnabled = scroll;
    }

    @ProtoField(description = "Toolbar", example = "")
    public PToolbar toolbar;

    @ProtoMethod(description = "Changes the background color using grayscale", example = "")
    @ProtoMethodParam(params = {"gray"})
    public void background(int gray) {
        uiHolderLayout.setBackgroundColor(Color.rgb(gray, gray, gray));
    }

    @ProtoMethod(description = "Changes the background color using RGB", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public void background(int red, int green, int blue) {
        uiHolderLayout.setBackgroundColor(Color.rgb(red, green, blue));
    }

    @ProtoMethod(description = "Changes the background color using Hexadecimal color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public void background(String c) {
        uiHolderLayout.setBackgroundColor(Color.parseColor(c));
    }

    /**
     * Button
     */
    @ProtoMethod(description = "Creates a new button", example = "")
    @ProtoMethodParam(params = {"label"})
    public PButton newButton(String label) {
        PButton b = new PButton(getContext());
        b.setText(label);
        return b;
    }

    @ProtoMethod(description = "Adds a button", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PButton addButton(String label, float x, float y, float w, float h) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, w, h);
        return b;
    }

    @ProtoMethod(description = "Adds a button", example = "")
    @ProtoMethodParam(params = {"label", "x", "y"})
    public PButton addButton(String label, float x, float y) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, -1, -1);
        return b;
    }

    @ProtoMethod(description = "Creates a new image button", example = "")
    @ProtoMethodParam(params = {})
    public PImageButton newImageButton(String imagePath) {
        final PImageButton ib = new PImageButton(getAppRunner());
        ib.image(imagePath);

        return ib;
    }

    public PImageButton addImageButton(String imagePath, float x, float y, float w, float h) {
        PImageButton pImageButton = newImageButton(imagePath);
        addViewAbsolute(pImageButton, x, y, w, h);

        return pImageButton;
    }


    /**
     * Text
     */
    @ProtoMethod(description = "Creates a new text", example = "")
    @ProtoMethodParam(params = {"text"})
    public PTextView newText(String text) {
        // TODO fix pixels to sp
        // int defaultTextSize = AndroidUtils.pixelsToSp(getContext(), 16);
        PTextView tv = new PTextView(getContext());
        // tv.setTextSize((float) defaultTextSize);
        tv.setTextSize(22);
        tv.setText(text);
        tv.setTextColor(Color.argb(255, 255, 255, 255));
        return tv;
    }

    @ProtoMethod(description = "Adds a text box defined only by its position", example = "")
    @ProtoMethodParam(params = {"label", "x", "y"})
    public PTextView addText(float x, float y) {
        PTextView tv = newText("");
        addViewAbsolute(tv, x, y, -1, -1);
        return tv;
    }

    @ProtoMethod(description = "Add a text box defined by its position and size", example = "")
    @ProtoMethodParam(params = {"text", "x", "y", "w", "h"})
    public PTextView addText(float x, float y, float w, float h) {
        PTextView tv = newText("");
        addViewAbsolute(tv, x, y, w, h);
        return tv;
    }

    @ProtoMethod(description = "Adds a text box defined only by its position", example = "")
    @ProtoMethodParam(params = {"text", "x", "y"})
    public PTextView addText(String text, float x, float y) {
        PTextView tv = newText(text);
        addViewAbsolute(tv, x, y, -1, -1);
        return tv;
    }

    @ProtoMethod(description = "Add a text box defined by its position and size", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PTextView addText(String label, float x, float y, float w, float h) {
        PTextView tv = newText(label);
        addViewAbsolute(tv, x, y, w, h);
        return tv;
    }

    /**
     * InputText
     */

    @ProtoMethod(description = "Creates a new input", example = "")
    @ProtoMethodParam(params = {"label"})
    public PInput newInput(String label) {
        final PInput et = new PInput(getContext());
        et.setHint(label);
        return et;
    }

    @ProtoMethod(description = "Adds an input box", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PInput addInput(float x, float y, float w, float h) {
        PInput et = newInput("");
        addViewAbsolute(et, x, y, w, h);
        return et;
    }

    @ProtoMethod(description = "Adds an input box", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PInput addInput(String label, float x, float y, float w, float h) {
        PInput et = newInput(label);
        addViewAbsolute(et, x, y, w, h);
        return et;
    }

    /**
     * Checkbox
     */
    @ProtoMethod(description = "Creates a new checkbox", example = "")
    @ProtoMethodParam(params = {"name", "boolean"})
    public PCheckBox newCheckbox(String label) {
        PCheckBox cb = new PCheckBox(getContext());
        cb.setText(label);
        return cb;
    }

    @ProtoMethod(description = "Adds a checkbox", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PCheckBox addCheckbox(String label, float x, float y, float w, float h) {
        PCheckBox cb = newCheckbox(label);
        addViewAbsolute(cb, x, y, w, h);
        return cb;
    }

    /**
     * Toggle
     */
    @ProtoMethod(description = "Creates a new toggle", example = "")
    @ProtoMethodParam(params = {"name"})
    public PToggleButton newToggle(final String label) {
        PToggleButton tb = new PToggleButton(getContext());
        tb.setChecked(false);
        tb.setText(label);
        return tb;
    }

    @ProtoMethod(description = "Adds a toggle", example = "")
    @ProtoMethodParam(params = {"text", "x", "y", "w", "h"})
    public PToggleButton addToggle(final String text, float x, float y, float w, float h) {
        PToggleButton tb = newToggle(text);
        addViewAbsolute(tb, x, y, w, h);
        return tb;
    }

    /**
     * Switch
     */
    @ProtoMethod(description = "Creates a new switch", example = "")
    @ProtoMethodParam(params = {"text"})
    public PSwitch newSwitch(String text) {
        PSwitch s = new PSwitch(getContext());
        s.setText(text);
        return s;
    }

    @ProtoMethod(description = "Adds a switch", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PSwitch addSwitch(String text, float x, float y, float w, float h) {
        PSwitch s = newSwitch(text);
        addViewAbsolute(s, x, y, w, h);
        return s;
    }

    // TODO
    @ProtoMethod(description = "Creates a new pager", example = "")
    public PViewPager newViewPager() {
        PViewPager pViewPager = new PViewPager(getContext());
        return pViewPager;
    }

    /**
     * Slider
     */

    @ProtoMethod(description = "Creates a new slider", example = "")
    @ProtoMethodParam(params = {"max", "max"})
    public PSlider newSlider() {
        final PSlider sb = new PSlider(getContext()).range(0, 1);
        return sb;
    }

    @ProtoMethod(description = "Adds a slider with a [0, 1] range", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PSlider addSlider(float x, float y, float w, float h) {
        PSlider sb = newSlider();
        addViewAbsolute(sb, x, y, w, -1);
        return sb;
    }

    /**
     * Progress
     */
    @ProtoMethod(description = "Creates a progress bar of n units", example = "")
    @ProtoMethodParam(params = {"units"})
    public PProgressBar newProgress() {
        PProgressBar pb = new PProgressBar(getContext(), android.R.attr.progressBarStyleHorizontal);
        return pb;
    }

    @ProtoMethod(description = "Add a progress bar", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "max"})
    public PProgressBar addProgressBar(float x, float y, float w, float h) {
        PProgressBar pb = newProgress();
        addViewAbsolute(pb, x, y, w, -1);
        return pb;
    }


    /**
     * RadioButtonGroup
     */
    public PRadioButtonGroup newPRadioButtonGroup() {
        PRadioButtonGroup pRadioButtonGroup = new PRadioButtonGroup(getContext());
        return pRadioButtonGroup;
    }

    @ProtoMethod(description = "Adds a radio button group", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PRadioButtonGroup addRadioButtonGroup(float x, float y) {
        PRadioButtonGroup rbg = newPRadioButtonGroup();
        addViewAbsolute(rbg, x, y, -1, -1);
        return rbg;
    }

    @ProtoMethod(description = "Adds a radio button group", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PRadioButtonGroup addRadioButtonGroup(float x, float y, float w, float h) {
        PRadioButtonGroup rbg = newPRadioButtonGroup();
        addViewAbsolute(rbg, x, y, w, h);
        return rbg;
    }

    /**
     * Image
     */
    @ProtoMethod(description = "Creates a new image view", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public PImageView newImage(String imagePath) {
        final PImageView iv = new PImageView(getAppRunner());
        if (imagePath != null) {
            iv.load(imagePath);
        }
        return iv;
    }

    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = { "x", "y",})
    public PImageView addImage(float x, float y) {
        final PImageView iv = newImage(null);
        addViewAbsolute(iv, x, y, -1, -1);
        return iv;
    }


    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PImageView addImage(float x, float y, float w, float h) {
        final PImageView iv = newImage(null);
        addViewAbsolute(iv, x, y, w, h);
        return iv;
    }

    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = { "imagePath", "x", "y",})
    public PImageView addImage(String imagePath, float x, float y) {
        final PImageView iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, -1, -1);

        return iv;
    }

    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"imagePath", "x", "y", "w", "h"})
    public PImageView addImage(String imagePath, float x, float y, float w, float h) {
        final PImageView iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, w, h);
        return iv;
    }

    /**
     * ChoiceBox
     */
    @ProtoMethod(description = "Creates a new choice box", example = "")
    @ProtoMethodParam(params = {"array"})
    public PSpinner newChoiceBox(final String[] array) {
        PSpinner pSpinner = new PSpinner(getContext());
        pSpinner.setData(array);
        pSpinner.setPrompt("qq");

        return pSpinner;
    }

    @ProtoMethod(description = "Adds a new choice box", example = "")
    @ProtoMethodParam(params = {"array"})
    public PSpinner addChoiceBox(final String[] array, float x, float y, float w, float h) {
        PSpinner pSpinner = newChoiceBox(array);
        addViewAbsolute(pSpinner, x, y, w, h);
        return pSpinner;
    }

    /**
     * NumberPIcker
     */
    @ProtoMethod(description = "Creates a new number picker", example = "")
    @ProtoMethodParam(params = {"from", "to"})
    public PNumberPicker newNumberPicker(int from, int to) {
        PNumberPicker pNumberPicker = new PNumberPicker(getContext());
        pNumberPicker.setMinValue(from);
        pNumberPicker.setMaxValue(to);

        return pNumberPicker;
    }

    @ProtoMethod(description = "Adds a numberpicker", example = "")
    @ProtoMethodParam(params = {"array"})
    public PNumberPicker addNumberPicker(int from, int to, float x, float y, float w, float h) {
        PNumberPicker pNumberPicker = newNumberPicker(from, to);
        addViewAbsolute(pNumberPicker, x, y, w, h);

        return pNumberPicker;
    }

    boolean enableCamera2 = false;

    /**
     * Camera
     */
    @ProtoMethod(description = "Creates a new camera view", example = "")
    @ProtoMethodParam(params = {"['front','back']"})
    public Object newCameraView(String type) {
        int camNum = -1;
        switch (type) {
            case "front":
                camNum = CameraNew.MODE_CAMERA_FRONT;
                break;
            case "back":
                camNum = CameraNew.MODE_CAMERA_BACK;
                break;
        }

        Object pCamera = null;
        if (AndroidUtils.isVersionMarshmallow() && enableCamera2) {
            pCamera = new PCamera2(getAppRunner(), camNum, PCamera.MODE_COLOR_COLOR);
        } else {
            if (check("camera", PackageManager.FEATURE_CAMERA, Manifest.permission.CAMERA)) {
                pCamera = new PCamera(getAppRunner(), camNum, PCamera.MODE_COLOR_COLOR);
            }
        }

        return pCamera;
    }

    private boolean check(String what, String feature, String permission) {
        boolean ret = false;

        PackageManager pm = getContext().getPackageManager();

        // check if available
        if (!pm.hasSystemFeature(feature)) throw new FeatureNotAvailableException(what);
        if (!getActivity().checkPermission(permission)) throw new PermissionNotGrantedException(what);
        ret = true;

        return ret;
    }

    @ProtoMethod(description = "Add camera view", example = "")
    @ProtoMethodParam(params = {"type", "x", "y", "w", "h"})
    public Object addCameraView(String type, float x, float y, float w, float h) {
        Object pCamera = newCameraView(type);
        addViewAbsolute((View) pCamera, x, y, w, h);
        return pCamera;
    }

    /**
     * Video
     */
    @ProtoMethod(description = "Adds a video view and starts playing the fileName", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public PVideo newVideo(final String videoFile) {
        final PVideo video = new PVideo(getAppRunner(), videoFile);
        return video;
    }

    @ProtoMethod(description = "Adds a video view and starts playing the fileName", example = "")
    @ProtoMethodParam(params = {"fileName", "x", "y", "w", "h"})
    public PVideo addVideo(final String videoFile, float x, float y, float w, float h) {
        PVideo video = newVideo(videoFile);
        addViewAbsolute(video, x, y, w, h);
        return video;
    }

    /**
     * Webview
     */
    @ProtoMethod(description = "Creates a new web view", example = "")
    @ProtoMethodParam(params = {""})
    public PWebView newWebView() {
        PWebView webView = new PWebView(getAppRunner());
        return webView;
    }

    @ProtoMethod(description = "Adds a webview", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PWebView addWebView(float x, float y, float w, float h) {
        PWebView webView = newWebView();
        addViewAbsolute(webView, x, y, w, h);
        return webView;
    }

    /**
     * Plot
     */
    @ProtoMethod(description = "Creates a new plot", example = "")
    @ProtoMethodParam(params = {"min", "max"})
    public PPlotView newPlot() {
        PPlotView pPlotView = new PPlotView(getContext());
        pPlotView.range(0, 1);
        return pPlotView;
    }

    @ProtoMethod(description = "Adds a plot, by default the range is [0, 1]", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PPlotView addPlot(float x, float y, float w, float h) {
        PPlotView pPlotView = newPlot();
        addViewAbsolute(pPlotView, x, y, w, h);
        return pPlotView;
    }

    /**
     * XYPad
     */
    @ProtoMethod(description = "Creates a new touch pad", example = "")
    @ProtoMethodParam(params = {"function(data)"})
    public PPadView newTouchPad(final ReturnInterface callbackfn) {
        PPadView taV = new PPadView(getContext());
        taV.setTouchAreaListener(new PPadView.OnTouchAreaListener() {
            @Override
            public void onGenericTouch(ArrayList<PPadView.TouchEvent> t) {
                ReturnObject o = new ReturnObject();
                o.put("points", t);
                callbackfn.event(o);
            }
        });

        return taV;
    }

    @ProtoMethod(description = "Creates a new touch pad", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "function(o)"})
    public PPadView addXYPad(int x, int y, int w, int h, final ReturnInterface callbackfn) {
        PPadView taV = newTouchPad(callbackfn);
        addViewAbsolute(taV, x, y, w, h);

        return taV;
    }

    /**
     * Canvas
     */
    @ProtoMethod(description = "Creates a new drawing canvas", example = "")
    @ProtoMethodParam(params = {"width", "height"})
    public PCanvas newCanvas() {
        PCanvas canvasView = new PCanvas(getAppRunner());
        return canvasView;
    }

    @ProtoMethod(description = "Adds a canvas view", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PCanvas addCanvas(float x, float y, float w, float h) {
        PCanvas canvasView = newCanvas();
        addViewAbsolute(canvasView, x, y, w, h);
        return canvasView;
    }


    /**
     * Map
     */
    @ProtoMethod(description = "Creates a new map", example = "")
    @ProtoMethodParam(params = {""})
    public PMap newMap() {
        PMap mapView = new PMap(getAppRunner());
        return mapView;
    }

    @ProtoMethod(description = "Add a openstreetmap", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PMap addMap(float x, float y, float w, float h) {
        PMap mapView = newMap();
        addViewAbsolute(mapView, x, y, w, h);
        return mapView;
    }

    /**
     * Processing
     * TODO add the newProcessing
     */
    @ProtoMethod(description = "Adds a processing view", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "mode=['p2d', 'p3d']"})
    public PApplet addProcessing(float x, float y, float w, float h, String mode) {
        // Create the main layout. This is where all the items actually go
        FrameLayout fl = new FrameLayout(getContext());
        fl.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        fl.setId(200 + (int) (200 * Math.random()));

        // Add the view
        addViewAbsolute(fl, x, y, w, h);

        PProcessing p = new PProcessing();

        Bundle bundle = new Bundle();
        bundle.putString("mode", mode);
        p.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), p, String.valueOf(fl.getId()));
        ft.commit();

        return p;
    }

    @ProtoMethod(description = "Create a new linear layout", example = "")
    @ProtoMethodParam(params = {""})
    public PLinearLayout newLinearLayout() {
        PLinearLayout pLinearLayout = new PLinearLayout(getAppRunner());
        pLinearLayout.orientation("vertical");

        return pLinearLayout;
    }

    @ProtoMethod(description = "Adds a linear layout", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PLinearLayout addLinearLayout(float x, float y) {
        PLinearLayout pLinearLayout = newLinearLayout();
        addViewAbsolute(pLinearLayout, x, y, -1, -1);

        return pLinearLayout;
    }

    @ProtoMethod(description = "Adds a linear layout", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PLinearLayout addLinearLayout(float x, float y, float w, float h) {
        PLinearLayout pLinearLayout = newLinearLayout();
        addViewAbsolute(pLinearLayout, x, y, w, h);

        return pLinearLayout;
    }

    /**
     * Popup
     */

    public PPopupDialogFragment popup() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PPopupDialogFragment pPopupCustomFragment = PPopupDialogFragment.newInstance(fm);

        return pPopupCustomFragment;
    }

    /**
     * Toast
     */
    @ProtoMethod(description = "Shows a little popup with a given text", example = "")
    @ProtoMethodParam(params = {"text"})
    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @ProtoMethod(description = "Shows a little popup with a given text during t time", example = "")
    @ProtoMethodParam(params = {"text", "duration"})
    public void toast(String text, int duration) {
        Toast.makeText(getContext(), text, duration).show();
    }

    /*
     * Utilities
     */
    @ProtoMethod(description = "Resize a view to a given width and height. If a parameter is -1 then that dimension is not changed", example = "")
    @ProtoMethodParam(params = {"View", "width", "height"})
    public void resize(final View v, int w, int h, boolean animated) {
        if (!animated) {
            if (h != -1) {
                v.getLayoutParams().height = h;
            }
            if (w != -1) {
                v.getLayoutParams().width = w;
            }
            v.setLayoutParams(v.getLayoutParams());
        } else {
            int initHeight = v.getLayoutParams().height;
            int initWidth = v.getLayoutParams().width;
            // v.setLayoutParams(v.getLayoutParams());

            ValueAnimator animH = ValueAnimator.ofInt(initHeight, h);
            animH.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    v.getLayoutParams().height = val;
                    v.setLayoutParams(v.getLayoutParams());
                }
            });
            animH.setDuration(200);
            animH.start();

            ValueAnimator animW = ValueAnimator.ofInt(initWidth, w);
            animW.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    v.getLayoutParams().width = val;
                    v.setLayoutParams(v.getLayoutParams());
                }
            });
            animW.setDuration(200);
            animW.start();
        }
    }


    public void movable(View viewHandler, View viewContainer, ReturnInterface callback) {
        WidgetHelper.setMovable(viewHandler, viewContainer, callback);
    }

    public void removeMovable(View viewHandler) {
        WidgetHelper.removeMovable(viewHandler);
    }

    public void onTouch(View view, final ReturnInterface callback) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();

                ReturnObject r = new ReturnObject();
                r.put("x", event.getX());
                r.put("y", event.getY());

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        r.put("action", "down");
                        break;

                    case MotionEvent.ACTION_MOVE:
                        r.put("action", "move");
                        break;

                    case MotionEvent.ACTION_UP:
                        r.put("action", "up");
                        break;
                }
                callback.event(r);

                return true;
            }
        });
    }

    @Override
    public void __stop() {

    }

}