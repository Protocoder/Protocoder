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

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.FaceDetector;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.SignalUtils;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.MLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class PUtil extends PInterface {

	private final Handler handler;
	ArrayList<Runnable> rl = new ArrayList<Runnable>();

	public PUtil(Context a) {
		super(a);
		WhatIsRunning.getInstance().add(this);
		handler = new Handler();
	}

	// --------- getRequest ---------//
	interface getRequestCB {
		void event(int eventType, String responseString);
	}

	public class Looper {
        private LooperCB mCallbackfn;
        Runnable task;

		public int speed;
		boolean paused = false;

		Looper(final int duration, final LooperCB callbackkfn) {
            mCallbackfn = callbackkfn;
			speed = duration;

			task = new Runnable() {

				@Override
				public void run() {
                if (mCallbackfn != null) {
                    mCallbackfn.event();
                }

                if (!paused) {
                    handler.postDelayed(this, speed);
                }
				}
			};

			rl.add(task);
		}

        public Looper onLoop(LooperCB callbackfn) {
            mCallbackfn = callbackfn;

            return this;
        }

		@ProtoMethod(description = "Change the current time speed to a new one", example = "")
		@ProtoMethodParam(params = { "duration" })
		public Looper speed(int duration) {
			this.speed = duration;
            if (duration < this.speed) {
                stop();
                start();
            }
            return this;
		}


		@ProtoMethod(description = "Pause the looper", example = "")
		@ProtoMethodParam(params = { "boolean" })
		public Looper pause(boolean b) {
			this.paused = b;
			if (b == false) {
				handler.postDelayed(task, speed);
			}

            return this;
        }


		@ProtoMethod(description = "Stop the looper", example = "")
		public Looper stop() {
			handler.removeCallbacks(task);

            return this;

        }

        @ProtoMethod(description = "Start the looper", example = "")
        public Looper start() {
            handler.post(task);

            return this;
        }

	}

	// --------- Looper ---------//
    public interface LooperCB {
		void event();
	}


	@ProtoMethod(description = "Creates a looper that loops a given function every 'n' milliseconds", example = "")
	@ProtoMethodParam(params = { "milliseconds", "function()" })
	public Looper loop(final int duration, final LooperCB callbackkfn) {
		return new Looper(duration, callbackkfn);
	}

	@ProtoMethod(description = "Creates a looper that loops a given function every 'n' milliseconds", example = "")
	@ProtoMethodParam(params = { "milliseconds" })
	public Looper loop(final int duration) {
		return new Looper(duration, null);
	}

	// --------- delay ---------//
	public interface delayCB {
		void event();
	}


	@ProtoMethod(description = "Delay a given function 'n' milliseconds", example = "")
	@ProtoMethodParam(params = { "milliseconds", "function()" })
	public void delay(final int duration, final delayCB fn) {

		Runnable task = new Runnable() {
			@Override
			public void run() {
				// handler.postDelayed(this, duration);
				fn.event();
				handler.removeCallbacks(this);
				rl.remove(this);
			}
		};
		handler.postDelayed(task, duration);

		rl.add(task);
	}



    @ProtoMethod(description = "Stop all timers", example = "")
    @ProtoMethodParam(params = { "" })
	public void stopAllTimers() {
		Iterator<Runnable> ir = rl.iterator();
		while (ir.hasNext()) {
			handler.removeCallbacks(ir.next());
			// handler.post(ir.next());
		}
	}

	public void stop() {
		stopAllTimers();
	}

	// http://stackoverflow.com/questions/4605527/converting-pixels-to-dp

    @ProtoMethod(description = "Convert given dp to pixels", example = "")
    @ProtoMethodParam(params = { "" })
	public float dpToPixels(float dp) {
		Resources resources = getContext().getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}


    @ProtoMethod(description = "Convert given px to dp", example = "")
    @ProtoMethodParam(params = { "" })
	public float pixelsToDp(float px) {
		Resources resources = getContext().getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}


    @ProtoMethod(description = "Convert given mm to pixels", example = "")
    @ProtoMethodParam(params = { "" })
	public float mmToPixels(float mm) {
		float px = TypedValue
				.applyDimension(TypedValue.COMPLEX_UNIT_MM, mm, getContext().getResources().getDisplayMetrics());
		return px;
	}


    @ProtoMethod(description = "Convert given pixels to mm", example = "")
    @ProtoMethodParam(params = { "" })
	public float pixelsToMm(int px) {
		float onepx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, getContext().getResources()
				.getDisplayMetrics());

		return px * onepx;
	}

    public interface AnimCB {
        void event(float data);
    }


    @ProtoMethod(description = "Animate a variable from min to max in a specified time using 'bounce', 'linear', 'decelerate', 'anticipate', 'aovershoot', 'accelerate' type  ", example = "")
    @ProtoMethodParam(params = { "type", "min", "max", "time", "function(val)" })
    public ValueAnimator anim(String type, float min, float max, int time, final AnimCB callback) {
        TimeInterpolator interpolator = null;
        if (type.equals("bounce")) {
            interpolator = new BounceInterpolator();
        } else if (type.equals("linear")) {
            interpolator = new LinearInterpolator();
        } else if (type.equals("decelerate")) {
            interpolator = new DecelerateInterpolator();
        } else if (type.equals("anticipate")) {
            interpolator = new AnticipateInterpolator();
        } else if (type.equals("aovershoot")) {
            interpolator = new AnticipateOvershootInterpolator();
        } else {
            interpolator = new AccelerateDecelerateInterpolator();
        }

        ValueAnimator va = ValueAnimator.ofFloat(min, max);
        va.setDuration(time);
        va.setInterpolator(interpolator);

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                callback.event(value);
                MLog.d(TAG, "val " + value + " " + animation.getAnimatedValue());
            }
        });

        return va;
    }


    @ProtoMethod(description = "Parse a color and return and int representing it", example = "")
    @ProtoMethodParam(params = { "colorString" })
    public int parseColor(String c) {
        return Color.parseColor(c);
    }


    @ProtoMethod(description = "Loads a font", example = "")
    @ProtoMethodParam(params = { "fontFile" })
    public Typeface loadFont(String fontName) {
        return Typeface.createFromFile(AppRunnerSettings.get().project.getStoragePath() + File.separator + fontName);
    }


    @ProtoMethod(description = "Detect faces in a bitmap", example = "")
    @ProtoMethodParam(params = { "Bitmap", "numFaces" })
    public int detectFaces(Bitmap bmp, int num_faces) {
        FaceDetector face_detector = new FaceDetector(bmp.getWidth(), bmp.getHeight(), num_faces);
        FaceDetector.Face[] faces = new FaceDetector.Face[num_faces];
        int face_count = face_detector.findFaces(bmp, faces);

        return face_count;
    }



    @ProtoMethod(description = "Converts byte array to bmp", example = "")
    @ProtoMethodParam(params = { "encodedImage" })
    public Bitmap decodeBase64ToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);

        MLog.d(TAG, "bytes--> " + decodedString);
        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;

        final Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, bitmap_options);

        MLog.d(TAG, "bitmap --> " + bitmap);

        return bitmap;
    }

    public SignalUtils signal(int n) {
        return new SignalUtils(getContext(), n);
    }

}