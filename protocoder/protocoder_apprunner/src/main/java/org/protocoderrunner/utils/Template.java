/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
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

package org.protocoderrunner.utils;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {

	/**
	 * Merge a file into another file with the ${contents} tag in the template
	 * file
	 * 
	 * @param context
	 * @param template
	 * @param file
	 * @return the contents
	 */
	public static String mergeAssetFile(Context activity, String templatePath, String contents) {
		String templateContents = null;
		try {
			templateContents = FileIO.readFromAssets(activity, templatePath);

			HashMap<String, String> vars = new HashMap<String, String>();
			vars.put("contents", contents);
			return substituteVariables(templateContents, vars);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Substitute variables into a string ${variable}
	 * 
	 * @param template
	 * @param Map
	 *            <String, String> variables
	 * @return the contents
	 */
	public static String substituteVariables(String template, Map<String, String> variables) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(template);
		// StringBuilder cannot be used here because Matcher expects
		// StringBuffer
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			if (variables.containsKey(matcher.group(1))) {
				String replacement = variables.get(matcher.group(1));
				// quote to work properly with $ and {,} signs
				matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

}
