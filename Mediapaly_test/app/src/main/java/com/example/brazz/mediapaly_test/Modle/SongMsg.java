package com.example.brazz.mediapaly_test.Modle;

/**
 * Created by BrazZ on 2016/4/23.
 */
public class SongMsg {
    public String SongName;
    public String SingerName;
    public int ImageResource;
    public String MediaResource;
    public String LrcPath;
    public String PictureResource;
    public String Type;
    public boolean IsWebSong;


    public SongMsg(String SongName, String SingerName, int ImageResource, String MediaResource,boolean IsWebSong)
    {
        this.SongName = SongName;
        this.SingerName = SingerName;
        this.ImageResource = ImageResource;
        this.MediaResource = MediaResource;
        this.IsWebSong = IsWebSong;
    }

    public SongMsg(String SongName, String SingerName, String lrcPath, String MediaResource,String pictureResource,String Type,boolean IsWebSong)
    {
        this.SongName = SongName;
        this.SingerName = SingerName;
        this.LrcPath = lrcPath;
        this.MediaResource = MediaResource;
        this.PictureResource = pictureResource;
        this.Type = Type;
        this.IsWebSong = IsWebSong;
    }
}
