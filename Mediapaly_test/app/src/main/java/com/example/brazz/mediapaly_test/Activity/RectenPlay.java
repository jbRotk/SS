package com.example.brazz.mediapaly_test.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.brazz.mediapaly_test.Control.data.sql.RecentPlayControl;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RectenPlay extends Activity {

    @InjectView(R.id.recentplaylist)
    protected ListView recentplay;

    private ArrayList<SongMsg> songMsgs;

    private RecentPlayControl recentPlayControl;
    private DownloadMp3.DownloadAdapter recentplayadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recten_play);
        init();
        Try();
    }

    private void init()
    {
        ButterKnife.inject(this);
        recentPlayControl = new RecentPlayControl(RectenPlay.this);
        songMsgs = recentPlayControl.QueryRecent();
    }
    private void Try()
    {
        try {
            recentplayadapter = new DownloadMp3.DownloadAdapter(songMsgs,RectenPlay.this);
            recentplay.setAdapter(recentplayadapter);
            recentplayadapter.notifyDataSetChanged();
        }
        catch (Exception e){

        }
    }


}
