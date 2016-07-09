package com.example.brazz.mediapaly_test.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.brazz.mediapaly_test.Control.data.Service.Mp3Play_Service;
import com.example.brazz.mediapaly_test.Control.data.sql.DownloadDatabases;
import com.example.brazz.mediapaly_test.Control.data.sql.RecentPlayDatabses;
import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.R;

import java.io.File;

public class MainMp3 extends AppCompatActivity implements View.OnClickListener{
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_RUN = "first";
    private boolean first;


    protected SeekBar Mp3Progress;


    protected TextView currentime;


    protected TextView durration;

    private RelativeLayout localmusic;//本地音乐
    private RelativeLayout download;//下载管理
    private RelativeLayout musiclics;//乐库
    private RelativeLayout recentplay;//最近播放

    private ImageView searchBtn;
    private EditText searchText;
    private TextView Songname;
    private TextView Singer;
    private ImageView NextSong;
    private ImageView PauseSong;
    private RelativeLayout songindex;
    private ImageView songpic;

    private Mp3Play_Service mp3Play_service;
    private MediaPlayer mediaPlayer;
    private RequestQueue requestQueue;

    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mp3Play_service = ((Mp3Play_Service.Mp3PlayBinder)service).getMp3Play_Service();

            mediaPlayer = mp3Play_service.getMediapaly();
            thread.start();



            PauseSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp3Play_service.getMediapaly().isPlaying()) {
                        PauseSong.setBackgroundResource(R.drawable.playmusic);
                    } else {
                        PauseSong.setBackgroundResource(R.drawable.stop);
                    }
                    mp3Play_service.Pause();
                }
            });
            NextSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PauseSong.setBackgroundResource(R.drawable.stop);
                    mp3Play_service.NextSong();
                    Songname.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
                    Singer.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);
                    File pic = new File(Constant.ImgPath + mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName + ".png");
                    if (pic.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                        songpic.setImageBitmap(bitmap);
                    } else {
                        try {
                            com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource, requestQueue, songpic);
                        } catch (Exception e) {
                            Toast.makeText(MainMp3.this,"很抱歉，这张图片飞了！",Toast.LENGTH_SHORT).show();}
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            thread.interrupt();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mp3);
        IsFirst();
        init();

        DownloadDatabases downloadDatabases = new DownloadDatabases(MainMp3.this);
        RecentPlayDatabses recentPlayDatabses = new RecentPlayDatabses(MainMp3.this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        try {
            Songname.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
            Singer.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);

            if(mp3Play_service.getMediapaly().isPlaying())
            {
                PauseSong.setBackgroundResource(R.drawable.stop);
            }
            else {
                PauseSong.setBackgroundResource(R.drawable.playmusic);
            }
        }
        catch (Exception e){}
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
            try {
                File pic = new File(Constant.ImgPath+mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName+".png");
                if(pic.exists())
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                    songpic.setImageBitmap(bitmap);
                }
                else
                {
                    com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource,requestQueue,songpic);
                }
            Songname.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
            Singer.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);
            if(mp3Play_service.getMediapaly().isPlaying())
            {
                PauseSong.setBackgroundResource(R.drawable.stop);
            }
            else {
                PauseSong.setBackgroundResource(R.drawable.playmusic);
            }
        }
        catch (Exception e){}
    }

    final int milliseconds = 1000;
    Thread thread = new Thread(){//开线程
        @Override
        public void run() {
            super.run();
            while (true)
            {
                try {
                    Message message = new Message();
                    message.arg1 = 1;
                    mHandler.sendMessage(message);
                    sleep(milliseconds);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 == 1)
            {
                try {
                    int time = mediaPlayer.getCurrentPosition()/1000;
                    int position = mediaPlayer.getCurrentPosition();

                    int time1 = mediaPlayer.getDuration();
                    int max =Mp3Progress.getMax();

                    Mp3Progress.setProgress(position*max/time1);
                }
                catch (Exception e){}
            }
        }
    };


    private void init()
    {
        //ButterKnife.inject(this);
        Mp3Progress = (SeekBar)findViewById(R.id.seekBar2);
        currentime = (TextView)findViewById(R.id.mediacurrentime);
        durration = (TextView)findViewById(R.id.mediaduration);
        searchBtn = (ImageView)findViewById(R.id.searchView);
        searchText = (EditText)findViewById(R.id.SearcheditText);
        Songname = (TextView)findViewById(R.id.SongName);
        Singer = (TextView)findViewById(R.id.SingerName);
        download = (RelativeLayout)findViewById(R.id.download);
        NextSong = (ImageView)findViewById(R.id.nextsong);
        PauseSong = (ImageView)findViewById(R.id.stopsong);
        musiclics = (RelativeLayout)findViewById(R.id.Musics);
        recentplay = (RelativeLayout)findViewById(R.id.recentplay);

        localmusic = (RelativeLayout)findViewById(R.id.LOCALMUSIC);
        songindex = (RelativeLayout)findViewById(R.id.SongIndex);
        songpic = (ImageView)findViewById(R.id.songpic);

        songindex.setOnClickListener(this);
        localmusic.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        download.setOnClickListener(this);
        musiclics.setOnClickListener(this);
        recentplay.setOnClickListener(this);
        songpic.setScaleType(ImageView.ScaleType.FIT_XY);

        requestQueue = Volley.newRequestQueue(MainMp3.this);
        Intent intent = new Intent(MainMp3.this, Mp3Play_Service.class);
        bindService(intent,con,Context.BIND_AUTO_CREATE);

        Mp3Progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int dest = seekBar.getProgress();
                int time = mediaPlayer.getDuration();
                int max = seekBar.getMax();
                mediaPlayer.seekTo(time * dest / max);
            }
        });


    }

    private void IsFirst()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        first = settings.getBoolean(FIRST_RUN, true);
        if (first) {
            File file = new File(Constant.MP3Path);
            if(!file.exists())
            {
                File mp3dir = new File(Constant.MP3Path);
                mp3dir.mkdirs();
                File imgdir = new File(Constant.ImgPath);
                imgdir.mkdirs();
                File lrcdir = new File(Constant.LrcPath);
                lrcdir.mkdirs();
            }
            DownloadDatabases downloadDatabases = new DownloadDatabases(MainMp3.this);
            SQLiteDatabase sqLiteDatabase = downloadDatabases.getWritableDatabase();
            RecentPlayDatabses recentPlayDatabses = new RecentPlayDatabses(MainMp3.this);
            SQLiteDatabase sqLiteDatabase1 = recentPlayDatabses.getWritableDatabase();
        } else {
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(con);
        super.onDestroy();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (first) {
            editor.putBoolean(FIRST_RUN, false);
        }
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.LOCALMUSIC: startActivity(new Intent(MainMp3.this,LocalMp3.class));break;
            case R.id.SongIndex:
            {
                Intent intent = new Intent(MainMp3.this,mp3_play.class);
                intent.putExtra("CurrenTime",mediaPlayer.getCurrentPosition());
                startActivity(intent);
                break;
            }
            case R.id.searchView:
            {
                Intent intent = new Intent(MainMp3.this,SearchMp3.class);
                intent.putExtra("Type",searchText.getText().toString());
                startActivity(intent);
                break;
            }
            case R.id.download: startActivity(new Intent(MainMp3.this,DownloadMp3.class));break;
            case R.id.Musics:startActivity(new Intent(MainMp3.this,Musiclibrary.class));break;
            case R.id.recentplay: startActivity(new Intent(MainMp3.this,RectenPlay.class));break;
        }
    }
}
