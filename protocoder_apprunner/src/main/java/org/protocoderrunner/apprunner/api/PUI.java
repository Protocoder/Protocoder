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

package org.protocoderrunner.apprunner.api;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.graphics.Palette;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.animation.CycleInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.AppSettings;
import org.protocoderrunner.R;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.api.media.PCamera;
import org.protocoderrunner.apprunner.api.other.PAnimation;
import org.protocoderrunner.apprunner.api.other.PProcessing;
import org.protocoderrunner.apprunner.api.other.PosVector;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.apprunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.apprunner.api.widgets.PButton;
import org.protocoderrunner.apprunner.api.widgets.PCanvas;
import org.protocoderrunner.apprunner.api.widgets.PCard;
import org.protocoderrunner.apprunner.api.widgets.PCheckBox;
import org.protocoderrunner.apprunner.api.widgets.PEditText;
import org.protocoderrunner.apprunner.api.widgets.PGrid;
import org.protocoderrunner.apprunner.api.widgets.PImageButton;
import org.protocoderrunner.apprunner.api.widgets.PImageView;
import org.protocoderrunner.apprunner.api.widgets.PList;
import org.protocoderrunner.apprunner.api.widgets.PMap;
import org.protocoderrunner.apprunner.api.widgets.PNumberPicker;
import org.protocoderrunner.apprunner.api.widgets.PPadView;
import org.protocoderrunner.apprunner.api.widgets.PPlotView;
import org.protocoderrunner.apprunner.api.widgets.PPopupCustomFragment;
import org.protocoderrunner.apprunner.api.widgets.PProgressBar;
import org.protocoderrunner.apprunner.api.widgets.PRadioButton;
import org.protocoderrunner.apprunner.api.widgets.PSlider;
import org.protocoderrunner.apprunner.api.widgets.PSpinner;
import org.protocoderrunner.apprunner.api.widgets.PSwitch;
import org.protocoderrunner.apprunner.api.widgets.PTextView;
import org.protocoderrunner.apprunner.api.widgets.PToggleButton;
import org.protocoderrunner.apprunner.api.widgets.PUIGeneric;
import org.protocoderrunner.apprunner.api.widgets.PVideo;
import org.protocoderrunner.apprunner.api.widgets.PWebView;
import org.protocoderrunner.apprunner.api.widgets.PWindow;
import org.protocoderrunner.apprunner.api.widgets.WidgetHelper;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.utils.MLog;
import org.protocoderrunner.views.TouchAreaView;

import java.io.File;

import processing.core.PApplet;

import static android.view.ScaleGestureDetector.OnScaleGestureListener;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PUI extends PUIGeneric {

    String TAG = "PUI";

    //key pressed callback
    private OnKeyDownCB mOnKeyDownfn;
    private OnKeyUpCB mOnKeyUpfn;

    private boolean keyInit = false;


    public PUI(Context a) {
        super(a);
        WhatIsRunning.getInstance().add(this);
    }

    @ProtoMethod(description = "Gets the main layout, usually absolute", example = "")
    @ProtoMethodParam(params = {""})
    public View mainLayout() {
        return getFragment().mainLayout;
    }

    @ProtoMethod(description = "Gets the parent layout where the mainLayout resides", example = "")
    @ProtoMethodParam(params = {""})
    public View parentLayout() {
        View v = (View) (getFragment()).mainLayout.getParent();
        return v;
    }

    @ProtoMethod(description = "Gets the activity layout, including the action bar", example = "")
    @ProtoMethodParam(params = {""})
    public View appLayout() {
        View v = (View) (getFragment()).mainLayout.getParent().getParent().getParent().getParent().getParent();
        return v;
    }

    //TODO doesnt work properly

    @ProtoMethod(description = "Shows/Hide the home bar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void showHomeBar(boolean b) {
        getActivity().showHomeBar(b);
    }


    @ProtoMethod(description = "Sets the fullscreen / immersive / dimBars mode", example = "")
    @ProtoMethodParam(params = {"mode={fullscreen, immersive, lightsOut}"})
    public void screenMode(String mode) {
        if (mode.equals("fullscreen")) {
            getActivity().setFullScreen();
            isFullscreenMode = true;
        } else if (mode.equals("lightsOut")) {
            getActivity().lightsOutMode();
        } else if (mode.equals("immersive")) {
            // isImmersiveMode = true;
            getActivity().setImmersive();
            updateScreenSizes();
            //do nothing
        } else {

        }

    }


    @ProtoMethod(description = "Forces landscape mode in the app", example = "")
    @ProtoMethodParam(params = {"mode={'landscape', 'portrait', 'other'"})
    public void screenOrientation(String mode) {
        if (mode.equals("landscape")) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (mode.equals("portrait")) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }


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

//
//	
//    @APIMethod(description = "Sets the main layout padding", example = "")
//    @APIParam(params = { "left", "top", "right", "bottom" })
//	public void padding(int left, int top, int right, int bottom) {
//		initializeLayout();
//		uiAbsoluteLayout.setPadding(left, top, right, bottom);
//	}


    @ProtoMethod(description = "Resize a view to a given width and height. If a parameter is -1 then that dimension is not changed", example = "")
    @ProtoMethodParam(params = {"View", "width", "height"})
    public void resize(final View v, int h, int w) {
        boolean animated = false;

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
            // v.setLayoutParams(v.getLayoutParams());

            ValueAnimator anim = ValueAnimator.ofInt(initHeight, h);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    v.getLayoutParams().height = val;
                    v.setLayoutParams(v.getLayoutParams());

                }
            });
            anim.setDuration(200);
            anim.start();
        }
    }


    @ProtoMethod(description = "Show/Hide a view", example = "")
    @ProtoMethodParam(params = {"View", "boolean"})
    public void show(View v, boolean b) {
        if (b) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }


    @ProtoMethod(description = "Moves a view to a position using a normal transition", example = "")
    @ProtoMethodParam(params = {"View", "x", "y"})
    public void move(View v, float x, float y) {
        v.animate().x(x).setDuration(AppSettings.animGeneralSpeed);
        v.animate().y(y).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Moves a view by given units from the current position using a normal transition", example = "")
    @ProtoMethodParam(params = {"View", "x", "y"})
    public void moveBy(View v, float x, float y) {
        v.animate().xBy(x).setDuration(AppSettings.animGeneralSpeed);
        v.animate().yBy(y).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Animate a view", example = "")
    @ProtoMethodParam(params = {"View", "x", "y"})
    public PAnimation anim(View v) {
        return new PAnimation(v);
    }

    //@TargetApi(L)

    @ProtoMethodParam(params = {"View"})
    public void clipAndShadow(View v, int type, int r) {
        AndroidUtils.setViewGenericShadow(v, type, 0, 0, v.getWidth(), v.getHeight(), r);
    }

    //@TargetApi(L)

    @ProtoMethodParam(params = {"View"})
    public void clipAndShadow(View v, int type, int x, int y, int w, int h, int r) {
        AndroidUtils.setViewGenericShadow(v, type, x, y, w, h, r);
    }

    //@TargetApi(L)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    @ProtoMethodParam(params = {"View"})
    public void reveal(final View v) {
        // previously invisible view

        // get the center for the clipping circle
        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = v.getWidth();

        // create and start the animator for this view
        // (the start radius is zero)
        Animator anim = (Animator) ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);

        anim.setDuration(1000);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                v.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    @ProtoMethodParam(params = {"View"})
    public void unreveal(final View v) {

        // get the center for the clipping circle
        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = v.getWidth();

        // create the animation (the final radius is zero)
        Animator anim = (Animator) ViewAnimationUtils.createCircularReveal(v, cx, cy, initialRadius, 0);
        anim.setDuration(1000);


        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }


    @ProtoMethodParam(params = {"View", "x", "y", "w", "h"})
    public void clipCircle(View v, final int x, final int y, final int w, final int h) {
        Outline outline = new Outline();
        outline.setOval(x, y, w, h);
        v.setClipToOutline(true);


        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                outline.setOval(x, y, w, h);
            }
        };

        v.setOutlineProvider(viewOutlineProvider);
    }


    public interface DraggableCallback {
        void event(int x, int y);
    }

    // http://stackoverflow.com/questions/16557076/how-to-smoothly-move-mContext-image-view-with-users-finger-on-android-emulator
    public void draggable(View v, final DraggableCallback callback) {
        v.setOnTouchListener(new OnTouchListener() {
            PointF downPT = new PointF(); // Record Mouse Position When Pressed
            // Down
            PointF startPT = new PointF(); // Record Start Position of 'img'

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - downPT.x, event.getY() - downPT.y);
                        int posX = (int) (startPT.x + mv.x);
                        int posY = (int) (startPT.y + mv.y);
                        v.setX(posX);
                        v.setY(posY);
                        startPT = new PointF(v.getX(), v.getY());

                        if (callback != null) callback.event(posX, posY);

                        break;
                    case MotionEvent.ACTION_DOWN:
                        downPT.x = event.getX();
                        downPT.y = event.getY();
                        startPT = new PointF(v.getX(), v.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        // Nothing have to do
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    public void movable(View viewHandler, View viewContainer, WidgetHelper.MoveCallback callback) {
        WidgetHelper.setMovable(viewHandler, viewContainer, callback);
    }

    public void removeMovable(View viewHandler) {
        WidgetHelper.removeMovable(viewHandler);
    }

    interface TouchCallback {
        void event(boolean touch, float rawX, float rawY);
    }

    public void onTouch(View view, final TouchCallback callback) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        callback.event(true, event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        callback.event(true, event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        callback.event(false, event.getX(), event.getY());
                        break;
                }
                return true;
            }
        });
    }

    @ProtoMethod(description = "Makes the current view draggable or cancel the drag depending on the boolean state", example = "")
    @ProtoMethodParam(params = {"View", "boolean"})
    public void removeDraggable(View v) {
        v.setOnTouchListener(null);
    }


    @ProtoMethod(description = "Change the animation speed for the default animations", example = "")
    @ProtoMethodParam(params = {"View"})
    public void animSpeed(int speed) {
        AppSettings.animGeneralSpeed = speed;
    }


    @ProtoMethod(description = "Makes the view jump", example = "")
    @ProtoMethodParam(params = {"View"})
    public void jump(View v) {

        ValueAnimator w = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.9f, 1.2f, 1f);
        w.setDuration(AppSettings.animGeneralSpeed);

        ValueAnimator h = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.9f, 1.2f, 1f);
        h.setDuration(AppSettings.animGeneralSpeed);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(w).with(h);
        animatorSet.start();
    }


    @ProtoMethod(description = "Makes the view blink", example = "")
    @ProtoMethodParam(params = {"View", "num"})
    public void blink(View v, int num) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
        anim.setDuration(AppSettings.animGeneralSpeed);
        anim.setInterpolator(new CycleInterpolator(1));
        anim.setRepeatCount(num);
        anim.start();
    }


    @ProtoMethod(description = "Makes the view blink", example = "")
    @ProtoMethodParam(params = {"View", "speed", "num"})
    public void blink(View v, int speed, int num) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
        anim.setDuration(speed);
        //anim.setInterpolator(new CycleInterpolator(num));
        anim.setRepeatCount(num);
        anim.start();
    }

    interface AnimFinishCB {
        public void event();
    }

    @ProtoMethod(description = "Rotates the view in the x axis", example = "")
    @ProtoMethodParam(params = {"View", "x"})
    public ViewPropertyAnimator rotate(View v, float x, final AnimFinishCB cb) {
        return v.animate().rotation(x).setDuration(AppSettings.animGeneralSpeed).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cb.event();
            }
        });
    }


    @ProtoMethod(description = "Rotates the view in the x axis by given degrees", example = "")
    @ProtoMethodParam(params = {"View", "x"})
    public void rotateBy(View v, float x) {
        v.animate().rotationBy(x).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Rotates the view in the x axis in a given time", example = "")
    @ProtoMethodParam(params = {"View", "time", "x"})
    public void rotate(View v, int time, float x) {
        v.animate().rotation(x).setDuration(time);
    }


    @ProtoMethod(description = "Rotates the view in the x, y, z axis", example = "")
    @ProtoMethodParam(params = {"View", "x", "y", "z"})
    public void rotate(View v, float x, float y, float z) {
        v.animate().rotation(x).setDuration(AppSettings.animGeneralSpeed);
        // looks weird but it works more consistent
        v.animate().rotationX(y).setDuration(AppSettings.animGeneralSpeed);
        v.animate().rotationY(z).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Rotates the view in the x, y, z axis by given degrees", example = "")
    @ProtoMethodParam(params = {"View", "x", "y", "z"})
    public void rotateBy(View v, float x, float y, float z) {
        v.animate().rotationBy(x).setDuration(AppSettings.animGeneralSpeed);
        // looks weird but it works more consistent
        v.animate().rotationXBy(y).setDuration(AppSettings.animGeneralSpeed);
        v.animate().rotationYBy(z).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Changes the alpha of a view", example = "")
    @ProtoMethodParam(params = {"View", "float={0,1}"})
    public void alpha(View v, float alpha) {
        v.animate().alpha(alpha).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Scales a view with animation to a given size", example = "")
    @ProtoMethodParam(params = {"View", "x", "y"})
    public void scale(View v, float x, float y) {
        v.animate().scaleX(x).setDuration(AppSettings.animGeneralSpeed);
        v.animate().scaleY(y).setDuration(AppSettings.animGeneralSpeed);
    }


    @ProtoMethod(description = "Scales a view with animation by a given size", example = "")
    @ProtoMethodParam(params = {"View", "x", "y"})
    public void scaleBy(View v, float x, float y) {
        v.animate().scaleXBy(x).setDuration(AppSettings.animGeneralSpeed);
        v.animate().scaleYBy(y).setDuration(AppSettings.animGeneralSpeed);
    }

    //TODO doesnt work always

    @ProtoMethod(description = "Scales and moves a view to a given position and size", example = "")
    @ProtoMethodParam(params = {"View", "x", "y", "w", "h"})
    public void amplify(View v, float x, float y, float w, float h) {
        this.move(v, x, y);
        float pX = v.getPivotX();
        float pY = v.getPivotY();
        v.setPivotX(0);
        v.setPivotY(0);
        float sX = w / v.getWidth();
        float sY = h / v.getHeight();
        this.scale(v, sX, sY);
        v.setPivotX(pX);
        v.setPivotY(pY);
    }

    public void position(View v, float x, float y) {
        v.setX(x);
        v.setY(y);
    }

    public void position(View v, PosVector vector) {
        v.setX(vector.x);
        v.setY(vector.y);
    }

    public PosVector position(View v) {
        return new PosVector(v.getX(), v.getY());
    }


    public Palette getPalette(Bitmap bmp) {
        Palette palette = Palette.generate(bmp);

        return palette;
    }

    class GestureDetectorReturn {
        public String type;
        public JSONObject data;

    }

    // --------- addGestureDetector ---------//
    public interface addGestureDetectorCB {
        void event(GestureDetectorReturn g);
    }


    @ProtoMethod(description = "Starts a gesture detector over a view", example = "")
    @ProtoMethodParam(params = {"View", "function(data)"})
    //http://stackoverflow.com/questions/6599329/can-one-ongesturelistener-object-deal-with-two-gesturedetector-objects
    public void gestureDetector(View v, final addGestureDetectorCB cb) {
        final GestureDetectorReturn g = new GestureDetectorReturn();

        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                g.type = "up";
                cb.event(g);
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                g.type = "showpress";
                cb.event(g);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                g.type = "scroll";
                g.data = new JSONObject();
                try {
                    g.data.put("distanceX", distanceX);
                    g.data.put("distanceY", distanceY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cb.event(g);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                g.type = "longpress";
                cb.event(g);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                g.type = "fling";
                g.data = new JSONObject();
                try {
                    g.data.put("velocityX", velocityX);
                    g.data.put("velocityY", velocityY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cb.event(g);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                g.type = "down";
                cb.event(g);
                return true;
            }


        });


        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), new OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                g.type = "scale";
                cb.event(g);
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                g.type = "scaleBegin";
                cb.event(g);
                return false;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                g.type = "scaleEnd";
                cb.event(g);
            }
        });


        v.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                if (scaleGestureDetector.isInProgress()) return true;
                gestureDetector.onTouchEvent(event);

                return true;
            }
        });
    }

    @ProtoMethod(description = "Changes the background color using grayscale", example = "")
    @ProtoMethodParam(params = {"gray"})
    public void backgroundColor(int gray) {
        initializeLayout();
        holderLayout.setBackgroundColor(Color.rgb(gray, gray, gray));
    }

    @ProtoMethod(description = "Changes the background color using RGB", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public void backgroundColor(int red, int green, int blue) {
        initializeLayout();
        holderLayout.setBackgroundColor(Color.rgb(red, green, blue));
    }


    @ProtoMethod(description = "Changes the background color using Hex", example = "")
    @ProtoMethodParam(params = {"hex"})
    public void backgroundColor(String c) {
        initializeLayout();
        holderLayout.setBackgroundColor(Color.parseColor(c));
    }


    @ProtoMethod(description = "Sets an image as background", example = "")
	@ProtoMethodParam(params = { "imageName" })
	public void backgroundImage(String imagePath) {
		initializeLayout();

        // Add the bg image asynchronously from the sdcard
        new SetBgImageTask(bgImageView, false).execute(
                AppRunnerSettings.get().project.getStoragePath()
                        + File.separator
                        + imagePath);
	}

    @ProtoMethod(description = "Sets an image as tiled background", example = "")
	@ProtoMethodParam(params = { "imageName" })
	public void backgroundImageTile(String imagePath) {
		initializeLayout();

        // Add the bg image asynchronously
        new SetBgImageTask(bgImageView, true).execute(
                AppRunnerSettings.get().project.getStoragePath()
                        + File.separator
                        + imagePath);
	}


    @ProtoMethod(description = "Creates an absolute layout ", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PAbsoluteLayout addAbsoluteLayout(int x, int y, int w, int h) {
        PAbsoluteLayout al = newAbsoluteLayout();
        addViewAbsolute(al, x, y, w, h);

        return al;
    }


    @ProtoMethod(description = "Creates a card ", example = "")
    @ProtoMethodParam(params = {"label"})
    public PCard addCard() {
        PCard c = newCard();
        addViewLinear(c);

        return c;
    }


    @ProtoMethod(description = "Adds a card that can hold views", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PCard addCard(String label, int x, int y, int w, int h) {
        PCard c = newCard();
        c.setTitle(label);

        addViewAbsolute(c, x, y, w, h);
        return c;
    }


    @ProtoMethod(description = "Creates a window that can hold views ", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PWindow addWindow(String label, int x, int y, int w, int h) {
        PWindow wn = newWindow();
        wn.setTitle(label);

        addViewAbsolute(wn, x, y, w, h);
        return wn;
    }


    @ProtoMethod(description = "Adds a button", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h", "function()"})
    public PButton addButton(String label, int x, int y, int w, int h) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, w, h);
        return b;
    }


    @ProtoMethod(description = "Adds a button", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "function()"})
    public PButton addButton(String label, int x, int y) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, -1, -1);
        return b;
    }


    @ProtoMethod(description = "Adds an invisible touch area", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "bShowArea", "function(touching, x, y)"})
    public TouchAreaView addTouchArea(int x, int y, int w, int h, boolean showArea,
                                      final addGenericTouchAreaCB callbackfn) {
        TouchAreaView taV = newTouchArea(showArea, callbackfn);
        addViewAbsolute(taV, x, y, w, h);

        return taV;
    }


    @ProtoMethod(description = "Adds a touch area that allows multitouch", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "function(touching, x, y)"})
    public PPadView addXYPad(int x, int y, int w, int h, final addPadCB callbackfn) {
        PPadView taV = newTouchPad(callbackfn);
        addViewAbsolute(taV, x, y, w, h);

        return taV;
    }


    //TODO removed old one this is a place holder
    //
    //@APIMethod(description = "Knob", example = "")
    //@APIParam(params = { "function(progress)" })
    public View addKnob(final addGenericKnobCB callbackfn) {
        //PKnob pKnob = newKnob(mContext);
        //addViewAbsolute(pKnob, x, y, w, h);

        return null;
    }


    @ProtoMethod(description = "Adds a slider", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "max", "progress", "function(progress)"})
    public PSlider addSlider(int x, int y, int w, int h, int min, int max) {
        PSlider sb = newSlider(min, max);
        addViewAbsolute(sb, x, y, w, -1);
        return sb;
    }


    @ProtoMethod(description = "Adds a list of items passed as array", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "arrayStrings", "function(data)"})
    public PSpinner addChoiceBox(int x, int y, int w, int h, final String[] array, final addGenericSpinnerCB callbackfn) {
        PSpinner spinner = newChoiceBox(array, callbackfn);
        addViewAbsolute(spinner, x, y, w, h);

        return spinner;
    }


    @ProtoMethod(description = "Add a progress bar", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "max"})
    public PProgressBar addProgressBar(int x, int y, int w, int h, int max) {
        PProgressBar pb = newProgress(max);
        addViewAbsolute(pb, x, y, w, -1);
        return pb;
    }


    @ProtoMethod(description = "Add a text box defined by its position and size", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PTextView addText(int x, int y, int w, int h) {
        PTextView tv = newText("");
        addViewAbsolute(tv, x, y, w, h);

        return tv;
    }


    @ProtoMethod(description = "Add a text box defined by its position and size", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PTextView addText(String label, int x, int y, int w, int h) {
        PTextView tv = newText(label);
        addViewAbsolute(tv, x, y, w, h);

        return tv;
    }


    @ProtoMethod(description = "Adds a text box defined only by its position", example = "")
    @ProtoMethodParam(params = {"label", "x", "y"})
    public PTextView addText(String label, int x, int y) {
        PTextView tv = newText(label);
        addViewAbsolute(tv, x, y, -1, -1);

        return tv;
    }


    @ProtoMethod(description = "Adds a text box defined only by its position", example = "")
    @ProtoMethodParam(params = {"label", "x", "y"})
    public PTextView addText(int x, int y) {
        PTextView tv = newText("");
        addViewAbsolute(tv, x, y, -1, -1);

        return tv;
    }


    @ProtoMethod(description = "Adds an input box", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h", "function()"})
    public PEditText addInput(String label, int x, int y, int w, int h) {
        PEditText et = newInput(label);
        addViewAbsolute(et, x, y, w, h);

        return et;
    }


    @ProtoMethod(description = "Adds an input box", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h"})
    public PEditText addInput(int x, int y, int w, int h) {
        PEditText et = newInput("");
        addViewAbsolute(et, x, y, w, h);

        return et;
    }


    //number picker

    @ProtoMethod(description = "Adds a number picker", example = "")
    @ProtoMethodParam(params = {"from", "to", "x", "y", "w", "h"})
    public void addNumberPicker(int from, int to, int x, int y, int w, int h, NewGenericNumberPickerCB callback) {
        PNumberPicker np = newNumberPicker(from, to, callback);
        addViewAbsolute(np, x, y, w, h);
    }


    @ProtoMethod(description = "Adds a toggle", example = "")
    @ProtoMethodParam(params = {"text", "x", "y", "w", "h", "checked", "function(checked)"})
    public PToggleButton addToggle(final String label, int x, int y, int w, int h, boolean initstate) {

        PToggleButton tb = newToggle(label, initstate);
        addViewAbsolute(tb, x, y, w, h);

        return tb;
    }


    @ProtoMethod(description = "Adds a checkbox", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h", "checked", "function(checked)"})
    public PCheckBox addCheckbox(String label, int x, int y, int w, int h, boolean initstate) {
        PCheckBox cb = newCheckbox(label, initstate);
        addViewAbsolute(cb, x, y, w, h);

        return cb;
    }


    @ProtoMethod(description = "Adds a checkbox", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "checked", "function(checked)"})
    public PSwitch addSwitch(int x, int y, int w, int h, boolean initstate) {

        PSwitch s = newSwitch(initstate);
        addViewAbsolute(s, x, y, w, h);

        return s;
    }


    @ProtoMethod(description = "Adds a radio button", example = "")
    @ProtoMethodParam(params = {"label", "x", "y", "w", "h", "checked", "function(checked)"})
    public PRadioButton addRadioButton(String label, int x, int y, int w, int h, boolean initstate,
                                       final addGenericRadioButtonCB callbackfn) {

        PRadioButton rb = newRadioButton(label, initstate, callbackfn);
        addViewAbsolute(rb, x, y, w, h);

        return rb;
    }


    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "imagePath"})
    public PImageView addImage(String imagePath, int x, int y, int w, int h) {

        final PImageView iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, w, h);

        return iv;
    }


    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "imagePath"})
    public PImageView addImage(int x, int y, int w, int h) {

        final PImageView iv = newImage(null);
        addViewAbsolute(iv, x, y, w, h);

        return iv;
    }


    @ProtoMethod(description = "Adds an image", example = "")
    @ProtoMethodParam(params = {"x", "y", "imagePath"})
    public PImageView addImage(String imagePath, int x, int y) {

        final PImageView iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, -1, -1);

        return iv;
    }


    @ProtoMethod(description = "Adds a plot with a range", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "min", "max"})
    public PPlotView addPlot(int x, int y, int w, int h, int min, int max) {
        PPlotView pPlotView = newPlot(min, max);
        addViewAbsolute(pPlotView, x, y, w, h);

        return pPlotView;
    }


    @ProtoMethod(description = "Adds a checkbox", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "imageName", "function()"})
    public PImageButton addImageButton(int x, int y, int w, int h, String imagePath) {
        return newImageButton(x, y, w, h, imagePath, "", false);
    }

    @ProtoMethodParam(params = {"x", "y", "w", "h", "imageNameNotPressed", "imageNamePressed", "function()"})
    public PImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed) {
        return newImageButton(x, y, w, h, imgNotPressed, imgPressed, false);
    }


    @ProtoMethod(description = "Adds a grid of elements given an array of strings", example = "")
    @ProtoMethodParam(params = {"type", "arrayStrings", "x", "y", "w", "h", "function(data)"})
    public PGrid addGridOf(String type, NativeArray array, int cols, int x, int y, int w, int h, final addGridOfCB callbackfn) {
        PGrid grid = newGridOf(type, array, cols, callbackfn);
        addViewAbsolute(grid, x, y, w, h);

        return grid;
    }


    @ProtoMethod(description = "Adds a canvas view", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PCanvas addCanvas(int x, int y, int w, int h, boolean autoDraw) {

        PCanvas canvasView = newCanvas(w, h);
        canvasView.autoDraw(autoDraw);
        addViewAbsolute(canvasView, x, y, w, h);

        return canvasView;
    }


    @ProtoMethod(description = "Adds a canvas view", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PCanvas addCanvas(int x, int y, int w, int h) {
        return addCanvas(x, y, w, h, false);
    }

    public PList addList(int x, int y, int w, int h) {
        PList plist = new PList(getContext());
        return plist;

    }

    //reenable again

    @ProtoMethod(description = "Adds mContext processing view", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h", "mode={'p2d', 'p3d'"})
    public PApplet addProcessing(int x, int y, int w, int h, String mode) {

        initializeLayout();

        // Create the main layout. This is where all the items actually go
        FrameLayout fl = new FrameLayout(getContext());
        fl.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        fl.setId(200 + (int) (200 * Math.random()));
        fl.setBackgroundResource(R.color.transparent);

        // Add the view
        addViewAbsolute(fl, x, y, w, h);

        PProcessing p = new PProcessing();

        Bundle bundle = new Bundle();
        bundle.putString("mode", mode);
        p.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), p, String.valueOf(fl.getId()));

        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // ft.setCustomAnimations(android.R.anim.fade_in,
        // android.R.anim.fade_out);
        ft.addToBackStack(null);
        ft.commit();

        return p;

    }

//TODO add again the editor
//	public EditorFragment addEditor(int x, int y, int w, int h) {
//		initializeLayout();
//
//		// Create the main layout. This is where all the items actually go
//		FrameLayout fl = new FrameLayout(mContext);
//		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		fl.setId(125);
//		fl.setBackgroundResource(R.color.transparent);
//
//		// Add the view
//		addViewAbsolute(fl, x, y, w, h);
//
//		EditorFragment ef = new EditorFragment();
//
//		FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
//		ft.add(fl.getId(), ef, String.valueOf(fl.getId()));
//
//		// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//		// ft.setCustomAnimations(android.R.anim.fade_in,
//		// android.R.anim.fade_out);
//		ft.addToBackStack(null);
//		ft.commit();
//
//		return ef;
//	}


    @ProtoMethod(description = "Adds a webview", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PWebView addWebView(int x, int y, int w, int h) {
        PWebView webView = newWebview();
        addViewAbsolute(webView, x, y, w, h);

        return webView;

    }

    @ProtoMethod(description = "Add camera view", example = "")
    @ProtoMethodParam(params = {"type", "x", "y", "w", "h"})
    public PCamera addCameraView(String type, int x, int y, int w, int h) {

        PCamera pCamera = newCameraView(type);
        addViewAbsolute(pCamera, x, y, w, h);

        return pCamera;
    }

    @ProtoMethod(description = "Adds a video view and starts playing the fileName", example = "")
    @ProtoMethodParam(params = {"fileName", "x", "y", "w", "h"})
    public PVideo addVideo(final String videoFile, int x, int y, int w, int h) {
        PVideo video = newVideo(videoFile);
        addViewAbsolute(video, x, y, w, h);

        return video;
    }


    @ProtoMethod(description = "Add a openstreetmap", example = "")
    @ProtoMethodParam(params = {"x", "y", "w", "h"})
    public PMap addMap(int x, int y, int w, int h) {
        PMap mapView = newMap();

        addViewAbsolute(mapView, x, y, w, h);
        return mapView;
    }


    // --------- yesno dialog ---------//
    public interface popupCB {
        void event(boolean b);
    }


    @ProtoMethod(description = "Shows a popup with a given text", example = "")
    @ProtoMethodParam(params = {"title", "message", "okButton", "cancelButton", "function(boolean)"})
    public void popupInfo(String title, String msg, String ok, String cancel, final popupCB callbackfn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(msg);

        if (!ok.isEmpty()) {
            // Set up the buttons
            builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callbackfn != null) {
                        callbackfn.event(true);
                    }
                }
            });
        }

        if (!cancel.isEmpty()) {
            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (callbackfn != null) {
                        callbackfn.event(false);
                    }
                }
            });
        }

        if (!(getActivity()).isFinishing()) {
            builder.show();
        }
    }


    // --------- inputDialog ---------//
    interface inputDialogCB {
        void event(String text);
    }


    @ProtoMethod(description = "Shows an input dialog", example = "")
    @ProtoMethodParam(params = {"title", "function(text)"})
    public void popupInput(String title, final inputDialogCB callbackfn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        final EditText input = new EditText(getContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                callbackfn.event(text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    // --------- choiceDialog ---------//
    public interface choiceDialogCB {
        void event(String string);
    }


    @ProtoMethod(description = "Shows a choice dialog using a given array of strings", example = "")
    @ProtoMethodParam(params = {"title", "arrayStrings", "function(text)"})
    public void popupChoice(String title, final String[] choices, final choiceDialogCB callbackfn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        // Set up the buttons
        builder.setItems(choices, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callbackfn.event(choices[which]);

            }
        });

        builder.show();
    }

    //    TODO it works but need mContext way to wait for it to be shown
    public PPopupCustomFragment popupCustom() {

        PPopupCustomFragment pPopupCustomFragment = new PPopupCustomFragment();

        android.app.FragmentManager fm = getActivity().getFragmentManager();
        pPopupCustomFragment.show(fm, "popUpCustom");

        return pPopupCustomFragment;
    }

    @ProtoMethod(description = "Takes a screenshot of the whole app and stores it to a given file name", example = "")
    @ProtoMethodParam(params = {"imageName"})
    public void takeScreenshot(String imagePath) {
        AndroidUtils.takeScreenshot(AppRunnerSettings.get().project.getStoragePath(), imagePath, uiAbsoluteLayout);
    }

    @ProtoMethod(description = "Takes a screenshot of a view and save it to an image", example = "")
    @ProtoMethodParam(params = {"view", "imageName"})
    public void takeViewScreenshot(View v, String imagePath) {
        AndroidUtils.takeScreenshotView(AppRunnerSettings.get().project.getStoragePath(), imagePath, v);
    }

    @ProtoMethod(description = "Takes a screenshot of a view", example = "")
    @ProtoMethodParam(params = {"view"})
    public Bitmap takeViewScreenshot(View v) {
        return AndroidUtils.takeScreenshotView("", "", v);
    }

    // it only works with absolute layout and only when
    // mContext layout is been used
    @ProtoMethod(description = "Show the virtual keyboard", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void showVirtualKeys(boolean show) {
        initializeLayout();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);

        if (show) {
            imm.showSoftInput(getActivity().getCurrentFocus(), InputMethodManager.SHOW_FORCED);
            uiAbsoluteLayout.setFocusable(true);
            uiAbsoluteLayout.setFocusableInTouchMode(true);

        } else {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    // --------- onKeyDown ---------//
    interface OnKeyDownCB {
        void event(int eventType);
    }

    public void keyInit() {
        keyInit = true;
        (getActivity()).addOnKeyListener(new onKeyListener() {

            @Override
            public void onKeyUp(int keyCode) {
                if (mOnKeyUpfn != null) {
                    mOnKeyUpfn.event(keyCode);
                }
            }

            @Override
            public void onKeyDown(int keyCode) {
                if (mOnKeyDownfn != null) {
                    mOnKeyDownfn.event(keyCode);
                }
            }
        });
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(keyNumber)"})
    public void onKeyDown(final OnKeyDownCB fn) {
        if (!keyInit) {
            keyInit();
        }

        mOnKeyDownfn = fn;
    }

    // --------- onKeyUp ---------//
    interface OnKeyUpCB {
        void event(int eventType);
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(keyNumber)"})
    public void onKeyUp(final OnKeyUpCB fn) {
        if (!keyInit) {
            keyInit();
        }

        mOnKeyUpfn = fn;
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void enableVolumeKeys(boolean b) {
        getActivity().keyVolumeEnabled = b;
    }

    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void enableBackKey(boolean b) {
        getActivity().keyBackEnabled = b;
    }

    public interface onKeyListener {
        public void onKeyDown(int keyCode);

        public void onKeyUp(int keyCode);
    }


    public void stop() {

    }

}