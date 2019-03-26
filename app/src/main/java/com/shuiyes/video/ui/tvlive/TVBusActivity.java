package com.shuiyes.video.ui.tvlive;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.shuiyes.video.R;
import com.shuiyes.video.base.BaseActivity;
import com.tvbus.engine.TVCore;
import com.tvbus.engine.TVListener;
import com.tvbus.engine.TVService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class TVBusActivity extends BaseActivity {

    protected Context mContext;
    protected TextView mTitleView, mTimeView, mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_tvbus);

        mContext = this;

        if(!TVService.RUN){
            startService(new Intent(this, TVService.class));
        }

        mStatusView = (TextView) findViewById(R.id.tv_status);
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mTimeView = (TextView) findViewById(R.id.tv_time);
        mTitleView.setText(getIntent().getStringExtra("title"));

        initExoPlayer();
        initTVCore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHandler.removeMessages(MSG_UPDATE_TIME);
        player.setPlayWhenReady(false);
        mTVCore.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        player = null;

    }

    private SimpleExoPlayer player;
    private long mMPCheckTime = 0;

    private void initExoPlayer() {
        PlayerView playerView = (PlayerView) this.findViewById(R.id.exoplayer_view);
        playerView.requestFocus();
        playerView.setControllerAutoShow(false);
        playerView.setUseController(false);
        playerView.setKeepScreenOn(true);

        DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(this,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        TrackSelector trackSelector = new DefaultTrackSelector();
        DefaultLoadControl.Builder builder = new DefaultLoadControl.Builder();
        builder.setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE));
        builder.setBufferDurationsMs(2000, 15000, 1500, 0
        );
        LoadControl loadControl = builder.createDefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(this, rendererFactory, trackSelector, loadControl);
        player.addVideoListener(new com.google.android.exoplayer2.video.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            }

            @Override
            public void onRenderedFirstFrame() {
                mMPCheckTime = System.nanoTime();
            }
        });

        playerView.setPlayer(player);
    }

    private TVCore mTVCore = null;
    private int mBuffer;
    private int mTmPlayerConn;
    private static String playbackUrl;

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

        String url = getIntent().getStringExtra("url");
        Log.e(TAG, "play url=" + url);
        startChannel(url, null);
    }

    private void startChannel(String address, String accessCode) {
        stoPlayback();
        mMPCheckTime = Long.MAX_VALUE;
        mTmPlayerConn = mBuffer = 0;

        mTVCore.stop();
        if (accessCode == null) {
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

                if (System.nanoTime() > mMPCheckTime) {
                    int playbackState = player.getPlaybackState();
                    if (!(playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED)) {
                        startPlayback(playbackUrl);
                    }
                }
            }
        });
    }

    private void stoPlayback() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                player.stop(true);
            }
        });
    }

    // 10 second
    private final static long MP_START_CHECK_INTERVAL = 10 * 1000 * 1000 * 1000L;

    private void startPlayback(String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMPCheckTime = System.nanoTime() + MP_START_CHECK_INTERVAL;
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, "tvbus", null);
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));

//                Log.e(TAG, "startPlayback " + url);

                player.prepare(videoSource);
                player.setPlayWhenReady(true);
            }
        });
    }

    ;

    private boolean parseCallbackInfo(String event, String result) {
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
            if ((jsonObj.optInt("tvcore", 1)) == 0) {
                statusMessage = "Init success!";
            } else {
                statusMessage = "Init error!";
            }
        } else if ("onStart".equals(event)) {

        } else if ("onPrepared".equals(event)) {
//            Log.e(TAG, "onPrepared " + result);

            if (jsonObj.has("http")) {
                playbackUrl = jsonObj.optString("http", null);
            } else {
                return false;
            }
        } else if ("onInfo".equals(event)) {
//            Log.e(TAG, "onInfo " + result);

            mTmPlayerConn = jsonObj.optInt("hls_last_conn", 0);
            mBuffer = jsonObj.optInt("buffer", 0);

            if(mTmPlayerConn == 0){
                statusMessage =  "";
            }else{
                int rate = jsonObj.optInt("download_rate", 0);
                if(rate < 512 * 1024 && mBuffer < 100){
                    statusMessage =  android.text.format.Formatter.formatFileSize(mContext, rate) + "/s " + mBuffer+ "%";
                } else{
                    statusMessage =  android.text.format.Formatter.formatFileSize(mContext, rate) + "/s";
                }
            }
        } else if ("onStop".equals(event)) {
            if (jsonObj.optInt("errno", 1) < 0) {
                statusMessage = "errno: " + jsonObj.optInt("errno", 1);
            }
        } else if ("onQut".equals(event)) {
        }

        if (statusMessage != null) {
            updateStatusView(statusMessage);
        }
        return true;
    }

    private void updateStatusView(String status) {
        final String fStatus = status;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusView.setText(fStatus);
            }
        });
    }

    protected final int MSG_UPDATE_TIME = 10;

    @Override
    public void handleOtherMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_TIME:
                Calendar now = Calendar.getInstance();
                mTimeView.setText(String.format("TVBus %02d:%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND)));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                break;
        }
    }

}
