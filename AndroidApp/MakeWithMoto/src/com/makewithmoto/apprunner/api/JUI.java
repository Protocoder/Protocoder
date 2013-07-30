package com.makewithmoto.apprunner.api;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makewithmoto.apidoc.APIAnnotation;

public class JUI extends JInterface {

    FrameLayout mMainLayout;
    Boolean isMainLayoutSetup = false;

    public JUI(Activity a) {
        super(a);
    }

    private void initializeLayout() {
        if (!isMainLayoutSetup) {
            mMainLayout = new FrameLayout(c.get());
            mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            c.get().setContentView(mMainLayout);
            isMainLayoutSetup = true;
        }
    }

    private void positionView(View v, int x, int y) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
    }

    private void positionView(View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(w, h);
        params.leftMargin = x;
        params.topMargin = y;
        v.setLayoutParams(params);
    }

    @JavascriptInterface
    @APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
    public void button(String label, int x, int y, int w, int h, final String callbackfn) {
        initializeLayout();

        //Create the button
        Button b = new Button(c.get());
        b.setText(label);
        positionView(b, x, y, w, h);

        //Set on click behavior
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback(callbackfn);
            }
        });

        //Add the view to the layout
        mMainLayout.addView(b);
    }

    public void postLayout() {
        // TODO: Do we even need this??
        //  c.get().setContentView(mMainLayout);  
    }

    @JavascriptInterface
    public void seekbar(String label, String min, String max, String progress, boolean circular, final String callbackfn) {

        // Add a seekbar, circular flag to indicate if its a circular seekbar. If the slider position is changed,
        // call the callback function

    }

    @JavascriptInterface
    public void addTextLabel(String label, int x, int y, int w, int h) {
        //TODO 
        TextView tv = new TextView(c.get());
        tv.setText(label);
        positionView(tv, x, y, w, h);

        mMainLayout.addView(tv);
    }

    public void input(String label, int x, int y, int w, int h, final String callbackfn) {

        // Adds an edit box. on out of focus calls the callback fn

    }

    @JavascriptInterface
    public void addAToggleButton(final String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {
        /*
        moldableFragment.get().addAToggleButton(label, new OnCheckedChangeListener() {

        	@Override
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		Log.d("addAToggleButton", "is checked: " + isChecked);
        		applicationWebView.runJavascript("window['" + callback + "']('"
        				+ isChecked + "')");
        	}
        });
        */

        //TODO: Add a toggle button and set the initial state as initstate, if the button state changes, call the callbackfn
        // 
    }

    public void checkbox(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        // Adds a checkbox and set the initial state as initstate. if the button state changes, call the callbackfn

    }

    public void radiobutton(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        //Create and add a radiobutton

    }

    public void image(String label, int x, int y, int w, int h, String location) {

        // Create and position the image view
        ImageView iv = new ImageButton(c.get());
        iv.setContentDescription(label);
        positionView(iv, x, y, w, h);

        //Add the view
        mMainLayout.addView(iv);

    }

    public void imagebutton(String label, int x, int y, int w, int h, String location, final String callbackfn) {

        // Create and position the image button
        ImageButton ib = new ImageButton(c.get());
        ib.setContentDescription(label);
        positionView(ib, x, y, w, h);

        //TODO: Set the image file based on location

        //Set on click behavior
        ib.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(ib);

    }

    public void background(String label, int x, int y, int w, int h, boolean colimg, String color_location) {
        // Sets the background image or color. colimg flag indicates if its color or an image
    }

    //	PlotView plotView;
    //	Plot plot;

    @JavascriptInterface
    public void addPlot(final String callback) {
        //		plotView = moldableFragment.get().addPlot();
        //		plot = plotView.new Plot(Color.RED);
        //		plotView.addPlot(plot);
    }

    @JavascriptInterface
    public void addPlotValue(float value) {
        //		plotView.setValue(plot, value);
    }

    @JavascriptInterface
    public void showToast(final String text, final String duration) {
        if (duration.equalsIgnoreCase("long")) {//HSHIEH: Please use the correct string for duration. In my opinion it should either be "LONG" or "LENGTH_LONG"
            Toast.makeText(c.get(), text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(c.get(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void startTrackingTouches(String b) {
    }
}