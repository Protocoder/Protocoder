package com.makewithmoto.apprunner;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import android.app.Activity;
import android.os.Environment;

/**
 * Helper class for file handling.
 * @author Mikael Kindborg
 * Email: mikael.kindborg@gmail.com
 * Blog: divineprogrammer@blogspot.com
 * Twitter: @divineprog
 * Copyright (c) Mikael Kindborg 2010
 * Source code license: MIT
 */
public class ScriptFileHandler 
{
    private ScriptFileHandler()
    {
    }
    
    public static ScriptFileHandler create()
    {
        return new ScriptFileHandler();
    }
    
    public String readStringFromFileOrUrl(String fileOrUrl) throws Exception
    {
        if (fileOrUrl.startsWith("http://"))
        {
            return readString(openUrl(fileOrUrl));
        }
        else
        {
            return readString(openExternalStorageFileInputStream(fileOrUrl));
        }
    }

    public void writeStringToFile(String file, String data) throws Exception
    {
          writeString(openExternalStorageFileOutputStream(file), data);
    }
    
    public String readStringFromApplicationFile(Activity activity, String filename) throws Exception
    {
         return readString(openApplicationFileInputStream(activity, filename));
    }
    
    public InputStream openApplicationFileInputStream(Activity activity, String filename) throws Exception
    {
        return activity.openFileInput(filename);
    }

    public InputStream openExternalStorageFileInputStream(String filename) throws Exception
    {
        // Might be useful: content://com.android.htmlfileprovider/sdcard/example/file.html
        return new FileInputStream(
                new File(Environment.getExternalStorageDirectory() + "/" + filename));
    }

    public OutputStream openExternalStorageFileOutputStream(String filename) throws Exception
    {
        return new FileOutputStream(
            new File(Environment.getExternalStorageDirectory() + "/" + filename));
    }
    
    public boolean externalStorageFileExists(String filename) throws Exception
    {
        return new File(Environment.getExternalStorageDirectory() + "/" + filename).exists();
    }
    
    public boolean externalStorageCreateDirectory(String path) throws Exception
    {
        return new File(Environment.getExternalStorageDirectory() + "/" + path).mkdirs();
    }
    
    public void installFile(String file, String url)  throws Exception
    {
        writeStringToFile(file, readStringFromFileOrUrl(url));
    }
    
    public InputStream openUrl(String url) throws Exception
    {
            return new URL(url).openStream();
    }
    
    public String readString(InputStream stream) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer dataBuf = new StringBuffer();
        while (true)
        {
            String data = reader.readLine();
            if (null == data)  { break; }
            dataBuf.append(data + "\n");
        }
        reader.close();
        return dataBuf.toString();
    }

    public void writeString(OutputStream stream, String data) throws Exception
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        writer.write(data);
        writer.close();
    }
    
    public String readStringRaw(InputStream stream) throws Exception
    {
        BufferedInputStream bufIn = new BufferedInputStream(stream);
        StringBuffer dataBuf = new StringBuffer();
        while (true)
        {
            int data = bufIn.read();
            if (data == -1)
            {
                break;
            }
            else
            {
                dataBuf.append((char) data);
            }
        }
        
        bufIn.close();
        stream.close();
        
        return dataBuf.toString();
    }
}
