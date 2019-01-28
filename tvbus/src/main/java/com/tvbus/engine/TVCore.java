package com.tvbus.engine;

import android.content.Context;
import android.util.Log;

public class TVCore {

    public static synchronized TVCore getInstance() {
        if (inst == null) {
            inst = new TVCore();
            try {
                nativeHandle = inst.initialise();
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }

            if (nativeHandle == 0) {
                return null;
            }
        }

        return inst;
    }

    // set params
    public void setTVListener(TVListener listener) {
        try {
            setListener(nativeHandle, listener);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void setPlayPort(int iPort) {
        try {
            setPlayPort(nativeHandle, iPort);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setServPort(int iPort) {
        try {
            setServPort(nativeHandle, iPort);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // actions
    public void start(String url) {
        Log.e("HAHA", url.length()+", "+url);

        try {
            start(nativeHandle, url);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void start(String url, String accessCode) {
        try {
            start2(nativeHandle, url, accessCode);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            stop(nativeHandle);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    int init(Context context) {
        try {
            return init(nativeHandle, context);
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    int run() {
        try {
            return run(nativeHandle);
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    void quit() {
        try {
            quit(nativeHandle);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            System.loadLibrary("tvcore");
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }


    // natives
    private native int init(long handle, Context context);

    private native int run(long handle);

    // async start and stop
    private native void start(long handle, String url);

    private native void start2(long handle, String url, String accessCode);

    private native void stop(long handle);

    private native void quit(long handle);

    private native void setServPort(long handle, int iPort);

    private native void setPlayPort(long handle, int iPort);

    private native void setListener(long handle, TVListener listener);

    private native long initialise();


    private static TVCore inst;
    private static long nativeHandle;

    private TVCore() {
    }


}