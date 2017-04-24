package me.bkkn.translate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {

private static final String DB_NAME = "translator_db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "history_table";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FROM = "text_from_column";
    public static final String COLUMN_TO = "text_to_column";
    public static final String COLUMN_LANG = "language_column";
    public static final String COLUMN_FAV = "favorites_column";
    public static final String COLUMN_DATE = "date_column";

    private static final String DB_FAVORITES_TABLE = "favorites_table";
    public static final String COLUMN_FAVORITES_ID = "_id";
    public static final String COLUMN_FAVORITES_FROM = "text_from_column";
    public static final String COLUMN_FAVORITES_TO = "text_to_column";
    public static final String COLUMN_FAVORITES_LANGUAGE = "language_column";
    public static final String COLUMN_FAVORITES_DATE = "date_column";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_FROM + " text, " +
                    COLUMN_TO + " text, " +
                    COLUMN_LANG + " text, " +
                    COLUMN_FAV + " integer, " +
                    COLUMN_DATE + " date" +
                    ");";

    private static final String DB_FAV_CREATE =
            "create table " + DB_FAVORITES_TABLE + "(" +
                    COLUMN_FAVORITES_ID + " integer primary key autoincrement, " +
                    COLUMN_FAVORITES_FROM + " text, " +
                    COLUMN_FAVORITES_TO + " text, " +
                    COLUMN_FAVORITES_LANGUAGE + " text, " +
                    COLUMN_FAVORITES_DATE + " date" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, COLUMN_ID + " desc");
    }

    public Cursor getAllDataFav() {
        return mDB.query(DB_FAVORITES_TABLE, null, null, null, null, null, COLUMN_ID + " desc");
    }

    public void addRec(String txt1, String txt2, String txt3, int img) {
        ContentValues cv = new ContentValues();

        long date = System.currentTimeMillis();

        cv.put(COLUMN_FROM, txt1);
        cv.put(COLUMN_TO, txt2);
        cv.put(COLUMN_LANG, txt3);
        cv.put(COLUMN_FAV, img);
        cv.put(COLUMN_DATE, date);
        mDB.insert(DB_TABLE, null, cv);

        txt1 = txt1.replaceAll("'",",");
        txt2 = txt2.replaceAll("'",",");

        mDB.delete(DB_TABLE, COLUMN_FROM + " = '" + txt1 + "' and " + COLUMN_TO + " = '" + txt2 + "' and " +
                   COLUMN_LANG + " = '" + txt3 + "' and " + COLUMN_DATE + " < " + date, null);

    }

    public void addRecFav(String txt1, String txt2, String txt3) {
        ContentValues cv = new ContentValues();
        long date = System.currentTimeMillis();

        cv.put(COLUMN_FAVORITES_FROM, txt1);
        cv.put(COLUMN_FAVORITES_TO, txt2);
        cv.put(COLUMN_FAVORITES_LANGUAGE, txt3);
        cv.put(COLUMN_FAVORITES_DATE, date);
        mDB.insert(DB_FAVORITES_TABLE, null, cv);

        txt1 = txt1.replaceAll("'",",");
        txt2 = txt2.replaceAll("'",",");

        mDB.delete(DB_FAVORITES_TABLE, COLUMN_FAVORITES_FROM + " = '" + txt1 + "' and " + COLUMN_FAVORITES_TO + " = '" + txt2 + "' and " +
                COLUMN_FAVORITES_LANGUAGE + " = '" + txt3 + "' and " + COLUMN_FAVORITES_DATE + " < " + date, null);
    }

    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    public void delRecFav(long id) {
        mDB.delete(DB_FAVORITES_TABLE, COLUMN_ID + " = " + id, null);
    }

    public void trunc_table() {mDB.execSQL("delete from "+DB_TABLE+";"); }

    public void trunc_fav_table() {mDB.execSQL("delete from "+DB_FAVORITES_TABLE+";"); }


    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(DB_FAV_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
