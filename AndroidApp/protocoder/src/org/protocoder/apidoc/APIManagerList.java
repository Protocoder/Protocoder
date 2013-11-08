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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import android.util.Log;

public class APIManagerList {


	class API { 
		
		public Class cls; 
		protected Method methods; 
		
		
		public API(Class cls, Object obj, String name, Method method) {
			this.cls = cls; 
			this.methods = method; 
		} 
		
	}

	private static final String TAG = "MethodsExtract";
	

	String methodAnnotationName = "JavaScriptInterface"; 
	private Vector<API> apis; 
	
	APIManagerList() {
		methodAnnotationName = "JavaScriptInterface";

		apis = new Vector<API>();
		//PackageUtils.getClasseNamesInPackage(jarName, packageName);
		//Log.d(TAG, "" + java.lang.Class.class.getClasses().toString());
	}

	public void addObject(Object obj) {

		Class cls = obj.getClass();

		Log.d(TAG, " -- adding new object with Class " + cls.getName() + " "
				+ cls.getSimpleName());	
		
		
		 

		// searching fields with annotations
		Field attr[] = cls.getDeclaredFields(); 
		Log.d(TAG, "Declared annotations " + cls.getDeclaredAnnotations());

		for (int i = 0; i < attr.length; i++) {

			attr[i].setAccessible(true); 
			Field url = attr[i];
			String name = attr[i].getName();
			Class<?> type = attr[i].getType();

			// foreach annotation in this object
			Annotation a[] = attr[i].getAnnotations();
			for (int j = 0; j < a.length; j++) {

				String objectName = a[j].annotationType().getSimpleName();
		
//				if (objectName.equals(annotationName)) {
//
//					// guardar aqui la referencia al objeto
//					API api = new API(cls, obj, name, attr[i]); 
//					apis.add(api);
//
//				}

			}

		} 
		
		//------------------ get declared methods 
		Method methods[] = cls.getDeclaredMethods(); 

		for (int i = 0; i < methods.length; i++) {

			Method method = methods[i];
			method.setAccessible(true); 
			String name = methods[i].getName();


			// foreach annotation in this object
			Annotation a[] = method.getAnnotations();
			for (int j = 0; j < a.length; j++) {

				String objectName = a[j].annotationType().getSimpleName();
		
				if (objectName.equals(methodAnnotationName)) {

			
					Log.d(TAG, "annotation method: " + method + " " + name);
					
					// save object reference 
					API qq = new API(cls, obj, name, method); 
					apis.add(qq);


				}

			}

		} 
		

	}

	public Object getValue(Object obj, Field attr) {
		Object value = null;

		// get value
		try {
			value = attr.get(obj);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		return value;
	}

	public void callMethod(Object obj, Method method) {
		try {
			method.invoke(obj, null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
	
	}

}
