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

package org.protocoder.views;

import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.api.widgets.PCanvas;


public class Overlay extends PCanvas {
    public Overlay(AppRunner appRunner) {
        super(appRunner);
    }

    public void setFrame() {
        noFill();
        stroke(0, 0, 255);
        strokeWidth(25);
        rect(0, 0, getWidth(), getHeight());
        float x1 = getWidth() - 50;
        float y1 = getHeight() / 2;
        ellipse(x1, y1, 50, 50);
        fill(255, 255, 255);
        textSize(25);
        text("2", x1, y1);
        invalidate();
    }

    public void setHighlight() {
        fill(255, 255, 255, 125);
        noStroke();
        rect(0, 0, getWidth(), getHeight());
        invalidate();
    }
}
