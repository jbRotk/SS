package com.example.brazz.mediapaly_test.Control.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;

import java.util.ArrayList;

/**
 * Created by BrazZ on 2016/6/5.
 */
public class RecentPlayControl {
    Context context;

    public RecentPlayControl(Context context)
    {
        this.context = context;
    }

    public ArrayList<SongMsg> QueryRecent()//返回所有的
    {
        ArrayList<SongMsg> songMsgs = new ArrayList<SongMsg>();
        SQLiteDatabase db = context.openOrCreateDatabase(RecentPlayDatabses.DatabaseName,Context.MODE_PRIVATE,null);
        Cursor cursor;
        try {
            cursor = db.query(RecentPlayDatabses.TableName,null,null,null,null,null,null);
            while (!cursor.isLast())
            {
                cursor.moveToNext();
                String SongName = cursor.getString(1);
                String Singer = cursor.getString(2);
                String Type = cursor.getString(3);
                SongMsg songMsg = new SongMsg(SongName,Singer, Constant.LrcPath,Constant.MP3Path,Constant.ImgPath,Type,false);
                songMsgs.add(0,songMsg);
            }
        }catch (Exception e){}
        return songMsgs;
    }

    public SongMsg QueryObject(String Name,String Singer)
    {
        SQLiteDatabase db = context.openOrCreateDatabase(RecentPlayDatabses.DatabaseName,Context.MODE_PRIVATE,null);
        Cursor cursor;
        try {
            cursor = db.query(RecentPlayDatabses.TableName,null,null,null,null,null,null);
            while (!cursor.isLast())
            {
                cursor.moveToNext();
                String SongName = cursor.getString(1);
                String SingerName = cursor.getString(2);
                String Type = cursor.getString(3);
                if(SongName.equals(Name) && Singer.equals(SingerName))
                {
                    SongMsg songMsg = new SongMsg(SongName,Singer, Constant.LrcPath,Constant.MP3Path,Constant.ImgPath,Type,false);
                    return songMsg;
                }
            }
        }catch (Exception e){}
        return null;
    }
    public void DeleteObject(String Name,String Singer)
    {
        SQLiteDatabase db = context.openOrCreateDatabase(RecentPlayDatabses.DatabaseName,Context.MODE_PRIVATE,null);
        String ID="";
        Cursor cursor;
        try {
            cursor = db.query(RecentPlayDatabses.TableName,null,null,null,null,null,null);
            while (!cursor.isLast())
            {
                cursor.moveToNext();
                String SongName = cursor.getString(1);
                String SingerName = cursor.getString(2);
                if(SongName.equals(Name) && Singer.equals(SingerName))
                {
                    ID = cursor.getString(0);
                    break;
                }
            }
            String[] args = {ID};
            db.delete(DownloadDatabases.TableName,"id=?",args);
        }catch (Exception e){}
    }

    public void InserObject(SongMsg songMsg)
    {
        SQLiteDatabase db = context.openOrCreateDatabase(RecentPlayDatabses.DatabaseName,Context.MODE_PRIVATE,null);
        // db.insert()
        ContentValues contentValues = new ContentValues();
        contentValues.put("SongName",songMsg.SongName);
        contentValues.put("SingerName",songMsg.SingerName);
        contentValues.put("Type",songMsg.Type);
        db.insert(RecentPlayDatabses.TableName,null,contentValues);
    }
}
