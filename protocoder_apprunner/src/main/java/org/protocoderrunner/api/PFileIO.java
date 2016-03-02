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

package org.protocoderrunner.api;

import android.os.FileObserver;

import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.AppRunner;
import org.protocoderrunner.PInterface;
import org.protocoderrunner.api.other.PSqLite;
import org.protocoderrunner.base.utils.FileIO;
import org.protocoderrunner.models.Project;

import java.io.File;
import java.io.IOException;

public class PFileIO extends PInterface {

    private final Project mCurrentProject;
    String TAG = "PFileIO";
    private FileObserver fileObserver;

    public PFileIO(AppRunner appRunner) {
        super(appRunner);
        mCurrentProject = getAppRunner().project;
    }


    @ProtoMethod(description = "Create a directory", example = "")
    @ProtoMethodParam(params = {"dirName"})
    public void createDir(String name) {
        File file = new File(mCurrentProject.getFullPath() + name);
        file.mkdirs();
    }

    @ProtoMethod(description = "Delete a filename", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public void delete(String name) {
        FileIO.deleteFileDir(mCurrentProject.getFullPath(), name);
    }


    @ProtoMethod(description = "Get 1 is is a file, 2 if is a directory and -1 if the file doesnt exist", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public int type(String name) {
        int ret = 0;

        File file = new File(mCurrentProject.getFullPath() + name);

        if (!file.exists()) ret = -1;
        else if (file.isFile()) ret = 1;
        else if (file.isDirectory()) ret = 2;

        return ret;
    }


    @ProtoMethod(description = "Move a file to a directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void moveFileToDir(String name, String to) {
        File fromFile = new File(mCurrentProject.getFullPath() + name);
        File dir = new File(mCurrentProject.getFullPath() + to);

        dir.mkdirs();
        try {
            FileUtils.moveFileToDirectory(fromFile, dir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Move a directory to another directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void moveDirToDir(String name, String to) {
        File fromDir = new File(mCurrentProject.getFullPath() + name);
        File dir = new File(mCurrentProject.getFullPath() + to);

        dir.mkdirs();
        try {
            FileUtils.moveDirectoryToDirectory(fromDir, dir, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Copy a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void copyFileToDir(String name, String to) {
        File file = new File(mCurrentProject.getFullPath() + name);
        File dir = new File(mCurrentProject.getFullPath() + to);
        dir.mkdirs();

        try {
            FileUtils.copyFileToDirectory(file, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Copy a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void copyDirToDir(String name, String to) {
        File file = new File(mCurrentProject.getFullPath() + name);
        File dir = new File(mCurrentProject.getFullPath() + to);
        dir.mkdirs();

        try {
            FileUtils.copyDirectoryToDirectory(file, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //TODO reenable this
    @ProtoMethod(description = "Rename a file or directory", example = "")
    @ProtoMethodParam(params = {"name", "destination"})
    public void rename(String oldName, String newName) {
        //File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
        //file.mkdirs();

        File origin = new File(mCurrentProject.getFullPath() + oldName);
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
    @ProtoMethodParam(params = {"name", "destination"})
    public void createEmptyFile(String name) {
        File file = new File(mCurrentProject.getFullPath() + name);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ProtoMethod(description = "Save an array with text into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "lines[]"})
    public void saveStrings(String fileName, String[] lines) {
        FileIO.saveStrings(fileName, lines);
    }


    @ProtoMethod(description = "Save a String into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "lines[]"})
    public void saveString(String fileName, String line) {
        String[] lines = {line};
        FileIO.saveStrings(fileName, lines);
    }


    @ProtoMethod(description = "Append an array of text into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "lines[]"})
    public void appendString(String fileName, String[] lines) {
        FileIO.appendStrings(fileName, lines);
    }


    @ProtoMethod(description = "Append a String into a file", example = "")
    @ProtoMethodParam(params = {"fileName", "line"})
    public void appendString(String fileName, String line) {
        String[] lines = {line};
        FileIO.appendStrings(fileName, lines);
    }


    @ProtoMethod(description = "Load the Strings of a text file into an array", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public String[] loadStrings(String fileName) {
        return FileIO.loadStrings(mCurrentProject.getFullPath() + fileName);
    }


    @ProtoMethod(description = "List all the files in the directory", example = "")
    @ProtoMethodParam(params = {"url"})
    public File[] listFiles(String url) {
        return listFiles(url, "");
    }


    @ProtoMethod(description = "List all the files with a given extension", example = "")
    @ProtoMethodParam(params = {"fileName"})
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
    @ProtoMethodParam(params = {"filename"})
    public PSqLite openSqlLite(String db) {
        return new PSqLite(getAppRunner(), db);
    }

    public interface addZipUnzipCB {
        void event();
    }

    @ProtoMethod(description = "Zip a file/folder into a zip", example = "")
    @ProtoMethodParam(params = {"folder", "filename"})
    public void zip(String path, final String fDestiny, final addZipUnzipCB callbackfn) {
        final String fOrigin = mCurrentProject.getFullPath();
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
    @ProtoMethodParam(params = {"zipFile", "folder"})
    public void unzip(final String src, final String dst, final addZipUnzipCB callbackfn) {
        final String projectPath = mCurrentProject.getFullPath();
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
    @ProtoMethodParam(params = {"path", "function(action, file"})
    public void observeFolder(String path, final FileObserverCB callback) {

        fileObserver = new FileObserver(mCurrentProject.getFullPath() + path, FileObserver.CREATE | FileObserver.MODIFY | FileObserver.DELETE) {

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