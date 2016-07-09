package com.example.brazz.mediapaly_test.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.example.brazz.mediapaly_test.Control.data.sql.DownloadControl;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;

import java.util.ArrayList;

public class DownloadMp3 extends AppCompatActivity {


    ArrayList<SongMsg> songMsgs;

    private ListView downloadlist;

    private DownloadAdapter downloadAdapter;

    TextView ContentValue;
    BootstrapProgressBar DownloadProgress;

    private DownloadControl downloadControl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mp3);
        init();
    }

    private void init()
    {
        downloadlist = (ListView)findViewById(R.id.downloadinglist);

        downloadControl = new DownloadControl(DownloadMp3.this);
        songMsgs = downloadControl.QueryDownload();
        try {
            downloadAdapter = new DownloadAdapter(songMsgs,this);
            downloadlist.setAdapter(downloadAdapter);
            downloadAdapter.notifyDataSetChanged();
        }
        catch (Exception e){}
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }



    public static class DownloadAdapter extends BaseAdapter
    {
        ArrayList<SongMsg> songMsgs;
        Context context;
        DownloadAdapter(ArrayList<SongMsg> songMsgs,Context context)
        {
            this.songMsgs = songMsgs;
            this.context = context;
        }

        @Override
        public int getCount() {
            return songMsgs.size();
        }

        @Override
        public Object getItem(int position) {
            return songMsgs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.downloadlist_adapter,null);
            }

            TextView SongName = (TextView)convertView.findViewById(R.id.SongName);
            TextView SingerName = (TextView)convertView.findViewById(R.id.Singer);
            TextView Type = (TextView)convertView.findViewById(R.id.Type);

            SongName.setText(songMsgs.get(position).SongName);
            SingerName.setText(songMsgs.get(position).SingerName);
            Type.setText("("+songMsgs.get(position).Type+")");

            return convertView;
        }
    }


}
