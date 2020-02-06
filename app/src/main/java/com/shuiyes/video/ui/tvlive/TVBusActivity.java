package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.ui.tvlive.TVPlayActivity;
import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

import org.json.JSONException;
import org.json.JSONObject;

public class TVBusActivity extends TVPlayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TVService.RUN){
            startService(new Intent(this, TVService.class));
        }

        initTVCore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startChannel(mUrl, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mTVCore.stop();
    }

    private long mMPCheckTime = 0;

    private TVCore mTVCore = null;
    private int mBuffer, mTmPlayerConn;
    private String playbackUrl;

    // tvbus p2p module related
    private void initTVCore() {
        mTVCore = TVCore.getInstance();
        assert mTVCore != null;

        // start tvcore
        mTVCore.setTVListener(new TVListener() {
            @Override
            public void onInited(String result) {
                parseCallbackInfo("onInited", result);
            }

            @Override
            public void onStart(String result) {
                parseCallbackInfo("onStart", result);
            }

            @Override
            public void onPrepared(String result) {
                if (parseCallbackInfo("onPrepared", result)) {
                    startPlayback(playbackUrl);
                }
            }

            @Override
            public void onInfo(String result) {
                parseCallbackInfo("onInfo", result);
                checkPlayer();
            }

            @Override
            public void onStop(String result) {
                parseCallbackInfo("onStop", result);
            }

            @Override
            public void onQuit(String result) {
                parseCallbackInfo("onQuit", result);
            }
        });
    }

    private void startChannel(String address, String accessCode) {
        stoPlayback();
        mMPCheckTime = Long.MAX_VALUE;
        mTmPlayerConn = mBuffer = 0;

        mTVCore.stop();
        if (TextUtils.isEmpty(accessCode)) {
            mTVCore.start(address);
        } else {
            mTVCore.start(address, accessCode);
        }
    }

    // player related
    private void checkPlayer() {
        // Attention
        // check player playing must run in main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTmPlayerConn > 20 && mBuffer > 50) {
                    stoPlayback();
                }

                if (System.nanoTime() > mMPCheckTime && !mVideoView.isPlaying()) {
                    try {
                        Log.e(TAG, "startPlayback again " + playbackUrl);
                        mVideoView.setVideoURI(Uri.parse(playbackUrl));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void stoPlayback() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVideoView.stopPlayback();
            }
        });
    }

    // 10 second
    private final static long MP_START_CHECK_INTERVAL = 10 * 1000 * 1000 * 1000L;

    @Override
    protected void startPlayback(final String url) {
        if(url == null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMPCheckTime = System.nanoTime() + MP_START_CHECK_INTERVAL;

                try {
                    Log.e(TAG, "startPlayback " + url);
                    if(url.endsWith("m3u8")){
                        mVideoView.setVideoURI(Uri.parse(url));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean parseCallbackInfo(String event, String result) {
//        Log.e(TAG, event+" "+result);

        JSONObject jsonObj = null;
        String statusMessage = null;

        try {
            jsonObj = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonObj == null) {
            return false;
        }

        if ("onInited".equals(event)) {
            // {"tvcore":"0"}
            if ((jsonObj.optInt("tvcore", 1)) == 0) {
                statusMessage = "Init success!";
            } else {
                statusMessage = "Init error!";
            }
        } else if ("onStart".equals(event)) {
            // {"address":"tvbus://3CrMPFZqaFxTGeDtQX2E5gkF6ZdVKk2bj72ff3RTSwJdYWwhg6","mkcache":"","peers":"40"}
        } else if ("onPrepared".equals(event)) {
            // {"hls":"http://127.0.0.1:8902/70016/index.m3u8"}
            if (jsonObj.has("http")) {
                playbackUrl = jsonObj.optString("http", null);
            } else if (jsonObj.has("hls")) {
                playbackUrl = jsonObj.optString("hls", null);
            } else {
                return false;
            }
        } else if ("onInfo".equals(event)) {
            // {"buffer":"96","download_rate":"1175423","download_total":"8","hls_last_conn":"1","upload_rate":"0","upload_total":"0"}
            // {"buffer":"100","download_rate":"1175423","download_total":"9","hls_last_conn":"2","upload_rate":"0","upload_total":"0"}
            mBuffer = jsonObj.optInt("buffer", 0);
            mTmPlayerConn = jsonObj.optInt("hls_last_conn", 0);
            if(mBuffering){
                int rate = jsonObj.optInt("download_rate", 0);
                if(mBuffer < 50 || (rate < 100 * 1024 && mBuffer < 99)){
                    statusMessage =  android.text.format.Formatter.formatFileSize(mContext, rate) + "/s " + mBuffer+ "%";
                } else {
                    statusMessage =  android.text.format.Formatter.formatFileSize(mContext, rate) + "/s";
                }
            }else{
                statusMessage = "";
            }
        } else if ("onStop".equals(event)) {
            // {"errno":"0"}

            int errno = jsonObj.optInt("errno", 0);
            if (errno < 0) {
                statusMessage = "errno: " + errno + ((errno == -104)?"[源失效]":"");
            }
        } else if ("onQut".equals(event)) {
            //
        }

        if (statusMessage != null) {
            updateStatusView(statusMessage);
        }
        return true;
    }

    private void updateStatusView(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusView.setText(status);
            }
        });
    }

    @Override
    protected boolean onMediaInfo(int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                // 第一帧绘制开始
                mMPCheckTime = System.nanoTime();
                break;
        }
        return super.onMediaInfo(what, extra);
    }
}