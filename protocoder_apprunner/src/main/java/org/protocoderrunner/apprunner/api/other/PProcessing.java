package org.protocoderrunner.apprunner.api.other;

import android.os.Bundle;
import android.os.Looper;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;

import processing.core.PApplet;
import processing.core.PGraphics;

public class PProcessing extends PApplet {
    private PInterfaceDraw pfnDraw;
    private PInterfaceSetup pfnSetup;
    private String mode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mode = bundle.getString("mode", "p2d");
    }

    public PGraphics getGraphics() {
        return g;
    }

    @Override
    public void setup() {
        if (pfnSetup != null) {
            pfnSetup.setup(this);
        }
    }

    @Override
    public void draw() {

        if (frameCount == 1) {
            Looper.prepare();
            // tell activity Processing is ready
        } else {
            if (pfnDraw != null) {
                pfnDraw.draw(this);
            }
        }

    }

    public interface PInterfaceSetup {
        // void setup(PApplet p);

        void setup(PApplet p);
    }


    @ProtoMethod(description = "Sets up the processing setup", example = "")
    @ProtoMethodParam(params = {"function(p)"})
    public void setup(PInterfaceSetup pIface) {
        pfnSetup = pIface;
    }

    public interface PInterfaceDraw {
        // void setup(PApplet p);
        void draw(PApplet p);
    }


    @ProtoMethod(description = "Sets up the processing drawing loop", example = "")
    @ProtoMethodParam(params = {"function(p)"})
    public void draw(PInterfaceDraw pIface) {
        pfnDraw = pIface;
    }

    public String sketchRenderer() {
        String pMode;

        if (mode.toLowerCase().equals("p3d")) {
            pMode = P3D;
        } else {
            pMode = P2D;
        }

        return pMode;
    }

}