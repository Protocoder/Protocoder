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


import android.view.View;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

public class PAnimation {
    private final View myView;

    public PAnimation(View view) {
        WhatIsRunning.getInstance().add(this);

        myView = view;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAnimation move(float x, float y) {
        myView.animate().x(x).y(y);
        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAnimation moveBy(float x, float y) {
        myView.animate().xBy(x).yBy(y);
        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAnimation rotate(float x) {
        myView.animate().rotation(x);
        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public PAnimation rotate(float x, float y, float z) {
        //return myView.animate().rotation(x).rotationX(y).rotationY(z);
        return this;
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void start() {
        // myView.
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {""})
    public void stop() {

    }


}
