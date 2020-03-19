package com.shuiyes.video.ui.base;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

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
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.shuiyes.exoplayer.R;

import java.io.IOException;

public abstract class BaseExoPlayerActivity extends Activity implements View.OnClickListener, MediaSourceEventListener {

    private static final String TAG = "ExoActivity";

    protected Context mContext;

    protected boolean mPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        mContext = this;
        initExoPlayer();

        mIntentUrl = getIntent().getStringExtra("url");
        Log.e(TAG, "play url=" + mIntentUrl);

        String title = getIntent().getStringExtra("title");
        Log.e(TAG, "play title=" + title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }


    @Override
    protected void onDestroy() {
        mExoPlayer.release();
        mExoPlayer = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DPAD_UP:
                // TODO
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


        DefaultRenderersFactory rendererFactory = new DefaultRenderersFactory(this, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
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
                Log.e(TAG, "onLoadingChanged " + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e(TAG, "onPlayerStateChanged playWhenReady=" + playWhenReady + ", playbackState=" + playbackState);
                if (playbackState == 3) {
                    // TODO MSG_PALY_VIDEO
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError ");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.e(TAG, "onPositionDiscontinuity " + reason);
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
    }

    ;

    protected String mVid, mIntentUrl, mPlayUrl;

    protected int mCurrentPosition;

    protected boolean mIsError;

    protected void fault(String text) {
        mIsError = true;
    }

    protected void fault(Exception e) {
        fault(e.getClass().toString() + " " + e.getLocalizedMessage());
    }

    private void playUrl(String url) {
        mPlayUrl = url;
        mPrepared = false;

        mExoPlayer.stop(true);
        startPlayback(url);

        if (mCurrentPosition != 0) {
            Log.e(TAG, "seekTo=" + mCurrentPosition);
            mExoPlayer.seekTo(mCurrentPosition);
        }
    }


    protected abstract void playVideo();


    @Override
    public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        Log.e(TAG, " =========================== onMediaPeriodCreated");
    }

    @Override
    public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
        Log.e(TAG, " =========================== onMediaPeriodReleased");
    }

    @Override
    public void onLoadStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onLoadStarted");
        // load start
    }

    @Override
    public void onLoadCompleted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onLoadCompleted");
        // load end
    }

    @Override
    public void onLoadCanceled(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onLoadCanceled");
        // load failed
    }

    @Override
    public void onLoadError(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        String err = "onError(" + error.getLocalizedMessage() + ")";
        Log.e(TAG, " =========================== " + err);
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
    public void onDownstreamFormatChanged(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
        Log.e(TAG, " =========================== onDownstreamFormatChanged");
    }
}
