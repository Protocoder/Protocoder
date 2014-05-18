package org.protocoder.apprunner.api.other;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apprunner.ProtocoderScript;

import android.app.Activity;

public class PProtocoderFeedback {

	protected Activity a;
	private boolean show;
	private boolean showBackground;
	private int color;
	private int textSize;

	public PProtocoderFeedback(Activity a) {
		this.a = a;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void show(boolean b) {
		this.show = b;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void showBackground(boolean b) {
		this.showBackground = b;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void backgroundColor(int color) {
		this.color = color;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void textSize(int textSize) {
		this.textSize = textSize;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	public void show(String text) {
		// this.text = text;
	}

	private void add() {

	}

}
