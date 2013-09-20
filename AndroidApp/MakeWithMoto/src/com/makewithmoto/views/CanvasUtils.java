package com.makewithmoto.views;

/*
 * Taken from processing.org source code 
 * 
 */

public class CanvasUtils {

	static public final float lerp(float start, float stop, float amt) {
		return start + (stop - start) * amt;
	}

	/**
	 * Normalize a value to exist between 0 and 1 (inclusive). Mathematically
	 * the opposite of lerp(), figures out what proportion a particular value is
	 * relative to start and stop coordinates.
	 */
	static public final float norm(float value, float start, float stop) {
		return (value - start) / (stop - start);
	}

	/**
	 * Convenience function to map a variable from one coordinate space to
	 * another. Equivalent to unlerp() followed by lerp().
	 */
	static public final float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}


}
