package com.shuiyes.video;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.shuiyes.video.widget.Tips;

public class PlayActivity extends Activity {

    protected Context mContext;
    protected TextView mTitleView;
    protected TextView mStateView;
    protected VideoView mVideoView;
    protected boolean mPrepared = false;
    protected ProgressBar mLoadingProgress;
    protected Button mClarity, mSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mContext = this;

        mLoadingProgress = (ProgressBar) findViewById(R.id.loading);
        mClarity = (Button) findViewById(R.id.btn_clarity);
        mSelect = (Button) findViewById(R.id.btn_select);

        mTitleView = (TextView) findViewById(R.id.tv_title);
        mStateView = (TextView) findViewById(R.id.tv_state);

        mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
        MediaController controller = new MediaController(this);
        controller.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                Log.e("HAHA", " =========================== onPrepared");
                return false;
            }
        });

//        ProgressBar pb;
//        try {
//            Field f_mProgress =MediaController.class.getDeclaredField("mProgress");
//            f_mProgress.setAccessible(true);
//
//            Method m_initControllerView = MediaController.class.getDeclaredMethod("initControllerView", View.class);
//            m_initControllerView.setAccessible(true);
//            m_initControllerView.invoke(this, v);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mVideoView.setMediaController(controller);
        mVideoView.requestFocus();

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                Log.e("HAHA", " =========================== onInfo(" + what + ", " + extra + ")");
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        Log.e("HAHA", " =========================== MEDIA_INFO_BUFFERING_START");
                        mLoadingProgress.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        Log.e("HAHA", " =========================== MEDIA_INFO_BUFFERING_END");
                        mLoadingProgress.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        Log.e("HAHA", " =========================== MEDIA_INFO_VIDEO_RENDERING_START");
                        mLoadingProgress.setVisibility(View.GONE);
                        break;
                }

                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPrepared && !mVideoView.isPlaying()) {
            if (mCurrentPosition > 0) {
                mVideoView.seekTo(mCurrentPosition);
            }
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private long mPrevBackTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long time = System.currentTimeMillis();
                if ((time - mPrevBackTime) < 2000) {
                    finish();
                } else {
                    Tips.show(this, "再按一次退出播放", 0);
                }
                mPrevBackTime = time;
                return false;
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_UP:
                mClarity.requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mVideoView.requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
                Tips.show(this, "onKeyDown=" + keyCode, 0);
                Log.e("HAHA", "onKeyDown=" + keyCode);
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private int mCurrentPosition;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//            Log.e("HAHA", " =========================== onBufferingUpdate=" + mediaPlayer.getCurrentPosition() + "/" + i);
            if (mediaPlayer.isPlaying()) {
                mCurrentPosition = mediaPlayer.getCurrentPosition();
            }
        }
    };

}
