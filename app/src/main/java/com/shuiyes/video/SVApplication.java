package com.shuiyes.video;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class SVApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        CrashReport.initCrashReport(getApplicationContext(), "a96983310d", true);

    }
}
