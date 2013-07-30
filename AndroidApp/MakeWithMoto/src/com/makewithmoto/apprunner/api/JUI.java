package com.makewithmoto.apprunner.api;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
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
            mMainLayout = new FrameLayout(c.get());
            mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            c.get().setContentView(mMainLayout);
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
    public void addTextLabel(String label, int x, int y, int w, int h) {

        //Create the TextView
        TextView tv = new TextView(c.get());
        tv.setText(label);
        positionView(tv, x, y, w, h);

        //Add the view
        mMainLayout.addView(tv);
    }

    public void input(String label, int x, int y, int w, int h, final String callbackfn) {

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

        // Create and position the image view
        final ImageView iv = new ImageView(c.get());
        positionView(iv, x, y, w, h);

        //Add the image from file
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            //Get the bitmap with appropriate options
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

            //Get the inSampleSize
            options.inSampleSize = calculateInSampleSize(options, iv.getWidth(), iv.getHeight());

            //Decode bitmap with just the inSampleSize
            options.inJustDecodeBounds = false;
            //Set the bitmap to the ImageView
            iv.setImageBitmap(bmp);
        }

        //Add the view
        iv.setBackgroundColor(0x33b5e5);
        mMainLayout.addView(iv);

    }

    public void webimage(int x, int y, int w, int h, String address) {

        // Create and position the image view
        final ImageView iv = new ImageView(c.get());
        positionView(iv, x, y, w, h);

        //Get image from url
        URL url;
        try {
            url = new URL(address);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            //Get the inSampleSize
            options.inSampleSize = calculateInSampleSize(options, iv.getWidth(), iv.getHeight());
            //Decode bitmap with just the inSampleSize
            options.inJustDecodeBounds = false;
            iv.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Add the view
        mMainLayout.addView(iv);

    }

    public void imagebutton(int x, int y, int w, int h, String imagePath, final String callbackfn) {

        // Create and position the image button
        ImageButton ib = new ImageButton(c.get());
        positionView(ib, x, y, w, h);

        //Add the image from file
        File imgFile = new File(imagePath);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);

        //Get the inSampleSize
        options.inSampleSize = calculateInSampleSize(options, ib.getWidth(), ib.getHeight());

        //Decode bitmap with just the inSampleSize
        options.inJustDecodeBounds = false;
        //Set the bitmap to the ImageView
        ib.setImageBitmap(bmp);

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

    public void backgroundColor(int color) {
        mMainLayout.setBackgroundColor(color);
    }

    public void backgroundColor(int red, int green, int blue) {
        mMainLayout.setBackgroundColor(Color.rgb(red, green, blue));
    }

    public void backgroundImage(String imagePath) {
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
}