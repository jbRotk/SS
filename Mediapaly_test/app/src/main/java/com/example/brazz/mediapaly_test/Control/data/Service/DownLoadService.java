package com.example.brazz.mediapaly_test.Control.data.Service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.example.brazz.mediapaly_test.Modle.Constant;
import com.example.brazz.mediapaly_test.Modle.SongMsg;
import com.example.brazz.mediapaly_test.Tool.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DownLoadService extends Service {

    private ArrayList<SongMsg> songMsgs;

    public  int TotalMaxSize = 0;
    public  int Mp3MaxSize = 0;
    public  int LrcMaxSize = 0;
    public  int PictureMaxSize = 0;

    public int TotalProgress = 0;
    public int Mp3Progress = 0;
    public int LrcProgress = 0;
    public int PictureProgress = 0;


    private int progress = 0;



    public int GetProgress()
    {
        return progress;
    }
    public int GetMaxSize()
    {
        return 0;
    }

    public static int DownLoadFile(String urlStr,String path,Thread thread , int position)
    {

        InputStream input = null;
        try{
            FileUtils fu = new FileUtils();
            if(fu.isFileExist(path)){   // 是否存在文件，如果存在返回1
                return 1;
            }else{

                URL url = new URL(urlStr);
                HttpURLConnection httpc = (HttpURLConnection)url.openConnection();


                input = httpc.getInputStream();
                File f = new File(path);
                int size = httpc.getContentLength();
               // TotalMaxSize += size;
                OutputStream os = new FileOutputStream(f);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                try {
                    thread.start();
                }
                catch (Exception e){}
                if(path.endsWith(".lrc"))
                {
                    StringBuffer sb = new StringBuffer();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(input,"GB2312"));
                    String data = "";
                    while ((data = br.readLine()) != null) {
                        sb.append(data+ "\r\n");
                    }
                    os.write(sb.toString().getBytes());
                }
                else
                {
                    while ((bytesRead = input.read(buffer, 0, 8192)) != -1) {
                        os.write(buffer, 0, bytesRead);
                        //      TotalProgress += bytesRead;
                        //   System.out.println("loading:" + Progress*100/ MAXSIZE + "%");
                        //  System.out.println("loading:" + TotalProgress*100/ TotalMaxSize + "%");
                    }
                }
                os.close();
                input.close();
                if(f==null){
                    return -1;              // 数据有异常的时候返回-1
                }
                input.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void StartDownload(String SongName,String MP3URL,String LrcURL,String PictureURL)
    {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(MP3URL, Constant.MP3Path+SongName+".mp3",LrcURL,Constant.LrcPath+SongName+".lrc",PictureURL,Constant.ImgPath+SongName+".png");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new DownloadBinder();
    }
    public class DownloadBinder extends Binder
    {
        DownloadBinder(){}

        public DownLoadService getDownloadService()
        {
            return DownLoadService.this;
        }
    }
    class DownloadTask extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {

            DownLoadFile(params[0], params[1], downloadThread,0);
            DownLoadFile(params[2], params[3],null,0);
            DownLoadFile(params[4], params[5], null, 0);
            return null;
        }
    }
    private int sleeptime =1000;

    Thread downloadThread = new Thread()
    {
        @Override
        public void run() {
            super.run();
            while ( TotalProgress < TotalMaxSize )
            {
                try {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
        }
    };

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)//有东西在下载时候则发送消息
            {
                System.out.println("LOAding:" + TotalProgress*100/ TotalMaxSize + "%");
            }
            if(msg.what == 2)//下载完成
            {
               // System.out.println("Finish Download!:" + TotalProgress*100/ TotalMaxSize + "%");
               // System.out.println(FormetFileSize(TotalMaxSize));
                Toast.makeText(DownLoadService.this,"下载完成！",Toast.LENGTH_SHORT).show();
                downloadThread.interrupt();
            }
        }
    };

    public static String FormetFileSize(int filelength)
    {
        String fileSizeString = "";
        if (filelength < 1024){
            fileSizeString = Double.valueOf(filelength) + "B";
        }
        else if (filelength < 1048576){
            fileSizeString = Double.valueOf(filelength/1024) + "KB";
        }
        else if (filelength < 1073741824){
            fileSizeString = Double.valueOf(filelength/ 1048576) + "MB";
        }
        else{
            fileSizeString = Double.valueOf(filelength / 1073741824) + "GB";
        }
        return fileSizeString;
    }

}
