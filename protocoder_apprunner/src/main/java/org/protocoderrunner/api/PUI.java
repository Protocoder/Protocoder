package org.protocoderrunner.api;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.protocoderrunner.AppRunnerFragment;
import org.protocoderrunner.api.media.PCamera;
import org.protocoderrunner.api.other.PProcessing;
import org.protocoderrunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.api.widgets.PButton;
import org.protocoderrunner.api.widgets.PCanvas;
import org.protocoderrunner.api.widgets.PCheckBox;
import org.protocoderrunner.api.widgets.PEditText;
import org.protocoderrunner.api.widgets.PImageView;
import org.protocoderrunner.api.widgets.PLinearLayout;
import org.protocoderrunner.api.widgets.PMap;
import org.protocoderrunner.api.widgets.PPlotView;
import org.protocoderrunner.api.widgets.PProgressBar;
import org.protocoderrunner.api.widgets.PRadioButtonGroup;
import org.protocoderrunner.api.widgets.PScrollView;
import org.protocoderrunner.api.widgets.PSlider;
import org.protocoderrunner.api.widgets.PSwitch;
import org.protocoderrunner.api.widgets.PTextView;
import org.protocoderrunner.api.widgets.PToggleButton;
import org.protocoderrunner.api.widgets.PToolbar;
import org.protocoderrunner.api.widgets.PVideo;
import org.protocoderrunner.api.widgets.PViewPager;
import org.protocoderrunner.api.widgets.PWebView;
import org.protocoderrunner.apidoc.annotation.ProtoField;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.base.gui.CameraNew;

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


    /**
     * Interface for key up / down
     */
    public interface onKeyListener {
        public void onKeyDown(int keyCode);
        public void onKeyUp(int keyCode);
    }

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
            uiAbsoluteLayout = new PAbsoluteLayout(getContext());
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

    @ProtoMethod(description = "Adds the given view to the layout", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    protected void addViewAbsolute(View v, float x, float y, float w, float h) {
        addView(v);
        uiAbsoluteLayout.addView(v, x, y, w, h);
    }

    protected void addView(View v) {
        v.setAlpha(0);
        v.setRotationX(-30);
        v.animate().alpha(1).rotationX(0).setDuration(500).setStartDelay(100 * (1 + viewArray.size()));
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
    public PEditText newInput(String label) {
        final PEditText et = new PEditText(getContext());
        et.setHint(label);
        return et;
    }

    @ProtoMethod(description = "Adds an input box", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PEditText addInput(float x, float y, float w, float h) {
        PEditText et = newInput("");
        addViewAbsolute(et, x, y, w, h);
        return et;
    }

    @ProtoMethod(description = "Adds an input box", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PEditText addInput(String label, float x, float y, float w, float h) {
        PEditText et = newInput(label);
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
    public PSlider newSlider(float min, float max) {
        final PSlider sb = new PSlider(getContext()).min(min).max(max);
        return sb;
    }

    @ProtoMethod(description = "Adds a slider with a [0, 1] range", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PSlider addSlider(float x, float y, float w, float h) {
        PSlider sb = newSlider(0, 1);
        addViewAbsolute(sb, x, y, w, -1);
        return sb;
    }

    @ProtoMethod(description = "Adds a slider with a [min, max] range", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "min", "max"})
    public PSlider addSlider(float x, float y, float w, float h, float min, float max) {
        PSlider sb = newSlider(min, max);
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
     * Camera
     */
    @ProtoMethod(description = "Creates a new camera view", example = "")
    @ProtoMethodParam(params = {"['front','back']"})
    public PCamera newCameraView(String type) {
        int camNum = -1;
        switch (type) {
            case "front":
                camNum = CameraNew.MODE_CAMERA_FRONT;
                break;
            case "back":
                camNum = CameraNew.MODE_CAMERA_BACK;
                break;
        }
        PCamera pCamera = new PCamera(getAppRunner(), camNum, PCamera.MODE_COLOR_COLOR);

        return pCamera;
    }

    @ProtoMethod(description = "Add camera view", example = "")
    @ProtoMethodParam(params = {"type", "x", "y", "w", "h"})
    public PCamera addCameraView(String type, float x, float y, float w, float h) {
        PCamera pCamera = newCameraView(type);
        addViewAbsolute(pCamera, x, y, w, h);
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
        canvasView.autoDraw(true);
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
    public PLinearLayout addLinearLayout(float x, float y, float w, float h) {
        PLinearLayout pLinearLayout = newLinearLayout();
        addViewAbsolute(pLinearLayout, x, y, w, h);

        return pLinearLayout;
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

    @Override
    public void __stop() {

    }

}