package com.lamkansing.matternote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by line on 11/26/14.
 */
public class NotebookDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "notebookdb";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTEBOOK_NAME = "notebookname";
    public static final String COLUMN_NOTE_CONTENT = "natecontent";
    public static final String COLUMN_COVER_COLOR = "color";
    public static final String COLUMN_LASTEDIT = "lastedit";

    private static final String DATABASE_NAME = "notebook.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NOTEBOOK_NAME + " text not null, " +
            COLUMN_NOTE_CONTENT + "  text not null, " +
            COLUMN_COVER_COLOR + "  integer not null, " +
            COLUMN_LASTEDIT + "  datetime not null);" ;


    public NotebookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NotebookDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
