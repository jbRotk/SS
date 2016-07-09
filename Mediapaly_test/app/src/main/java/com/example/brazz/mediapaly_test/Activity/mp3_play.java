package com.example.brazz.mediapaly_test.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.brazz.mediapaly_test.Control.data.Service.DownLoadService;
import com.example.brazz.mediapaly_test.Control.data.Service.Mp3Play_Service;
import com.example.brazz.mediapaly_test.Control.data.sql.DownloadControl;
import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;
import com.example.brazz.mediapaly_test.Tool.HttpDownloader;
import com.example.brazz.mediapaly_test.View.AddPopWindow;
import com.example.brazz.mediapaly_test.View.Lrc_TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class mp3_play extends Activity {
    @InjectView(R.id.lrctext1)//歌词
    protected Lrc_TextView lrc;
    @InjectView(R.id.Mp3Progress)//进度条
    protected SeekBar Mp3Progress;
    @InjectView(R.id.mediacurrentime)//当前时间
    protected TextView currentime;
    @InjectView(R.id.mediaduration)//总的时间
    protected TextView duration;
    @InjectView(R.id.SongName)
    protected TextView SongName;
    @InjectView(R.id.Singer)
    protected TextView SingerName;
    @InjectView(R.id.back)
    protected ImageView back;
    @InjectView(R.id.back1)
    protected TextView back1;
    @InjectView(R.id.playBy)
    protected Button playBy;
    @InjectView(R.id.Download)
    protected ImageView Downloadmp3;
    @InjectView(R.id.previewsong)
    protected ImageView previewsong;
    @InjectView(R.id.pausesong)
    protected ImageView pausesong;
    @InjectView(R.id.nextsong1)
    protected ImageView nextsong;
    @InjectView(R.id.songpic)
    protected ImageView songpic;





    private MediaPlayer mediaPlayer;
    private RequestQueue requestQueue;


    private Mp3Play_Service mp3Play_service;
    private DownLoadService downLoadService;

    private ArrayList<SongMsg> songMsgs;
    private int playing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_play);
        setTitle("mp3_play");
        init();
        Try();
    }


    public void init()
    {
        ButterKnife.inject(this);
        requestQueue = Volley.newRequestQueue(mp3_play.this);
        songpic.setScaleType(ImageView.ScaleType.FIT_XY);
        lrc.setPainColor(Color.WHITE);
        bindService(new Intent(mp3_play.this, Mp3Play_Service.class), connection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(mp3_play.this, DownLoadService.class), connection1, Context.BIND_AUTO_CREATE);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mp3_play.this, MainMp3.class));
                finish();
            }
        });
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mp3_play.this, MainMp3.class));
                finish();
            }
        });

    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mp3Play_service = ((Mp3Play_Service.Mp3PlayBinder)service).getMp3Play_Service();
            mediaPlayer = mp3Play_service.getMediapaly();
            songMsgs = mp3Play_service.getSongMsgs();
            playing = mp3Play_service.getPlaying();
            Try();
            thread.start();


            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    int currentProgress = Mp3Progress.getMax()
                            * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
                    System.out.println(currentProgress);
                    Mp3Progress.setSecondaryProgress(percent);
                }
            });
            playBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AddPopWindow addPopWindow = new AddPopWindow(mp3_play.this, playBy);
                    addPopWindow.showPopupWindow(playBy);
                    addPopWindow.setOnPopClicklistener(new AddPopWindow.onPopClicklistener() {
                        @Override
                        public void onRandomclick() {
                            mp3Play_service.setPlayBy(Mp3Play_Service.RandomPlay);
                            playBy.setText("随机播放");
                            addPopWindow.dismiss();
                        }

                        @Override
                        public void onOrderclick() {
                            mp3Play_service.setPlayBy(Mp3Play_Service.OrderPlay);
                            playBy.setText("顺序播放");
                            addPopWindow.dismiss();
                        }

                        @Override
                        public void onSingleclick() {
                            mp3Play_service.setPlayBy(Mp3Play_Service.SinglePlay);
                            playBy.setText("单曲循环");
                            addPopWindow.dismiss();
                        }
                    });
                }
            });

            previewsong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp3Play_service.PreviewSong();
                    pausesong.setBackgroundResource(R.drawable.stop);
                    Try();

                }
            });

            nextsong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pausesong.setBackgroundResource(R.drawable.stop);
                    mp3Play_service.NextSong();
                    DowloadLrcTask dowloadLrcTask = new DowloadLrcTask();
                    dowloadLrcTask.execute(songMsgs.get(mp3Play_service.getPlaying()).LrcPath,Constant.LrcPath+songMsgs.get(mp3Play_service.getPlaying()).SongName+".lrc");
                    File pic = new File(Constant.ImgPath+mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName+".png");
                    if(pic.exists())
                    {
                        Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                        songpic.setImageBitmap(bitmap);
                    }
                    else
                    {
                        try {
                            DowloadLrcTask dowloadimgTask = new DowloadLrcTask();
                            dowloadimgTask.execute(songMsgs.get(mp3Play_service.getPlaying()).PictureResource,Constant.MP3Path+songMsgs.get(mp3Play_service.getPlaying()).SongName+".png");
                            com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource, requestQueue, songpic);
                        } catch (Exception e) {
                            Toast.makeText(mp3_play.this,"很抱歉，这张图片飞了！",Toast.LENGTH_SHORT).show();}
                    }
                    Try();
                }
            });

            pausesong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mp3Play_service.getMediapaly().isPlaying())
                    {
                        pausesong.setBackgroundResource(R.drawable.play);
                    }
                    else {
                        pausesong.setBackgroundResource(R.drawable.stop);
                    }
                    mp3Play_service.Pause();
                }
            });

            try
            {
                if(! mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).IsWebSong)
                {
                    Downloadmp3.setVisibility(View.INVISIBLE); //隐藏下载图标
                }
            }
            catch (Exception E){}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            thread.interrupt();
        }
    };

    ServiceConnection connection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            downLoadService = ((DownLoadService.DownloadBinder)service).getDownloadService();

            Downloadmp3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //MP3下载
                    try {
                        System.out.println(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
                        String SongName  =  mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName;
                        String Mp3URL = mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).MediaResource;
                        String LrcURL = mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).LrcPath;
                        String PictureURL = mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource;
                        Toast.makeText(mp3_play.this, "开始下载！", Toast.LENGTH_SHORT).show();
                        downLoadService.StartDownload(SongName, Mp3URL, LrcURL, PictureURL);//开始下载


                        PutSongContentValue(SongName, mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);//存入数据库
                        DownloadControl downloadControl = new DownloadControl(mp3_play.this);
                        downloadControl.InserObject(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()));
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(mp3_play.this, "下载失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        Try();

    }
    @Override
    protected void onRestart()
    {
        super.onRestart();
        Try();
    }

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
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    final int milliseconds = 1000;
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 == 1)
            {
                try {
                    int time = mediaPlayer.getCurrentPosition()/1000;
                    currentime.setText(time/60+" : "+time%60);
                    duration.setText(mediaPlayer.getDuration()/1000/60 + " : "+ (mediaPlayer.getDuration()/1000)%60);
                    int position = mediaPlayer.getCurrentPosition();

                    int time1 = mediaPlayer.getDuration();
                    int max = Mp3Progress.getMax();

                    Mp3Progress.setProgress(position*max/time1);
                    lrc.setCurrentLrc_Position(lrc.LrcIndex(lrc,mediaPlayer));
                }
                catch (Exception e){}
                lrc.invalidate();
            }
        }
    };

    private void Try()
    {
        try {
            SongName.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
            SingerName.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);
            Mp3Progress.setProgress(mediaPlayer.getCurrentPosition());
            lrc.SetLrc(Constant.LrcPath + songMsgs.get(mp3Play_service.getPlaying()).SongName + ".lrc",lrc);
            Message message = new Message();
            message.arg1 = 1;
            mHandler.sendMessage(message);
            if(mp3Play_service.getMediapaly().isPlaying())
            {
                pausesong.setBackgroundResource(R.drawable.stop);
            }
            else {
                pausesong.setBackgroundResource(R.drawable.play);
            }
            File pic = new File(Constant.ImgPath+mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName+".png");
            if(pic.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                songpic.setImageBitmap(bitmap);
            }
            else
            {
                DowloadLrcTask dowloadimgTask = new DowloadLrcTask();
                dowloadimgTask.execute(songMsgs.get(mp3Play_service.getPlaying()).PictureResource,Constant.MP3Path+songMsgs.get(mp3Play_service.getPlaying()).SongName+".png");
                com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource,requestQueue,songpic);
            }
        }
        catch (Exception e){}
    }

    private void PutSongContentValue(String Songname,String Singer)
    {
        ContentResolver resolver = this.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.TITLE, Songname);
        contentValues.put(MediaStore.Audio.Media.ARTIST, Singer);
        contentValues.put(MediaStore.Audio.Media.DATA, Constant.MP3Path+Songname+".mp3");
        contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, Songname);

        resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);

    }


    public  class DowloadLrcTask extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            HttpDownloader httpDownloader = new HttpDownloader();
            httpDownloader.downloadFile(params[0], params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                SongName.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
                SingerName.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);
                Mp3Progress.setProgress(mediaPlayer.getCurrentPosition());
                lrc.SetLrc(Constant.LrcPath + songMsgs.get(mp3Play_service.getPlaying()).SongName + ".lrc",lrc);
                Message message = new Message();
                message.arg1 = 1;
                mHandler.sendMessage(message);
            }
            catch (Exception e){ lrc.SetLrc("",lrc);}
        }
    }




    @Override
    protected void onDestroy()
    {
        unbindService(connection);
        unbindService(connection1);
        super.onDestroy();
    }
}
