package com.example.processingfragment.example1;

import processing.core.PApplet;
import processing.core.PFont;
import android.os.Bundle;
import android.os.Looper;

public class ProcessingSketchDefaultRenderer1 extends PApplet {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	PFont font;

	public void setup() {
		frameRate(28);
		smooth();
		font = createFont("Verdana-Bold", 48);
		// background(250, 250, 205);
		background(0);
		noStroke();
	}

	public void draw() { // background(255);

		if (frameCount == 1) {
			Looper.prepare();
		}

		fill(0, 12);
		rect(0, 0, width, height);

		// you have to set a font first!
		textFont(font);

		// pushMatrix();
		// translate(width / 2, height / 2);
		for (int i = 0; i < 52; i++) {
			fill(random(125, 255));
			ellipse(random(width), random(height), 2, 2);
		}

		// popMatrix();

	}

	/*
	 * public int sketchWidth() { return 500; }
	 * 
	 * public int sketchHeight() { return 500; }
	 */

}
