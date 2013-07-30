package com.makewithmoto.apprunner.api;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makewithmoto.apidoc.APIAnnotation;

public class JUI extends JInterface {

    FrameLayout mMainLayout;
    Boolean isMainLayoutSetup = false;

    public JUI(Activity a) {
        super(a);
    }

    private void initializeLayout() {
        if (!isMainLayoutSetup) {
            ScrollView sv = new ScrollView(c.get());
            sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mMainLayout = new FrameLayout(c.get());
            mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            sv.addView(mMainLayout);

            c.get().setContentView(sv);
            isMainLayoutSetup = true;
        }
    }

    //This method helps us optimize our bitmap sizes so we Android doesn't implode on itself
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

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

    // @GOPI
    //API CHANGES!! Max and progress are now an integers. Also, SeekBar doesn't take a min value.
    //We'll add in the circular view as a nice to have later once all the other widgets are handled.
    @JavascriptInterface
    public void seekbar(int max, int progress, int x, int y, int w, int h, final String callbackfn) {

        initializeLayout();
        //Create the position the view
        SeekBar sb = new SeekBar(c.get());
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
                // @GOPI: Should we do something here? Any callback  
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //When the value changes, call the callback function.
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(sb);

    }

    @JavascriptInterface
    public void label(String label, int x, int y, int w, int h) {

        initializeLayout();
        //Create the TextView
        TextView tv = new TextView(c.get());
        tv.setText(label);
        positionView(tv, x, y, w, h);

        //Add the view
        mMainLayout.addView(tv);
    }
    
    @JavascriptInterface
    public void label(String label, int x, int y, int w, int h, int textSize) {

        initializeLayout();
        //Create the TextView
        TextView tv = new TextView(c.get());
        tv.setText(label);
        tv.setTextSize((float)textSize);
        positionView(tv, x, y, w, h);

        //Add the view
        mMainLayout.addView(tv);
    }

    public void input(String label, int x, int y, int w, int h, final String callbackfn) {

        initializeLayout();
        //Create view
        EditText et = new EditText(c.get());
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

    @JavascriptInterface
    public void toggleButton(final String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {
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

        initializeLayout();
        //Create the view
        ToggleButton tb = new ToggleButton(c.get());
        tb.setChecked(initstate);
        tb.setText(label);
        positionView(tb, x, y, w, h);

        //Add change listener
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(tb);
    }

    public void checkbox(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        initializeLayout();
        // Adds a checkbox and set the initial state as initstate. if the button state changes, call the callbackfn
        CheckBox cb = new CheckBox(c.get());
        cb.setChecked(initstate);
        cb.setText(label);
        positionView(cb, x, y, w, h);

        //Add the click callback
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //FIXME: The callback function needs to take a param for isChecked
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(cb);

    }

    public void radiobutton(String label, int x, int y, int w, int h, boolean initstate, final String callbackfn) {

        initializeLayout();
        //Create and position the radio button
        RadioButton rb = new RadioButton(c.get());
        rb.setChecked(initstate);
        rb.setText(label);
        positionView(rb, x, y, w, h);

        //Add the click callback
        rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //FIXME: The callback function needs to take a param for isChecked
                callback(callbackfn);
            }
        });

        //Add the view
        mMainLayout.addView(rb);

    }

    public void image(int x, int y, int w, int h, String imagePath) {

        initializeLayout();
        // Create and position the image view
        final ImageView iv = new ImageView(c.get());
        positionView(iv, x, y, w, h);

        //Add the image from file
        new SetImageTask(iv).execute(imagePath);

        //Add the view
        iv.setBackgroundColor(0x33b5e5);
        mMainLayout.addView(iv);

    }

    public void webimage(int x, int y, int w, int h, String address) {

        initializeLayout();
        // Create and position the image view
        final ImageView iv = new ImageView(c.get());
        positionView(iv, x, y, w, h);

        //Add image asynchronously
        new DownloadImageTask(iv).execute(address);

        //Add the view
        mMainLayout.addView(iv);

    }

    public void imagebutton(int x, int y, int w, int h, String imagePath, final String callbackfn) {

        initializeLayout();
        // Create and position the image button
        ImageButton ib = new ImageButton(c.get());
        positionView(ib, x, y, w, h);

        //Add image asynchronously
        new SetImageTask(ib).execute(imagePath);

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
    
    public void imagebutton(int x, int y, int w, int h, String imagePath, boolean hideBackground, final String callbackfn) {

        initializeLayout();
        // Create and position the image button
        ImageButton ib = new ImageButton(c.get());
        positionView(ib, x, y, w, h);
        
        //Hide the background if desired
        if (hideBackground){
            ib.setBackgroundResource(0);
        }

        //Add image asynchronously
        new SetImageTask(ib).execute(imagePath);

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
    
    public void setPadding(int left, int top, int right, int bottom){
        initializeLayout();
        mMainLayout.setPadding(left, top, right, bottom);
    }

    public void backgroundColor(int color) {
        initializeLayout();
        mMainLayout.setBackgroundColor(color);
    }

    public void backgroundColor(int red, int green, int blue) {
        initializeLayout();
        mMainLayout.setBackgroundColor(Color.rgb(red, green, blue));
    }

    public void backgroundImage(String imagePath) {
        initializeLayout();
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            //Get the bitmap
            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Convert the bitmap to BitmapDrawable
            Drawable d = new BitmapDrawable(c.get().getResources(), bmp);
            //Set the background
            mMainLayout.setBackground(d);
        }
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
    
    //We need to set the web image asynchronously 
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
    
    //We need to set the bitmap image asynchronously 
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
    
    
    
}