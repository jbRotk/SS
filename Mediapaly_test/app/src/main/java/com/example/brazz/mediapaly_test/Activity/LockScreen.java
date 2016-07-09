package com.example.brazz.mediapaly_test.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.brazz.mediapaly_test.Control.data.Service.DownLoadService;
import com.example.brazz.mediapaly_test.Control.data.Service.Mp3Play_Service;
import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;
import com.example.brazz.mediapaly_test.View.AddPopWindow;
import com.example.brazz.mediapaly_test.View.Lrc_TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LockScreen extends Activity {
    final int milliseconds = 1000;

    @InjectView(R.id.pause)
    protected ImageView pause;
    @InjectView(R.id.next)
    protected ImageView next;
    @InjectView(R.id.preview)
    protected ImageView preview;
    @InjectView(R.id.playby)
    protected Button playby;
    @InjectView(R.id.download)
    protected ImageView download;
    @InjectView(R.id.currentime)
    protected TextView curremtime;
    @InjectView(R.id.durationtime)
    protected TextView durrationtime;
    @InjectView(R.id.progress)
    protected SeekBar progress;
    @InjectView(R.id.lrctext)
    protected Lrc_TextView lrc;
    @InjectView(R.id.SongName)
    protected TextView SongName;
    @InjectView(R.id.SingerName)
    protected TextView SingerName;
    @InjectView(R.id.total)
    protected ImageView total;
    @InjectView(R.id.year)
    protected TextView YEAR;
    @InjectView(R.id.back)
    protected ImageView back;


    private Mp3Play_Service mp3Play_service;
    private DownLoadService downLoadService;

    private MediaPlayer mediaPlayer;
    private ArrayList<SongMsg> songMsgs;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        setTitle("LockScreen");
        init();

        back.setOnClickListener(new View.OnClickListener() {//退出锁屏界面
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init()
    {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        ButterKnife.inject(this);
        bindService(new Intent(LockScreen.this, Mp3Play_Service.class), Mp3conn, Context.BIND_AUTO_CREATE);
        bindService(new Intent(LockScreen.this,DownLoadService.class),Downloadconn,Context.BIND_AUTO_CREATE);
        requestQueue = Volley.newRequestQueue(LockScreen.this);
        total.setScaleType(ImageView.ScaleType.FIT_XY);

        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour; // 0-23
        int minute = t.minute;
        int second = t.second;
        YEAR.setText(year + "年 " + month + "月 " + date + "日");
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        lrc.setPainColor(Color.WHITE);
    }


    ServiceConnection Mp3conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mp3Play_service = ((Mp3Play_Service.Mp3PlayBinder)service).getMp3Play_Service();
            Try();
            thread.start();
            playby.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AddPopWindow addPopWindow = new AddPopWindow(LockScreen.this, playby);
                    addPopWindow.showPopupWindow(playby);
                    addPopWindow.setOnPopClicklistener(new AddPopWindow.onPopClicklistener() {
                        @Override
                        public void onRandomclick() {
                            mp3Play_service.setPlayBy(Mp3Play_Service.RandomPlay);
                            playby.setText("随机播放");
                            addPopWindow.dismiss();
                        }

                        @Override
                        public void onOrderclick() {
                            mp3Play_service.setPlayBy(Mp3Play_Service.OrderPlay);
                            playby.setText("顺序播放");
                            addPopWindow.dismiss();
                        }

                        @Override
                        public void onSingleclick() {
                            mp3Play_service.setPlayBy(Mp3Play_Service.SinglePlay);
                            playby.setText("单曲循环");
                            addPopWindow.dismiss();
                        }
                    });
                }
            });

            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp3Play_service.PreviewSong();
                    pause.setBackgroundResource(R.drawable.stop);
                    Try();

                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pause.setBackgroundResource(R.drawable.stop);
                    mp3Play_service.NextSong();
                    SearchMp3.DowloadLrcTask dowloadLrcTask = new SearchMp3.DowloadLrcTask();
                    File pic = new File(Constant.ImgPath + mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName + ".png");
                    if (pic.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                        total.setImageBitmap(bitmap);
                    } else {
                        try {
                            com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource, requestQueue, total);
                        } catch (Exception e) {
                            Toast.makeText(LockScreen.this, "很抱歉，这张图片飞了！", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Try();
                }
            });

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp3Play_service.getMediapaly().isPlaying()) {
                        pause.setBackgroundResource(R.drawable.play);
                    } else {
                        pause.setBackgroundResource(R.drawable.stop);
                    }
                    mp3Play_service.Pause();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            thread.interrupt();
        }
    };

    ServiceConnection Downloadconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadService = ((DownLoadService.DownloadBinder)service).getDownloadService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


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


    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 == 1)
            {
                try {
                    int time = mediaPlayer.getCurrentPosition()/1000;
                    curremtime.setText(time/60+" : "+time%60);
                    durrationtime.setText(mediaPlayer.getDuration()/1000/60 + " : "+ (mediaPlayer.getDuration()/1000)%60);
                    int position = mediaPlayer.getCurrentPosition();

                    int time1 = mediaPlayer.getDuration();
                    int max = progress.getMax();

                    progress.setProgress(position*max/time1);
                    lrc.setCurrentLrc_Position(lrc.LrcIndex(lrc, mediaPlayer));
                }
                catch (Exception e){}
                lrc.invalidate();
            }
        }
    };

    private void Try()
    {
        try {
            mediaPlayer = mp3Play_service.getMediapaly();
            songMsgs = mp3Play_service.getSongMsgs();
            SongName.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName);
            SingerName.setText(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SingerName);
            progress.setProgress(mediaPlayer.getCurrentPosition());
            lrc.SetLrc(Constant.LrcPath + songMsgs.get(mp3Play_service.getPlaying()).SongName + ".lrc",lrc);
            final Message message = new Message();
            message.arg1 = 1;
            mHandler.sendMessage(message);
            if(mp3Play_service.getMediapaly().isPlaying())
            {
                pause.setBackgroundResource(R.drawable.stop);
            }
            else {
                pause.setBackgroundResource(R.drawable.play);
            }
            if(! mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).IsWebSong)
            {
               download.setVisibility(View.INVISIBLE); //隐藏下载图标
            }
            File pic = new File(Constant.ImgPath+mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).SongName+".png");
            if(pic.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                total.setImageBitmap(bitmap);
            }
            else
            {
                com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(mp3Play_service.getSongMsgs().get(mp3Play_service.getPlaying()).PictureResource,requestQueue,total);
            }
        }
        catch (Exception e){}
    }


    @Override//不响应返回键
    public void onBackPressed() {
        // do nothing
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //屏蔽home键
        if(keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy()
    {
        unbindService(Mp3conn);
        unbindService(Downloadconn);
        super.onDestroy();
    }
}
