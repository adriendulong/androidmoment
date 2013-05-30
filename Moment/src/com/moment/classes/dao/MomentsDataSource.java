package com.moment.classes.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.moment.classes.Moment;
import com.moment.classes.SQLiteHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MomentsDataSource {

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private String[] dbColumns =
            {
                SQLiteHelper.COLUMN_ID,
                SQLiteHelper.COLUMN_NAME
            };

    public MomentsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Moment createMoment(String name) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_NAME, name);
        long insertId = db.insert(SQLiteHelper.TABLE_MOMENTS, null, values);
        Cursor cursor = db.query(SQLiteHelper.TABLE_MOMENTS, dbColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Moment newMoment = cursorToMoment(cursor);
        cursor.close();
        return newMoment;
    }

    public List<Moment> getAllMoments() {
        List<Moment> moments = new ArrayList<Moment>();

        Cursor cursor = db.query(SQLiteHelper.TABLE_MOMENTS, dbColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Moment moment = cursorToMoment(cursor);
            moments.add(moment);
            cursor.moveToNext();
        }
        cursor.close();
        return moments;
    }

    private Moment cursorToMoment(Cursor cursor) {
        Moment moment = new Moment();
        moment.setId(cursor.getInt(0));
        moment.setName(cursor.getString(1));
        return moment;
    }
}
