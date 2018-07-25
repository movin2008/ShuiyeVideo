package com.shuiyes.video;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
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

import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.util.YoukuUtils;
import com.shuiyes.video.widget.ClarityView;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;

public class YoukuActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private TextView mTitleView;
    private TextView mStateView;
    private VideoView mVideoView;
    private boolean mPrepared = false;
    private ProgressBar mLoadingProgress;
    private Button mClarity, mSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mContext = this;
        mLoadingProgress = (ProgressBar) findViewById(R.id.loading);

        mClarity = (Button) findViewById(R.id.btn_clarity);
        mClarity.setOnClickListener(this);
        mSelect = (Button) findViewById(R.id.btn_select);
        mSelect.setOnClickListener(this);

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

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e("HAHA", " =========================== onPrepared");
                mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

                mPrepared = true;
                mHandler.sendEmptyMessage(MSG_PALY_VIDEO);
                mediaPlayer.start();
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e("HAHA", " =========================== onError(" + i + "," + i1 + ")");
                String err = "视频无法播放(" + i + "," + i1 + ")";
                Tips.show(mContext, err, 0);
                fault(err);
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("HAHA", " =========================== onCompletion");
                if (!mIsError) {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    playVideo();
                }
            }
        });

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

        String url = getIntent().getStringExtra("url");
        Log.e("HAHA", "now url=" + url);

        String key = "show/id_";
//        if(url.contains("soku.com")){
//            key = "show/";
//        }
        int index = url.indexOf(key);
        if (url.indexOf(".html") != -1) {
            mVid = url.substring(index + key.length(), url.indexOf(".html"));
        } else {
            mVid = url.substring(index + key.length());
        }
        Log.e("HAHA", "now mVid=" + mVid);


        mTitleView.setText(getIntent().getStringExtra("title"));

        playVideo();
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

    private String mVid;
    private String mTitle;
    private String mToken;
    /**
     * 专辑可选集
     */
    private boolean mIsAlbum;
    private List<ListVideo> mVideoList = new ArrayList<ListVideo>();
    private List<PlayVideo> mUrlList = new ArrayList<PlayVideo>();

    private void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                    if (mToken == null) {
                        mToken = YoukuUtils.fetchCna();
                        Log.e("HAHA", "new mToken=" + mToken);
                    } else {
                        Log.e("HAHA", "prev mToken=" + mToken);
                    }

                    if (mToken == null) {
                        fault("鉴权异常请重试");
                        return;
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    String video = YoukuUtils.fetchVideo(mVid, mToken);

                    if (video == null) {
                        fault("解析异常请重试");
                        return;
                    }

                    File file = new File("/sdcard/video");
                    if (file.exists()) {
                        file.delete();
                    }
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    bw.write(video);
                    bw.close();


                    JSONObject data = new JSONObject(video).getJSONObject("data");

                    if (data.has("error")) {
                        fault(data.getJSONObject("error").getString("note"));
                        return;
                    }

                    JSONObject videoInfo = data.getJSONObject("video");
                    Message msg = mHandler.obtainMessage(MSG_SET_TITLE);
                    msg.obj = videoInfo.getString("title");
                    mHandler.sendMessage(msg);

                    mIsAlbum = data.has("videos");
                    if (mIsAlbum) {
                        JSONObject videos = data.getJSONObject("videos");

                        if (videos.has("next")) {
                            mVid = videos.getJSONObject("next").getString("encodevid");
                            Log.e("HAHA", "next vid=" + mVid);
                        }
                        if (videos.has("list")) {
                            mVideoList.clear();

                            JSONArray videoList = videos.getJSONArray("list");
                            for (int i = 0; i < videoList.length(); i++) {
                                JSONObject listVideo = (JSONObject) videoList.get(i);
                                String encodevid = listVideo.getString("encodevid");
                                String title = listVideo.getString("title");
                                mVideoList.add(new ListVideo(i + 1, title, encodevid));
                            }
                            Log.e("HAHA", "VideoList=" + mVideoList.size());
                        }
                    }

                    mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);

                    JSONArray streams = data.getJSONArray("stream");
                    int streamsLen = streams.length();

                    mUrlList.clear();
                    for (int i = 0; i < streamsLen; i++) {
                        JSONObject stream = (JSONObject) streams.get(i);

                        int size = stream.getInt("size");
                        String stream_type = stream.getString("stream_type");
                        String m3u8Url = stream.getString("m3u8_url");

                        mUrlList.add(new PlayVideo(PlayVideo.formateVideoType(stream_type), size, m3u8Url));
                    }

                    Log.e("HAHA", "UrlList=" + mUrlList.size() + "/" + streamsLen);
                    if (mUrlList.isEmpty()) {
                        fault("无视频地址");
                    } else {
                        Collections.sort(mUrlList, new Comparator<PlayVideo>() {
                            @Override
                            public int compare(PlayVideo v1, PlayVideo v2) {
                                return v2.getSize() - v1.getSize();
                            }
                        });

                        for (PlayVideo v : mUrlList) {
                            Log.i("HAHA", v.toStr(mContext));
                        }

                        mCurrentPosition = 0;

                        msg = mHandler.obtainMessage(MSG_CACHE_VIDEO);
                        msg.obj = mUrlList.get(0);
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.e("HAHA", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean mIsError;

    private void fault(String text) {
        mIsError = true;

        Message msg = mHandler.obtainMessage(MSG_FAULT);
        msg.obj = text;
        mHandler.sendMessage(msg);
    }

    private final int MSG_FAULT = 0;
    private final int MSG_FETCH_TOKEN = 1;
    private final int MSG_FETCH_VIDEO = 2;
    private final int MSG_CACHE_VIDEO = 3;
    private final int MSG_PALY_VIDEO = 4;
    private final int MSG_SET_TITLE = 5;
    private final int MSG_UPDATE_SELECT = 6;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FAULT:
                    Object error = msg.obj;
                    mLoadingProgress.setVisibility(View.GONE);
                    mStateView.setText(mStateView.getText() + "[失败]\n" + (error != null ? error : "") + " 5s后返回...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 5555);
                    break;
                case MSG_FETCH_TOKEN:
                    mStateView.setText("初始化...[成功]\n获取Token...");
                    mStateView.setVisibility(View.VISIBLE);
                    break;
                case MSG_FETCH_VIDEO:
                    mStateView.setText(mStateView.getText() + "[成功]\n解析视频地址...");
                    break;
                case MSG_CACHE_VIDEO:
                    if (!mUrlList.isEmpty()) {
                        if (mUrlList.size() > 1) {
                            mClarity.setEnabled(true);
                        } else {
                            mClarity.setEnabled(false);
                        }
                    }

                    PlayVideo video = (PlayVideo) msg.obj;
                    String profile = video.getType().getProfile();
                    mClarity.setText(profile);
                    mStateView.setText(mStateView.getText() + "[成功]\n开始缓存" + profile + "视频...");

                    mVideoView.stopPlayback();
                    Log.e("HAHA", "setVideoURI=" + video.getUrl());
                    mVideoView.setVideoURI(Uri.parse(video.getUrl()));
                    if (mCurrentPosition != 0) {
                        Log.e("HAHA", "seekTo=" + mCurrentPosition);
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
                    }, 1111);
                    break;
                case MSG_SET_TITLE:
                    Log.e("HAHA", "setTitle=" + msg.obj);
                    mTitleView.setText((String) msg.obj);
                case MSG_UPDATE_SELECT:
                    if (mVideoList.isEmpty()) {
                        mSelect.setVisibility(View.GONE);
                    } else {
                        mSelect.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

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

    private ClarityDialog mClarityDialog;
    private AlbumDialog mAlbumDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clarity:
                if (mClarityDialog != null && mClarityDialog.isShowing()) {
                    mClarityDialog.dismiss();
                }
                mClarityDialog = new ClarityDialog(this, mUrlList);
                mClarityDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mClarityDialog != null && mClarityDialog.isShowing()) {
                            mClarityDialog.dismiss();
                        }

                        mStateView.setText("初始化...");

                        Message msg = mHandler.obtainMessage(MSG_CACHE_VIDEO);
                        msg.obj = ((ClarityView) view).getPlayVideo();
                        mHandler.sendMessage(msg);
                    }
                });
                mClarityDialog.show();
                break;
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
                        mTitleView.setText(v.getTitle());
                        mVid = v.getUrl();

                        mVideoView.stopPlayback();

                        playVideo();
                    }
                });
                mAlbumDialog.show();
                break;
        }
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
