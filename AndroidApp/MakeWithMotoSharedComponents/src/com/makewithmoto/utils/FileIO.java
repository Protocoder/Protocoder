/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makewithmoto.utils;

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

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.makewithmoto.base.BaseMainApp;
import com.makewithmoto.network.ALog;

/**
 * Helpers for doing basic file IO.
 * 
 * @author yariv
 *
 */
public class FileIO {

    private static final String TAG = "FILEIO";

    /**
     * Write the data to the file indicate by fileName. The file is created
     * if it doesn't exist.
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
        if (fileName.contains("/0/")) fileName = fileName.replace("/0/", "/legacy/");
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
     * Read the contents of a file in the assets directory
     * indicated by fileName
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
            } catch (IOException ex) {}
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
        File f = new File(dir.getAbsoluteFile() + File.separator + "script.js");

        try {
            if (!f.exists()){
                f.createNewFile();
                Log.d(TAG, "New file is being created!");
            } else {
                Log.d(TAG, "The file already exists!");
                //We should probably do something here to handle multiple file cases
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

}
