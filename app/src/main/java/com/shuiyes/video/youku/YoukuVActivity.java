package com.shuiyes.video.youku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.dialog.AlbumDialog;
import com.shuiyes.video.base.PlayActivity;
import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;

public class YoukuVActivity extends PlayActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClarityView.setOnClickListener(this);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, " =========================== onPrepared");
                mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

                mPrepared = true;
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

        String key = "show/id_";
        int index = mUrl.indexOf(key);
        if (mUrl.indexOf(".html") != -1) {
            mVid = mUrl.substring(index + key.length(), mUrl.indexOf(".html"));
        } else {
            mVid = mUrl.substring(index + key.length());
        }
        Log.e(TAG, "now mVid=" + mVid);

        playVideo();
    }

    private String mToken;

    /**
     * 专辑可选集
     */
    private boolean mIsAlbum;
    private List<YoukuVideo> mUrlList = new ArrayList<YoukuVideo>();

    private void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                    if (mToken == null) {
                        mToken = YoukuUtils.fetchCna();
                        Log.e(TAG, "new mToken=" + mToken);
                    } else {
                        Log.e(TAG, "prev mToken=" + mToken);
                    }

                    if (mToken == null) {
                        fault("鉴权异常请重试");
                        return;
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    String info = HttpUtils.open(YoukuUtils.getVideoUrl(mVid, mToken));

                    if (info == null) {
                        fault("解析异常请重试");
                        return;
                    }

                    Utils.setFile("/sdcard/youku", info);

                    JSONObject data = new JSONObject(info).getJSONObject("data");

                    if (data.has("error")) {
                        fault(data.getJSONObject("error").getString("note"));
                        return;
                    }

                    JSONObject video = data.getJSONObject("video");

                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, video.getString("title")));

                    mIsAlbum = data.has("videos");
                    if (mIsAlbum) {
                        JSONObject videos = data.getJSONObject("videos");

                        if (videos.has("next")) {
                            String nid = videos.getJSONObject("next").getString("encodevid");
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, nid));
                        }else{
                            Log.e(TAG, "No next video.");
                        }
//                        Log.e(TAG, "videos=" + videos);

                        if (videos.has("list")) {
                            mVideoList.clear();

                            JSONArray videoList = videos.getJSONArray("list");
                            for (int i = 0; i < videoList.length(); i++) {
                                JSONObject listVideo = (JSONObject) videoList.get(i);
                                String encodevid = listVideo.getString("encodevid");
                                String title = listVideo.getString("title");
                                mVideoList.add(new ListVideo(i + 1, title, encodevid));
                            }
                            Log.e(TAG, "VideoList=" + mVideoList.size());
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

                        mUrlList.add(new YoukuVideo(YoukuVideo.formateVideoType(stream_type), size, m3u8Url));
                    }

                    Log.e(TAG, "UrlList=" + mUrlList.size() + "/" + streamsLen);
                    if (mUrlList.isEmpty()) {
                        fault("无视频地址");
                    } else {
                        Collections.sort(mUrlList, new Comparator<YoukuVideo>() {
                            @Override
                            public int compare(YoukuVideo v1, YoukuVideo v2) {
                                return v2.getSize() - v1.getSize();
                            }
                        });

                        for (YoukuVideo v : mUrlList) {
                            Log.i(TAG, v.toStr(mContext));
                        }

                        mCurrentPosition = 0;

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, mUrlList.get(0)));
                    }
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, ((MiscView) view).getPlayVideo()));
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
        if (mUrlList.size() < 2) {
            mClarityView.setEnabled(false);
        }else{
            mClarityView.setEnabled(true);
        }

        mClarityView.setText(((YoukuVideo)video).getType().getProfile());
    }

}
