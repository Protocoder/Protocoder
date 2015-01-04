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

package org.protocoderrunner.apprunner.api;

import android.content.Context;
import android.os.FileObserver;

import net.lingala.zip4j.exception.ZipException;

import org.mozilla.javascript.Scriptable;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PSqLite;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.FileIO;

import java.io.File;

public class PFileIO extends PInterface {

	String TAG = "PFileIO";
    private FileObserver fileObserver;

    public PFileIO(Context c) {
		super(c);
        WhatIsRunning.getInstance().add(this);
	}

	@ProtocoderScript
	@APIMethod(description = "Create a directory", example = "")
	@APIParam(params = { "dirName" })
	public void createDir(String name) {

		File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
		file.mkdirs();
	}

	@ProtocoderScript
	@APIMethod(description = "Delete a filename", example = "")
	@APIParam(params = { "fileName" })
	public void delete(String name) {
		FileIO.deleteFileDir(ProjectManager.getInstance().getCurrentProject().getStoragePath(), name);
	}

    @ProtocoderScript
    @APIMethod(description = "Save an array with text into a file", example = "")
    @APIParam(params = { "fileName", "lines[]" })
    public void saveStrings(String fileName, String[] lines) {
        FileIO.saveStrings(fileName, lines);
    }

	@ProtocoderScript
	@APIMethod(description = "Save a String into a file", example = "")
	@APIParam(params = { "fileName", "lines[]" })
	public void saveString(String fileName, String line) {
		String[] lines = { line };
		FileIO.saveStrings(fileName, lines);
	}

    @ProtocoderScript
	@APIMethod(description = "Append an array of text into a file", example = "")
	@APIParam(params = { "fileName", "lines[]" })
	public void appendString(String fileName, String[] lines) {
		FileIO.appendStrings(fileName, lines);
	}

	@ProtocoderScript
	@APIMethod(description = "Append a String into a file", example = "")
	@APIParam(params = { "fileName", "line" })
	public void appendString(String fileName, String line) {
		String[] lines = { line };
		FileIO.appendStrings(fileName, lines);
	}


	@ProtocoderScript
	@APIMethod(description = "Load the Strings of a text file into an array", example = "")
	@APIParam(params = { "fileName" })
	public String[] loadStrings(String fileName) {
        return FileIO.loadStrings(fileName);
	}

	@ProtocoderScript
	@APIMethod(description = "List all the files in the directory", example = "")
	@APIParam(params = { "url" })
	public Scriptable listFiles(String url) {
		return listFiles(url, "");
	}

	@ProtocoderScript
	@APIMethod(description = "List all the files with a given extension", example = "")
	@APIParam(params = { "fileName" })
	public Scriptable listFiles(String url, String filter) {

        File files[] = FileIO.listFiles(url, filter);
       // ProtocoderNativeArray filesNativeArray = new ProtocoderNativeArray(files.length);
        Scriptable filesNativeArray = AppRunnerSettings.get().newArray(files);



        //for (int i = 0; i < files.length; i++) {
        //    filesNativeArray.put(i, 0, files[i].getName());
            //filesNativeArray.addPE(i, files[i].getName());
        //}

		return filesNativeArray;
	}

	@ProtocoderScript
	@APIMethod(description = "Open a sqlite database", example = "")
	@APIParam(params = { "filename" })
	public PSqLite openSqlLite(String db) {
		return new PSqLite(getContext(), db);
	}

    public interface addZipUnzipCB {
        void event();
    }

    @ProtocoderScript
    @APIMethod(description = "Zip a file/folder into a zip", example = "")
    @APIParam(params = { "folder", "filename" })
    public void zip(String path, final String fDestiny, final addZipUnzipCB callbackfn) {
        final String fOrigin = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + path;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileIO.zipFolder(fOrigin, fDestiny);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callbackfn.event();
            }
        });
        t.start();

    }

    @ProtocoderScript
    @APIMethod(description = "Unzip a file into a folder", example = "")
    @APIParam(params = { "zipFile", "folder" })
    public void unzip(final String src, final String dst, final addZipUnzipCB callbackfn) {
        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileIO.unZipFile(projectPath + "/" + src, projectPath + "/" + dst);
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public interface FileObserverCB {
        public void event(String action, String data);
    }


    @ProtocoderScript
    @APIMethod(description = "Observer file changes in a folder", example = "")
    @APIParam(params = { "path", "function(action, file" })
    public void observeFolder(String path, final FileObserverCB callback) {

         fileObserver = new FileObserver(ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + path, FileObserver.CREATE | FileObserver.MODIFY |  FileObserver.DELETE) {

             @Override
             public void onEvent(int event, String file) {

                 if ((FileObserver.CREATE & event) != 0) {
                     callback.event("create", file);
                 } else if ((FileObserver.DELETE & event) != 0) {
                     callback.event("delete", file);
                 } else if ((FileObserver.MODIFY & event) != 0) {
                     callback.event("modify", file);
                 }
             }

         };
        fileObserver.startWatching();
    }

    public void stop() {
        if (fileObserver != null) {
            fileObserver.stopWatching();
            fileObserver = null;
        }
    }
}