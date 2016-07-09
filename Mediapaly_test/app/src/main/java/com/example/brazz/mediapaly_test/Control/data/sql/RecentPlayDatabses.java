package com.example.brazz.mediapaly_test.Control.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by BrazZ on 2016/6/5.
 */
public class RecentPlayDatabses extends SQLiteOpenHelper {
    static String DatabaseName = "RecentPlay";
    static String TableName = "RecentPlay";
    static String ID = "id";
    static String SongName = "SongName";
    static String SingerName = "SingerName";
    static String Type = "Type";
    Context context;

    public RecentPlayDatabses(Context context)
    {
        super(context,DatabaseName,null,1);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+TableName+" ( "+ID+" INTEGER primary key autoincrement, "+SongName+" text not null, "+SingerName+" text not null, "+Type+" text not null ); ";
        db.execSQL(sql);
        Toast.makeText(context, "RecentDatabases OK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
