package com.moment.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper{

    public static final String TABLE_MOMENTS = "moments";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String DATABASE_NAME = "moment.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_CREATE = "create table "
            + TABLE_MOMENTS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOMENTS);
        onCreate(db);
    }
}
