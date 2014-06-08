package com.example.processingfragment.example1;

import processing.core.PApplet;
import android.os.Bundle;
import android.os.Looper;

public class ProcessingSketchDefaultRenderer1 extends PApplet {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// PFont font;

	@Override
	public void setup() {
		frameRate(28);
		smooth();
		// font = createFont("Verdana-Bold", 48);
		// background(250, 250, 205);
		background(0);
		noStroke();
	}

	float count = 0.0f;

	@Override
	public void draw() { // background(255);

		if (frameCount == 1) {
			Looper.prepare();
		}

		rectMode(CORNER);
		fill(255, 125);
		rect(0, 0, width, height);

		// you have to set a font first!
		// textFont(font);

		// pushMatrix();
		// translate(width / 2, height / 2);

		rectMode(CENTER);
		fill(0);
		for (int i = 0; i < width + 15; i = i + 25) {
			rect(i, 130, 25, 60 + 15 * sin(TWO_PI * count + i / 125f));
		}

		count = count + 0.01f;

		// for (int j = 0; j < height; j = j + 25) {
		// for (int i = 0; i < width; i = i + 25) {
		// fill(random(225, 255));
		// rect(i, j, 25, 25);
		// }
		// }

		// popMatrix();

	}
	/*
	 * public int sketchWidth() { return 500; }
	 * 
	 * public int sketchHeight() { return 500; }
	 */

}
