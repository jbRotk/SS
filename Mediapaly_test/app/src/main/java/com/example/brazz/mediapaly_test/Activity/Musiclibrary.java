package com.example.brazz.mediapaly_test.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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
import com.example.brazz.mediapaly_test.Control.data.Service.Mp3Play_Service;
import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Musiclibrary extends AppCompatActivity {

    private ListView musicslibrary;
    private ArrayList<SongMsg> songMsgs;
    private MusiclibsAdapter musiclibsAdapter;
    private RequestQueue requestQueue;
    private Mp3Play_Service mp3Play_service;
    private MediaPlayer mediaPlayer;
    private RelativeLayout totallayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclibrary);
        init();
    }


    private void init()
    {
        bindService(new Intent(Musiclibrary.this,Mp3Play_Service.class),connection, Context.BIND_AUTO_CREATE);
        songMsgs = new ArrayList<SongMsg>();
        musicslibrary = (ListView)findViewById(R.id.musiclibs);
        totallayout = (RelativeLayout)findViewById(R.id.yuekutatollayout);

        musiclibsAdapter = new MusiclibsAdapter();
        requestQueue = Volley.newRequestQueue(this);


        musicslibrary.setAdapter(musiclibsAdapter);
        GetList();
    }


    private void GetList()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.SearchALL, null, new Response.Listener<JSONObject>() {
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
                    musiclibsAdapter.notifyDataSetChanged();
                    totallayout.setBackgroundResource(R.drawable.background);
                }
                catch (Exception e)
                {
                    Toast.makeText(Musiclibrary.this,"JSON解析错误！",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Musiclibrary.this,"网络错误！",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mp3Play_service = ((Mp3Play_Service.Mp3PlayBinder)service).getMp3Play_Service();
            mediaPlayer = mp3Play_service.getMediapaly();

            musicslibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mp3Play_service.setSongMsgs(songMsgs);
                    mp3Play_service.setPlaying(position);
                    mp3Play_service.playUrl(songMsgs.get(position).MediaResource);
                    SearchMp3.DowloadLrcTask dowloadLrcTask = new SearchMp3.DowloadLrcTask();
                    File song = new File(Constant.LrcPath+songMsgs.get(position).SongName+".lrc");
                    if(! song.exists())
                    {
                        dowloadLrcTask.execute(songMsgs.get(position).LrcPath,Constant.LrcPath+songMsgs.get(position).SongName+".lrc");
                    }
                    File pic = new File(Constant.ImgPath+songMsgs.get(position).SongName+".png");
                    if(! pic.exists())
                    {
                        SearchMp3.DowloadLrcTask dowloadPic =  new SearchMp3.DowloadLrcTask();
                        dowloadPic.execute(songMsgs.get(position).PictureResource,Constant.ImgPath+songMsgs.get(position).SongName+".png");
                    }

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

    class MusiclibsAdapter extends BaseAdapter
    {
        MusiclibsAdapter(){
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder ;



            if(convertView == null)
            {
                convertView = LayoutInflater.from(Musiclibrary.this).inflate(R.layout.musiclibrary,null);
                viewHolder = new ViewHolder();
                viewHolder.SongName = (TextView)convertView.findViewById(R.id.SongName);
                viewHolder.Singer = (TextView)convertView.findViewById(R.id.Singer);
                viewHolder.Type = (TextView)convertView.findViewById(R.id.Type);
                viewHolder.Pic = (ImageView)convertView.findViewById(R.id.webimage);

                viewHolder.Pic.setScaleType(ImageView.ScaleType.FIT_XY);

                convertView.setTag(viewHolder);
                viewHolder.SongName.setTag(position);
                viewHolder.Pic.setTag(songMsgs.get(position).PictureResource);
              //  viewHolder.seekBar.setTag(0);


                File pic = new File(Constant.ImgPath+songMsgs.get(position).SongName+".png");
                if(! pic.exists())
                {
                    SearchMp3.DowloadLrcTask dowloadPic =  new SearchMp3.DowloadLrcTask();
                    dowloadPic.execute(songMsgs.get(position).PictureResource,Constant.ImgPath+songMsgs.get(position).SongName+".png");
                    com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(songMsgs.get(position).PictureResource,requestQueue,viewHolder.Pic);
                }
                else
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                    viewHolder.Pic.setImageBitmap(bitmap);
                }
            }else
            {
                viewHolder = (ViewHolder)convertView.getTag();
                viewHolder.Pic.setScaleType(ImageView.ScaleType.FIT_XY);
                ImageView img = viewHolder.Pic;

                if(img.getTag() != null && img.getTag().equals(songMsgs.get(position).PictureResource))
                {

                }
                else
                {
                    viewHolder.Pic.setBackgroundResource(R.drawable.background);
                    File pic = new File(Constant.ImgPath+songMsgs.get(position).SongName+".png");
                    if(! pic.exists())
                    {
                        SearchMp3.DowloadLrcTask dowloadPic =  new SearchMp3.DowloadLrcTask();
                        dowloadPic.execute(songMsgs.get(position).PictureResource,Constant.ImgPath+songMsgs.get(position).SongName+".png");
                        com.example.brazz.mediapaly_test.Tool.ImageLoader imageLoader = new com.example.brazz.mediapaly_test.Tool.ImageLoader(songMsgs.get(position).PictureResource,requestQueue,viewHolder.Pic);
                    }
                    else
                    {
                        Bitmap bitmap = BitmapFactory.decodeFile(pic.getPath());
                        viewHolder.Pic.setImageBitmap(bitmap);
                    }
                }

                if(viewHolder.SongName.getTag() != null && viewHolder.SongName.getTag().equals(position))
                {

                }
                else
                {
                  //  viewHolder.thread.interrupt(); //停止线程
                   // viewHolder.seekBar.setProgress(0);//初始化
                  //  viewHolder.thread.start();
                }


                viewHolder.Pic.setTag(songMsgs.get(position).PictureResource);
            }
            viewHolder.SongName.setText(songMsgs.get(position).SongName);
            viewHolder.Singer.setText("歌手：" + songMsgs.get(position).SingerName);
            viewHolder.Type.setText("类型：" + songMsgs.get(position).Type);

            // new NormalLoadPictrue().getPicture(songMsgs.get(position).PictureResource,viewHolder.Pic);
            return convertView;
        }
    }

    class ListTask extends AsyncTask<String,ImageView,Void>
    {

        @Override
        protected Void doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onProgressUpdate(ImageView... values) {
            super.onProgressUpdate(values);
        }
    }



    public static class ViewHolder
    {
        TextView SongName;
        TextView Singer;
        TextView Type;
        ImageView Pic;
      //  SeekBar seekBar;
      //  Thread thread;
    }
}
