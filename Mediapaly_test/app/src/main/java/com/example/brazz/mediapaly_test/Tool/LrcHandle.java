package com.example.brazz.mediapaly_test.Tool;

import android.media.MediaPlayer;
import android.os.Environment;

import com.example.brazz.mediapaly_test.Modle.Lrc_Content;
import com.example.brazz.mediapaly_test.View.Lrc_TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by BrazZ on 2016/5/28.
 */
public class LrcHandle {
    private ArrayList<Lrc_Content> lrc_contents;
    private Lrc_Content lrc_content;

    public LrcHandle()
    {
        lrc_contents = new ArrayList<Lrc_Content>();
        lrc_content = new Lrc_Content();
    }
    public String ReadLRC(String path) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        File file = new File(path);
        FileInputStream fi = new FileInputStream(file);
        InputStreamReader is = new InputStreamReader(fi);
        BufferedReader bf = new BufferedReader(is);

        String s ="";
        while((s = bf.readLine()) != null)//解析歌词
        {
            s = s.replace("[","");
            s = s.replace("]","&");

            String LrcData[] = s.split("&");

            if(LrcData.length >1)
            {
                lrc_content = new Lrc_Content();
                lrc_content.setLrcStr(LrcData[1]);//歌词部分
                lrc_content.setLrcTime(LrcTimeStrToLrcTimeInt(LrcData[0])); //歌词时间部分
                lrc_contents.add(lrc_content);
            }
        }
        return "OK";
    }

    public ArrayList<Lrc_Content> getLrc_contents() {
        return lrc_contents;
    }

    public int LrcTimeStrToLrcTimeInt(String Lrc) // 歌词时间格式为 00:00.00
    {
        Lrc = Lrc.replace(":",".");
        Lrc = Lrc.replace(".","&");
        String LrcTimeStr[] = Lrc.split("&");

        int minute = Integer.parseInt(LrcTimeStr[0]);
        int second = Integer.parseInt(LrcTimeStr[1]);
        int millsecond = Integer.parseInt(LrcTimeStr[2]);

        int time = (minute*60 + second)*1000 + millsecond*10;

        return time;
    }
    public static void SetLrc(String filepath,Lrc_TextView lrc_textView)
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            String Path = filepath;
            LrcHandle lrcHandle = new LrcHandle();
            try {
                //System.out.println(lrcHandle.ReadLRC(Path));
                lrcHandle.ReadLRC(Path);
                lrc_textView.setLrc_contents(lrcHandle.getLrc_contents());
                lrc_textView.setCurrentLrc_Position(0);
                lrc_textView.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int LrcIndex(MediaPlayer mediaPlayer,Lrc_TextView lrc)
    {
        int currentTime = 0;
        int duration=0;
        int positon = 0;
        int length = lrc.getLrc_contents().size();
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
}
