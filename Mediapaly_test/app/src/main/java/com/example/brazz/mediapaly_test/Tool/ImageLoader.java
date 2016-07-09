package com.example.brazz.mediapaly_test.Tool;

import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.example.brazz.mediapaly_test.R;

public class ImageLoader {//加载网络类
    public ImageLoader(String url, RequestQueue requestQueue, ImageView imageView){
        com.android.volley.toolbox.ImageLoader imageLoader = new com.android.volley.toolbox.ImageLoader(requestQueue,new BitmapCache());
        com.android.volley.toolbox.ImageLoader.ImageListener listener = com.android.volley.toolbox.ImageLoader.getImageListener(imageView, R.drawable.imageloading, R.drawable.imagefalse);
        imageLoader.get(url,listener);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

}