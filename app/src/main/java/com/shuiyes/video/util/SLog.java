package com.shuiyes.video.util;

import android.util.Log;

import java.io.File;

/**
 * SHUIYES LOG
 */
public class SLog {

    private static final boolean DEBUGE = new File("/sdcard/.shuiyes/debug").exists();
    private static final boolean DEBUGD = false;
    private static final String TAG = "SHUIYES";

    public static void e(String tag, String text){
        if(DEBUGE){
            Log.e(tag, text);
        }
    }

    public static void e(Throwable e){
//        Log.e(TAG, e.getLocalizedMessage());
//        StackTraceElement[] stes = e.getStackTrace();
//        for(StackTraceElement ste: stes){
//            Log.e(TAG, ste.toString());
//        }
        e(TAG, Log.getStackTraceString(e));
    }

    public static void e(String text, Throwable e){
        e(TAG, text + "\n" + Log.getStackTraceString(e));
    }

    public static void d(String tag, String text){
        if(DEBUGD){
            Log.d(tag, text);
        }
    }
}
