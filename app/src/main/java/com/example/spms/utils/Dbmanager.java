package com.example.spms.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Dbmanager {

    private static MySqliteHelper helper;
    public static MySqliteHelper getInstance(Context context){
        if(helper==null){
            helper=new MySqliteHelper(context);
        }
        return helper;
    }
    public static void execSQL(SQLiteDatabase db, String sql){
        if(db!=null){
            if(sql!=null&&"".equals(sql)) {
                db.execSQL(sql);
            }
        }
    }
}
