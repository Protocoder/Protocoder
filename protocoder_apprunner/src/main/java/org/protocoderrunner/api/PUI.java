package org.protocoderrunner.api;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.AppRunnerFragment;
import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnInterfaceWithReturn;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.api.media.PCamera;
import org.protocoderrunner.api.media.PCamera2;
import org.protocoderrunner.api.other.PLooper;
import org.protocoderrunner.api.other.PProcessing;
import org.protocoderrunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.api.widgets.PButton;
import org.protocoderrunner.api.widgets.PCanvas;
import org.protocoderrunner.api.widgets.PCheckBox;
import org.protocoderrunner.api.widgets.PImage;
import org.protocoderrunner.api.widgets.PImageButton;
import org.protocoderrunner.api.widgets.PInput;
import org.protocoderrunner.api.widgets.PKnob;
import org.protocoderrunner.api.widgets.PLinearLayout;
import org.protocoderrunner.api.widgets.PList;
import org.protocoderrunner.api.widgets.PMap;
import org.protocoderrunner.api.widgets.PMatrix;
import org.protocoderrunner.api.widgets.PNumberPicker;
import org.protocoderrunner.api.widgets.PPlot;
import org.protocoderrunner.api.widgets.PPopupDialogFragment;
import org.protocoderrunner.api.widgets.PProgressBar;
import org.protocoderrunner.api.widgets.PRadioButtonGroup;
import org.protocoderrunner.api.widgets.PScrollView;
import org.protocoderrunner.api.widgets.PSlider;
import org.protocoderrunner.api.widgets.PSpinner;
import org.protocoderrunner.api.widgets.PSwitch;
import org.protocoderrunner.api.widgets.PText;
import org.protocoderrunner.api.widgets.PToggle;
import org.protocoderrunner.api.widgets.PToolbar;
import org.protocoderrunner.api.widgets.PTouchPad;
import org.protocoderrunner.api.widgets.PVideo;
import org.protocoderrunner.api.widgets.PViewMethodsInterface;
import org.protocoderrunner.api.widgets.PViewPager;
import org.protocoderrunner.api.widgets.PWebView;
import org.protocoderrunner.api.widgets.WidgetHelper;
import org.protocoderrunner.apidoc.annotation.ProtoField;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apidoc.annotation.ProtoObject;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.FeatureNotAvailableException;
import org.protocoderrunner.apprunner.PermissionNotGrantedException;
import org.protocoderrunner.apprunner.StyleProperties;
import org.protocoderrunner.base.gui.CameraNew;
import org.protocoderrunner.base.utils.AndroidUtils;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import processing.core.PApplet;

/**
 * Hola
 * @author Victor Diaz
 */
@ProtoObject
public class PUI extends ProtoBase {


    private final AppRunner mAppRunner;
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

    LinkedHashMap<String, StyleProperties> styles = new LinkedHashMap<>();

    public StyleProperties style;
    public StyleProperties theme;

    public PUI(AppRunner appRunner) {
        super(appRunner);
        mAppRunner = appRunner;
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
     * It has to be programatically created since it might be used somewhere else without access to the R file
     */
    protected void initializeLayout() {
        if (!isMainLayoutSetup) {
            MLog.d(TAG, "" + getAppRunner());
            MLog.d(TAG, "" + getAppRunner().interp);

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
            uiScrollView = new PScrollView(getContext(), false);
            uiScrollView.setLayoutParams(layoutParams);
            // uiScrollView.setFillViewport(true);
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

            // style.put("x", style, 0);
            // style.put("y", style, 0);
            // nativeObject.put("width", nativeObject, 100);
            // nativeObject.put("height", nativeObject, 100);
            setTheme();
            setStyle();
            background((String) theme.get("secondaryColor"));
        }
    }


    private void setTheme() {
        theme = new StyleProperties();
        theme.put("accentColor", "#FDD629");
        theme.put("primaryColor", "#efefef");
        theme.put("secondaryColor", "#2c2c2c");
    }

    private void setStyle() {
        String accentColor = (String) theme.get("accentColor");
        String primaryColor = (String) theme.get("primaryColor");
        String secondaryColor = (String) theme.get("secondaryColor");
        String transparentColor = "#00FFFFFF";

        style = new StyleProperties();
        style.put("enabled", style, true);
        style.put("opacity", style, 1.0f);
        style.put("visibility", style, "visible");
        style.put("background", style, primaryColor);
        style.put("backgroundHover", style, "#88000000");
        style.put("backgroundPressed", style, accentColor);
        style.put("backgroundSelected", style, "#88000000");
        style.put("backgroundChecked", style, "#88000000");
        style.put("borderColor", style, transparentColor);
        style.put("borderWidth", style, 0);
        style.put("borderRadius", style, 10);

        style.put("src", style, "");
        style.put("srcPressed", style, "");

        style.put("textColor", style, secondaryColor);
        style.put("textSize", style, 18);
        style.put("textFont", style, "normal");
        style.put("textStyle", style, "normal");
        style.put("textAlign", style, "center");
        style.put("textTransform", style, "none");
        style.put("padding", style, 0);

        style.put("hintColor", style, "#88FFFFFF");

        style.put("animInBefore", style, "this.x(0).y(100)");
        style.put("animIn", style, "this.animate().x(100)");
        style.put("animOut", style, "this.animate().x(0)");

        style.put("slider", style, accentColor);
        style.put("sliderPressed", style, accentColor);
        style.put("sliderHeight", style, 20);
        style.put("sliderBorderSize", style, 0);
        style.put("sliderBorderColor", style, transparentColor);
        style.put("padSize", style, 20);
        style.put("padColor", style, "#00BB00");
        style.put("padBorderColor", style, "#0000BB");
        style.put("padBorderSize", style, 20);

        style.put("knobBorderWidth", style, 5);
        style.put("knobProgressSeparation", style, 50);
        style.put("knobBorderColor", style, accentColor);
        style.put("knobProgressColor", style, accentColor);

        style.put("matrixCellColor", style, "#00000000");
        style.put("matrixCellSelectedColor", style, accentColor);
        style.put("matrixCellBorderSize", style, 3);
        style.put("matrixCellBorderColor", style, secondaryColor);
        style.put("matrixCellBorderRadius", style, 2);

        style.put("plotColor", style, "#000000");
        style.put("plotWidth", style, 5);

        styles.put("*", style);

        StyleProperties buttonStyle = new StyleProperties();
        buttonStyle.put("textStyle", buttonStyle, "bold");
        buttonStyle.put("textAlign", buttonStyle, "center");
        styles.put("button", buttonStyle);

        StyleProperties imageStyle = new StyleProperties();
        imageStyle.put("background", imageStyle, transparentColor);
        imageStyle.put("srcMode", imageStyle, "fit");
        styles.put("image", imageStyle);

        StyleProperties imageButtonStyle = new StyleProperties();
        imageButtonStyle.put("srcMode", imageButtonStyle, "resize");
        style.put("srcTintPressed", style, primaryColor);
        styles.put("imagebutton", imageButtonStyle);

        StyleProperties knobStyle = new StyleProperties();
        knobStyle.put("background", knobStyle, transparentColor);
        knobStyle.put("textColor", knobStyle, accentColor);
        knobStyle.put("textFont", knobStyle, "monospace");
        knobStyle.put("textSize", knobStyle, 10);
        styles.put("knob", knobStyle);

        StyleProperties textStyle = new StyleProperties();
        textStyle.put("background", textStyle, transparentColor);
        textStyle.put("textColor", textStyle, "#ffffff");
        textStyle.put("textSize", textStyle, 25);
        textStyle.put("textAlign", textStyle, "left");
        styles.put("text", textStyle);

        StyleProperties toggleStyle = new StyleProperties();
        toggleStyle.put("textColor", toggleStyle, primaryColor);
        toggleStyle.put("background", toggleStyle, secondaryColor);
        toggleStyle.put("backgroundChecked", toggleStyle, accentColor);
        toggleStyle.put("borderColor", toggleStyle, "#ffffff");
        toggleStyle.put("borderWidth", toggleStyle, 5);
        styles.put("toggle", toggleStyle);

        StyleProperties inputStyle = new StyleProperties();
        inputStyle.put("textAlign", inputStyle, "left");
        inputStyle.put("background", inputStyle, transparentColor);
        inputStyle.put("borderColor", inputStyle, "#ffffff");
        inputStyle.put("borderWidth", inputStyle, 5);
        inputStyle.put("textColor", inputStyle, "#ffffff");
        styles.put("input", inputStyle);


        StyleProperties matrixStyle = new StyleProperties();
        textStyle.put("background", matrixStyle, transparentColor);
        textStyle.put("backgroundPressed", matrixStyle, transparentColor);
        styles.put("matrix", matrixStyle);

        StyleProperties plotStyle = new StyleProperties();
        plotStyle.put("textColor", plotStyle, "#55000000");

        styles.put("plot", plotStyle);
    }

    public void registerStyle(String name, StyleProperties widgetProp) {
        if (styles.containsKey(name) == false) {
            styles.put(name, widgetProp);
        }
    }


    public LinkedHashMap<String, StyleProperties> getStyles() {
        return styles;
    }

    public void startEditor() {
        PLooper loop = mAppRunner.pUtil.loop(1000, new PLooper.LooperCB() {
            @Override
            public void event() {
                ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
                for (int i = 0; i < viewArray.size(); i++) {
                    PViewMethodsInterface view = (PViewMethodsInterface) viewArray.get(i);
                    Map style = view.getStyle();
                    HashMap<String, Object> o = new HashMap<String, Object>();
                    o.put("name", view.getClass().getSimpleName());
                    o.put("type", view.getClass().getSimpleName());
                    // MLog.d(TAG, "-->" + view);
                    Bitmap bmpView = takeViewScreenshot((View) view);
                    String base64ImgString = mAppRunner.pUtil.bitmapToBase64String(bmpView);
                    // MLog.d(TAG, base64ImgString);
                    o.put("image", "data:image/png;base64," + base64ImgString);
                    o.put("osc", "/osc");
                    o.put("x", ((View) view).getX()); 
                    o.put("y", ((View) view).getY() + 27); // adding status bar
                    o.put("width", ((View) view).getWidth());
                    o.put("height", ((View) view).getHeight());
                    // o.put("properties", getStyles.props);
                    arrayList.add(o);
                }
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(arrayList);
                // MLog.d(TAG, json.toString());

                // send event
                MLog.d("views", "sending event");
                Intent i = new Intent("org.protocoder.intent.VIEWS_UPDATE");
                i.putExtra("views", json.toString());
                mAppRunner.getAppContext().sendBroadcast(i);

            }
        });
        loop.start();
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

    public void updateScreenSizes() {
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

        if (v instanceof PViewMethodsInterface) ((PViewMethodsInterface) v).set(x, y, w, h);
        uiAbsoluteLayout.addView(v, x, y, w, h);
    }

    protected void addView(View v) {
        v.setAlpha(0);
        // v.setRotationX(-30);
        v.animate().alpha(1).setDuration(300).setStartDelay(100 * (1 + viewArray.size()));
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


    public void addTitle(String title) {

        /*
        PText t = newText(title);

        t.setX(-100f);
        t.setAlpha(0.0f);
        t.animate().x(50).alpha(1.0f);
        t.props.put("background", t.props, "#000000");
        t.props.put("textFont", t.props, "monospace");
        t.props.put("textStyle", t.props, "bold");
        t.props.put("textColor", t.props, "#f5d328");
        t.props.put("textSize", t.props, 20);
        t.props.put("borderRadius", t.props, 0);
        t.setPadding(20, 10, 20, 10);
        t.setAllCaps(true);

        addViewAbsolute(t, 0.0f, 0.05f, -1, -1);
        */


        getFragment().changeTitle(title);

    }

    public void addSubtitle(String subtitle) {
        getFragment().changeSubtitle(subtitle);
    }

    /**
     * Button
     */
    @ProtoMethod(description = "Creates a new button", example = "")
    @ProtoMethodParam(params = {"label"})
    public PButton newButton(String label) {
        PButton b = new PButton(mAppRunner);
        b.setText(label);
        return b;
    }

    /**
     * Adds a button to the main screen
     *
     * @param label Text that appears in the button
     * @param x Horizontal position
     * @param y Vertical position
     * @param w Width
     * @param h Height
     * @return llal
     */
    @ProtoMethod
    public PButton addButton(String label, float x, float y, float w, float h) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, w, h);

        return b;
    }

    /**
     * Adds a button to the main screen
     *
     * @param label Text that appears in the button
     * @param x Horizontal position
     * @param y Vertical position
     * @return lall
     */
    @ProtoMethod(description = "Adds a button", example = "")
    @ProtoMethodParam(params = {"label", "x", "y"})
    public PButton addButton(String label, float x, float y) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, -1, -1);
        return b;
    }

    private float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    public PButton addButton(Map style) {
        PButton b = newButton("hi");
        b.setStyle(style);
        addViewAbsolute(b, toFloat(style.get("x")), toFloat(style.get("y")), toFloat(style.get("width")), toFloat(style.get("height")));

        return b;
    }

    @ProtoMethod(description = "Creates a new image button", example = "")
    @ProtoMethodParam(params = {})
    public PImageButton newImageButton(String imagePath) {
        final PImageButton ib = new PImageButton(getAppRunner());
        ib.load(imagePath);

        return ib;
    }

    public PImageButton addImageButton(String imagePath, float x, float y, float w, float h) {
        PImageButton pImageButton = newImageButton(imagePath);
        addViewAbsolute(pImageButton, x, y, w, h);
        return pImageButton;
    }

    public PImageButton addImageButton(String imagePath, float x, float y) {
        PImageButton pImageButton = newImageButton(imagePath);
        addViewAbsolute(pImageButton, x, y, -1, -1);
        return pImageButton;
    }

    /**
     * Text
     */
    @ProtoMethod(description = "Creates a new text", example = "")
    @ProtoMethodParam(params = {"text"})
    public PText newText(String text) {
        // TODO fix pixels to sp
        // int defaultTextSize = AndroidUtils.pixelsToSp(getContext(), 16);
        PText tv = new PText(mAppRunner);
        // tv.setTextSize((float) defaultTextSize);
        tv.setTextSize(22);
        tv.setText(text);
        tv.setTextColor(Color.argb(255, 255, 255, 255));
        return tv;
    }

    @ProtoMethod(description = "Adds a text box defined only by its position", example = "")
    @ProtoMethodParam(params = {"label", "x", "y"})
    public PText addText(float x, float y) {
        PText tv = newText("");
        addViewAbsolute(tv, x, y, -1, -1);
        return tv;
    }

    @ProtoMethod(description = "Add a text box defined by its position and size", example = "")
    @ProtoMethodParam(params = {"text", "x", "y", "w", "h"})
    public PText addText(float x, float y, float w, float h) {
        PText tv = newText("");
        addViewAbsolute(tv, x, y, w, h);
        return tv;
    }

    @ProtoMethod(description = "Adds a text box defined only by its position", example = "")
    @ProtoMethodParam(params = {"text", "x", "y"})
    public PText addText(String text, float x, float y) {
        PText tv = newText(text);
        addViewAbsolute(tv, x, y, -1, -1);
        return tv;
    }

    @ProtoMethod(description = "Add a text box defined by its position and size", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PText addText(String label, float x, float y, float w, float h) {
        PText tv = newText(label);
        addViewAbsolute(tv, x, y, w, h);
        return tv;
    }

    /**
     * InputText
     */

    @ProtoMethod(description = "Creates a new input", example = "")
    @ProtoMethodParam(params = {"label"})
    public PInput newInput(String label) {
        final PInput et = new PInput(mAppRunner);
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
        PCheckBox cb = new PCheckBox(mAppRunner);
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
    public PToggle newToggle(final String label) {
        PToggle tb = new PToggle(mAppRunner);
        tb.checked(false);
        tb.text(label);
        return tb;
    }

    @ProtoMethod(description = "Adds a toggle", example = "")
    @ProtoMethodParam(params = {"text", "x", "y", "w", "h"})
    public PToggle addToggle(final String text, float x, float y, float w, float h) {
        PToggle tb = newToggle(text);
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
        // final PSlider slider = new PSlider(mAppRunner).range(0, 1);
        final PSlider slider = new PSlider(mAppRunner);
        return slider;
    }

    @ProtoMethod(description = "Adds a slider with a [0, 1] range", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PSlider addSlider(float x, float y, float w, float h) {
        PSlider slider = newSlider();
        addViewAbsolute(slider, x, y, w, h);
        return slider;
    }

    /**
     * Knob
     */

    @ProtoMethod(description = "Creates a new slider", example = "")
    @ProtoMethodParam(params = {"max", "max"})
    public PKnob newKnob() {
        final PKnob slider = new PKnob(mAppRunner);
        return slider;
    }

    @ProtoMethod(description = "Adds a slider with a [0, 1] range", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PKnob addKnob(float x, float y, float w, float h) {
        PKnob slider = newKnob();
        addViewAbsolute(slider, x, y, w, h);
        return slider;
    }

    @ProtoMethod(description = "Creates a new slider", example = "")
    @ProtoMethodParam(params = {"max", "max"})
    public PMatrix newMatrix(int m, int n) {
        final PMatrix matrix = new PMatrix(mAppRunner, m, n);
        return matrix;
    }

    @ProtoMethod(description = "Adds a matrix with M and N size", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "m", "n" })
    public PMatrix addMatrix(float x, float y, float w, float h, int m, int n) {
        PMatrix matrix = newMatrix(m, n);
        addViewAbsolute(matrix, x, y, w, h);
        return matrix;
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
    public PImage newImage(String imagePath) {
        final PImage iv = new PImage(mAppRunner);
        if (imagePath != null) iv.load(imagePath);

        return iv;
    }

    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = { "x", "y",})
    public PImage addImage(float x, float y) {
        final PImage iv = newImage(null);
        addViewAbsolute(iv, x, y, -1, -1);
        return iv;
    }


    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PImage addImage(float x, float y, float w, float h) {
        final PImage iv = newImage(null);
        addViewAbsolute(iv, x, y, w, h);
        return iv;
    }

    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = { "imagePath", "x", "y",})
    public PImage addImage(String imagePath, float x, float y) {
        final PImage iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, -1, -1);

        return iv;
    }

    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"imagePath", "x", "y", "w", "h"})
    public PImage addImage(String imagePath, float x, float y, float w, float h) {
        final PImage iv = newImage(imagePath);
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
    public PPlot newPlot() {
        PPlot pPlot = new PPlot(mAppRunner);
        // pPlotView.range(0, 1);
        return pPlot;
    }

    @ProtoMethod(description = "Adds a plot, by default the range is [0, 1]", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PPlot addPlot(float x, float y, float w, float h) {
        PPlot pPlot = newPlot();
        addViewAbsolute(pPlot, x, y, w, h);
        return pPlot;
    }

    /**
     * XYPad
     */
    @ProtoMethod(description = "Creates a new touch pad", example = "")
    @ProtoMethodParam(params = {"function(data)"})
    public PTouchPad newTouchPad() {
        PTouchPad taV = new PTouchPad(mAppRunner);

        return taV;
    }

    @ProtoMethod(description = "Creates a new touch pad", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PTouchPad addTouchPad(float x, float y, float w, float h) {
        PTouchPad taV = newTouchPad();
        addViewAbsolute(taV, x, y, w, h);

        return taV;
    }

    /**
     * Canvas
     */
    @ProtoMethod(description = "Creates a new drawing canvas", example = "")
    @ProtoMethodParam(params = {"width", "height"})
    public PCanvas newCanvas() {
        PCanvas canvasView = new PCanvas(mAppRunner);
        return canvasView;
    }

    private PCanvas newCanvas(int w, int h) {
        PCanvas canvasView = new PCanvas(mAppRunner);
        return canvasView;
    }

    @ProtoMethod(description = "Adds a canvas view", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PCanvas addCanvas(float x, float y, float w, float h) {
        final PCanvas canvasView = newCanvas(); // (int) w, (int) h);
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

    public PList newList(int numCols, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        return new PList(getAppRunner(), numCols, data, creating, binding);
    }


    public PList addGrid(float x, float y, float w, float h, int numCols, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        PList list = newList(numCols, data, creating, binding);
        addViewAbsolute(list, x, y, w ,h);

        return list;
    }

    public PList addList(float x, float y, float w, float h, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        PList list = newList(1, data, creating, binding);
        addViewAbsolute(list, x, y, w ,h);

        return list;
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

    public void showWeb(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(Color.BLUE);
        builder.addDefaultShareMenuItem();
        builder.setInstantAppsEnabled(true);

        // builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        // builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
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


    @ProtoMethodParam(params = {"View"})
    public void clipAndShadow(View v, int type, int r) {
        AndroidUtils.setViewGenericShadow(v, type, 0, 0, v.getWidth(), v.getHeight(), r);
        // v.setElevation();
        // v.setZ();
        // v.animate().
    }

    //@TargetApi(L)

    @ProtoMethodParam(params = {"View"})
    public void clipAndShadow(View v, int type, int x, int y, int w, int h, int r) {
        AndroidUtils.setViewGenericShadow(v, type, x, y, w, h, r);
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
        /* Maybe this?
        view.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        */

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ArrayList<ReturnObject> ar = new ArrayList<ReturnObject>();
                int action = MotionEventCompat.getActionMasked(event);
                int index = MotionEventCompat.getActionIndex(event);

                String actionString = AndroidUtils.actionToString(action);
                // MLog.d(TAG, "pointer " + index + " " + actionString);

                boolean ret = false;
                try {
                    int numPoints = event.getPointerCount();
                    for (int i = 0; i < numPoints; i++) {
                        int id = event.getPointerId(i);
                        ReturnObject r = new ReturnObject();
                        r.put("x", event.getX(id));
                        r.put("y", event.getY(id));
                        r.put("id", id);
                        r.put("action", "move");
                        ar.add(r);
                    }

                    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                        ReturnObject r = ar.get(index);
                        r.put("action", "down");
                        ret = true;
                    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                        ReturnObject r = ar.get(index);
                        r.put("action", "up");
                    }

                    ReturnObject returnObject = new ReturnObject();
                    returnObject.put("touches", ar);
                    returnObject.put("num", numPoints);

                    callback.event(returnObject);
                } catch (IllegalArgumentException e) {}


                return ret;
            }
        });
    }

    @ProtoMethod(description = "Takes a screenshot of a view", example = "")
    @ProtoMethodParam(params = {"view"})
    public Bitmap takeViewScreenshot(View v) {
        return AndroidUtils.takeScreenshotView(v);
    }

    /*
     * Load thingies
     */


    @Override
    public void __stop() {

    }

}