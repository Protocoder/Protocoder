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

package org.protocoder.appApi;

import org.protocoderrunner.apprunner.api.PConsole;
import org.protocoderrunner.apprunner.api.PDashboard;

public class WebEditor {

    private final Protocoder protocoder;
    public PDashboard dashboard = null;
    public PConsole console = null;

    WebEditor(Protocoder protocoder) {
        this.protocoder = protocoder;
        console = new PConsole(Protocoder.mActivityContext);
        dashboard = new PDashboard(Protocoder.mActivityContext);
    }

    public void open(String folder, String appName) {

    }
    public void openInNewWindow(String folder, String appName) {

    }
    public void openTab(String filePath) {

    }
    public void closeTab(String filePath) {

    }
    public void closeAllTabs() {

    }
    public void refresh() {

    }
    public void showBar(boolean b) {

    }

}
