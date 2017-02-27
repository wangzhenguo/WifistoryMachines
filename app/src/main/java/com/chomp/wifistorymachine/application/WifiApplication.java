package com.chomp.wifistorymachine.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/*import com.tencent.bugly.crashreport.CrashReport;
import com.unisound.karrobot.util.EasemobHelper;*/

/**
 * Created by unisound on 2017/1/9.
 */
public class WifiApplication extends Application {

    private static WifiApplication instance;

        @Override
        public void onCreate() {
            super.onCreate();
            instance = this;
            //EasemobHelper.getInstance().initEasemob(getApplicationContext());
            initImageLoader(getApplicationContext());

            //初始化bugly
          //  CrashReport.initCrashReport(getApplicationContext(), "3f93c3fe01", false);

            File folder = new File(Environment.getExternalStorageDirectory() + "/CHOMP/");
            if(!folder.exists()){
                folder.mkdirs();
            }
        }

        //初始化imageloader
        private void initImageLoader(Context context) {
            File cacheDir = StorageUtils.getOwnCacheDirectory(
                    getApplicationContext(), "CHOMP/"); // 设置内存卡的路径
            ImageLoaderConfiguration config = new
                    ImageLoaderConfiguration.Builder(
                    context).threadPriority(Thread.NORM_PRIORITY - 2)// 设置当前线程优先级
                    .denyCacheImageMultipleSizesInMemory() // 缓存显示不同 大小的同一张图片
                    .diskCacheSize(50 * 1024 * 1024) // 本地Sd卡的缓存最大值
                    .diskCache(new UnlimitedDiscCache(cacheDir))// sd卡缓存
                    .memoryCache(new WeakMemoryCache()) // 内存缓存
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();

            ImageLoader.getInstance().init(config);
        }

    public static WifiApplication getInstance() {
        return instance;
    }


}
