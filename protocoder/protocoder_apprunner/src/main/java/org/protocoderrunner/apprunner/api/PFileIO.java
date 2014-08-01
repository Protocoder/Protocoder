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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.apprunner.api.other.PSqlLite;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.FileIO;

import android.app.Activity;

import net.lingala.zip4j.exception.ZipException;

public class PFileIO extends PInterface {

	String TAG = "JFileIO";

	public PFileIO(Activity a) {
		super(a);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "dirName" })
	public void createDir(String name) {

		File file = new File(AppRunnerSettings.get().project.getStoragePath() + File.separator + name);
		file.mkdirs();
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName" })
	public void remove(String name) {
		FileIO.deleteFileDir(ProjectManager.getInstance().getCurrentProject().getStoragePath(), name);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName", "lines[]" })
	public void saveString(String fileName, String line) {
		String[] lines = { line };
		FileIO.saveStrings(fileName, lines);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName", "lines[]" })
	public void appendString(String fileName, String[] lines) {
		FileIO.appendStrings(fileName, lines);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName", "line" })
	public void appendString(String fileName, String line) {
		String[] lines = { line };
		FileIO.appendStrings(fileName, lines);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName", "lines[]" })
	public void saveStrings(String fileName, String[] lines) {
		FileIO.saveStrings(fileName, lines);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName" })
	public String[] loadStrings(String fileName) {
		return FileIO.loadStrings(fileName);
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName" })
	public NativeArray listFiles() {
		return listFiles("");
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName" })
	public NativeArray listFiles(String filter) {

        File files[] = FileIO.listFiles(filter);
        NativeArray filesNativeArray = new NativeArray(files.length);
        for (int i = 0; i < files.length; i++) {
            filesNativeArray.put(i, filesNativeArray, files[i].getName());
        }

		return filesNativeArray;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "fileName" })
	public String[] listFilesInDir() {
		return null;
	}

	@ProtocoderScript
	@APIMethod(description = "", example = "")
	@APIParam(params = { "filename" })
	public PSqlLite openSqlLite(String db) {
		return new PSqlLite(a.get(), db);
	}

    public interface addZipUnzipCB {
        void event(String data);
    }

    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "filename" })
    public void zip(String path, String fDestiny, final addZipUnzipCB callbackfn) {
        String fOrigin = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + path;
        try {
            FileIO.zipFolder(fOrigin, fDestiny);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ProtocoderScript
    @APIMethod(description = "", example = "")
    @APIParam(params = { "filename" })
    public void unzip(String src, String dst, final addZipUnzipCB callbackfn) {
        String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();
        try {
            FileIO.unZipFile(projectPath + "/" + src, projectPath + "/" + dst);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
