package com.shuiyes.video;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.crashreport.CrashReport;

public class SVApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this.getApplicationContext();
        
//        CrashReport.initCrashReport(getApplicationContext(), "a96983310d", true);

    }

    public static Context getAppContext(){
        return mContext;
    }

}
