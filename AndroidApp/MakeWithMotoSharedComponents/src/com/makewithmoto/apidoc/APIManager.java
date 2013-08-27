package com.makewithmoto.apidoc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.makewithmoto.apidoc.annotation.APIMethod;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

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
		if (instance == null) {
			instance = new APIManager();
		}
		return instance;
	}

	private static final String TAG = "APIManager";
	private static APIManager instance;

	HashMap<String, API> apis = new HashMap<String, API>();
	APIManagerDoc doc = new APIManagerDoc();

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
			APIManagerClass apiClass = new APIManagerClass();
			apiClass.name = c.getSimpleName();
			Log.d(TAG, "" + c.getName());

			//getting all the methods 
			Method m[] = c.getDeclaredMethods();
			for (int i = 0; i < m.length; i++) {

				APIManagerMethod apiMethod = new APIManagerMethod();
				//Log.d(TAG, "" + m[i]);
				//Log.d(TAG, "" + m[i].getName());
				apiMethod.name = m[i].getName();
				Paranamer paranamer = new AdaptiveParanamer();
				String[] paramNames = paranamer.lookupParameterNames(m[i], false); // will return null if not found
				  
				
				Class<?>[] param = m[i].getParameterTypes();
				for (int j = 0; j < paramNames.length; j++) { 
					apiMethod.parameters += /*param[j].toString() + " " + */ paramNames[j];
				} 
				apiMethod.returnType = m[i].getReturnType().toString();
				
				//Log.d("qmqm", apiMethod.parameters + " " + apiMethod.returnType);
				

				//get method information 
				if (apiMethod.name.contains("$") == false) {

					Annotation[] annotations = m[i].getDeclaredAnnotations();
					
					// check if annotation exist and add apidocs
					for (Annotation annotation2 : annotations) {

						if (annotation2.annotationType().getSimpleName()
								.equals(APIMethod.class.getSimpleName())) {
							apiMethod.description = ((APIMethod) annotation2)
									.description();
							apiMethod.example = ((APIMethod) annotation2)
									.example();

						}
					}

					apiClass.apiMethods.add(apiMethod);
				}
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
							+ " " + APIMethod.class.getSimpleName());

					if (annotation2.annotationType().getSimpleName()
							.equals(APIMethod.class.getSimpleName())) {
						String desc = ((APIMethod) annotation2)
								.description();
						String example = ((APIMethod) annotation2)
								.example();
						Log.d(TAG, desc);
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
