package com.makewithmoto.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;


/*
 * Usage 
 * 		APIManager.getInstance().addClass(JAndroid.class);
 *		APIManager.getInstance().addClass(JUI.class);
 *		APIManager.getInstance().listAPIs();
 *	
 */
public class APIManager {

	private static final String TAG = "APIManager";
	HashMap<String, API> apis = new HashMap<String, API>();
	private static APIManager instance;

	public static APIManager getInstance() {
		if (instance == null)
			instance = new APIManager();
		return instance;
	}

	public APIManager() {

	}

	public void addClass(Class class1) {

		try {
			// Class c = Class.forName("JUI");
			Class c = class1;
			Method m[] = c.getDeclaredMethods();
			for (int i = 0; i < m.length; i++)
				System.out.println(m[i].toString());

			apis.put(c.getSimpleName(), new API(c, m));
		} catch (Throwable e) {
			System.err.println(e);
		}

	}

	public HashMap<String, API> getAPIs() {

		return apis;
	}

	public void listAPIs() {
		Iterator it = apis.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			
			API p = (API) pairs.getValue(); 
			Method[] methods = p.methods;
			
			for (Method m : methods) {
				Log.d(TAG, pairs.getKey() + " = " + m.getName());
				
			}
			it.remove(); // avoids a ConcurrentModificationException
		}

	}

	class API {

		public Class cls;
		public Method[] methods;

		public API(Class cls, Method[] m) {
			this.cls = cls;
			this.methods = m;
		}

	}
}
