package com.makewithmoto.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Hardware {


	public static void setSpeakerOn(boolean b) {
		
		Class<?> audioSystemClass;
		try {
			audioSystemClass = Class.forName("android.media.AudioSystem");
			Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
			// First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to the default
			// behavior, use FORCE_NONE (0).
			setForceUse.invoke(null, 1, 1);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	
}