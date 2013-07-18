package com.makewithmoto.sensors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

public class WhatIsRunning {

	private static WhatIsRunning instance;

	protected WhatIsRunning() {

	}

	public static WhatIsRunning getInstance() {
		if (instance == null)
			instance = new WhatIsRunning();
		return instance;

	}

	Vector<Object> runners = new Vector<Object>();

	public void stopAll() {
		for (Object o : runners) {
			Method method = null;

			try {
				method = o.getClass().getMethod("stop");
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}

			try {
				method.invoke(o);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {

			}
		}
	}

	public void add(Object object) {
		runners.add(object);
		
	}
}
