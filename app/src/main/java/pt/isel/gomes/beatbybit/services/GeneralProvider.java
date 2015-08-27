package pt.isel.gomes.beatbybit.services;

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

import pt.isel.gomes.beatbybit.util.Engine;


public class GeneralProvider extends ContentProvider {

    private static Engine engine = Engine.getInstance();
    static final String dbName = "data";
    static final int fileTable = 0, maleTable = 1, femaleTable = 2;
    static final String fileCreate = " CREATE TABLE " + "fileTable" + " (ecg TEXT NOT NULL," + " tags TEXT NOT NULL); ";
    static final String maleCreate = " CREATE TABLE " + "maleTable" + " (age INTEGER NOT NULL, " + "vgood INTEGER NOT NULL, " + "avgmax INTEGER NOT NULL, " + "avgmin INTEGER NOT NULL, " + "vbad INTEGER NOT NULL); ";
    static final String femaleCreate = " CREATE TABLE " + "femaleTable" + " (age INTEGER NOT NULL, " + "vgood INTEGER NOT NULL, " + "avgmax INTEGER NOT NULL, " + "avgmin INTEGER NOT NULL, " + " vbad INTEGER NOT NULL); ";
    static final int version = 1;
    private static final String URL = "content://" + "com.example.provider.GeneralProvider";
    private static final Uri CONTENT_URI = Uri.parse(URL);
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(engine.getProvider(), "fileTable", fileTable);
        uriMatcher.addURI(engine.getProvider(), "maleTable", maleTable);
        uriMatcher.addURI(engine.getProvider(), "femaleTable", femaleTable);

    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = getType(uri);
        long rowID = db.insert(table, "", values);
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
        String table = getType(uri);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(table);
        Cursor c = qb.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        String table;
        switch (uriMatcher.match(uri)) {
            case fileTable:
                table = "fileTable";
                break;
            case maleTable:
                table = "maleTable";
                break;
            case femaleTable:
                table = "femaleTable";
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return table;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = getType(uri);
        int count = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, dbName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(fileCreate);
            db.execSQL(maleCreate);
            db.execSQL(femaleCreate);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + fileTable);
            db.execSQL("DROP TABLE IF EXISTS " + maleTable);
            db.execSQL("DROP TABLE IF EXISTS " + femaleTable);
            onCreate(db);
        }
    }

}