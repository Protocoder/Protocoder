package com.makewithmoto.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class Template {
	
	/**
	 * Merge a file into another file with the 
	 * ${contents} tag in the template file
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
	 * Substitute variables into a string
	 * ${variable}
	 * 
	 *  @param template
	 *  @param Map<String, String> variables
	 *  @return the contents
	 */
	public static String substituteVariables(String template, Map<String, String> variables) {
	    Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
	    Matcher matcher = pattern.matcher(template);
	    // StringBuilder cannot be used here because Matcher expects StringBuffer
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
