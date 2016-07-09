package com.example.brazz.mediapaly_test.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.brazz.mediapaly_test.Modle.Lrc_Content;
import com.example.brazz.mediapaly_test.Tool.LrcHandle;

import java.util.ArrayList;

/**
 * Created by BrazZ on 2016/5/28.
 */
public class Lrc_TextView extends TextView {

    private Paint CurrentLrc_Pain;//当前歌词的画笔
    private Paint NotCurrentLrc_Pain;//非当前字的画笔

    private int CurrentLrc_Position;//播放到哪里了

    private float Text_high;
    private float Text_weigh;

    private float Context_high = 100;
    private float TextSize = 20;

    private int CurrentPaintAlph = 255;

    private int NotCurrentPaintAlph = 124;

    private ArrayList<Lrc_Content> lrc_contents;

    public Lrc_TextView(Context context) {
        super(context);
        init();
    }

    public Lrc_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Lrc_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Lrc_TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init()
    {
        setFocusable(true);


        CurrentLrc_Pain = new Paint();
        CurrentLrc_Pain.setAntiAlias(true);//抗锯齿
        CurrentLrc_Pain.setTextAlign(Paint.Align.CENTER);

        NotCurrentLrc_Pain = new Paint();
        NotCurrentLrc_Pain.setAlpha(NotCurrentPaintAlph);
        NotCurrentLrc_Pain.setAntiAlias(true);
        NotCurrentLrc_Pain.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if(canvas == null) return;

        CurrentLrc_Pain.setTextSize(TextSize + 20);
        CurrentLrc_Pain.setTypeface(Typeface.SERIF);

        NotCurrentLrc_Pain.setTextSize(TextSize+10);
        NotCurrentLrc_Pain.setTypeface(Typeface.DEFAULT);
        NotCurrentLrc_Pain.setAlpha(NotCurrentPaintAlph);

        try {
            setText("");

            canvas.drawText(lrc_contents.get(CurrentLrc_Position).getLrcStr(),Text_weigh/2,Text_high/2,CurrentLrc_Pain);

            float NotCurrentLrcY = Text_high/2;
            for(int i=CurrentLrc_Position-1;i>=0;i--)
            {
                NotCurrentLrcY = NotCurrentLrcY - Context_high; //设置非当前歌词的Y轴值
                canvas.drawText(lrc_contents.get(i).getLrcStr(),Text_weigh/2,NotCurrentLrcY,NotCurrentLrc_Pain);
            }
            NotCurrentLrcY = Text_high/2;
            for (int i=CurrentLrc_Position+1;i<=lrc_contents.size()-1;i++)
            {
                NotCurrentLrcY = NotCurrentLrcY + Context_high;
                canvas.drawText(lrc_contents.get(i).getLrcStr(),Text_weigh/2,NotCurrentLrcY,NotCurrentLrc_Pain);
            }
        }
        catch (Exception e)
        {
            canvas.drawText("没有歌词",Text_weigh/2,Text_high/2,CurrentLrc_Pain);
        }
    }

    //设置歌词文件地址
    public void SetLrc(String filepath,Lrc_TextView lrc)
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            String Path = filepath;
            LrcHandle lrcHandle = new LrcHandle();
            try {
                lrcHandle.ReadLRC(Path);
                lrc.setLrc_contents(lrcHandle.getLrc_contents());
                lrc.setCurrentLrc_Position(0);
                lrc.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //设置播放位置
    public int LrcIndex(Lrc_TextView lrc,MediaPlayer mediaPlayer)
    {
        int currentTime = 0;
        int duration=0;
        int positon = 0;
        int length = 0;
        try {
            length = lrc.getLrc_contents().size();
        }
        catch (Exception e){ }
        if(mediaPlayer.isPlaying())
        {
            currentTime = mediaPlayer.getCurrentPosition();
            duration = mediaPlayer.getDuration();
        }
        if(currentTime < duration)
        {
            for(int i=0;i<length;i++)
            {
                if(i<length-1)
                {
                    if(currentTime<lrc.getLrc_contents().get(i).getLrcTime() && i==0)//最初的时候
                    {
                        positon = i;
                    }
                    if(currentTime > lrc.getLrc_contents().get(i).getLrcTime() && currentTime < lrc.getLrc_contents().get(i+1).getLrcTime())
                    {
                        positon = i;
                    }
                }
                if(i==length-1 && currentTime > lrc.getLrc_contents().get(i).getLrcTime())
                {
                    positon = i;
                }
            }
        }

        return positon;
    }

    public void setPainColor(int color)
    {
        CurrentLrc_Pain.setColor(color);
        NotCurrentLrc_Pain.setColor(color);
    }
    public void setLrc_contents(ArrayList<Lrc_Content> lrc_contents) {
        this.lrc_contents = lrc_contents;
    }

    public ArrayList<Lrc_Content> getLrc_contents() {
        return lrc_contents;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Text_high = h;
        Text_weigh = w;
    }

    public void setCurrentLrc_Position(int currentLrc_Position) {
        CurrentLrc_Position = currentLrc_Position;
    }
}
