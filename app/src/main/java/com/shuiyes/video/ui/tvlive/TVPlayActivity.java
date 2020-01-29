package com.shuiyes.video.ui.tvlive;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseActivity;
import com.shuiyes.video.ui.vip.VipUtils;
import com.shuiyes.video.widget.Tips;

import java.util.Calendar;


public class TVPlayActivity extends BaseActivity {

    protected Context mContext;
    protected VideoView mVideoView;
    protected TextView mTimeView, mStatusView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_tvlive);

        mContext = this;

        mStatusView = (TextView) findViewById(R.id.tv_status);
        mTimeView = (TextView) findViewById(R.id.tv_time);

        mVideoView = (VideoView) findViewById(R.id.videoview);
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                return onMediaInfo(what, extra);
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "======== onPrepared");

                mPrepared = true;
                mediaPlayer.start();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                String err = "onError(" + what + "," + extra + ")";
                Log.e(TAG, "======== " + err);

                if(what == 100){
                    // 重新播放
                    mVideoView.stopPlayback();
                    startPlayback();
                    return true;
                }else{
                    mBuffering = false;
                    Tips.show(mContext, err, 0);
                    return false;
                }
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, "======== onCompletion");
            }
        });

        String title = getIntent().getStringExtra("title");
        setTitle(title);

        mUrl = getIntent().getStringExtra("url");
        Log.e(TAG, "play url=" + mUrl);
    }

    protected String mUrl;
    protected boolean mPrepared = false;

    @Override
    protected void onResume() {
        super.onResume();

        startPlayback();
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 100);
    }

    protected void startPlayback(){
        if(!mUrl.startsWith("tvbus")){
            try {
                mVideoView.setVideoURI(Uri.parse(mUrl));
                mHandler.removeCallbacks(mRefreshRxTask);
                mHandler.postDelayed(mRefreshRxTask, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(MSG_UPDATE_TIME);
        mVideoView.stopPlayback();
    }

    protected final int MSG_UPDATE_TIME = 10;

    @Override
    public void handleOtherMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_TIME:
                Calendar now = Calendar.getInstance();
                mTimeView.setText(String.format(getTitle() + " %02d:%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND)));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                break;
        }
    }

    protected boolean mBuffering = true;

    protected boolean onMediaInfo(int what, int extra){
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.e(TAG, "======== MEDIA_INFO_BUFFERING_START");
                mBuffering = true;
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.e(TAG, "======== MEDIA_INFO_BUFFERING_END");
                mBuffering = false;
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                Log.e(TAG, "======== MEDIA_INFO_VIDEO_RENDERING_START");
                mBuffering = false;
                break;
            default:
                Log.e(TAG, "======== onInfo(" + what + ", " + extra + ")");
                break;
        }
        return false;
    }

    private long mTotalRx;
    private double mLastTime;

    Runnable mRefreshRxTask = new Runnable() {
        @Override
        public void run() {
            if(mBuffering){
                long rx = TrafficStats.getTotalRxBytes();
                double time = System.currentTimeMillis();
                double timeDiff = (time - mLastTime) / 1000;

                String rxStr;
                if (mTotalRx == 0) {
                    rxStr = "0.00 K";
                } else {
                    float tmp = rx - mTotalRx;
                    tmp /= timeDiff;
                    rxStr = VipUtils.formateBytes(tmp);
                }
                mTotalRx = rx;
                mLastTime = time;
                mStatusView.setText(rxStr + "/s");
            }else{
                mTotalRx = 0;
                mStatusView.setText("");
            }

            mHandler.postDelayed(mRefreshRxTask, 999);
        }
    };

}