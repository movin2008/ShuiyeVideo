package com.shuiyes.video.ui.letv;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BasePlayActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LetvVActivity extends BasePlayActivity implements View.OnClickListener {

    private List<LetvStream> mUrlList = new ArrayList<LetvStream>();
    private List<PlayVideo> mSourceList = new ArrayList<PlayVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "乐视视频";
        mVid = LetvUtils.getPlayVid(mIntentUrl);
        playVideo();
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
                        MiscView v = (MiscView) view;
                        mStream = v.getPlayVideo().getText();

                        final LetvStream stream = (LetvStream) v.getPlayVideo();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    playUrl(stream.getUrl(), stream.getStreamStr());
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
                    Iterator<String> iterator = dispatch.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String url = host + dispatch.getJSONArray(key).get(0);

                        int tmp = Integer.parseInt(key.replace("P", "").replace("p", ""));
                        mUrlList.add(new LetvStream(tmp, url));
                    }

                    Log.e(TAG, "UrlList=" + mUrlList.size());

                    if (mUrlList.isEmpty()) {
                        fault("无视频地址");
                    } else {
                        Collections.sort(mUrlList, new Comparator<LetvStream>() {
                            @Override
                            public int compare(LetvStream v1, LetvStream v2) {
                                return v2.getStream() - v1.getStream();
                            }
                        });

                        LetvStream playVideo = null;
                        for (LetvStream v : mUrlList) {
                            Log.i(TAG, v.toStr()+" mStream="+mStream);

                            if(playVideo == null || v.getText().equals(mStream)){
                                playVideo = v;
                            }
                        }

                        playUrl(playVideo.getUrl(), playVideo.getText());
                    }

                    if(playurl.has("total")){
                        listAlbum(playurl.getInt("total"));
                    }
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
        JSONArray nodelist = data.getJSONArray("nodelist");
        int nodelistLen = nodelist.length();

        mSourceList.clear();
        for (int i = 0; i < nodelistLen; i++) {
            JSONObject node = (JSONObject) nodelist.get(i);

            String m3u8Url = node.getString("location");
            String name = node.getString("name").replace("中国", "").replaceAll("-", "");

            mSourceList.add(new PlayVideo(name, m3u8Url));
        }

        Log.e(TAG, "SourceList=" + mSourceList.size() + "/" + nodelistLen);
        if (mSourceList.isEmpty()) {
            fault("无视频源地址");
        } else {
            for (PlayVideo v : mSourceList) {
                Log.i(TAG, v.toStr());
            }

            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, mSourceList.get(0)));
        }
    }

    private void listAlbum(int albumCount){
        if(mVideoList.isEmpty() && albumCount > 1){
            boolean find = false;
            List<ListVideo> videoList = new ArrayList<ListVideo>();
            try {
                //  最后获取专辑信息
                String album = LetvUtils.fetchAlbum(mVid);

                JSONObject obj = new JSONObject(album);


                if (!"200".equals(obj.getString("code"))) {
                    Log.e(TAG, obj.getString("msg"));
                    return;
                }

                if(obj.get("data") instanceof String){
                    Log.e(TAG, obj.getString("data"));
                    return;
                }

                JSONArray videolist = obj.getJSONObject("data").getJSONObject("episode").getJSONArray("videolist");
                int videolistLen = videolist.length();

                for (int i = 0; i < videolistLen; i++) {
                    JSONObject stream = (JSONObject) videolist.get(i);

                    // 此接口会获取相关视频，所以要判断下是否有当前视频
                    if(mVid.equals(""+stream.getInt("vid"))){
                        find = true;
                    }

                    String episode = stream.getString ("episode");
                    String title = stream.getString ("title");
                    String url = stream.getString("url");
                    if(TextUtils.isEmpty(episode) || Integer.parseInt(episode) > 100){
                        if(stream.has("subTitle")){
                            String subTitle = stream.getString ("subTitle");
                            if(title.contains(subTitle)){
                                episode = title;
                            }else{
                                episode = stream.getString ("subTitle");
                            }
                        }else{
                            episode = title;
                        }
                    }
                    videoList.add(new ListVideo(episode, title, url));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(find){
                    mVideoList.addAll(videoList);
                    Log.e(TAG, "videoList "+mVideoList.size()+"/"+albumCount);
                    mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                }else{
                    Log.e(TAG, "Letv("+mVid+") is no album.");
                }
            }

        }

    }

    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mSourceList.size() > 1) {
            mSourceView.setVisibility(View.VISIBLE);
        } else {
            mSourceView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void playNextVideo(String title, String url) {
        super.playNextVideo(title, url);
        mVid = LetvUtils.getPlayVid(url);
        playVideo();
    }

}
