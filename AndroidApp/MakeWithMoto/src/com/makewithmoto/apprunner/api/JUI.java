package com.makewithmoto.apprunner.api;

import java.io.File;
import java.io.InputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
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
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makewithmoto.apidoc.APIAnnotation;
import com.makewithmoto.apprunner.AppRunnerActivity;
import com.makewithmoto.base.AppSettings;
import com.makewithmoto.base.BaseMainApp;
import com.makewithmoto.events.ProjectManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class JUI extends JInterface {

	final static int MAXVIEW = 20;
    FrameLayout mMainLayout;
    Boolean isMainLayoutSetup = false;
    int viewCount = 0;
    View viewArray[] = new View[MAXVIEW];

    public JUI(Activity a) {
        super(a);
    }

    private void initializeLayout() {
        if (!isMainLayoutSetup) {
            //We need to let the view scroll, so we're creating a scroll view
            ScrollView sv = new ScrollView(a.get());
            sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            //Create the main layout. This is where all the items actually go
            mMainLayout = new FrameLayout(a.get());
            mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            sv.addView(mMainLayout);

            //Set the content view
            a.get().setContentView(sv);
            isMainLayoutSetup = true;
        }
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

    /**
     * Adds a button to the view
     * @param label
     * @param x
     * @param y
     * @param w
     * @param h
     * @param callbackfn
     */
    @JavascriptInterface
    @APIAnnotation(description = "Creates a button ", example = "ui.button(\"button\"); ")
    public void button(String label, int x, int y, int w, int h, final String callbackfn) {
        initializeLayout();

        //Create the button
        Button b = new Button(a.get());
        b.setText(label);
        positionView(b, x, y, w, h);

        //Set on click behavior
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Callback should capture the checked state
                callback(callbackfn);
            }
        });

        //Add the view to the layout
        mMainLayout.addView(b);
    }

    /**
     * Adds a seekbar with a callback function
     * @param max
     * @param progress
     * @param x
     * @param y
     * @param w
     * @param h
     * @param callbackfn
     */
    //We'll add in the circular view as a nice to have later once all the other widgets are handled.
    @JavascriptInterface
    public void seekbar(int max, int progress, int x, int y, int w, int h, final String callbackfn) {

        initializeLayout();
        //Create the position the view
        SeekBar sb = new SeekBar(a.get());
        sb.setMax(max);
        sb.setProgress(progress);
        positionView(sb, x, y, w, h);

        //Add the change listener
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
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO Callback should capture the checked state
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(sb);

    }

    /**
     * Adds a TextView. Note that the user doesn't specify font size
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
        //Create the TextView
        TextView tv = new TextView(a.get());
        tv.setText(label);
        tv.setTextSize((float) textSize);
        positionView(tv, x, y, w, h);

        //Add the view
        mMainLayout.addView(tv);
        
        viewArray[viewCount] = tv;
        
        viewCount += 1;
        
        return (viewCount-1);
    }

    
    @JavascriptInterface
    public void labelSetText(int view, String text) {
    	TextView tv = (TextView)viewArray[view];
    	tv.setText(text);
    }
    
      
    
    /**
     * Adds an EditText view
     * @param label
     * @param x
     * @param y
     * @param w
     * @param h
     * @param callbackfn
     */
    public void input(String label, int x, int y, int w, int h, final String callbackfn) {

        initializeLayout();
        //Create view
        EditText et = new EditText(a.get());
        et.setHint(label);
        positionView(et, x, y, w, h);

        //On focus lost, we need to call the callback function
        et.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    callback(callbackfn);
                }
            }
        });

        //Add the view
        mMainLayout.addView(et);

    }

    /**
     * Adds a toggle button
     * @param label
     * @param x
     * @param y
     * @param w
     * @param h
     * @param initstate
     * @param callbackfn
     */
    @JavascriptInterface
    public void toggleButton(final String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {
        initializeLayout();
        //Create the view
        ToggleButton tb = new ToggleButton(a.get());
        tb.setChecked(initstate);
        tb.setText(label);
        positionView(tb, x, y, w, h);

        //Add change listener
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO Callback should capture the checked state
                callback(callbackfn, isChecked);
            }
        });

        //Add the view
        mMainLayout.addView(tb);
    }

    /**
     * Adds a checkbox
     * @param label
     * @param x
     * @param y
     * @param w
     * @param h
     * @param initstate
     * @param callbackfn
     */
    public void checkbox(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        initializeLayout();
        // Adds a checkbox and set the initial state as initstate. if the button state changes, call the callbackfn
        CheckBox cb = new CheckBox(a.get());
        cb.setChecked(initstate);
        cb.setText(label);
        positionView(cb, x, y, w, h);

        //Add the click callback
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO Callback should capture the checked state
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(cb);

    }

    /**
     * Adds a switch
     * @param x
     * @param y
     * @param w
     * @param h
     * @param initstate
     * @param callbackfn
     */
    public void toggleswitch(int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        initializeLayout();
        // Adds a switch. If the state changes, we'll call the callback function
        Switch s = new Switch(a.get());
        s.setChecked(initstate);
        positionView(s, x, y, w, h);

        //Add the click callback
        s.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO Callback should capture the checked state
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(s);

    }

    /**
     * Adds a radiobutton
     * @param label
     * @param x
     * @param y
     * @param w
     * @param h
     * @param initstate
     * @param callbackfn
     */
    public void radiobutton(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        initializeLayout();
        //Create and position the radio button
        RadioButton rb = new RadioButton(a.get());
        rb.setChecked(initstate);
        rb.setText(label);
        positionView(rb, x, y, w, h);

        //Add the click callback
        rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO Callback should capture the checked state
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(rb);

    }

    /**
     * Adds an imageview
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

        //Add the image from file
        new SetImageTask(iv).execute(((AppRunnerActivity) a.get()).getCurrentDir() + File.separator + imagePath);

        //Add the view
        iv.setBackgroundColor(0x33b5e5);
        mMainLayout.addView(iv);

    }

    /**
     * Adds an image from a URL
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

        //Add image asynchronously
        new DownloadImageTask(iv).execute(address);

        //Add the view
        mMainLayout.addView(iv);

    }

    /**
     * Adds an image button with the default background
     * @param x
     * @param y
     * @param w
     * @param h
     * @param imagePath
     * @param callbackfn
     */
    public void imagebutton(int x, int y, int w, int h, String imagePath, final String callbackfn) {
        imagebutton(x, y, w, h, imagePath, false, callbackfn);
    }

    /**
     * Adds an image with the option to hide the default background
     * @param x
     * @param y
     * @param w
     * @param h
     * @param imagePath
     * @param hideBackground
     * @param callbackfn
     */
    public void imagebutton(int x, int y, int w, int h, String imagePath, boolean hideBackground, final String callbackfn) {

        initializeLayout();
        // Create and position the image button
        ImageButton ib = new ImageButton(a.get());
        positionView(ib, x, y, w, h);

        //Hide the background if desired
        if (hideBackground) {
            ib.setBackgroundResource(0);
        }

        //Add image asynchronously
        new SetImageTask(ib).execute(((AppRunnerActivity) a.get()).getCurrentDir() + File.separator + imagePath);

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

    /**
     * Set padding on the entire view
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
     * @param color
     */
    public void backgroundColor(int color) {
        initializeLayout();
        mMainLayout.setBackgroundColor(color);
    }

    /**
     * The more common way to set background color, set bg color via RGB
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
     * @param imagePath
     */
    public void backgroundImage(String imagePath) {
        initializeLayout();
        //Add the bg image asynchronously
        new SetBgImageTask(mMainLayout).execute(((AppRunnerActivity) a.get()).getCurrentDir() + File.separator + imagePath);

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
        /*if (duration.equalsIgnoreCase("long")) {//HSHIEH: Please use the correct string for duration. In my opinion it should either be "LONG" or "LENGTH_LONG"
            Toast.makeText(c.get(), text, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(c.get(), text, Toast.LENGTH_SHORT).show();
        }*/
    }

    @JavascriptInterface
    public void startTrackingTouches(String b) {
    }

    /**
     * This class lets us download an image asynchronously without blocking the UI thread
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
                //Get the bitmap with appropriate options
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
     * @author ncbq76
     *
     */
    //We need to set the bitmap image asynchronously 
    private class SetBgImageTask extends AsyncTask<String, Void, Bitmap> {
        FrameLayout fl;

        public SetBgImageTask(FrameLayout fl) {
            this.fl = fl;
        }

        protected Bitmap doInBackground(String... paths) {
            String imagePath = paths[0];
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                //Get the bitmap with appropriate options
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