package com.shuiyes.video.letv;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BasePlayActivity;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LetvVActivity extends BasePlayActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "乐视视频";

        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

        String key = "/vplay/";
        int index = mIntentUrl.indexOf(key);
        if (mIntentUrl.indexOf(".html") != -1) {
            mVid = mIntentUrl.substring(index + key.length(), mIntentUrl.indexOf(".html"));
            String[] vids = mVid.split("_");
            mVid = vids[vids.length-1];
        } else {
            mVid = mIntentUrl.substring(index + key.length());
        }
        Log.e(TAG, "play mVid=" + mVid);

        playVideo();
    }

    @Override
    protected void playNextSection(int index) {

    }

    private List<LetvStream> mUrlList = new ArrayList<LetvStream>();
    private List<LetvSource> mSourceList = new ArrayList<LetvSource>();

    @Override
    protected void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String info = HttpUtils.open(LetvUtils.getVideoInfoUrl(mVid));

                    Utils.setFile("letv", info);

                    JSONObject data = new JSONObject(info).getJSONObject("msgs");
                    if (data.getInt("statuscode") != 1001) {
//                        Log.e(TAG, info);
                        fault(data.getString("content"));
                        return;
                    }

                    JSONObject playurl = data.getJSONObject("playurl");

                    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, playurl.getString("title")));

                    JSONArray domain = playurl.getJSONArray("domain");
                    JSONObject dispatch = playurl.getJSONObject("dispatch");

                    if (playurl.has("nextvid")) {
                        String nid = playurl.getInt("nextvid") + "";
                        Log.e(TAG, "next vid=" + nid);
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_NEXT, nid));
                    }else{
                        Log.e(TAG, "No next video.");
                    }

                    String host = domain.getString(0);

                    mUrlList.clear();
                    String streamUrl = "";
                    int prevStreamID = 0;
                    Iterator<String> iterator = dispatch.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String url = host + dispatch.getJSONArray(key).get(0);

                        int tmp = Integer.parseInt(key.replace("P", "").replace("p", ""));
                        mUrlList.add(new LetvStream(tmp, url));
                        if (tmp > prevStreamID) {
                            prevStreamID = tmp;
                            streamUrl = url;
                        }
                    }

                    for (LetvStream v : mUrlList) {
                        Log.i(TAG, v.toStr(mContext));
                    }

                    playUrl(streamUrl, prevStreamID + "P");


                    // TODO
                    // http://d-api-m.le.com/card/dynamic?platform=pc&vid=25214343&cid=2&id=84390&pagesize=100&type=episode&isvip=0&page=1&_=1538233546437

                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void playUrl(String url, String streamStr) throws Exception {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FETCH_VIDEO, streamStr));
        String video = HttpUtils.open(LetvUtils.getVideoPlayUrl(url, mVid));

        if (TextUtils.isEmpty(video)) {
            fault("解析异常请重试");
            return;
        }

        Utils.setFile("letv", video);

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

        Log.e(TAG, "UrlList=" + mSourceList.size() + "/" + streamsLen);
        if (mSourceList.isEmpty()) {
            fault("无视频源地址");
        } else {
            for (LetvSource v : mSourceList) {
                Log.i(TAG, v.toStr(mContext));
            }

            mCurrentPosition = 0;
            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, mSourceList.get(0)));
        }
    }

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
            case R.id.btn_next:
                mStateView.setText("初始化...");
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
        }
    }

}
