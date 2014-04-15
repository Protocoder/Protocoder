package org.protocoder.apprunner.api.other;

import org.protocoder.apprunner.api.JUI;

import processing.core.PApplet;
import processing.core.PGraphics;
import android.os.Bundle;
import android.os.Looper;

public class QQ extends PApplet {
	String setupfn = "";
	String drawfn = "";
	private JUI jui;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public PGraphics getGraphics() {
		return g;
	}

	public String sketchRenderer() {
		return OPENGL;
	}

	public void setup() {

	}

	public void draw() {

		if (frameCount == 1) {
			Looper.prepare();
			// tell activity Processing is ready
		} else {
			// jui.callback(drawfn);
			line(12, 12, 125, 125);
		}
	}

	public void setup1(String setup1fn) {
		setupfn = setup1fn;
	}

	public void draw1(String draw1fn) {
		drawfn = draw1fn;

	}

	public void setContext(JUI jui) {
		this.jui = jui;
	}

}