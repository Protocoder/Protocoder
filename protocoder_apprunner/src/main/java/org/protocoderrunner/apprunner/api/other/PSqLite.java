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

package org.protocoderrunner.apprunner.api.other;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.project.ProjectManager;

import java.util.ArrayList;

public class PSqLite extends PInterface {

	String TAG = "PSqlite";
	private SQLiteDatabase db;

	public PSqLite(Context a, String dbName) {
		super(a);

		open(dbName);
		WhatIsRunning.getInstance().add(this);
	}


	@ProtoMethod(description = "Open a SQLite ", example = "")
	@ProtoMethodParam(params = { "dirName" })
	public void open(String dbName) {
		db = getContext().openOrCreateDatabase(
				ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + dbName, getContext().MODE_PRIVATE,
				null);

	}


	@ProtoMethod(description = "Executes a SQL sentence", example = "")
	@ProtoMethodParam(params = { "sql" })
	public void execSql(String sql) {
		db.execSQL(sql);
	}

	// http://stackoverflow.com/questions/8830753/android-sqlite-which-query-query-or-rawquery-is-faster

	@ProtoMethod(description = "Querys the database in the given array of columns and returns a cursor", example = "")
	@ProtoMethodParam(params = { "table", "colums[]" })
	public Cursor query(String table, String[] columns) {
		for (String column : columns) {
			//MLog.d("qq", column);

		}
		Cursor c = db.query(table, columns, null, null, null, null, null);

		return c;
	}


	@ProtoMethod(description = "Close the database connection", example = "")
	@ProtoMethodParam(params = {})
	public void close() {
		db.close();
	}


	@ProtoMethod(description = "Deletes a table in the database", example = "")
	@ProtoMethodParam(params = {"tableName"})
	public void delete(String table) {
		this.db.delete(table, null, null);

	}


    @ProtoMethod(description = "Exectutes SQL sentence in the database", example = "")
    @ProtoMethodParam(params = { "table", "fields" })
	public void insert(String table, ArrayList<DBDataType> fields) {

		String names = "";
		String values = "";

		for (int i = 0; i < fields.size(); i++) {
			if (i != fields.size() - 1) {
				names = names + " " + fields.get(i).name + ",";
				values = values + " " + fields.get(i).obj.toString();
			} else {
				names = names + "" + fields.get(i).name;
				values = values + " " + fields.get(i).obj.toString();
			}
		}

		Log.d(TAG, " " + names + " " + values);

		db.execSQL("INSERT INTO " + table + " (" + names + ")" + " VALUES (" + values + ")");

	}

	public void stop() {
		if (db != null) {
			db.close();
		}
	}

	public class DBDataType {

		String name;
		Object obj;

		public DBDataType(String name, Object obj) {
			this.name = name;
			this.obj = obj;

		}
	}

}
