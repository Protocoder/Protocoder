package org.protocoderrunner.apprunner.api.other;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class PProcessing extends PApplet {
	private PInterfaceDraw pfnDraw;
	private PInterfaceSetup pfnSetup;
    private String mode;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mode = bundle.getString("mode", "p2d");
        Log.d("qq", "mode 1 " + mode);
	}

	public PGraphics getGraphics() {
		return g;
	}

	// @Override
	// public String sketchRenderer() {
	// return OPENGL;
	// }

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

	public void setup(PInterfaceSetup pIface) {
		pfnSetup = pIface;
	}

	public interface PInterfaceDraw {
		// void setup(PApplet p);
		void draw(PApplet p);
	}

	public void draw(PInterfaceDraw pIface) {
		pfnDraw = pIface;
	}

    public String sketchRenderer() {
        String pMode;

      //  Log.d("qq", "mode 2" + mode);
        if (mode.toLowerCase().equals("p3d")) {
            pMode = P3D;
        } else {
            pMode = P2D;
        }
      //  Log.d("qq", "mode 3 " + pMode);


        return pMode;
    }

}