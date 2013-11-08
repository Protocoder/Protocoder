/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.apidoc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apidoc.annotation.APIParam;

import android.util.Log;

import com.google.gson.Gson;

/*
 * Usage 
 * 		APIManager.getInstance().addClass(JAndroid.class);
 *		APIManager.getInstance().addClass(JUI.class);
 *		APIManager.getInstance().getDocumentation();
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

			// getting all the methods
			Method m[] = c.getDeclaredMethods();
			for (int i = 0; i < m.length; i++) {

				//get method
				APIManagerMethod apiMethod = new APIManagerMethod();
				apiMethod.name = m[i].getName();
			
				//get parameter types 
				Class<?>[] param = m[i].getParameterTypes();
				String[] paramsType = new String[param.length];
				
				for (int j = 0; j < param.length; j++) {
					String p = param[j].getSimpleName().toString();
					paramsType[j] = p;
				}				
				apiMethod.paramsType = paramsType;
				
				//return type
				apiMethod.returnType = m[i].getReturnType().getSimpleName().toString();

				// get method information 
				if (apiMethod.name.contains("$") == false) {
					Annotation[] annotations = m[i].getDeclaredAnnotations();
					// check if annotation exist and add apidocs
					for (Annotation annotation2 : annotations) {

						// description and example 
						if (annotation2.annotationType().getSimpleName()
								.equals(APIMethod.class.getSimpleName())) {
							apiMethod.description = ((APIMethod) annotation2)
									.description();
							apiMethod.example = ((APIMethod) annotation2)
									.example();

						}

						//get parameters names 
						if (annotation2.annotationType().getSimpleName()
								.equals(APIParam.class.getSimpleName())) {
							apiMethod.parametersName = ((APIParam) annotation2)
									.params();
							Log.d(TAG, "getting names " + apiMethod.parametersName);
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
						String desc = ((APIMethod) annotation2).description();
						String example = ((APIMethod) annotation2).example();
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

	public void clear() {
		apis.clear();
		doc = null;
		apis = new HashMap<String, API>();
		doc = new APIManagerDoc();
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
