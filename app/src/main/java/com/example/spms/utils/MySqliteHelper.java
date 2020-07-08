package com.example.spms.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySqliteHelper extends SQLiteOpenHelper {

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MySqliteHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "create table " + Constant.TABLE_NAME_CHECKHEAD + "(" + Constant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Constant.CHECKJOB_ID + " varchar(20))";
        String sql2 = "create table " + Constant.TABLE_NAME_CHECKDETAIL + "(" + Constant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Constant.ITEM_ID + " Integer," + Constant.REQUESTNUMBER + " Integer," + Constant.ACTUALNUMBER + " Integer,"+Constant.CHECKJOB_ID_COPY+" varchar(20))";
        db.execSQL(sql1);
        db.execSQL(sql2);
        Log.i("tag", "-----创建成功-----");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
//        Log.i("tag", "-----onopen成功-----");
        super.onOpen(db);
    }
}
