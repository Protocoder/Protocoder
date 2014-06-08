package org.protocoder.apprunner.api.other;

import processing.core.PApplet;
import processing.core.PGraphics;
import android.os.Bundle;
import android.os.Looper;

public class PProcessing extends PApplet {
	private PInterfaceDraw pfnDraw;
	private PInterfaceSetup pfnSetup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

}