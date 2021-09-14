package com.sian0412.privatelocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "location.bd";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_TABLE_SQL =
            "CREATE TABLE " +
                    "location_list (_id INTEGER PRIMARY KEY, name TEXT, address TEXT, " +
                    "phone TEXT, remark TEXT);";
    public static final String DROP_TABLE_SQL =
            "DROP TABLE IF EXISTS location_list";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onDropTable(db);
        onCreate(db);
    }

    private void onDropTable(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_SQL);
    }
}
