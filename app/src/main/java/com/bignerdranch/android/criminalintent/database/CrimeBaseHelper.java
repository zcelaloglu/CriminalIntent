package com.bignerdranch.android.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by zafer on 21.12.15.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + "("
                + " _id integer primary key autoincrement, "
                + CrimeDbSchema.Cols.UUID + ", "
                + CrimeDbSchema.Cols.TITLE + ", "
                + CrimeDbSchema.Cols.DATE + ", "
                + CrimeDbSchema.Cols.SOLVED + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
