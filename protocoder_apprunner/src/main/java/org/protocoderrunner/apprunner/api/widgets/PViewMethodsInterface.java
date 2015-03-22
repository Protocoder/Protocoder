package org.protocoderrunner.apprunner.api.widgets;

import android.graphics.Typeface;
import android.view.View;

/**
 * Created by victormanueldiazbarrales on 25/07/14.
 */
public interface PViewMethodsInterface {

    public View font(Typeface font);

    public View color(String c);

    public View background(String c);

    public View html(String htmlText);

    public View boxsize(int w, int h);

    public View textSize(int size);

    public View pos(int x, int y);

}
