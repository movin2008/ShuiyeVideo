package com.shuiyes.video.letv;

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

import com.shuiyes.video.AlbumDialog;
import com.shuiyes.video.PlayActivity;
import com.shuiyes.video.widget.FullScreenDialog;
import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class LetvActivity extends PlayActivity implements View.OnClickListener {

    private Button mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSource = (Button) findViewById(R.id.btn_source);

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

        String url = getIntent().getStringExtra("url");
        url = "http://www.le.com/ptv/vplay/26101788.html";
        Log.e("HAHA", "now url=" + url);

        String key = "vplay/";
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

    private Random mRandom = new Random();

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
    private String mToken;
    /**
     * 专辑可选集
     */
    private boolean mIsAlbum;
    private List<ListVideo> mVideoList = new ArrayList<ListVideo>();
    private List<LetvSource> mUrlList = new ArrayList<LetvSource>();
    private List<LetvSource> mSourceList = new ArrayList<LetvSource>();

    private void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String info = LetvUtils.fetchVideo(LetvUtils.getVideoInfoUrl(mVid), false);

                    File file = new File("/sdcard/info");
                    if (file.exists()) {
                        file.delete();
                    }

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    bw.write(info);
                    bw.close();


                    JSONObject data = new JSONObject(info).getJSONObject("msgs");

                    if (data.getInt("statuscode") != 1001) {
                        fault("API 变更");
                        return;
                    }

                    JSONObject playurl = data.getJSONObject("playurl");

                    Message msg = mHandler.obtainMessage(MSG_SET_TITLE);
                    msg.obj = playurl.getString("title");
                    mHandler.sendMessage(msg);

                    JSONArray domain = playurl.getJSONArray("domain");
                    JSONObject dispatch = playurl.getJSONObject("dispatch");

                    if (playurl.has("nextvid")) {
                        mVid = playurl.getInt("nextvid")+"";
                        Log.e("HAHA", "next vid=" + mVid);
                    }

                    Iterator<String> iterator = dispatch.keys();
                    int streamID = 0;
                    while (iterator.hasNext()){
                        int tmp = Integer.parseInt(iterator.next());
                        if(tmp > streamID){
                            streamID = tmp;
                        }
                    }
                    String url = domain.getString(0)+dispatch.getJSONArray(streamID+"").get(0);

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    String video = LetvUtils.fetchVideo(LetvUtils.getVideoPlayUrl(url, mVid), false);

                    if (video == null) {
                        fault("解析异常请重试");
                        return;
                    }

                    file = new File("/sdcard/video");
                    if (file.exists()) {
                        file.delete();
                    }

                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    bw.write(video);
                    bw.close();

                    data = new JSONObject(video);
                    JSONArray streams = data.getJSONArray("nodelist");
                    int streamsLen = streams.length();

                    mSourceList.clear();
                    for (int i = 0; i < streamsLen; i++) {
                        JSONObject stream = (JSONObject) streams.get(i);

                        String m3u8Url = stream.getString("location");
                        String name = stream.getString("name");

                        mSourceList.add(new LetvSource(streamID, name, m3u8Url));
                    }

                    Log.e("HAHA", "SourceList=" + mSourceList.size() + "/" + streamsLen);
                    if (mSourceList.isEmpty()) {
                        fault("无视频源地址");
                    } else {
                        for (LetvSource v : mUrlList) {
                            Log.i("HAHA", v.toStr(mContext));
                        }

                        mCurrentPosition = 0;

                        msg = mHandler.obtainMessage(MSG_CACHE_VIDEO);
                        msg.obj = mSourceList.get(0);
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
    private final int MSG_FETCH_VIDEOINFO = 1;
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
                case MSG_FETCH_VIDEOINFO:
                    mStateView.setText("初始化...[成功]\n获取视频信息...");
                    mStateView.setVisibility(View.VISIBLE);
                    break;
                case MSG_FETCH_VIDEO:
                    mStateView.setText(mStateView.getText() + "[成功]\n解析视频地址...");
                    break;
                case MSG_CACHE_VIDEO:
                    LetvSource video = (LetvSource) msg.obj;
                    String profile = video.getProfile();
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

    private FullScreenDialog mClarityDialog;
    private AlbumDialog mAlbumDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clarity:
                if (mClarityDialog != null && mClarityDialog.isShowing()) {
                    mClarityDialog.dismiss();
                }
                mClarityDialog = new LetvClarityDialog(this, mUrlList);
                mClarityDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mClarityDialog != null && mClarityDialog.isShowing()) {
                            mClarityDialog.dismiss();
                        }

                        mStateView.setText("初始化...");

                        Message msg = mHandler.obtainMessage(MSG_CACHE_VIDEO);
                        msg.obj = ((LetvClarityView) view).getPlayVideo();
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


}
