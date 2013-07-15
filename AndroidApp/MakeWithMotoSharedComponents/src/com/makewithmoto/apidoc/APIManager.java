package com.makewithmoto.apidoc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;

/*
 * Usage 
 * 		APIManager.getInstance().addClass(JAndroid.class);
 *		APIManager.getInstance().addClass(JUI.class);
 *		APIManager.getInstance().getDocumentation();
 *	
 */
/**
 * @author Victor Diaz
 * 
 */
public class APIManager {

	public static APIManager getInstance() {
		if (instance == null)
			instance = new APIManager();
		return instance;
	}

	private static final String TAG = "APIManager";
	private static APIManager instance;

	HashMap<String, API> apis = new HashMap<String, API>();
	APIDoc doc = new APIDoc();

	public APIManager() {

	}

	/**
	 * add a new class to extract the methods
	 * 
	 * @param class1
	 */
	public void addClass(Class c) {

		try {
			// api docs
			APIClass apiClass = new APIClass();
			apiClass.name = c.getSimpleName();
			// apiClass.description = ;
			Log.d("qq1", "" + c.getName());

			Method m[] = c.getDeclaredMethods();
			for (int i = 0; i < m.length; i++) {

				APIMethod apiMethod = new APIMethod();
				Log.d("qq2", "" + m[i]);
				Log.d("qq3", "" + m[i].getName());
				apiMethod.name = m[i].getName();
				//Log.d("qq", apiMethod.name);
				
				Annotation[] annotations = m[i].getDeclaredAnnotations();

				// check if annotation exist and add apidocs
				for (Annotation annotation2 : annotations) {

					if (annotation2.annotationType().getSimpleName()
							.equals(APIAnnotation.class.getSimpleName())) {
						apiMethod.description = ((APIAnnotation) annotation2)
								.description();
						apiMethod.example = ((APIAnnotation) annotation2)
								.example();

					}
				}
				
				apiClass.apiMethods.add(apiMethod);
			}

			doc.apiClasses.add(apiClass);

			// classes and methods
			// apis.put(c.getSimpleName(), new API(c, m));

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
				// get class and method
				Log.d(TAG, pairs.getKey() + " = " + m.getName());

				Annotation[] annotations = m.getDeclaredAnnotations();

				for (Annotation annotation2 : annotations) {

					Log.d(TAG, annotation2.toString() + " "
							+ annotation2.annotationType().getSimpleName()
							+ " " + APIAnnotation.class.getSimpleName());

					if (annotation2.annotationType().getSimpleName()
							.equals(APIAnnotation.class.getSimpleName())) {
						String desc = ((APIAnnotation) annotation2)
								.description();
						String example = ((APIAnnotation) annotation2)
								.example();
						Log.d("desc", desc);
					}
				}

			}
			it.remove(); // avoids a ConcurrentModificationException
		}

	}

	public String getDocumentation() {
		Gson gson = new Gson();
		String json = gson.toJson(doc);
		
		return json.toString();
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
