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

import org.apache.commons.io.FileUtils;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.other.PSqLite;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.utils.FileIO;

import java.io.File;
import java.io.IOException;

public class PFileIO extends PInterface {

	String TAG = "PFileIO";
    private FileObserver fileObserver;

    public PFileIO(Context c) {
		super(c);
        WhatIsRunning.getInstance().add(this);
	}


	@ProtoMethod(description = "Create a directory", example = "")
	@ProtoMethodParam(params = { "dirName" })
	public void createDir(String name) {
		File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
		file.mkdirs();
	}


	@ProtoMethod(description = "Delete a filename", example = "")
	@ProtoMethodParam(params = { "fileName" })
	public void delete(String name) {
		FileIO.deleteFileDir(ProjectManager.getInstance().getCurrentProject().getStoragePath(), name);
	}



    @ProtoMethod(description = "Get 1 is is a file, 2 if is a directory and -1 if the file doesnt exist", example = "")
    @ProtoMethodParam(params = { "fileName" })
    public int type (String name) {
        int ret = 0;

        File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);

        if (!file.exists()) ret = -1;
        else if (file.isFile()) ret = 1;
        else if (file.isDirectory()) ret = 2;

        return ret;
    }




    @ProtoMethod(description = "Move a file to a directory", example = "")
    @ProtoMethodParam(params = { "name", "destination" })
    public void moveFileToDir(String name, String to) {
        File fromFile = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        File dir = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + to);

        dir.mkdirs();
        try {
            FileUtils.moveFileToDirectory(fromFile, dir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Move a directory to another directory", example = "")
    @ProtoMethodParam(params = { "name", "destination" })
    public void moveDirToDir(String name, String to) {
        File fromDir = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        File dir = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + to);

        dir.mkdirs();
        try {
            FileUtils.moveDirectoryToDirectory(fromDir, dir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Copy a file or directory", example = "")
    @ProtoMethodParam(params = { "name", "destination" })
    public void copyFileToDir(String name, String to) {
        File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        File dir = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + to);
        dir.mkdirs();

        try {
            FileUtils.copyFileToDirectory(file, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Copy a file or directory", example = "")
    @ProtoMethodParam(params = { "name", "destination" })
    public void copyDirToDir(String name, String to) {
        File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        File dir = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + to);
        dir.mkdirs();

        try {
            FileUtils.copyDirectoryToDirectory(file, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Rename a file or directory", example = "")
    @ProtoMethodParam(params = { "name", "destination" })
    public void rename(String oldName, String newName) {
        //File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        //file.mkdirs();


        File origin = new File(ProjectManager.getInstance().getCurrentProject().getStoragePath() + File.separator + oldName);
        String path = origin.getParentFile().toString();

        origin.renameTo(new File(path + File.separator + newName));
//
//        MLog.d(TAG, path);
//        String file = origin.getAbsoluteFile().toString();
//
//        if (origin.isDirectory()) {
//            MLog.d(TAG, "is dir");
//
//            try {
//                FileUtils.moveDirectory(origin, new File(path + File.separator + newName));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            MLog.d(TAG, "is not dir");
//
//            try {
//                File to = new File(path + File.separator + newName);
//
//                FileUtils.moveFileToDirectory(origin, to, false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


    @ProtoMethod(description = "Move a file or directory", example = "")
    @ProtoMethodParam(params = { "name", "destination" })
    public void createEmptyFile(String name) {
        File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Save an array with text into a file", example = "")
    @ProtoMethodParam(params = { "fileName", "lines[]" })
    public void saveStrings(String fileName, String[] lines) {
        FileIO.saveStrings(fileName, lines);
    }


	@ProtoMethod(description = "Save a String into a file", example = "")
	@ProtoMethodParam(params = { "fileName", "lines[]" })
	public void saveString(String fileName, String line) {
		String[] lines = { line };
		FileIO.saveStrings(fileName, lines);
	}


	@ProtoMethod(description = "Append an array of text into a file", example = "")
	@ProtoMethodParam(params = { "fileName", "lines[]" })
	public void appendString(String fileName, String[] lines) {
		FileIO.appendStrings(fileName, lines);
	}


	@ProtoMethod(description = "Append a String into a file", example = "")
	@ProtoMethodParam(params = { "fileName", "line" })
	public void appendString(String fileName, String line) {
		String[] lines = { line };
		FileIO.appendStrings(fileName, lines);
	}



	@ProtoMethod(description = "Load the Strings of a text file into an array", example = "")
	@ProtoMethodParam(params = { "fileName" })
	public String[] loadStrings(String fileName) {
        return FileIO.loadStrings(AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName);
	}


	@ProtoMethod(description = "List all the files in the directory", example = "")
	@ProtoMethodParam(params = { "url" })
	public File[] listFiles(String url) {
		return listFiles(url, "");
	}


	@ProtoMethod(description = "List all the files with a given extension", example = "")
	@ProtoMethodParam(params = { "fileName" })
	public File[] listFiles(String url, String filter) {
        File files[] = FileIO.listFiles(url, filter);
       // ProtocoderNativeArray filesNativeArray = new ProtocoderNativeArray(files.length);
       // Scriptable filesNativeArray = AppRunnerSettings.get().newArray(files);



        //for (int i = 0; i < files.length; i++) {
        //    filesNativeArray.put(i, 0, files[i].getName());
            //filesNativeArray.addPE(i, files[i].getName());
        //}

		return files;
	}


	@ProtoMethod(description = "Open a sqlite database", example = "")
	@ProtoMethodParam(params = { "filename" })
	public PSqLite openSqlLite(String db) {
		return new PSqLite(getContext(), db);
	}

    public interface addZipUnzipCB {
        void event();
    }


    @ProtoMethod(description = "Zip a file/folder into a zip", example = "")
    @ProtoMethodParam(params = { "folder", "filename" })
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


    @ProtoMethod(description = "Unzip a file into a folder", example = "")
    @ProtoMethodParam(params = { "zipFile", "folder" })
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



    @ProtoMethod(description = "Observer file changes in a folder", example = "")
    @ProtoMethodParam(params = { "path", "function(action, file" })
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