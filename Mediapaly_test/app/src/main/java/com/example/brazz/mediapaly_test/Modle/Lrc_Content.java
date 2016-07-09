package com.example.brazz.mediapaly_test.Modle;

import java.io.UnsupportedEncodingException;

/**
 * Created by BrazZ on 2016/5/28.
 */
public class Lrc_Content {//歌词模板类
    private String LrcStr;
    private int LrcTime;

    public int getLrcTime() {
        return LrcTime;
    }

    public void setLrcTime(int lrcTime) {
        LrcTime = lrcTime;
    }

    public void setLrcStr(String lrcStr) {
        try {
            lrcStr = new String(lrcStr.getBytes(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LrcStr = lrcStr;
    }

    public String getLrcStr() {
        return LrcStr;
    }
}
