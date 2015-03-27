package org.protocoder;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;

import java.util.ArrayList;

public class ProtoContentProvider extends ContentProvider {
    static final String TAG = "ProtoContentProvider";

    private static final String[] COLUMNS = new String[]{
            "_id",
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };


    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        return true;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("insert not supported");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("insert not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("insert not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("delete not supported");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query with uri: " + uri.toString());

        String query = uri.getLastPathSegment();

        MatrixCursor cursor = new MatrixCursor(COLUMNS);

        ArrayList<Project> list = ProjectManager.getInstance().list(ProjectManager.FOLDER_EXAMPLES, true);
        for (Project project : list) {
            addRow(cursor, project.getName());
        }

        return cursor;

    }

    private void addRow(MatrixCursor cursor, String string) {
        long id = cursor.getCount();
        cursor.newRow().add(id).add(string);
    }

}