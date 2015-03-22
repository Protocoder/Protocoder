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

package org.protocoderrunner.apprunner.api.other;

import android.os.Handler;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

import java.util.ArrayList;

public class PLooper {
    private LooperCB mCallbackfn;
    Runnable task;
    private final Handler handler;
    ArrayList<Runnable> rl = new ArrayList<Runnable>();


    public int speed;
    boolean paused = false;

    // --------- Looper ---------//
    public interface LooperCB {
        void event();
    }

    public PLooper(final int duration, final LooperCB callbackkfn) {
        handler = new Handler();

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

    public PLooper onLoop(LooperCB callbackfn) {
        mCallbackfn = callbackfn;

        return this;
    }

    @ProtoMethod(description = "Change the current time speed to a new one", example = "")
    @ProtoMethodParam(params = { "duration" })
    public PLooper speed(int duration) {
        this.speed = duration;
        if (duration < this.speed) {
            stop();
            start();
        }
        return this;
    }


    @ProtoMethod(description = "Pause the looper", example = "")
    @ProtoMethodParam(params = { "boolean" })
    public PLooper pause(boolean b) {
        this.paused = b;
        if (b == false) {
            handler.postDelayed(task, speed);
        }

        return this;
    }


    @ProtoMethod(description = "Stop the looper", example = "")
    public PLooper stop() {
        handler.removeCallbacks(task);

        return this;

    }

    @ProtoMethod(description = "Start the looper", example = "")
    public PLooper start() {
        handler.post(task);

        return this;
    }

}
