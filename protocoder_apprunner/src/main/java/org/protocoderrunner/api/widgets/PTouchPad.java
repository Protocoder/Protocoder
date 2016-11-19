package org.protocoderrunner.api.widgets;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;
import org.protocoderrunner.api.other.PLooper;
import org.protocoderrunner.apprunner.AppRunner;
import org.protocoderrunner.apprunner.StyleProperties;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by biquillo on 11/09/16.
 */
public class PTouchPad extends PCanvas implements PViewMethodsInterface {

    private static final String TAG = PTouchPad.class.getSimpleName();

    public StyleProperties props = new StyleProperties();
    public Styler styler;
    private ArrayList touches;

    public PTouchPad(AppRunner appRunner) {
        super(appRunner);
        MLog.d(TAG, "create touchpad");

        draw = mydraw;
        styler = new Styler(appRunner, this, props);

        appRunner.pUi.onTouch(this, new ReturnInterface() {
            @Override
            public void event(ReturnObject r) {
                touches = (ArrayList) r.get("touches");

                if (touches.size() > 0) {
                    MLog.d(TAG, "start touch");
                    looper.start();
                }
                if (touches.size() == 1) {
                    ReturnObject t = ((ReturnObject) touches.get(0));
                    if (t.get("action") == "up") {
                        MLog.d(TAG, "stop touch");
                        touches = null;
                        invalidate();
                        looper.stop();
                    }
                }
                // invalidate();
            }
        });

    }

    PLooper looper = new PLooper(mAppRunner, 50, new PLooper.LooperCB() {
        @Override
        public void event() {
            invalidate();
        }
    });

    OnDrawCallback mydraw = new OnDrawCallback() {
        @Override
        public void event(PCanvas c) {
            c.clear();
            c.mode(false);

            if (touches != null) {
                for (int i = 0; i < touches.size(); i++) {
                    ReturnObject r = (ReturnObject) touches.get(i);
                    float x = (float) r.get("x");
                    float y = (float) r.get("y");

                    if (true) {
                        if (x < 0) x = 0;
                        if (x > width) x = width;
                        if (y < 0) y = 0;
                        if (y > height) y = height;
                    }

                    c.fill(styler.padColor);
                    c.stroke(styler.padBorderColor);
                    c.strokeWidth(styler.padBorderSize);
                    c.ellipse(x, y, styler.padSize, styler.padSize);
                }
            }
        }
    };

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setStyle(Map style) {
        styler.setStyle(style);
    }

    @Override
    public Map getStyle() {
        return props;
    }

}
