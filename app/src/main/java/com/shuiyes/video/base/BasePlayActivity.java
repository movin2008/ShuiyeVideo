package com.shuiyes.video.base;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.AlbumDialog;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BasePlayActivity extends BaseActivity implements View.OnClickListener {

    protected Context mContext;
    protected VideoView mVideoView;
    protected ProgressBar mLoadingProgress;
    protected TextView mTitleView, mStateView, mTimeView;
    protected Button mDownloadView, mSourceView, mClarityView, mSelectView, mNextView;

    protected boolean mPrepared = false;
    protected String mBatName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mContext = this;

        mDownloadView = (Button) findViewById(R.id.btn_download);
        mSourceView = (Button) findViewById(R.id.btn_source);
        mClarityView = (Button) findViewById(R.id.btn_clarity);
        mSelectView = (Button) findViewById(R.id.btn_select);
        mNextView = (Button) findViewById(R.id.btn_next);

        mDownloadView.setOnClickListener(this);
        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

        mDownloadView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mPlayUrl));
                startActivity(intent);
                return true;
            }
        });

        mTitleView = (TextView) findViewById(R.id.tv_title);
        mStateView = (TextView) findViewById(R.id.tv_state);
        mTimeView = (TextView) findViewById(R.id.tv_time);

        mLoadingProgress = (ProgressBar) findViewById(R.id.loading);
        mVideoView = (VideoView) findViewById(R.id.videoview);
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
                String err = "onError(" + i + "," + i1 + ")";
                Log.e(TAG, " =========================== " + err);
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
                    mCurrentPosition = 0;
                    mLoadingProgress.setVisibility(View.VISIBLE);

                    completionToPlayNextVideo();
                }
            }
        });

        mIntentUrl = getIntent().getStringExtra("url");
        Log.e(TAG, "play url=" + mIntentUrl);

        String title = getIntent().getStringExtra("title");
        Log.e(TAG, "play title=" + title);

        mTitleView.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPrepared && !mIsError && !mVideoView.isPlaying()) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            if (mStateView.getText().length() == 0) {
                mStateView.setText("视频恢复中...");
            }
            if (mCurrentPosition > 0) {
                mVideoView.seekTo(mCurrentPosition);
            }
            mVideoView.start();
        }
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        mHandler.removeMessages(MSG_UPDATE_TIME);
    }

    protected MiscDialog mSourceDialog, mClarityDialog;
    protected AlbumDialog mAlbumDialog;

    @Override
    protected void onDestroy() {
        if (mSourceDialog != null && mSourceDialog.isShowing()) {
            mSourceDialog.dismiss();
        }
        if (mClarityDialog != null && mClarityDialog.isShowing()) {
            mClarityDialog.dismiss();
        }
        if (mAlbumDialog != null && mAlbumDialog.isShowing()) {
            mAlbumDialog.dismiss();
        }
        mVideoView.stopPlayback();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                if (mAlbumDialog != null && mAlbumDialog.isShowing()) {
                    mAlbumDialog.dismiss();
                }
                mAlbumDialog = new AlbumDialog(this, mVideoList);
                mAlbumDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mAlbumDialog != null && mAlbumDialog.isShowing()) {
                            mAlbumDialog.dismiss();
                        }

                        NumberView v = (NumberView) view;
                        playNextVideo(v.getTitle(), v.getUrl());
                    }
                });
                mAlbumDialog.show(getPlayIndex());
                break;
            case R.id.btn_next:
                playNextVideo();
                break;
            case R.id.btn_download:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", mPlayUrl);
                cm.setPrimaryClip(mClipData);

                Toast.makeText(this, "视频网址已复制到剪切板，到下载软件粘贴。 也可长按打开浏览器播放", 1).show();
            default:
                break;
        }
    }

    protected String mVid, mIntentUrl, mPlayUrl, mStream;
    protected PlayVideo mNextPlayVideo;// just for youku
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
        fault(e.getClass().toString() + " " + e.getLocalizedMessage());
    }

    private void playUrl(String url) {
        mPlayUrl = url;
        mPrepared = false;

        mLoadingProgress.setVisibility(View.VISIBLE);
        mDownloadView.setVisibility(View.VISIBLE);

        mVideoView.stopPlayback();
        try {
            mVideoView.setVideoURI(Uri.parse(url));
        } catch (Exception e) {
            Log.e(TAG, "setVideoURI(" + url + ") " + e.getLocalizedMessage());
        }

        if (mCurrentPosition != 0) {
            Log.e(TAG, "seekTo=" + mCurrentPosition);
            mVideoView.seekTo(mCurrentPosition);
        }
    }

    protected int getPlayIndex() {
        int index = 0;
        for (int i = 0; i < mVideoList.size() - 1; i++) {
            Log.e(TAG, i + ", getPlayIndex(" + mIntentUrl + ") " + mVideoList.get(i).getUrl());
            if (mIntentUrl.equals(mVideoList.get(i).getUrl())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getNextIndex() {
        int index = getPlayIndex() + 1;
        if (index == mVideoList.size()) {
            index = 0;
        }
        return index;
    }

    protected void playNextVideo() {
        playNextVideo(mNextPlayVideo.getText(), mNextPlayVideo.getUrl());
    }

    protected void playNextVideo(String title, String url) {
        mVideoView.stopPlayback();
        mTitleView.setText(title);
        mStateView.setText("初始化...");
        mLoadingProgress.setVisibility(View.VISIBLE);

        mIntentUrl = url;
        mPrepared = false;
        mCurrentPosition = 0;

        playVideo();
    }

    protected void completionToPlayNextVideo() {
        Log.e(TAG, "completionToPlayNextVideo " + mVideoList.size());
        if (mVideoList.size() > 0) {
            ListVideo video = mVideoList.get(getNextIndex());
            playNextVideo(video.getTitle(), video.getUrl());
        } else {
            playVideo();
        }
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
    protected final int MSG_CACHE_URL = 9;
    protected final int MSG_UPDATE_TIME = 10;
    protected final int MSG_FETCH_VIDEOID = 11;

    @Override
    public void handleOtherMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_TIME:
                Calendar now = Calendar.getInstance();
                mTimeView.setText(String.format("%s %02d:%02d:%02d", mBatName, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND)));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 1000);
                break;
            case MSG_FAULT:
                Object error = msg.obj;
                mLoadingProgress.setVisibility(View.GONE);
                mStateView.setText(mStateView.getText() + "[失败]\n" + (error != null ? error : "") /*+ " 5s后返回..."*/);

//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 5555);
                break;
            case MSG_FETCH_TOKEN:
                mStateView.setText(mStateView.getText() + "[成功]\n获取授权信息...");
                mStateView.setVisibility(View.VISIBLE);
                break;
            case MSG_FETCH_VIDEOID:
                mStateView.setText(mStateView.getText() + "[成功]\n获取视频ID...");
                mStateView.setVisibility(View.VISIBLE);
                break;
            case MSG_FETCH_VIDEOINFO:
                mStateView.setText(mStateView.getText() + "[成功]\n获取视频信息...");
                mStateView.setVisibility(View.VISIBLE);
                break;
            case MSG_FETCH_VIDEO:
                String streamStr = (String) msg.obj;
                if (TextUtils.isEmpty(streamStr)) {
                    mStateView.setText(mStateView.getText() + "[成功]\n解析视频地址...");
                } else {
                    mClarityView.setText(streamStr);
                    mStateView.setText(mStateView.getText() + "[成功]\n解析" + streamStr + "视频地址...");
                }
                break;
            case MSG_CACHE_VIDEO:
                PlayVideo video = (PlayVideo) msg.obj;
                mStateView.setText(mStateView.getText() + "[成功]\n开始缓存" + video.getText() + "视频...");
                cacheVideo(video);
                playUrl(video.getUrl());
                break;
            case MSG_CACHE_URL:
                String url = (String) msg.obj;
                mStateView.setText(mStateView.getText() + "[成功]\n开始缓存视频...");
                playUrl(url);
                break;
            case MSG_PALY_VIDEO:
                mLoadingProgress.setVisibility(View.GONE);
                if (mStateView.getText().length() != 0) {
                    mStateView.setText(mStateView.getText() + "[成功]\n开始播放...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStateView.setText("");
                        }
                    }, 3333);
                }
                break;
            case MSG_SET_TITLE:
                Log.e(TAG, "setTitle=" + msg.obj);
                mTitleView.setText((String) msg.obj);
                break;
            case MSG_UPDATE_SELECT:
                if (mVideoList.size() < 2) {
                    mSelectView.setVisibility(View.GONE);
                } else {
                    mSelectView.setVisibility(View.VISIBLE);
                }
                break;
            case MSG_UPDATE_NEXT:
                PlayVideo nVideo = (PlayVideo) msg.obj;
                Log.e(TAG, "mVid=" + mVid + ", next vid=" + nVideo.getUrl());

                if (mVid.equals(nVideo.getUrl())) {
                    mNextView.setVisibility(View.GONE);
                } else {
                    mNextPlayVideo = nVideo;
                    mNextView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    protected abstract void playVideo();

    protected abstract void cacheVideo(PlayVideo video);

}
