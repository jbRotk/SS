package com.example.brazz.mediapaly_test.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.brazz.mediapaly_test.Control.data.Service.Mp3Play_Service;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class LocalMp3 extends AppCompatActivity {


    private ArrayList<SongMsg> songMsgs;
    private ListView localMusic_list;
    private LocalMusicAdapter localMusicAdapter;

    private MediaPlayer mediaPlayer;

    private Mp3Play_Service mp3Play_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_mp3);
        init();
        GetMusic(songMsgs,this);
    }


    private void init()
    {
        ButterKnife.inject(this);
        songMsgs = new ArrayList<SongMsg>();

        localMusic_list = (ListView)findViewById(R.id.localmusic_list);
        localMusicAdapter = new LocalMusicAdapter(this,songMsgs);
        localMusic_list.setAdapter(localMusicAdapter);
        bindService(new Intent(LocalMp3.this,Mp3Play_Service.class),connection, Context.BIND_AUTO_CREATE);

    }


    private void GetMusic(ArrayList<SongMsg> songMsgs,Context context)
    {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

//遍历媒体数据库
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
//歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
//歌曲id
                int trackId=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//歌曲标题
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
//歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
//歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
//歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
//歌曲的总播放时长：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
//歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
//歌曲文件显示名字
                String disName=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                 SongMsg songMsg = new SongMsg(title,artist,R.drawable.family,url,false);
                  songMsgs.add(songMsg);
                cursor.moveToNext();
            }
            cursor.close();
        }
        localMusicAdapter.notifyDataSetChanged();
    }

    public class LocalMusicAdapter extends BaseAdapter
    {
        Context context;
        ArrayList<SongMsg> songMsgs;

        LocalMusicAdapter(Context context, ArrayList<SongMsg> songMsgs)
        {
            this.context = context;
            this.songMsgs = songMsgs;
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
                convertView = LayoutInflater.from(LocalMp3.this).inflate(R.layout.localmusiclist_adapter,null);
            }

            TextView songname = (TextView)convertView.findViewById(R.id.SONGNAME);
            TextView singer = (TextView)convertView.findViewById(R.id.SINGER);
            songname.setMaxEms(14);
            songname.setEllipsize(TextUtils.TruncateAt.END);
            songname.setSingleLine();
            songname.setText(songMsgs.get(position).SongName);
            singer.setText(songMsgs.get(position).SingerName);

            return convertView;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mp3Play_service = ((Mp3Play_Service.Mp3PlayBinder)service).getMp3Play_Service();
            mediaPlayer = mp3Play_service.getMediapaly();

            mediaPlayer.start();

            localMusic_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //  mediaPlayer.setDataSource(LocalMp3.this,songMsgs.get(position).MediaResource);
                    mp3Play_service.setSongMsgs(songMsgs);

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.reset();
                    }
                    try {
                        mediaPlayer.setDataSource(songMsgs.get(position).MediaResource);
                        mediaPlayer.prepare();
                        mp3Play_service.setPlaying(position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onDestroy()
    {
        unbindService(connection);
        super.onDestroy();
    }
}
