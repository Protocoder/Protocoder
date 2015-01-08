package org.protocoder.views;

import android.content.Context;

import org.protocoderrunner.apprunner.api.widgets.PCanvas;

/**
 * Created by victormanueldiazbarrales on 06/10/14.
 */
public class Overlay extends PCanvas {
    public Overlay(Context context) {
        super(context);
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
