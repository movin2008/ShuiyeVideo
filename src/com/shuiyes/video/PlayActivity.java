package com.shuiyes.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.shuiyes.video.bean.Video;
import com.shuiyes.video.util.YoukuUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayActivity extends Activity {

    private Context mContext;
    private TextView mStateView;
    private VideoView mVideoView;
    private boolean mPrepared = false;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mContext = this;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCanceledOnTouchOutside(false);

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
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                if(!mPrepared){
                    mPrepared = true;


                    mHandler.sendEmptyMessage(MSG_PALY_VIDEO);
                    mediaPlayer.start();
                }
            }
        });

        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e("HAHA", " =========================== onError(" + i + "," + i1 + ")");

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                mHandler.sendEmptyMessage(MSG_FAULT);
                Toast.makeText(mContext, "视频无法播放(" + i + "," + i1 + ")", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("HAHA", " =========================== onCompletion");
                playVideo();
            }
        });

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                Log.e("HAHA", " =========================== onInfo("+what+", "+extra+")");

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                    Log.e("HAHA", " =========================== MEDIA_INFO_VIDEO_RENDERING_START");
                }else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                    Log.e("HAHA", " =========================== MEDIA_INFO_BUFFERING_END");
                }

                return false;
            }
        });

        mVid = getIntent().getStringExtra("vid");
        ((TextView)findViewById(R.id.tv_title)).setText(getIntent().getStringExtra("title"));

        playVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        if (mPrepared && !mVideoView.isPlaying()) {
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
    private List<String> mVideoList = new ArrayList<String>();
    private List<Video> mUrlList = new ArrayList<Video>();

    private void playVideo() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                    if (mToken == null) {
                        mToken = YoukuUtils.fetchCna();
                        Log.e("HAHA", "new mToken=" + mToken);
                    }else{
                        Log.e("HAHA", "prev mToken=" + mToken);
                    }

                    if (mToken == null) {
                        mHandler.sendEmptyMessage(MSG_FAULT);
                        return;
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    String video = YoukuUtils.fetchVideo(YoukuUtils.getVideoUrl(mVid, mToken));

                    if (video == null) {
                        mHandler.sendEmptyMessage(MSG_FAULT);
                        return;
                    }

                    JSONObject obj = new JSONObject(video);
                    JSONObject videos = obj.getJSONObject("data").getJSONObject("videos");
                    mVid = videos.getJSONObject("next").getString("encodevid");
                    Log.e("HAHA", "next vid="+mVid);

                    if(mVideoList.isEmpty()){
                        JSONArray videoList = videos.getJSONArray("list");
                        for (int i=0; i<videoList.length(); i++){
                            mVideoList.add(((JSONObject)videoList.get(i)).getString("encodevid"));
                        }
                        Log.e("HAHA", "VideoList="+mVideoList.size());
                    }

                    JSONArray streams = obj.getJSONObject("data").getJSONArray("stream");
                    Log.e("HAHA", "video=" + streams.length());

                    mVideoList.clear();
                    for (int i = 0; i < streams.length(); i++) {
                        JSONObject stream = (JSONObject) streams.get(i);

                        int size = stream.getInt("size");
                        String stream_type = stream.getString("stream_type");
                        String m3u8Url = stream.getString("m3u8_url");

                        mUrlList.add(new Video(Video.formateVideoType(stream_type), size, m3u8Url));
                    }

                    Log.e("HAHA", "UrlList=" + mUrlList.size());
                    if(mUrlList.isEmpty()){
                        mHandler.sendEmptyMessage(MSG_FAULT);
                    }else{
                        mHandler.sendEmptyMessage(MSG_CACHE_VIDEO);

                        Collections.sort(mUrlList, new Comparator<Video>() {
                            @Override
                            public int compare(Video v1, Video v2) {
                                return v2.getSize() - v1.getSize();
                            }
                        });

                        for (Video v:mUrlList) {
                            Log.i("HAHA", v.toStr(mContext));
                        }

                        Message msg = mHandler.obtainMessage(MSG_SET_VIDEO);
                        msg.obj = mUrlList.get(0).getUrl();
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private final int MSG_FAULT = 0;
    private final int MSG_FETCH_TOKEN = 1;
    private final int MSG_FETCH_VIDEO = 2;
    private final int MSG_CACHE_VIDEO = 3;
    private final int MSG_PALY_VIDEO = 4;
    private final int MSG_SET_VIDEO = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FAULT:
                mStateView.setText(mStateView.getText() + "[失败]\n3s后返回...");
                mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3333);
                break;
                case MSG_FETCH_TOKEN:
                    mStateView.setText("初始化...[成功]\n获取Token...");
                    mStateView.setVisibility(View.VISIBLE);
                    break;
                case MSG_FETCH_VIDEO:
                    mStateView.setText(mStateView.getText() + "[成功]\n解析视频地址...");
                    break;
                case MSG_CACHE_VIDEO:
                    mStateView.setText(mStateView.getText() + "[成功]\n开始缓存高清视频...");
                    break;
                case MSG_PALY_VIDEO:
                    mStateView.setText(mStateView.getText() + "[成功]\n开始播放...");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStateView.setVisibility(View.GONE);
                        }
                    }, 1111);
                    break;
                case MSG_SET_VIDEO:
                    Log.e("HAHA", "setVideoURI=" + msg.obj);
                    mVideoView.setVideoURI(Uri.parse(String.valueOf(msg.obj)));
                    break;
            }
        }
    };

    private long mPrevBackTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long time = System.currentTimeMillis();
            if (time - mPrevBackTime < 2000) {
                finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
            mPrevBackTime = time;
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
