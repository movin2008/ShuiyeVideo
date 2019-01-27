package com.shuiyes.video.base;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.AlbumDialog;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class BasePlayerExoActivity extends BaseActivity implements View.OnClickListener, MediaSourceEventListener {

    protected Context mContext;
    protected ProgressBar mLoadingProgress;
    protected TextView mTitleView, mStateView, mTimeView;
    protected Button mSourceView, mClarityView, mSelectView, mNextView;

    protected boolean mPrepared = false;
    protected String mBatName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        mContext = this;

        mSourceView = (Button) findViewById(R.id.btn_source);
        mClarityView = (Button) findViewById(R.id.btn_clarity);
        mSelectView = (Button) findViewById(R.id.btn_select);
        mNextView = (Button) findViewById(R.id.btn_next);

        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

        mTitleView = (TextView) findViewById(R.id.tv_title);
        mStateView = (TextView) findViewById(R.id.tv_state);
        mTimeView = (TextView) findViewById(R.id.tv_time);

        mLoadingProgress = (ProgressBar) findViewById(R.id.loading);

        initExoPlayer();

        mIntentUrl = getIntent().getStringExtra("url");
        Log.e(TAG, "play url=" + mIntentUrl);

        String title = getIntent().getStringExtra("title");
        Log.e(TAG, "play title=" + title);

        mTitleView.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPrepared && !mIsError) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            if (mStateView.getText().length() == 0) {
                mStateView.setText("视频恢复中...");
            }
        }
        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
        if(mExoPlayer != null){
            mExoPlayer.setPlayWhenReady(true);
        }
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
        if(mExoPlayer != null){
            mExoPlayer.setPlayWhenReady(false);
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
        mExoPlayer.release();
        mExoPlayer = null;
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
                mPlayerView.requestFocus();
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
            default:
                break;
        }
    }

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private void initExoPlayer() {
        mPlayerView = (PlayerView) this.findViewById(R.id.exoplayer_view);
        mPlayerView.requestFocus();
        mPlayerView.setControllerAutoShow(false);
        mPlayerView.setUseController(false);
        mPlayerView.setKeepScreenOn(true);


        DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(this,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        TrackSelector trackSelector = new DefaultTrackSelector();
        DefaultLoadControl.Builder builder = new DefaultLoadControl.Builder();
        builder.setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE));
        builder.setBufferDurationsMs(2000, 15000, 1500, 0
        );
        LoadControl loadControl = builder.createDefaultLoadControl();

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, rendererFactory, trackSelector, loadControl);
        mExoPlayer.addVideoListener(new com.google.android.exoplayer2.video.VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            }

            @Override
            public void onRenderedFirstFrame() {
            }
        });
        mExoPlayer.addMetadataOutput(new MetadataOutput() {
            @Override
            public void onMetadata(Metadata metadata) {

            }
        });
        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.e(TAG, "onLoadingChanged "+isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e(TAG, "onPlayerStateChanged playWhenReady="+playWhenReady+", playbackState="+playbackState);
                if(playbackState == 3){
                    mHandler.sendEmptyMessage(MSG_PALY_VIDEO);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError ");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.e(TAG, "onPositionDiscontinuity "+reason);
            }

            @Override
            public void onSeekProcessed() {
                Log.e(TAG, "onSeekProcessed ");
            }
        });

        mPlayerView.setPlayer(mExoPlayer);
    }

    private void startPlayback(String url) {
        Log.e(TAG, "startPlayback " + url);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, mIntentUrl, null);
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));
        videoSource = new HlsMediaSource(Uri.parse(url), dataSourceFactory, null, this);

        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.prepare(videoSource);
    };

    protected String mVid, mIntentUrl, mPlayUrl, mStream;
    protected List<ListVideo> mVideoList = new ArrayList<ListVideo>();

    protected int mCurrentPosition;

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

        mExoPlayer.stop(true);
        startPlayback(url);

        if (mCurrentPosition != 0) {
            Log.e(TAG, "seekTo=" + mCurrentPosition);
            mExoPlayer.seekTo(mCurrentPosition);
        }
    }

    protected int getPlayIndex(){
        int index = 0;
        for (int i = 0; i < mVideoList.size() - 1; i++) {
            if (mIntentUrl.equals(mVideoList.get(i).getUrl())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getNextIndex(){
        int index = getPlayIndex();
        if(index == mVideoList.size()){
            index = 0;
        }
        return index;
    }

    private void playNextVideo() {
        mExoPlayer.stop(true);
        mStateView.setText("初始化...");
        mLoadingProgress.setVisibility(View.VISIBLE);

        mPrepared = false;
        mCurrentPosition = 0;

        playVideo();
    }

    protected void playNextVideo(String title, String url) {
        mExoPlayer.stop(true);
        mTitleView.setText(title);
        mStateView.setText("初始化...");
        mLoadingProgress.setVisibility(View.VISIBLE);

        mIntentUrl = url;
        mPrepared = false;
        mCurrentPosition = 0;

        playVideo();
    }

    protected void completionToPlayNextVideo() {
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
                if(mStateView.getText().length() != 0){
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
                String nid = (String) msg.obj;
                Log.e(TAG, "mVid=" + mVid + ", next vid=" + nid);

                if (mVid.equals(nid)) {
                    mNextView.setVisibility(View.GONE);
                } else {
                    mVid = nid;
                    mNextView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    protected abstract void playVideo();

    protected abstract void cacheVideo(PlayVideo video);

    @Override
    public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        Log.e(TAG, " =========================== onMediaPeriodCreated");
    }

    @Override
    public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        Log.e(TAG, " =========================== onMediaPeriodReleased");
    }

    @Override
    public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onLoadStarted");
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onLoadCompleted");
        mLoadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onLoadCanceled");
        mLoadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        String err = "onError(" + error.getLocalizedMessage()+ ")";
        Log.e(TAG, " =========================== "+err);
        Tips.show(mContext, err, 0);
        fault(err);
    }

    @Override
    public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        Log.e(TAG, " =========================== onReadingStarted");
    }

    @Override
    public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onUpstreamDiscarded");
    }

    @Override
    public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onDownstreamFormatChanged");
    }
}
