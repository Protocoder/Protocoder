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

package org.protocoderrunner.apprunner.api;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
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
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerActivity;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PCamera;
import org.protocoderrunner.apprunner.api.other.PProcessing;
import org.protocoderrunner.apprunner.api.other.PVideo;
import org.protocoderrunner.apprunner.api.widgets.PAbsoluteLayout;
import org.protocoderrunner.apprunner.api.widgets.PButton;
import org.protocoderrunner.apprunner.api.widgets.PCanvasView;
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
import org.protocoderrunner.apprunner.api.widgets.PWebView;
import org.protocoderrunner.apprunner.api.widgets.PWindow;
import org.protocoderrunner.sensors.WhatIsRunning;
import org.protocoderrunner.utils.AndroidUtils;
import org.protocoderrunner.views.TouchAreaView;

import java.io.File;

import processing.core.PApplet;

import static android.view.ScaleGestureDetector.OnScaleGestureListener;

//import android.support.v7.graphics.Palette;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class PUI extends PUIGeneric {

	String TAG = "PUI";



	public PUI(Activity a) {
		super(a);
		WhatIsRunning.getInstance().add(this);

	}

    @ProtocoderScript
    @APIMethod(description = "Gets the main layout, usually absolute", example = "")
    @APIParam(params = { "" })
    public View getMainLayout() {
        return ((AppRunnerActivity) a.get()).mainLayout;
    }


    @ProtocoderScript
    @APIMethod(description = "Gets the parent layout where the mainLayout resides", example = "")
    @APIParam(params = { "" })
    public View getParentLayout() {
        View v = (View) ((AppRunnerActivity) a.get()).mainLayout.getParent();
        return v;
    }


    @ProtocoderScript
    @APIMethod(description = "Gets the activity layout, including the action bar", example = "")
    @APIParam(params = { "" })
    public View getActivityLayout() {
        View v = (View) ((AppRunnerActivity) a.get()).mainLayout.getParent().getParent();
        return v;
    }


    @ProtocoderScript
	@APIMethod(description = "Set a title name", example = "")
	@APIParam(params = { "titleName" })
	public void setToolbarTitle(String title) {
		if (noActionBarAllowed) {
			return;
		}

        appRunnerActivity.get().getSupportActionBar().setTitle(title);

		//appRunnerActivity.get().setActionBar(null, null);
		//appRunnerActivity.get().actionBar.setTitle(title);
	}

	@ProtocoderScript
	@APIMethod(description = "Sets a secondary title", example = "")
	@APIParam(params = { "subtitleName" })
	public void setToolbarSubtitle(String title) {
		if (noActionBarAllowed) {
			return;
		}
        appRunnerActivity.get().getSupportActionBar().setSubtitle(title);
//
//		appRunnerActivity.get().setActionBar(null, null);
//		appRunnerActivity.get().actionBar.setSubtitle(title);
	}

	@ProtocoderScript
	@APIMethod(description = "Show/Hide title bar", example = "")
	@APIParam(params = { "boolean" })
	public void showToolbar(Boolean b) {
		if (noActionBarAllowed) {
			return;
		}

		//a.get().setActionBar(null, null);
		if (b) {
			appRunnerActivity.get().getSupportActionBar().show();
		} else {
			appRunnerActivity.get().getSupportActionBar().hide();
		}
	}

	@ProtocoderScript
	@APIMethod(description = "Changes the title bar color", example = "")
	@APIParam(params = { "r", "g", "b", "a" })
	public void setToolbarBgColor(int r, int g, int b, int alpha) {
		if (noActionBarAllowed) {
			return;
		}
		int c = Color.argb(alpha, r, g, b);
		appRunnerActivity.get().setActionBar(c, null);
	}

	@ProtocoderScript
	@APIMethod(description = "Changes the title text color", example = "")
	@APIParam(params = { "r", "g", "b", "a" })
	public void setToolbarTextColor(int r, int g, int b, int alpha) {
		if (noActionBarAllowed) {
			return;
		}

		int c = Color.argb(alpha, r, g, b);
		appRunnerActivity.get().setActionBar(null, c);
	}

	@ProtocoderScript
	@APIMethod(description = "Sets an image rather than text as a title", example = "")
	@APIParam(params = { "imageName" })
	public void setToolbarImage(String imagePath) {
		if (noActionBarAllowed) {
			return;
		}

		Bitmap myBitmap = BitmapFactory.decodeFile(AppRunnerSettings.get().project.getStoragePath() + imagePath);
		Drawable icon = new BitmapDrawable(a.get().getResources(), myBitmap);

		appRunnerActivity.get().actionBar.setIcon(icon);
	}

    //TODO doesnt work properly
	@ProtocoderScript
	@APIMethod(description = "Shows/Hide the home bar", example = "")
	@APIParam(params = { "boolean" })
	public void showHomeBar(boolean b) {
		appRunnerActivity.get().showHomeBar(b);
	}

	@ProtocoderScript
	@APIMethod(description = "Sets the fullscreen / immersive / dimBars mode", example = "")
    @APIParam(params = { "mode={fullscreen, immersive, lightsOut}" })
    public void setScreenMode(String mode) {
        if (mode.equals("fullscreen")) {
            appRunnerActivity.get().setFullScreen();
            isFullscreenMode = true;
        } else if (mode.equals("lightsOut")) {
            appRunnerActivity.get().lightsOutMode();
        } else if (mode.equals("immersive")) {
           // isImmersiveMode = true;
            appRunnerActivity.get().setImmersive();
            updateScreenSizes();
        //do nothing
        } else {

        }

	}

	@ProtocoderScript
	@APIMethod(description = "Forces landscape mode in the app", example = "")
    @APIParam(params = {"mode={'landscape', 'portrait', 'other'"})
	public void setScreenOrientation(String mode) {
        if (mode.equals("landscape")) {
            appRunnerActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if(mode.equals("portrait")) {
            appRunnerActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            appRunnerActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
	}

	@ProtocoderScript
	@APIMethod(description = "Shows a little popup with a given text", example = "")
	@APIParam(params = { "text" })
	public void toast(String text) {
		Toast.makeText(a.get(), text, Toast.LENGTH_SHORT).show();
	}

	@ProtocoderScript
	@APIMethod(description = "Shows a little popup with a given text during t time", example = "")
	@APIParam(params = { "text", "duration" })
	public void toast(String text, int duration) {
		Toast.makeText(a.get(), text, duration).show();
	}


	@ProtocoderScript
    @APIMethod(description = "Sets the main layout padding", example = "")
    @APIParam(params = { "left", "top", "right", "bottom" })
	public void setPadding(int left, int top, int right, int bottom) {
		initializeLayout();
		uiAbsoluteLayout.setPadding(left, top, right, bottom);
	}

	@ProtocoderScript
    @APIMethod(description = "Resize a view to a given width and height. If a parameter is -1 then that dimension is not changed", example = "")
    @APIParam(params = { "View", "width", "height" })
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

	@ProtocoderScript
    @APIMethod(description = "Show/Hide a view", example = "")
    @APIParam(params = { "View", "boolean" })
    public void show(View v, boolean b) {
        if (b) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
	}

	@ProtocoderScript
    @APIMethod(description = "Moves a view to a position using a normal transition", example = "")
    @APIParam(params = { "View", "x", "y" })
	public void move(View v, float x, float y) {
		v.animate().x(x).setDuration(AppSettings.animGeneralSpeed);
		v.animate().y(y).setDuration(AppSettings.animGeneralSpeed);
	}

	@ProtocoderScript
    @APIMethod(description = "Moves a view by given units from the current position using a normal transition", example = "")
    @APIParam(params = { "View", "x", "y" })
	public void moveBy(View v, float x, float y) {
		v.animate().xBy(x).setDuration(AppSettings.animGeneralSpeed);
		v.animate().yBy(y).setDuration(AppSettings.animGeneralSpeed);
	}

    //@TargetApi(L)
    @ProtocoderScript
    @APIParam(params = { "View" })
    public void clipAndShadow(View v, int type, int r) {
        AndroidUtils.setViewGenericShadow(v, type, 0, 0, v.getWidth(), v.getHeight(), r);
    }

    //@TargetApi(L)
    @ProtocoderScript
    @APIParam(params = { "View" })
    public void clipAndShadow(View v, int type, int x, int y, int w, int h, int r) {
        AndroidUtils.setViewGenericShadow(v, type, x, y, w, h, r);
    }

    //@TargetApi(L)
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @ProtocoderScript
	@APIParam(params = { "View" })
	public void reveal(final View v) {
		// previously invisible view

		// get the center for the clipping circle
		int cx = (v.getLeft() + v.getRight()) / 2;
		int cy = (v.getTop() + v.getBottom()) / 2;

		// get the final radius for the clipping circle
		int finalRadius = v.getWidth();

		// create and start the animator for this view
		// (the start radius is zero)
        ValueAnimator anim = (ValueAnimator) ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);

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
	@ProtocoderScript
	@APIParam(params = { "View" })
	public void unreveal(final View v) {

		// get the center for the clipping circle
		int cx = (v.getLeft() + v.getRight()) / 2;
		int cy = (v.getTop() + v.getBottom()) / 2;

		// get the initial radius for the clipping circle
		int initialRadius = v.getWidth();

		// create the animation (the final radius is zero)
		ValueAnimator anim = (ValueAnimator) ViewAnimationUtils.createCircularReveal(v, cx, cy, initialRadius, 0);
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

    @ProtocoderScript
    @APIParam(params = { "View", "x", "y", "w", "h" })
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


	// http://stackoverflow.com/questions/16557076/how-to-smoothly-move-a-image-view-with-users-finger-on-android-emulator
	public void draggable(View v) {
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
					v.setX((int) (startPT.x + mv.x));
					v.setY((int) (startPT.y + mv.y));
					startPT = new PointF(v.getX(), v.getY());
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

    @ProtocoderScript
    @APIMethod(description = "Makes the current view draggable or cancel the drag depending on the boolean state", example = "")
    @APIParam(params = { "View", "boolean" })
	public void draggable(View v, boolean b) {
		if (b) {
			this.draggable(v);
		} else {
			v.setOnTouchListener(null);
		}
	}

    @ProtocoderScript
    @APIMethod(description = "Change the animation speed for the default animations", example = "")
    @APIParam(params = { "View" })
    public void animSpeed(int speed) {
        AppSettings.animGeneralSpeed = speed;
    }

	@ProtocoderScript
    @APIMethod(description = "Makes the view jump", example = "")
    @APIParam(params = { "View" })
	public void jump(View v) {

		ValueAnimator w = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.9f, 1.2f, 1f);
		w.setDuration(AppSettings.animGeneralSpeed);

		ValueAnimator h = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.9f, 1.2f, 1f);
		h.setDuration(AppSettings.animGeneralSpeed);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(w).with(h);
		animatorSet.start();
	}

    @ProtocoderScript
    @APIMethod(description = "Makes the view blink", example = "")
    @APIParam(params = { "View", "num" })
    public void blink(View v, int num) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
        anim.setDuration(AppSettings.animGeneralSpeed);
        anim.setInterpolator(new CycleInterpolator(1));
        anim.setRepeatCount(num);
        anim.start();
    }

    @ProtocoderScript
    @APIMethod(description = "Makes the view blink", example = "")
    @APIParam(params = { "View", "speed", "num" })
    public void blink(View v, int speed, int num) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f, 1f);
        anim.setDuration(speed);
        //anim.setInterpolator(new CycleInterpolator(num));
        anim.setRepeatCount(num);
        anim.start();
    }

	@ProtocoderScript
    @APIMethod(description = "Rotates the view in the x axis", example = "")
    @APIParam(params = { "View", "x" })
	public void rotate(View v, float x) {
		v.animate().rotation(x).setDuration(AppSettings.animGeneralSpeed);
	}

	@ProtocoderScript
    @APIMethod(description = "Rotates the view in the x, y, z axis", example = "")
    @APIParam(params = { "View", "x", "y", "z" })
	public void rotate(View v, float x, float y, float z) {
		v.animate().rotation(x).setDuration(AppSettings.animGeneralSpeed);
		// looks weird but it works more consistent
		v.animate().rotationX(y).setDuration(AppSettings.animGeneralSpeed);
		v.animate().rotationY(z).setDuration(AppSettings.animGeneralSpeed);
	}

	@ProtocoderScript
    @APIMethod(description = "Changes the alpha of a view", example = "")
    @APIParam(params = { "View", "float={0,1}" })
	public void alpha(View v, float alpha) {
		v.animate().alpha(alpha).setDuration(AppSettings.animGeneralSpeed);
	}

	@ProtocoderScript
    @APIMethod(description = "Scales a view with animation to a given size", example = "")
    @APIParam(params = { "View", "x", "y" })
	public void scale(View v, float x, float y) {
		v.animate().scaleX(x).setDuration(AppSettings.animGeneralSpeed);
		v.animate().scaleY(y).setDuration(AppSettings.animGeneralSpeed);
	}

	@ProtocoderScript
    @APIMethod(description = "Scales a view with animation by a given size", example = "")
    @APIParam(params = { "View", "x", "y" })
	public void scaleBy(View v, float x, float y) {
		v.animate().scaleXBy(x).setDuration(AppSettings.animGeneralSpeed);
		v.animate().scaleYBy(y).setDuration(AppSettings.animGeneralSpeed);
	}

    //TODO doesnt work always
    @ProtocoderScript
    @APIMethod(description = "Scales and moves a view to a given position and size", example = "")
    @APIParam(params = { "View", "x", "y", "w", "h" })
    public void amplify(View v, float x, float y, float w, float h) {
        this.move(v, x, y);
        float pX = v.getPivotX();
        float pY = v.getPivotY();
        v.setPivotX(0);
        v.setPivotY(0);
        float sX =  w / v.getWidth();
        float sY =  h / v.getHeight();
        this.scale(v, sX, sY);
        v.setPivotX(pX);
        v.setPivotY(pY);
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


    @ProtocoderScript
    @APIMethod(description = "Starts a gesture detector over a view", example = "")
    @APIParam(params = { "View", "function(data)" })
    //http://stackoverflow.com/questions/6599329/can-one-ongesturelistener-object-deal-with-two-gesturedetector-objects
    public void gestureDetector(View v, final addGestureDetectorCB cb) {
		final GestureDetectorReturn g = new GestureDetectorReturn();

		final GestureDetector gestureDetector = new GestureDetector(a.get(), new GestureDetector.OnGestureListener() {

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



        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(a.get(), new OnScaleGestureListener() {
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
                if(scaleGestureDetector.isInProgress()) return true;
                gestureDetector.onTouchEvent(event);

                return true;
			}
		});
	}


    @ProtocoderScript
    @APIMethod(description = "Changes the background color using RGB", example = "")
	@APIParam(params = { "r", "g", "b" })
	public void setBackgroundColor(int red, int green, int blue) {
		initializeLayout();
		holderLayout.setBackgroundColor(Color.rgb(red, green, blue));
	}

    @ProtocoderScript
    @APIMethod(description = "Changes the background color using Hex", example = "")
    @APIParam(params = { "hex" })
    public void setBackgroundColor(String c) {
        initializeLayout();
        holderLayout.setBackgroundColor(Color.parseColor(c));
    }

    @ProtocoderScript
    @APIMethod(description = "Sets an image as background", example = "")
	@APIParam(params = { "imageName" })
	public void setBackgroundImage(String imagePath) {
		initializeLayout();
		// Add the bg image asynchronously
		new SetBgImageTask(bgImageView, false).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator
				+ imagePath);

	}

    @ProtocoderScript
    @APIMethod(description = "Sets an image as tiled background", example = "")
	@APIParam(params = { "imageName" })
	public void setBackgroundImageTile(String imagePath) {
		initializeLayout();
		// Add the bg image asynchronously
		new SetBgImageTask(bgImageView, true).execute(AppRunnerSettings.get().project.getStoragePath() + File.separator
				+ imagePath);
	}

	@ProtocoderScript
	@APIMethod(description = "Creates an absolute layout ", example = "")
	@APIParam(params = { "x", "y", "w", "h" })
	public PAbsoluteLayout addAbsoluteLayout(int x, int y, int w, int h) {
		PAbsoluteLayout al = newAbsoluteLayout();
		addViewAbsolute(al, x, y, w, h);

		return al;
	}

	@ProtocoderScript
	@APIMethod(description = "Creates a card ", example = "")
	@APIParam(params = { "label" })
	public PCard addCard() {
		PCard c = newCard();
		addViewLinear(c);

		return c;
	}

	@ProtocoderScript
	@APIMethod(description = "Adds a card that can hold views", example = "")
	@APIParam(params = { "label", "x", "y", "w", "h" })
	public PCard addCard(String label, int x, int y, int w, int h) {
		PCard c = newCard();
		c.setTitle(label);

		addViewAbsolute(c, x, y, w, h);
		return c;
	}

    @ProtocoderScript
    @APIMethod(description = "Creates a window that can hold views ", example = "")
    @APIParam(params = { "label", "x", "y", "w", "h" })
    public PWindow addWindow(String label, int x, int y, int w, int h) {
        PWindow wn = newWindow();
        wn.setTitle(label);

        addViewAbsolute(wn, x, y, w, h);
        return wn;
    }

	@ProtocoderScript
	@APIMethod(description = "Adds a button", example = "")
	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public PButton addButton(String label, int x, int y, int w, int h, final addGenericButtonCB callbackfn) {
		PButton b = newButton(label, callbackfn);
		addViewAbsolute(b, x, y, w, h);
		return b;
	}


    @ProtocoderScript
    @APIMethod(description = "Adds a button", example = "")
    @APIParam(params = { "label", "x", "y", "function()" })
    public PButton addButton(String label, int x, int y, final addGenericButtonCB callbackfn) {
        PButton b = newButton(label, callbackfn);
        addViewAbsolute(b, x, y, -1, -1);
        return b;
    }

    @ProtocoderScript
    @APIMethod(description = "Adds an invisible touch area", example = "")
    @APIParam(params = { "x", "y", "w", "h", "bShowArea", "function(touching, x, y)" })
	public TouchAreaView addTouchArea(int x, int y, int w, int h, boolean showArea,
			final addGenericTouchAreaCB callbackfn) {
		TouchAreaView taV = newTouchArea(showArea, callbackfn);
		addViewAbsolute(taV, x, y, w, h);

		return taV;
	}

	@ProtocoderScript
    @APIMethod(description = "Adds a touch area that allows multitouch", example = "")
    @APIParam(params = { "x", "y", "w", "h", "function(touching, x, y)" })
	public PPadView addXYPad(int x, int y, int w, int h, final addPadCB callbackfn) {
		PPadView taV = newTouchPad(callbackfn);
		addViewAbsolute(taV, x, y, w, h);

		return taV;
	}


    //TODO removed old one this is a place holder
	//@ProtocoderScript
    //@APIMethod(description = "Knob", example = "")
    //@APIParam(params = { "function(progress)" })
	public View addKnob(final addGenericKnobCB callbackfn) {
        //PKnob pKnob = newKnob(a.get());
        //addViewAbsolute(pKnob, x, y, w, h);

		return null;
	}

	@ProtocoderScript
    @APIMethod(description = "Adds a slider", example = "")
    @APIParam(params = { "x", "y", "w", "h", "max", "progress", "function(progress)" })
	public PSlider addSlider(int x, int y, int w, int h, int min, int max, final addGenericSliderCB callbackfn) {
		PSlider sb = newSlider(min, max, callbackfn);
		addViewAbsolute(sb, x, y, w, -1);
		return sb;

	}

	@ProtocoderScript
    @APIMethod(description = "Adds a list of items passed as array", example = "")
    @APIParam(params = { "x", "y", "w", "h", "arrayStrings", "function(data)" })
	public PSpinner addChoiceBox(int x, int y, int w, int h, final String[] array, final addGenericSpinnerCB callbackfn) {
		PSpinner spinner = newChoiceBox(array, callbackfn);
        addViewAbsolute(spinner, x, y, w, h);

        return spinner;
	}

	@ProtocoderScript
    @APIMethod(description = "Add a progress bar", example = "")
    @APIParam(params = { "x", "y", "w", "h", "max" })
	public PProgressBar addProgressBar(int x, int y, int w, int h, int max) {
		PProgressBar pb = newProgress(max);
		addViewAbsolute(pb, x, y, w, -1);
		return pb;
	}

	@ProtocoderScript
    @APIMethod(description = "Add a text box defined by its position and size", example = "")
    @APIParam(params = { "label", "x", "y", "w", "h" })
	public PTextView addText(String label, int x, int y, int w, int h) {
		PTextView tv = newText(label);
		addViewAbsolute(tv, x, y, w, h);

		return tv;
	}

	@ProtocoderScript
    @APIMethod(description = "Adds a text box defined only by its position", example = "")
    @APIParam(params = { "label", "x", "y" })
	public PTextView addText(String label, int x, int y) {
		PTextView tv = newText(label);
		addViewAbsolute(tv, x, y, -1, -1);

		return tv;
	}


    @ProtocoderScript
    @APIMethod(description = "Adds an input box", example = "")
	@APIParam(params = { "label", "x", "y", "w", "h", "function()" })
	public PEditText addInput(String label, int x, int y, int w, int h, final addGenericInputCB callbackfn) {
		PEditText et = newInput(label, callbackfn);
		addViewAbsolute(et, x, y, w, h);

		return et;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds an input box", example = "")
	@APIParam(params = { "label", "x", "y", "w", "h" })
	public PEditText addInput(String label, int x, int y, int w, int h) {
		PEditText et = newInput(label, null);
		addViewAbsolute(et, x, y, w, h);

		return et;
	}


    //number picker
    @ProtocoderScript
    @APIMethod(description = "Adds a number picker", example = "")
    @APIParam(params = { "from", "to", "x", "y", "w", "h" })
    public void addNumberPicker(int from, int to, int x, int y, int w, int h, NewGenericNumberPickerCB callback) {
        PNumberPicker np = newNumberPicker(from, to, callback);
        addViewAbsolute(np, x, y, w, h);
    }

	@ProtocoderScript
    @APIMethod(description = "Adds a toggle", example = "")
    @APIParam(params = { "text", "x", "y", "w", "h", "checked", "function(checked)" })
	public PToggleButton addToggle(final String label, int x, int y, int w, int h, boolean initstate,
			final addGenericToggleCB callbackfn) {

		PToggleButton tb = newToggle(label, initstate, callbackfn);
		addViewAbsolute(tb, x, y, w, h);

		return tb;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds a checkbox", example = "")
    @APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public PCheckBox addCheckbox(String label, int x, int y, int w, int h, boolean initstate,
			final addGenericCheckboxCB callbackfn) {
		PCheckBox cb = newCheckbox(label, initstate, callbackfn);
		addViewAbsolute(cb, x, y, w, h);

		return cb;
	}


    @ProtocoderScript
    @APIMethod(description = "Adds a checkbox", example = "")
	@APIParam(params = { "x", "y", "w", "h", "checked", "function(checked)" })
	public PSwitch addSwitch(int x, int y, int w, int h, boolean initstate, final addGenericSwitchCB callbackfn) {

		PSwitch s = newSwitch(initstate, callbackfn);
		addViewAbsolute(s, x, y, w, h);

		return s;
	}



    @ProtocoderScript
    @APIMethod(description = "Adds a radio button", example = "")
	@APIParam(params = { "label", "x", "y", "w", "h", "checked", "function(checked)" })
	public PRadioButton addRadioButton(String label, int x, int y, int w, int h, boolean initstate,
			final addGenericRadioButtonCB callbackfn) {

		PRadioButton rb = newRadioButton(label, initstate, callbackfn);
		addViewAbsolute(rb, x, y, w, h);

		return rb;
	}


    @ProtocoderScript
    @APIMethod(description = "Adds an image", example = "")
	@APIParam(params = { "x", "y", "w", "h", "imagePath" })
	public PImageView addImage(String imagePath, int x, int y, int w, int h) {

		final PImageView iv = newImage(imagePath);
		addViewAbsolute(iv, x, y, w, h);

		return iv;
	}


    @ProtocoderScript
    @APIMethod(description = "Adds an image", example = "")
    @APIParam(params = { "x", "y", "w", "h", "imagePath" })
    public PImageView addImage(int x, int y, int w, int h) {

        final PImageView iv = newImage(null);
        addViewAbsolute(iv, x, y, w, h);

        return iv;
    }

    @ProtocoderScript
    @APIMethod(description = "Adds an image", example = "")
	@APIParam(params = { "x", "y", "imagePath" })
	public PImageView addImage(String imagePath, int x, int y) {

		final PImageView iv = newImage(imagePath);
		addViewAbsolute(iv, x, y, -1, -1);

		return iv;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds a plot with a range", example = "")
	@APIParam(params = { "x", "y", "w", "h", "min", "max" })
	public PPlotView addPlot(int x, int y, int w, int h, int min, int max) {
		PPlotView jPlotView = newPlot(min, max);
		addViewAbsolute(jPlotView, x, y, w, h);

		return jPlotView;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds a checkbox", example = "")
    @APIParam(params = { "x", "y", "w", "h", "imageName", "function()" })
	public PImageButton addImageButton(int x, int y, int w, int h, String imagePath, final addImageButtonCB callbackfn) {
		return newImageButton(x, y, w, h, imagePath, "", false, callbackfn);
	}

	@APIParam(params = { "x", "y", "w", "h", "imageNameNotPressed", "imageNamePressed", "function()" })
	public PImageButton addImageButton(int x, int y, int w, int h, String imgNotPressed, String imgPressed,
			final addImageButtonCB callbackfn) {
		return newImageButton(x, y, w, h, imgNotPressed, imgPressed, false, callbackfn);
	}


    @ProtocoderScript
    @APIMethod(description = "Adds a grid of elements given an array of strings", example = "")
    @APIParam(params = { "type", "arrayStrings", "x", "y", "w", "h", "function(data)" })
    public PGrid addGridOf(String type, NativeArray array, int cols, int x, int y, int w, int h, final addGridOfCB callbackfn) {
        PGrid grid = newGridOf(type, array, cols, callbackfn);
        addViewAbsolute(grid, x, y, w, h);

        return grid;
    }

    @ProtocoderScript
    @APIMethod(description = "Adds a canvas view", example = "")
    @APIParam(params = { "x", "y", "w", "h" })
    public PCanvasView addCanvas(int x, int y, int w, int h, boolean autoDraw) {

        PCanvasView canvasView = newCanvas(w, h);
        canvasView.autoDraw(autoDraw);
        addViewAbsolute(canvasView, x, y, w, h);

        return canvasView;
    }


	@ProtocoderScript
    @APIMethod(description = "Adds a canvas view", example = "")
    @APIParam(params = { "x", "y", "w", "h" })
	public PCanvasView addCanvas(int x, int y, int w, int h) {
        return addCanvas(x, y, w, h, false);
    }

	public PList addList(int x, int y, int w, int h) {
		PList plist = new PList(a.get());
		return plist;

	}

    @ProtocoderScript
    @APIMethod(description = "Adds a processing view", example = "")
    @APIParam(params = { "x", "y", "w", "h", "mode={'p2d', 'p3d'" })
	public PApplet addProcessing(int x, int y, int w, int h, String mode) {

		initializeLayout();

		// Create the main layout. This is where all the items actually go
		FrameLayout fl = new FrameLayout(a.get());
		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fl.setId(200 + (int) (200 * Math.random()));
		fl.setBackgroundResource(R.color.transparent);

		// Add the view
		addViewAbsolute(fl, x, y, w, h);

		PProcessing p = new PProcessing();

        Bundle bundle = new Bundle();
        bundle.putString("mode", mode);
        p.setArguments(bundle);

		FragmentTransaction ft = appRunnerActivity.get().getSupportFragmentManager().beginTransaction();
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
//		FrameLayout fl = new FrameLayout(a.get());
//		fl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		fl.setId(125);
//		fl.setBackgroundResource(R.color.transparent);
//
//		// Add the view
//		addViewAbsolute(fl, x, y, w, h);
//
//		EditorFragment ef = new EditorFragment();
//
//		FragmentTransaction ft = a.get().getSupportFragmentManager().beginTransaction();
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

    @ProtocoderScript
    @APIMethod(description = "Adds a webview", example = "")
	@APIParam(params = { "x", "y", "w", "h" })
	public PWebView addWebView(int x, int y, int w, int h) {
		PWebView webView = newWebview();
		addViewAbsolute(webView, x, y, w, h);

		return webView;

	}

    @ProtocoderScript
    @APIMethod(description = "Add camera view", example = "")
	@APIParam(params = { "type", "x", "y", "w", "h" })
	public PCamera addCameraView(String type, int x, int y, int w, int h) {

		PCamera pCamera = newCamera(type);
        addViewAbsolute(pCamera, x, y, w, h);

		return pCamera;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds a video view and starts playing the fileName", example = "")
	@APIParam(params = { "fileName" })
	public PVideo newVideoView(final String videoFile) {
		PVideo video = newVideo(videoFile);

		return video;
	}

    @ProtocoderScript
    @APIMethod(description = "Adds a video view and starts playing the fileName", example = "")
	@APIParam(params = { "fileName", "x", "y", "w", "h" })
	public PVideo addVideoView(final String videoFile, int x, int y, int w, int h) {
		PVideo video = newVideo(videoFile);
		addViewAbsolute(video, x, y, w, h);

		return video;
	}

    @ProtocoderScript
    @APIMethod(description = "Add a openstreetmap", example = "")
	@APIParam(params = { "x", "y", "w", "h" })
	public PMap addMap(int x, int y, int w, int h) {
		PMap mapView = newMap();

		addViewAbsolute(mapView, x, y, w, h);
		return mapView;
	}




	// --------- yesno dialog ---------//
    public interface popupCB {
		void event(boolean b);
	}

    @ProtocoderScript
    @APIMethod(description = "Shows a popup with a given text", example = "")
	@APIParam(params = { "title", "message", "okButton", "cancelButton", "function(boolean)" })
	public void popupInfo(String title, String msg, String ok, String cancel, final popupCB callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
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

        if(!((Activity) a.get()).isFinishing()) {
            builder.show();
        }
	}


	// --------- inputDialog ---------//
	interface inputDialogCB {
		void event(String text);
	}

    @ProtocoderScript
    @APIMethod(description = "Shows an input dialog", example = "")
	@APIParam(params = { "title", "function(text)" })
	public void popupInput(String title, final inputDialogCB callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
		builder.setTitle(title);

		final EditText input = new EditText(a.get());

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
	interface choiceDialogCB {
		void event(String string);
	}

    @ProtocoderScript
    @APIMethod(description = "Shows a choice dialog using a given array of strings", example = "")
	@APIParam(params = { "title", "arrayStrings", "function(text)" })
	public void popupChoice(String title, final String[] choices, final choiceDialogCB callbackfn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a.get());
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


    //TODO it works but need a way to wait for it to be shown
    public PPopupCustomFragment popupCustom() {

        PPopupCustomFragment pPopupCustomFragment = new PPopupCustomFragment();

        android.app.FragmentManager fm = appRunnerActivity.get().getFragmentManager();
        pPopupCustomFragment.show(fm, "popUpCustom");

        return pPopupCustomFragment;
    }

	@ProtocoderScript
	@APIMethod(description = "Takes a screenshot of the whole app and stores it to a given file name", example = "")
	@APIParam(params = { "imageName" })
	public void takeScreenshot(String imagePath) {
		AndroidUtils.takeScreenshot(AppRunnerSettings.get().project.getStoragePath(), imagePath, uiAbsoluteLayout);
	}

	@ProtocoderScript
	@APIMethod(description = "Takes a screenshot of a view and save it to an image", example = "")
	@APIParam(params = { "view", "imageName" })
	public void takeViewScreenshot(View v, String imagePath) {
		AndroidUtils.takeScreenshotView(AppRunnerSettings.get().project.getStoragePath(), imagePath, v);
	}

	@ProtocoderScript
	@APIMethod(description = "Takes a screenshot of a view", example = "")
	@APIParam(params = { "view" })
	public Bitmap takeViewScreenshot(View v) {
		return AndroidUtils.takeScreenshotView("", "", v);
	}

	// it only works with absolute layout and only when
	// a layout is been used
	@ProtocoderScript
	@APIMethod(description = "Show the virtual keyboard", example = "")
	@APIParam(params = { "boolean" })
	public void showVirtualKeys(boolean show) {
        initializeLayout();
		InputMethodManager imm = (InputMethodManager) a.get().getSystemService(a.get().INPUT_METHOD_SERVICE);

		if (show) {
			imm.showSoftInput(appRunnerActivity.get().getCurrentFocus(), InputMethodManager.SHOW_FORCED);
			uiAbsoluteLayout.setFocusable(true);
			uiAbsoluteLayout.setFocusableInTouchMode(true);

		} else {
			imm.hideSoftInputFromWindow(appRunnerActivity.get().getCurrentFocus().getWindowToken(), 0);
		}
	}


	public void stop() {

	}

	// @JavascriptInterface
	// @APIParam( params = {"milliseconds", "function()"} )
	// public void startTrackingTouches(String b) {
	// }

}