/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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

package org.protocoder.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.base.BaseMainApp;
import org.protocoder.network.ALog;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class FileIO {

    private static final String TAG = "FILEIO";

    /**
     * Write the data to the file indicate by fileName. The file is created if it doesn't exist.
     * 
     * @param activity
     * @param data
     * @param fileName
     * @throws IOException
     */
    public static void write(Activity activity, String data, String fileName) throws IOException {
	FileOutputStream fo = activity.openFileOutput(fileName, 0);
	BufferedWriter bf = new BufferedWriter(new FileWriter(fo.getFD()));
	bf.write(data);
	bf.flush();
	bf.close();
    }

    /**
     * Read the contents of the file indicated by fileName
     * 
     * @param activity
     * @param fileName
     * @return the contents
     * @throws IOException
     */
    public static String read(Context activity, String fileName) throws IOException {
	if (fileName.contains("/0/"))
	    fileName = fileName.replace("/0/", "/legacy/");
	FileInputStream is = activity.openFileInput(fileName);
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	StringBuilder sb = new StringBuilder();
	while (br.ready()) {
	    String line = br.readLine();
	    sb.append(line);
	}
	String data = sb.toString();
	return data;
    }

    /**
     * Read the contents of a file in the assets directory indicated by fileName
     * 
     * @param activity
     * @params fileName
     * @return the contents
     * @throws IOException
     */
    public static String readFromAssets(Context activity, String fileName) throws IOException {
	AssetManager am = activity.getAssets();
	return read(am.open(fileName));
    }

    /**
     * Read the contents of the file indicated by fileName
     * 
     * @param fileName
     * @return the contents
     * @throws IOException
     */
    public static String read(InputStream is) throws IOException {
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	StringBuilder sb = new StringBuilder();
	while (br.ready()) {
	    String line = br.readLine();
	    sb.append("\n");
	    sb.append(line);
	}
	String data = sb.toString();
	return data;
    }

    // Read a file in the assets directory into a string
    public static String readAssetFile(Context c, String path) {
	String out = null;
	AssetManager am = c.getAssets();
	try {
	    InputStream in = am.open(path);
	    ByteArrayOutputStream buf = new ByteArrayOutputStream();
	    int i;
	    try {
		i = in.read();
		while (i != -1) {
		    buf.write(i);
		    i = in.read();
		}
		in.close();
	    } catch (IOException ex) {
	    }
	    out = buf.toString();
	} catch (IOException e) {
	    e.printStackTrace();
	    ALog.e(TAG, e.toString());
	}
	return out;
    }

    // Write a string to a file
    public static String writeStringToFile(String url, String name, String code) {
	Log.d(TAG, "Writing string to file name: " + name + " code: " + code);
	String filename = name.replaceAll("[^a-zA-Z0-9-_\\. ]", "_");
	String baseDir = url + File.separator + filename;
	Log.d(TAG, "The base directory is:" + baseDir);
	File dir = new File(baseDir);
	dir.mkdirs();
	File f = new File(dir.getAbsoluteFile() + File.separator + "main.js");

	try {
	    if (!f.exists()) {
		f.createNewFile();
		Log.d(TAG, "New file is being created!");
	    } else {
		Log.d(TAG, "The file already exists!");
		// We should probably do something here to handle multiple file
		// cases
	    }
	    FileOutputStream fo = new FileOutputStream(f);
	    byte[] data = code.getBytes();
	    fo.write(data);
	    fo.flush();
	    fo.close();
	} catch (FileNotFoundException ex) {
	    ALog.e(TAG, ex.toString());
	} catch (IOException e) {
	    e.printStackTrace();
	    ALog.e(TAG, e.toString());
	}
	Log.d(TAG, "Absolute path of new file: " + f.getAbsolutePath());
	return f.getAbsolutePath();
    }

    public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
	try {
	    String[] files = assetManager.list(fromAssetPath);
	    new File(toPath).mkdirs();
	    boolean res = true;
	    for (String file : files)
		if (file.contains("."))
		    res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
		else
		    res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
	    return res;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    private static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
	InputStream in = null;
	OutputStream out = null;
	try {
	    in = assetManager.open(fromAssetPath);
	    new File(toPath).createNewFile();
	    out = new FileOutputStream(toPath);
	    copyFile(in, out);
	    in.close();
	    in = null;
	    out.flush();
	    out.close();
	    out = null;
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
	byte[] buffer = new byte[1024];
	int read;
	while ((read = in.read(buffer)) != -1) {
	    out.write(buffer, 0, read);
	}
    }

    public static void copyFileOrDir(Context c, String path) {
	AssetManager assetManager = c.getAssets();
	String assets[] = null;
	try {
	    assets = assetManager.list(path);
	    if (assets.length == 0) {
		copyFile(c, path);
	    } else {
		String fullPath = BaseMainApp.baseDir + "/" + path;
		File dir = new File(fullPath);
		if (!dir.exists())
		    dir.mkdir();
		for (int i = 0; i < assets.length; ++i) {
		    copyFileOrDir(c, path + "/" + assets[i]);
		}
	    }
	} catch (IOException ex) {
	    Log.e("tag", "I/O Exception", ex);
	}
    }

    private static void copyFile(Context c, String filename) {
	AssetManager assetManager = c.getAssets();

	InputStream in = null;
	OutputStream out = null;
	try {
	    in = assetManager.open(filename);
	    String newFileName = BaseMainApp.baseDir + filename;
	    out = new FileOutputStream(newFileName);

	    byte[] buffer = new byte[1024];
	    int read;
	    while ((read = in.read(buffer)) != -1) {
		out.write(buffer, 0, read);
	    }
	    in.close();
	    in = null;
	    out.flush();
	    out.close();
	    out = null;
	} catch (Exception e) {
	    Log.e("tag", e.getMessage());
	}

    }

    public static void copyFile(File src, File dst) throws IOException {
	FileChannel inChannel = new FileInputStream(src).getChannel();
	FileChannel outChannel = new FileOutputStream(dst).getChannel();
	try {
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	} finally {
	    if (inChannel != null)
		inChannel.close();
	    if (outChannel != null)
		outChannel.close();
	}
    }

    public static void deleteDir(String name) {
	String fullPath = BaseMainApp.baseDir + "/" + name;
	Log.d(TAG, "deleting directory " + fullPath);
	File dir = new File(fullPath);

	if (dir.isDirectory()) {
	    Log.d(TAG, "deleting directory " + dir.getAbsolutePath());
	    String[] children = dir.list();
	    for (int i = 0; i < children.length; i++) {
		File f = new File(dir, children[i]);
		f.delete();
		Log.d(TAG, "deleting directory done" + f.getAbsolutePath());
	    }
	}
	Log.d(TAG, "deleting directory done" + name);
    }

    public static void deleteDir(File dir) {
	Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
	if (dir.isDirectory()) {
	    String[] children = dir.list();
	    for (int i = 0; i < children.length; i++) {
		File temp = new File(dir, children[i]);
		if (temp.isDirectory()) {
		    Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
		    deleteDir(temp);
		} else {
		    Log.d("DeleteRecursive", "Delete File" + temp.getPath());
		    boolean b = temp.delete();
		    if (b == false) {
			Log.d("DeleteRecursive", "DELETE FAIL");
		    }
		}
	    }

	}
	dir.delete();
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    public static void saveStrings(String filename, String strings[]) {
	saveStrings(saveFile(filename), strings);
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    public static File saveFile(String where) {
	return new File(AppRunnerSettings.get().project.getFolder() + File.separator + where);
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    static public void saveStrings(File file, String strings[]) {
	try {
	    String location = file.getAbsolutePath();
	    createPath(location);
	    OutputStream output = new FileOutputStream(location);
	    if (file.getName().toLowerCase().endsWith(".gz")) {
		output = new GZIPOutputStream(output);
	    }
	    saveStrings(output, strings);
	    output.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    static public void saveStrings(OutputStream output, String strings[]) {
	try {
	    OutputStreamWriter osw = new OutputStreamWriter(output, "UTF-8");
	    PrintWriter writer = new PrintWriter(osw);
	    for (int i = 0; i < strings.length; i++) {
		writer.println(strings[i]);
	    }
	    writer.flush();
	} catch (UnsupportedEncodingException e) {
	} // will not happen
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    public static String[] loadStrings(String filename) {
	InputStream is = createInput(AppRunnerSettings.get().project.getFolder() + File.separator + filename);
	if (is != null)
	    return loadStrings(is);

	System.err.println("The file \"" + filename + "\" " + "is missing or inaccessible, make sure "
		+ "the URL is valid or that the file has been " + "added to your sketch and is readable.");
	return null;
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    static public String[] loadStrings(InputStream input) {
	try {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

	    String lines[] = new String[100];
	    int lineCount = 0;
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		if (lineCount == lines.length) {
		    String temp[] = new String[lineCount << 1];
		    System.arraycopy(lines, 0, temp, 0, lineCount);
		    lines = temp;
		}
		lines[lineCount++] = line;
	    }
	    reader.close();

	    if (lineCount == lines.length) {
		return lines;
	    }

	    // resize array to appropriate amount for these lines
	    String output[] = new String[lineCount];
	    System.arraycopy(lines, 0, output, 0, lineCount);
	    return output;

	} catch (IOException e) {
	    e.printStackTrace();
	    // throw new RuntimeException("Error inside loadStrings()");
	}
	return null;
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    public static InputStream createInput(String filename) {
	InputStream input = createInputRaw(filename);

	return input;
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    public static InputStream createInputRaw(String filename) {
	// Additional considerations for Android version:
	// http://developer.android.com/guide/topics/resources/resources-i18n.html
	InputStream stream = null;

	if (filename == null)
	    return null;

	if (filename.length() == 0) {
	    // an error will be called by the parent function
	    // System.err.println("The filename passed to openStream() was empty.");
	    return null;
	}

	// Maybe this is an absolute path, didja ever think of that?
	File absFile = new File(filename);
	if (absFile.exists()) {
	    try {
		stream = new FileInputStream(absFile);
		if (stream != null) {
		    return stream;
		}
	    } catch (FileNotFoundException fnfe) {
		// fnfe.printStackTrace();
	    }
	}

	return null;
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    static public InputStream createInput(File file) {
	if (file == null) {
	    throw new IllegalArgumentException("File passed to createInput() was null");
	}
	try {
	    InputStream input = new FileInputStream(file);
	    if (file.getName().toLowerCase().endsWith(".gz")) {
		return new GZIPInputStream(input);
	    }
	    return input;

	} catch (IOException e) {
	    System.err.println("Could not createInput() for " + file);
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Takes a path and creates any in-between folders if they don't already exist. Useful when trying to save to a
     * subfolder that may not actually exist.
     */
    static public void createPath(String path) {
	createPath(new File(path));
    }

    static public void createPath(File file) {
	try {
	    String parent = file.getParent();
	    if (parent != null) {
		File unit = new File(parent);
		if (!unit.exists())
		    unit.mkdirs();
	    }
	} catch (SecurityException se) {
	    System.err.println("You don't have permissions to create " + file.getAbsolutePath());
	}
    }

    static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
	ZipOutputStream zip = null;
	FileOutputStream fileWriter = null;
	fileWriter = new FileOutputStream(destZipFile);
	zip = new ZipOutputStream(fileWriter);
	addFolderToZip("", srcFolder, zip);
	zip.flush();
	zip.close();
    }

    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
	File folder = new File(srcFile);
	if (folder.isDirectory()) {
	    addFolderToZip(path, srcFile, zip);
	} else {
	    byte[] buf = new byte[1024];
	    int len;
	    FileInputStream in = new FileInputStream(srcFile);
	    zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
	    while ((len = in.read(buf)) > 0) {
		zip.write(buf, 0, len);
	    }
	}
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
	File folder = new File(srcFolder);
	for (String fileName : folder.list()) {
	    if (path.equals("")) {
		addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
	    } else {
		addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
	    }
	}
    }
}
