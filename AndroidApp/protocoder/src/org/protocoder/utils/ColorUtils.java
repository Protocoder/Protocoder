package org.protocoder.utils;

public class ColorUtils {

	public static int calculateColor(float fraction, int startValue, int endValue) {

		int startInt = startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (startA + (int) (fraction * (endA - startA))) << 24
				| (startR + (int) (fraction * (endR - startR))) << 16
				| (startG + (int) (fraction * (endG - startG))) << 8 | ((startB + (int) (fraction * (endB - startB))));
	}

}
