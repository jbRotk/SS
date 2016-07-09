package com.example.brazz.mediapaly_test.Tool;

/**
 * Created by BrazZ on 2016/5/29.
 */

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtils {
    private String SDPath;

    public FileUtils(){
        //得到当前外部存储设备的目录
        SDPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    }

    /**
     * 在SD卡上创建文件
     * @param fileName
     * @return
     */
    public File createSDFile(String fileName){
        File file=new File(SDPath+fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName){
        File file=new File(SDPath+dirName);
        file.mkdir();
        return file;
    }

    /**
     * 判断SD卡上文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName){
        File file=new File(SDPath+fileName);
        return file.exists();
    }
    /**
     * 将一个inputStream里面的数据写到SD卡中
     * @param path
     * @param fileName
     * @param inputStream
     * @return
     */
    public File writeToSDfromInput(String path,String fileName,InputStream inputStream){
        //createSDDir(path);
        File file=createSDFile(path+fileName);
        OutputStream outStream=null;
        try {
            outStream=new FileOutputStream(file);
            byte[] buffer=new byte[4*1024];
            while(inputStream.read(buffer)!=-1){
                outStream.write(buffer);
            }
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public void inputstreamtofile(InputStream ins,File file){
        StringBuffer sb = new StringBuffer();
        try
        {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(ins,"GB2312"));
            byte[] buffer = new byte[8192];
        /*    while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
                System.out.println("Loading...");

            }*/
            String data = "";
            while ((data = br.readLine()) != null) {
                sb.append(data+ "\r\n");
            }
            os.write(sb.toString().getBytes());
            os.close();
            ins.close();
        }
        catch (Exception e)
        {

        }
    }
}
