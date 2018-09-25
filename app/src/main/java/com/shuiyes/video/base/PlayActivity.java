package com.shuiyes.video.base;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.AlbumDialog;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.widget.Tips;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    protected Context mContext;
    protected TextView mTitleView;
    protected TextView mStateView;
    protected VideoView mVideoView;
    protected boolean mPrepared = false;
    protected ProgressBar mLoadingProgress;
    protected Button mSourceView, mClarityView, mSelectView, mNextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mContext = this;

        mLoadingProgress = (ProgressBar) findViewById(R.id.loading);
        mSourceView = (Button) findViewById(R.id.btn_source);
        mClarityView = (Button) findViewById(R.id.btn_clarity);
        mSelectView = (Button) findViewById(R.id.btn_select);
        mNextView = (Button) findViewById(R.id.btn_next);

        mTitleView = (TextView) findViewById(R.id.tv_title);
        mStateView = (TextView) findViewById(R.id.tv_state);

        mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
        MediaController controller = new MediaController(this);
        controller.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                Log.e(TAG, " =========================== onPrepared");
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
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        Log.e(TAG, " =========================== MEDIA_INFO_BUFFERING_START");
                        mLoadingProgress.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        Log.e(TAG, " =========================== MEDIA_INFO_BUFFERING_END");
                        mLoadingProgress.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        Log.e(TAG, " =========================== MEDIA_INFO_VIDEO_RENDERING_START");
                        mLoadingProgress.setVisibility(View.GONE);
                        break;
                    default:
                        Log.e(TAG, " =========================== onInfo(" + what + ", " + extra + ")");
                        break;
                }

                return false;
            }
        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, " =========================== onPrepared");
                mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

                mPrepared = true;
                mIsError = false;

                mHandler.sendEmptyMessage(MSG_PALY_VIDEO);
                mediaPlayer.start();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e(TAG, " =========================== onError(" + i + "," + i1 + ")");
                String err = "视频无法播放(" + i + "," + i1 + ")";
                Tips.show(mContext, err, 0);
                fault(err);
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, " =========================== onCompletion");
                if (!mIsError) {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    playVideo();
                }
            }
        });

        mUrl = getIntent().getStringExtra("url");
        Log.e(TAG, "now url=" + mUrl);

        mTitleView.setText(getIntent().getStringExtra("title"));
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

    protected MiscDialog mSourceDialog;
    protected MiscDialog mClarityDialog;
    protected AlbumDialog mAlbumDialog;

    @Override
    protected void onDestroy() {
        if(mSourceDialog != null && mSourceDialog.isShowing()){
            mSourceDialog.dismiss();
        }
        if(mClarityDialog != null && mClarityDialog.isShowing()){
            mClarityDialog.dismiss();
        }
        if(mAlbumDialog != null && mAlbumDialog.isShowing()){
            mAlbumDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_UP:
                mClarityView.requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mVideoView.requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    protected String mVid, mUrl;
    protected List<ListVideo> mVideoList = new ArrayList<ListVideo>();

    protected int mCurrentPosition;
    protected MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
//            Log.e(TAG, " =========================== onBufferingUpdate=" + mediaPlayer.getCurrentPosition() + "/" + i);
            if (mediaPlayer.isPlaying()) {
                mCurrentPosition = mediaPlayer.getCurrentPosition();
            }
        }
    };


    protected boolean mIsError;

    protected void fault(String text) {
        mIsError = true;

        mHandler.sendMessage(mHandler.obtainMessage(MSG_FAULT, text));
    }

    protected void fault(Exception e) {
        fault(e.getClass().toString()+" "+e.getLocalizedMessage());
    }

    protected final int MSG_FAULT = 0;
    protected final int MSG_FETCH_TOKEN = 1;
    protected final int MSG_FETCH_VIDEO = 2;
    protected final int MSG_CACHE_VIDEO = 3;
    protected final int MSG_PALY_VIDEO = 4;
    protected final int MSG_SET_TITLE = 5;
    protected final int MSG_UPDATE_SELECT = 6;
    protected final int MSG_FETCH_VIDEOINFO = 7;
    protected final int MSG_UPDATE_NEXT = 8;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FAULT:
                    Object error = msg.obj;
                    mLoadingProgress.setVisibility(View.GONE);
                    mStateView.setText(mStateView.getText() + "[失败]\n" + (error != null ? error : "") /*+ " 5s后返回..."*/);
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    }, 5555);
                    break;
                case MSG_FETCH_TOKEN:
                    mStateView.setText(mStateView.getText() + "[成功]\n获取Token...");
                    mStateView.setVisibility(View.VISIBLE);
                    break;
                case MSG_FETCH_VIDEOINFO:
                    mStateView.setText(mStateView.getText() + "[成功]\n获取视频信息...");
                    mStateView.setVisibility(View.VISIBLE);
                    break;
                case MSG_FETCH_VIDEO:
                    String streamStr = (String) msg.obj;
                    if(streamStr == null){
                        mStateView.setText(mStateView.getText() + "[成功]\n解析视频地址...");
                    }else {
                        mClarityView.setText(streamStr);
                        mStateView.setText(mStateView.getText() + "[成功]\n解析" + streamStr + "视频地址...");
                    }
                    break;
                case MSG_CACHE_VIDEO:
                    PlayVideo video = (PlayVideo) msg.obj;
                    mStateView.setText(mStateView.getText() + "[成功]\n开始缓存" + video.getText() + "视频...");
                    cacheVideo(video);

                    mVideoView.stopPlayback();
                    Log.e(TAG, "setVideoURI=" + video.getUrl());
                    mVideoView.setVideoURI(Uri.parse(video.getUrl()));

                    if (mCurrentPosition != 0) {
                        Log.e(TAG, "seekTo=" + mCurrentPosition);
                        mVideoView.seekTo(mCurrentPosition);
                    }
                    break;
                case MSG_PALY_VIDEO:
                    mStateView.setText(mStateView.getText() + "[成功]\n开始播放...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStateView.setText("");
                        }
                    }, 2222);
                    break;
                case MSG_SET_TITLE:
                    Log.e(TAG, "setTitle=" + msg.obj);
                    mTitleView.setText((String) msg.obj);
                    break;
                case MSG_UPDATE_SELECT:
                    if (mVideoList.isEmpty()) {
                        mSelectView.setVisibility(View.GONE);
                    } else {
                        mSelectView.setVisibility(View.VISIBLE);
                    }
                    break;
                case MSG_UPDATE_NEXT:
                    String nid = (String) msg.obj;
                    Log.e(TAG, "mVid="+mVid+", next vid=" + nid);

                    if(mVid.equals(nid)){
                        mNextView.setVisibility(View.GONE);
                    }else{
                        mVid = nid;
                        mNextView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    protected abstract void cacheVideo(PlayVideo video);
    protected abstract void playVideo();

}
