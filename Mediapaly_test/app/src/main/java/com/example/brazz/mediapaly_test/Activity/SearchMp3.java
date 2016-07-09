package com.example.brazz.mediapaly_test.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.brazz.mediapaly_test.Control.data.Service.DownLoadService;
import com.example.brazz.mediapaly_test.Control.data.Service.Mp3Play_Service;
import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SearchMp3 extends AppCompatActivity {
    private String Type;

    private ArrayList<SongMsg> songMsgs;
    private ListView songlist;
    private RelativeLayout loading;

    private SearchMusicAdapter searchMusicAdapter;

    private TextView title;

    private RequestQueue requestQueue;

    private Mp3Play_Service mp3Play_service;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mp3);
        init();
        WebRequest();
    }


    private void init()
    {
        bindService(new Intent(SearchMp3.this,Mp3Play_Service.class),connection,Context.BIND_AUTO_CREATE);
        Type = this.getIntent().getStringExtra("Type");
        loading = (RelativeLayout)findViewById(R.id.loadinglayout);
        songMsgs = new ArrayList<SongMsg>();
        songlist = (ListView)findViewById(R.id.songlist);
        searchMusicAdapter = new SearchMusicAdapter(this,songMsgs);
        title = (TextView)findViewById(R.id.titleText);
        songlist.setAdapter(searchMusicAdapter);
        requestQueue = Volley.newRequestQueue(this);
        title.setText(Type);
    }

    private void WebRequest()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.SearchType + Type, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try
                {
                    JSONArray jsonArray = jsonObject.getJSONArray("Mp3Msg");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        String Name = jsonArray.getJSONObject(i).getString("Name");
                        String Singer = jsonArray.getJSONObject(i).getString("Singer");
                        String MP3Url = jsonArray.getJSONObject(i).getString("File");
                        String MP3Lrc = jsonArray.getJSONObject(i).getString("Special");
                        String Picture = jsonArray.getJSONObject(i).getString("Picture");
                        String Type = jsonArray.getJSONObject(i).getString("Type");

                        SongMsg songMsg = new SongMsg(Name,Singer,MP3Lrc,MP3Url,Picture,Type,true);
                        songMsgs.add(songMsg);
                    }
                    loading.setVisibility(View.INVISIBLE);
                    searchMusicAdapter.notifyDataSetChanged();

                }
                catch (Exception e)
                {
                    Toast.makeText(SearchMp3.this,"faild",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SearchMp3.this,"faild",Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    public class SearchMusicAdapter extends BaseAdapter
    {
        Context context;
        ArrayList<SongMsg> songMsgs;

        SearchMusicAdapter(Context context, ArrayList<SongMsg> songMsgs)
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
                convertView = LayoutInflater.from(context).inflate(R.layout.searchmp3list_adapter,null);
            }

            TextView songname = (TextView)convertView.findViewById(R.id.WEBSONGNAME);
            TextView singer = (TextView)convertView.findViewById(R.id.WEBSINGER);
            TextView type = (TextView)convertView.findViewById(R.id.type);
            songname.setText(songMsgs.get(position).SongName);
            singer.setText(songMsgs.get(position).SingerName);
            type.setText("("+songMsgs.get(position).Type+")");
            return convertView;
        }
    }


    @Override
    protected void onDestroy()
    {
        unbindService(connection);
        super.onDestroy();
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mp3Play_service = ((Mp3Play_Service.Mp3PlayBinder)service).getMp3Play_Service();
            mediaPlayer = mp3Play_service.getMediapaly();

            songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mp3Play_service.setSongMsgs(songMsgs);
                    mp3Play_service.setPlaying(position);
                    mp3Play_service.playUrl(songMsgs.get(position).MediaResource);
                    DowloadLrcTask dowloadLrcTask = new DowloadLrcTask();
                    File song = new File(Constant.LrcPath+songMsgs.get(position).SongName+".lrc");
                    if(! song.exists())
                    {
                        dowloadLrcTask.execute(songMsgs.get(position).LrcPath,Constant.LrcPath+songMsgs.get(position).SongName+".lrc");
                    }
                    File pic = new File(Constant.ImgPath+songMsgs.get(position).SongName+".png");
                    if(! pic.exists())
                    {
                        dowloadLrcTask.execute(songMsgs.get(position).PictureResource,Constant.ImgPath+songMsgs.get(position).SongName+".png");
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static class DowloadLrcTask extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {

            DownLoadService.DownLoadFile(params[0],params[1],null,0);
            return null;
        }
    }
}
