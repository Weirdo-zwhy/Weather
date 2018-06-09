package com.toosame.weather.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.widget.Toast;

public class CreateSQL extends SQLiteOpenHelper {
    public static final String CREATE_DIARY = "create table weather ("
            +"selectCode Integer primary key,"
            +"selectName text)"; //建立数据库表语句
    private Context mContext;
    public CreateSQL(Context context, String name, SQLiteDatabase.CursorFactory factory , int version){
        super(context,name,factory,version);
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY);//执行建表操作
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

