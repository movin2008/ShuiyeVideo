package com.tvbus.engine;

/**
 * Created by sopdev on 15/10/13.
 * VAN
 */
public interface TVListener {
    void onInited(String result);
    void onStart(String result);
    void onPrepared(String result);
    void onInfo(String result);
    void onStop(String result);
    void onQuit(String result);
}
