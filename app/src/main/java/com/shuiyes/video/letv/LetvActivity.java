package com.shuiyes.video.letv;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.AlbumDialog;
import com.shuiyes.video.PlayActivity;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.widget.MiscDialog;
import com.shuiyes.video.widget.MiscView;
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

public class LetvActivity extends PlayActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

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


    /**
     * 专辑可选集
     */
    private boolean mIsAlbum;
    private List<ListVideo> mVideoList = new ArrayList<ListVideo>();
    private List<LetvStream> mUrlList = new ArrayList<LetvStream>();
    private List<LetvSource> mSourceList = new ArrayList<LetvSource>();

    private void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String info = LetvUtils.fetchVideo(LetvUtils.getVideoInfoUrl(mVid), false);

                    File file = new File("/sdcard/LetvStream");
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

                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, playurl.getString("title")));

                    JSONArray domain = playurl.getJSONArray("domain");
                    JSONObject dispatch = playurl.getJSONObject("dispatch");

                    if (playurl.has("nextvid")) {
                        String nid = playurl.getInt("nextvid") + "";
                        Log.e("HAHA", "next vid=" + nid);
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, nid));
                    }else{
                        Log.e("HAHA", "No next video.");
                    }

                    String host = domain.getString(0);

                    mUrlList.clear();
                    int streamID = 0;
                    Iterator<String> iterator = dispatch.keys();
                    while (iterator.hasNext()) {
                        int tmp = Integer.parseInt(iterator.next());
                        mUrlList.add(new LetvStream(tmp, host + dispatch.getJSONArray(tmp + "").get(0)));
                        if (tmp > streamID) {
                            streamID = tmp;
                        }
                    }

                    for (LetvStream v : mUrlList) {
                        Log.i("HAHA", v.toStr(mContext));
                    }

                    String url = host + dispatch.getJSONArray(streamID + "").get(0);
                    playUrl(url, streamID + "P");
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void playUrl(String url, String streamStr) throws Exception {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FETCH_VIDEO, streamStr));

        String video = LetvUtils.fetchVideo(LetvUtils.getVideoPlayUrl(url, mVid), false);

        if (video == null) {
            fault("解析异常请重试");
            return;
        }

        File file = new File("/sdcard/LetvSource");
        if (file.exists()) {
            file.delete();
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        bw.write(video);
        bw.close();

        JSONObject data = new JSONObject(video);
        JSONArray streams = data.getJSONArray("nodelist");
        int streamsLen = streams.length();

        mSourceList.clear();
        for (int i = 0; i < streamsLen; i++) {
            JSONObject stream = (JSONObject) streams.get(i);

            String m3u8Url = stream.getString("location");
            String name = stream.getString("name").replace("中国", "").replaceAll("-", "");

            mSourceList.add(new LetvSource(streamStr, name, m3u8Url));
        }

        Log.e("HAHA", "UrlList=" + mSourceList.size() + "/" + streamsLen);
        if (mSourceList.isEmpty()) {
            fault("无视频源地址");
        } else {
            for (LetvSource v : mSourceList) {
                Log.i("HAHA", v.toStr(mContext));
            }

            mCurrentPosition = 0;

            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, mSourceList.get(0)));
        }
    }

    private MiscDialog mSourceDialog;
    private MiscDialog mClarityDialog;
    private AlbumDialog mAlbumDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_source:
                if (mSourceDialog != null && mSourceDialog.isShowing()) {
                    mSourceDialog.dismiss();
                }
                mSourceDialog = new MiscDialog(this, mSourceList);
                mSourceDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSourceDialog != null && mSourceDialog.isShowing()) {
                            mSourceDialog.dismiss();
                        }

                        mStateView.setText("初始化...");

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, ((MiscView) view).getPlayVideo()));
                    }
                });
                mSourceDialog.show();
                break;
            case R.id.btn_clarity:
                if (mClarityDialog != null && mClarityDialog.isShowing()) {
                    mClarityDialog.dismiss();
                }
                mClarityDialog = new MiscDialog(this, mUrlList);
                mClarityDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mClarityDialog != null && mClarityDialog.isShowing()) {
                            mClarityDialog.dismiss();
                        }

                        mStateView.setText("初始化...");


                        final LetvStream stream = (LetvStream) ((MiscView) view).getPlayVideo();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    playUrl(stream.getUrl(), stream.getStream());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    fault(e);
                                }
                            }
                        }).start();

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
            case R.id.btn_next:
                playVideo();
                break;
        }
    }

    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mSourceList.isEmpty()) {
            mSourceView.setVisibility(View.GONE);
        } else {
            mSourceView.setVisibility(View.VISIBLE);
            if (mSourceList.size() > 1) {
                mSourceView.setEnabled(true);
            } else {
                mSourceView.setEnabled(false);
            }
        }
    }

}
