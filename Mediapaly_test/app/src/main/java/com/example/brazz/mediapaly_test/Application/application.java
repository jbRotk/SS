package com.example.brazz.mediapaly_test.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.example.brazz.mediapaly_test.Activity.LockScreen;
import com.example.brazz.mediapaly_test.Control.data.BroadcastReceiVer.BrodacastScreenListener;
import com.example.brazz.mediapaly_test.Modle.MyActivityManager;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

/**
 * Created by BrazZ on 2016/6/4.
 */
public class application extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        BrodacastScreenListener brodacastScreenListener = new BrodacastScreenListener(application.this);
        brodacastScreenListener.begin(new BrodacastScreenListener.ScreenStateListener() {
            @Override
            public void onUserPresent() {
                System.out.println("USER_PARENT");
            }

            @Override
            public void onScreenOn() {
                System.out.println("ON");
            }

            @Override
            public void onScreenOff() {
                Intent lockscreen = new Intent(application.this, LockScreen.class);
                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // IntentUtils.getInstance().startActivity(MyActivityManager.getInstance().getCurrentActivity(),lockscreen);
                startActivity(lockscreen);
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(480, 800)
                        // default = device screen dimensions
                .threadPoolSize(3)
                        // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                        // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                        // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13) // default
                .discCacheSize(50 * 1024 * 1024) // 缓冲大小
                .discCacheFileCount(100) // 缓冲文件数目
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs().build();

                // 2.单例ImageLoader类的初始化
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(config);


        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() { //获取当前的Activity
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

    }


}
