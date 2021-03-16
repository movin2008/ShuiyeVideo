package com.shuiyes.video.ui.youku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.ui.base.BasePlayActivity;
import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;
import com.shuiyes.video.widget.Tips;

public class YoukuVActivity extends BasePlayActivity {

    private List<YoukuVideo> mUrlList = new ArrayList<YoukuVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "优酷视频";
        mVid = YoukuUtils.getPlayVid(mIntentUrl);

        YoukuUtils.updateCCodeIfNeed(this);
        playVideo();
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
                        YoukuVideo playVideo = (YoukuVideo) ((MiscView) view).getPlayVideo();
                        mStream = playVideo.getType().getType();

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));
                    }
                });
                mClarityDialog.show();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    protected void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                    String token = YoukuUtils.fetchCna();
                    Log.e(TAG, "cna=" + token);

                    if (token == null) {
                        fault("鉴权异常请重试");
                        return;
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    String info = YoukuUtils.fetchVideo(mVid, token);

                    if (TextUtils.isEmpty(info)) {
                        fault("解析异常请重试");
                        return;
                    }

                    Utils.setFile("youku", info);

                    JSONObject data = new JSONObject(info).getJSONObject("data");
                    JSONObject video = data.getJSONObject("video");
                    String title = video.getString("title");

                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));

                    if (data.has("videos")) {
                        JSONObject videos = data.getJSONObject("videos");

                        if (videos.has("next")) {
                            JSONObject next = videos.getJSONObject("next");
                            String ntitle = next.getString("title");
                            String nid = next.getString("encodevid");
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, new PlayVideo(ntitle, nid)));
                        } else {
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, new PlayVideo("", mVid)));
                        }
//                        Log.e(TAG, "videos=" + videos);

                        if (videos.has("list")) {
                            mVideoList.clear();

                            JSONArray videoList = videos.getJSONArray("list");
                            for (int i = 0; i < videoList.length(); i++) {
                                JSONObject listVideo = (JSONObject) videoList.get(i);
                                String encodevid = listVideo.getString("encodevid");
                                String listTitle = listVideo.getString("title");
                                mVideoList.add(new ListVideo((i + 1) + " " + listTitle, listTitle, encodevid));
                            }
                            Log.e(TAG, "VideoList=" + mVideoList.size());

                            mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                        }
                    }

                    if (data.has("error")) {
                        String msg = data.getJSONObject("error").getString("note");
                        // 观看此节目，请先登录！ Maybe VIP video
                        fault(msg, data.getJSONObject("error").getInt("code") == -3007);
                    } else {
                        boolean isVip = data.getJSONObject("show").getInt("video_pay") == 1;
                        if (isVip) {
                            fault("VIP视频只支持6分钟试看", true);
                            return;
                        }

                        JSONArray streams = data.getJSONArray("stream");
                        int streamsLen = streams.length();

                        mUrlList.clear();
                        for (int i = 0; i < streamsLen; i++) {
                            JSONObject stream = (JSONObject) streams.get(i);

                            long size = stream.getLong("size");
                            float duration = stream.getInt("milliseconds_video");
                            // VIP 视频换算为 6分钟试看的大小比例
                            duration = size * (360 / duration);
                            size = isVip ? (long) duration : size;
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
                                    return (int) (v2.getSize() - v1.getSize());
                                }
                            });

                            YoukuVideo playVideo = null;
                            for (YoukuVideo v : mUrlList) {
                                Log.i(TAG, v.toStr() + " mStream=" + mStream);

                                if (playVideo == null || v.getType().getType().equals(mStream)) {
                                    playVideo = v;
                                }
                            }

                            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));
                        }
                    }

//                    listJsonAlbums();
                    listHtmlAlbums(mVid);
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Deprecated
    private void listJsonAlbums() {
        try {
            String html = YoukuUtils.listAlbums(mVid);
            if (html.startsWith("Exception: ")) {
                fault(html);
                return;
            }

            Utils.setFile("album.youku", html);

            JSONObject obj = new JSONObject(html);
            if (obj.getInt("error") == 1) {
                Log.e(TAG, "listJsonAlbums maybe deprecated.");
                return;
            }


            html = obj.getString("html");

            String key = "item item-";
            if (html.contains(key)) {
                mVideoList.clear();
                while (html.contains(key)) {

                    html = html.substring(html.indexOf(key) + key.length());

                    String s = "seq=\"";
                    String tmp = html.substring(html.indexOf(s) + s.length());
                    s = "\"";
                    String seq = tmp.substring(0, tmp.indexOf(s));

                    s = "item-id=\"item_";
                    tmp = html.substring(html.indexOf(s) + s.length());
                    s = "\"";
                    String vid = tmp.substring(0, tmp.indexOf(s));


                    s = "title=\"";
                    tmp = html.substring(html.indexOf(s) + s.length());
                    s = "\"";
                    String title = tmp.substring(0, tmp.indexOf(s));

                    mVideoList.add(new ListVideo(seq, title, vid));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取前后共30集
     */
    private void listHtmlAlbums(String vid) {
        try {
            String html = HttpUtils.get(YoukuUtils.getPlayUrlByVid(vid));
            if (html.startsWith("Exception: ")) {
                Log.e(TAG, html);
                return;
            }

            Utils.setFile("youku.html", html);

            String key = "window.playerAnthology=";
            if (html.contains(key)) {
                String tmp = html.substring(html.indexOf(key) + key.length());
                key = "};";
                if (tmp.contains(key)) {
                    tmp = tmp.substring(0, tmp.indexOf(key) + 1);
                }

                Utils.setFile("album.youku", tmp);

                JSONObject obj = new JSONObject(tmp);
                if (obj.has("next")) {
                    JSONObject next = obj.getJSONObject("next");
                    String ntitle = next.getString("title");
                    String nid = next.getString("encodevid");
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, new PlayVideo(ntitle, nid)));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, new PlayVideo("", mVid)));
                }

                if (obj.has("list")) {
                    JSONArray arr = (JSONArray) obj.get("list");
                    mVideoList.clear();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject video = arr.getJSONObject(i);

                        String evid = video.getString("encodevid");
                        String title = video.getString("title");

                        String index;
                        if (video.has("seq")) {
                            index = video.getString("seq");
                        } else {
                            index = title;
                        }

                        mVideoList.add(new ListVideo(index, title, evid));
                    }

                    Log.e(TAG, "VideoList=" + mVideoList.size());
                }
            } else {
                Log.e(TAG, "listHtmlAlbums not album.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
    }

    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mUrlList.size() < 2) {
            mClarityView.setEnabled(false);
        } else {
            mClarityView.setEnabled(true);
        }
        mClarityView.setVisibility(View.VISIBLE);
        mClarityView.setText(((YoukuVideo) video).getType().getProfile());
    }

    @Override
    protected void playNextVideo(String title, String url) {
        mVideoView.stopPlayback();
        mTitleView.setText(title);
        mStateView.setText("初始化...");
        mLoadingProgress.setVisibility(View.VISIBLE);

        mVid = url;
        mIntentUrl = YoukuUtils.getPlayUrlByVid(mVid);
        mPrepared = false;
        mCurrentPosition = 0;

        playVideo();
    }

    @Override
    protected int getPlayIndex() {
        int index = 0;
        for (int i = 0; i < mVideoList.size() - 1; i++) {
            if (mVid.equals(mVideoList.get(i).getUrl())) {
                index = i;
                break;
            }
        }
        return index;
    }

}
