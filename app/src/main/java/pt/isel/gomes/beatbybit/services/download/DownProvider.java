package pt.isel.gomes.beatbybit.services.download;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Gomes on 25-05-2015.
 */
public class DownProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.provider.DownProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/data";
    static final Uri CONTENT_URI = Uri.parse(URL);
    String herp;
  /*  static final String c1 = "c1";
    static final String c2 = "c2";
    static final String c3 = "c3";
    static final String c4 = "c4";
    static final String c5 = "c5";
    static final String c6 = "c6";
    static final String date = "date";

    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;

    static final int STUDENTS = 1;
    static final int STUDENT_ID = 2;*/

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "data", 0);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String dbName = "data";
    static final String tableName = "current";
    static final int version = 1;
    static final String dbCreate =
            " CREATE TABLE " + tableName +
                    " (c1 TEXT NOT NULL," +
                    "c2 TEXT NOT NULL," +
                    "c3 TEXT NOT NULL," +
                    "c4 TEXT NOT NULL," +
                    "c5 TEXT NOT NULL," +
                    "c6 TEXT NOT NULL," +
                    "date TEXT NOT NULL);";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, dbName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(dbCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(tableName, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tableName);
        Cursor c = qb.query(db, null, null, null,
                null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = db.delete(tableName,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}