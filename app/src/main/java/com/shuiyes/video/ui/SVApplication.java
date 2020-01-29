package com.shuiyes.video.ui;

import android.app.Application;
import android.content.Context;

import com.shuiyes.video.BuildConfig;

public class SVApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this.getApplicationContext();

        android.util.Log.e("SY", "onCreate() " + BuildConfig.VERSION_NAME);

//        com.tencent.bugly.crashreport.CrashReport.initCrashReport(getApplicationContext(), "a96983310d", true);
    }

    public static Context getAppContext() {
        return mContext;
    }

}