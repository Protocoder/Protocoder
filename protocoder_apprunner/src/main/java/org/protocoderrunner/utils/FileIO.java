/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.project.ProjectManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
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
import java.util.zip.ZipInputStream;

public class FileIO {

    private static final String TAG = "FILEIO";

    /**
     * Write the data to the file indicate by fileName. The file is created if
     * it doesn't exist.
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
     */
    public static String read(Context activity, String fileName) throws IOException {
        if (fileName.contains("/0/")) {
            fileName = fileName.replace("/0/", "/legacy/");
        }
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
     * Read the contents of mContext file in the assets directory indicated by fileName
     */
    public static String readFromAssets(Context activity, String fileName) throws IOException {
        AssetManager am = activity.getAssets();
        return read(am.open(fileName));
    }

    /**
     * Read the contents of the file indicated by fileName
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

    // Read mContext file in the assets directory into mContext string
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
            MLog.e(TAG, e.toString());
        }
        return out;
    }

    // Write mContext string to mContext file
    public static String writeStringToFile(String url, String name, String code) {
        MLog.d(TAG, "Writing string to file name: " + name + " code: " + code);
        String filename = name.replaceAll("[^a-zA-Z0-9-_\\. ]", "_");
        String baseDir = url + File.separator + filename;
        File dir = new File(baseDir);
        dir.mkdirs();
        File f = new File(dir.getAbsoluteFile() + File.separator + "main.js");

        try {
            if (!f.exists()) {
                f.createNewFile();
            } else {
                // We should probably do something here to handle multiple file
                // cases
            }
            FileOutputStream fo = new FileOutputStream(f);
            byte[] data = code.getBytes();
            fo.write(data);
            fo.flush();
            fo.close();
        } catch (FileNotFoundException ex) {
            MLog.e(TAG, ex.toString());
        } catch (IOException e) {
            e.printStackTrace();
            MLog.e(TAG, e.toString());
        }
        return f.getAbsolutePath();
    }

    public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files) {
                if (file.contains(".")) {
                    res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
                } else {
                    res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
                }
            }
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
                String fullPath = ProjectManager.getInstance().getBaseDir() + path;
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                for (String asset : assets) {
                    copyFileOrDir(c, path + "/" + asset);
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
            String newFileName = ProjectManager.getInstance().getBaseDir() + filename;
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
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    public static void deleteFileDir(String path, String name) {
        String fullPath = path + "/" + name;
        MLog.d(TAG, "deleting directory " + fullPath);
        File dir = new File(fullPath);

        if (dir.isDirectory()) {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            MLog.d(TAG, "deleting directory " + dir.getAbsolutePath());
//			String[] children = dir.list();
//			for (String element : children) {
//				File f = new File(dir, element);
//				f.delete();
//				MLog.d(TAG, "deleting directory done" + f.getAbsolutePath());
//			}
        } else {
            dir.delete();
        }
        MLog.d(TAG, "deleting directory done" + name);
    }

    public static void deleteDir(File dir) {
        MLog.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                File temp = new File(dir, element);
                if (temp.isDirectory()) {
                    MLog.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                    deleteDir(temp);
                } else {
                    MLog.d("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false) {
                        MLog.d("DeleteRecursive", "DELETE FAIL");
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
        return new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + where);
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
            for (String string : strings) {
                writer.println(string);
            }
            writer.flush();
        } catch (UnsupportedEncodingException e) {
        } // will not happen
    }

    /*
     * Method borrowed from Processing PApplet.java
     */
    public static String[] loadStrings(String filename) {
        InputStream is = createInput(filename);
        if (is != null) {
            return loadStrings(is);
        }

        System.err.println("The file \"" + filename + "\" " + "is missing or inaccessible, make sure "
                + "the URL is valid or that the file has been " + "added to your sketch and is readable.");
        return null;
    }

    public static String loadFile(String filename) {
        String[] arr = loadStrings(filename);

        StringBuilder builder = new StringBuilder();
        for (String s : arr) {
            builder.append(s);
        }

        return builder.toString();
    }

    public static String loadCodeFromFile(String path) {

        String out = null;
        File f = new File(path);
        try {
            InputStream in = new FileInputStream(f);
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
            // Log.e("Project", e.toString());
        }
        return out;
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

        if (filename == null) {
            return null;
        }

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
     * Takes mContext path and creates any in-between folders if they don't already
     * exist. Useful when trying to save to mContext subfolder that may not actually
     * exist.
     */
    static public void createPath(String path) {
        createPath(new File(path));
    }

    static public void createPath(File file) {
        try {
            String parent = file.getParent();
            if (parent != null) {
                File unit = new File(parent);
                if (!unit.exists()) {
                    unit.mkdirs();
                }
            }
        } catch (SecurityException se) {
            System.err.println("You don't have permissions to create " + file.getAbsolutePath());
        }
    }

    static public void zipFolder(String src, String dst) throws Exception {
        File f = new File(dst);
        //make dirs if necessary
        f.getParentFile().mkdirs();

        ZipFile zipfile = new ZipFile(f.getAbsolutePath());
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        zipfile.addFolder(src, parameters);
    }

    static public void unZipFile(String src, String dst) throws ZipException {
        ZipFile zipFile = new ZipFile(src);
        zipFile.extractAll(dst);

    }

    static public void extractZip(String zipFile, String location) throws IOException {

        int size;
        int BUFFER_SIZE = 1024;

        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    public static File[] listFiles(String url, final String extension) {
        File f = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + url + File.separator);

        return f.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String fileName) {
                return fileName.endsWith(extension);
            }
        });

    }

    public static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }

        return extension;
    }

    public static void appendStrings(String fileName, String[] lines) {
        try {
            String fileUrl = ProjectManager.getInstance().getCurrentProject().getStoragePath() + File.separator
                    + fileName;
            File f = new File(fileUrl);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(f, true);

            for (String line : lines) {
                fo.write(line.getBytes());

            }
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
