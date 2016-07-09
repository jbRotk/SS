package com.example.brazz.mediapaly_test.Control.data.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.example.brazz.mediapaly_test.Activity.SearchMp3;
import com.example.brazz.mediapaly_test.Control.data.sql.RecentPlayControl;
import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Mp3Play_Service extends Service{


    public static final int OrderPlay = 1;
    public static  final  int RandomPlay =2;
    public static  final int SinglePlay = 3;

    private ArrayList<SongMsg> songMsgs;

    private int playing = 0;

    private int playBy = Mp3Play_Service.OrderPlay;


    private MediaPlayer mediaPlayer = new MediaPlayer();
    public Mp3Play_Service() {
    }

    public int get()
    {
        return 1;
    }

    public MediaPlayer getMediapaly()
    {
        return mediaPlayer;
    }

    @Override
    public IBinder onBind(Intent intent) {


        try
        {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();

                    RecentPlayControl recentPlayControl = new RecentPlayControl(Mp3Play_Service.this);
                    recentPlayControl.InserObject(songMsgs.get(playing));
                    SearchMp3.DowloadLrcTask dowloadLrcTask = new SearchMp3.DowloadLrcTask();
                    File song = new File(Constant.LrcPath+songMsgs.get(playing).SongName+".lrc");
                    if(! song.exists())
                    {
                        dowloadLrcTask.execute(songMsgs.get(playing).LrcPath,Constant.LrcPath+songMsgs.get(playing).SongName+".lrc");
                    }
                    File pic = new File(Constant.ImgPath+songMsgs.get(playing).SongName+".png");
                    if(! pic.exists())
                    {
                        SearchMp3.DowloadLrcTask dowloadPic =  new SearchMp3.DowloadLrcTask();
                        dowloadPic.execute(songMsgs.get(playing).PictureResource,Constant.ImgPath+songMsgs.get(playing).SongName+".png");
                    }
                }
            });
        }
        catch (Exception e){}
        return new Mp3PlayBinder();
    }
    public class Mp3PlayBinder extends Binder
    {
        public Mp3Play_Service getMp3Play_Service()
        {
            return Mp3Play_Service.this;
        }
    }


    public void playUrl(String videoUrl)
    {
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                System.out.println(mediaPlayer.getCurrentPosition());
            }
        });
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();//prepare之后自动播放
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void NextSong()
    {
        switch (playBy)
        {
            case Mp3Play_Service.OrderPlay: OrderSong();break;
            case Mp3Play_Service.RandomPlay: RandomSong();break;
            case Mp3Play_Service.SinglePlay: SingleSong();break;
        }

    }

    public void OrderSong()//顺序播放
    {
        try
        {
            mediaPlayer.reset();
        }
        catch (Exception e){}

        if(playing == songMsgs.size()-1)
        {
            playing = -1;
        }
        playing++;
        if(songMsgs.get(playing).IsWebSong)
        {
            playUrl(songMsgs.get(playing).MediaResource);
        }
        else
        {
            try {
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(songMsgs.get(playing).MediaResource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }

    public void SingleSong()//单曲循环
    {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songMsgs.get(playing).MediaResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void PreviewSong()//上一首
    {
        try
        {
            mediaPlayer.reset();
        }
        catch (Exception e){}
        if(playing == 0)
        {
            playing = songMsgs.size();
        }
        playing--;
        if(songMsgs.get(playing).IsWebSong)
        {
            playUrl(songMsgs.get(playing).MediaResource);
        }
        else
        {
            try {
                mediaPlayer.setDataSource(songMsgs.get(playing).MediaResource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }

    public void RandomSong()//随机播放
    {
        try
        {
            mediaPlayer.reset();
        }
        catch (Exception e){}
        getRandom();
        if(songMsgs.get(playing).IsWebSong)
        {
            playUrl(songMsgs.get(playing).MediaResource);
        }
        else
        {
            try {
                mediaPlayer.setDataSource(songMsgs.get(playing).MediaResource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }

    public void Pause()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
        else mediaPlayer.start();

    }


    private int getRandom()
    {
        int random = (int)(Math.random()*(songMsgs.size()-1));
        while (random == playing)
        {
            random = (int)(Math.random()*(songMsgs.size()-1));
        }
        playing = random;
        return playing;
    }


    public void setPlayBy(int PLAYBY)
    {
        playBy = PLAYBY;
    }
    public void setPlaying(int position)
    {
        playing = position;
    }
    public int getPlaying()
    {
        return playing;
    }

    public void setSongMsgs(ArrayList<SongMsg> songMsgs)
    {
        this.songMsgs = songMsgs;
    }

    public ArrayList<SongMsg> getSongMsgs()
    {
        return songMsgs;
    }
}
