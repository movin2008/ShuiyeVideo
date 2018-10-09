package com.shuiyes.video.qq;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BasePlayActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.letv.LetvStream;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QQVActivity extends BasePlayActivity {

    private List<QQStream> mUrlList = new ArrayList<QQStream>();
    private List<PlayVideo> mSourceList = new ArrayList<PlayVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "腾讯视频";
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

                        QQStream stream = (QQStream) v.getPlayVideo();

                        mClarityView.setText(stream.getCname());
                        mDefn = stream.getUrl();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    playVideoByDefn();
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

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOID);
                    String html = HttpUtils.open(mIntentUrl);

                    if (TextUtils.isEmpty(html)) {
                        fault("请重试");
                        return;
                    }

                    Utils.setFile("qq.html", html);

                    String key = "<link rel=\"canonical\" href=\"";
                    if (html.contains(key)) {
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("\"");
                        mIntentUrl = tmp.substring(0, len);

                        Log.e(TAG, "rurl=" + mIntentUrl);
                    }

//                    key = "<title>";
//                    if (html.contains(key)) {
//                        int len = html.indexOf(key);
//                        String tmp = html.substring(len + key.length());
//                        len = tmp.indexOf("</title>");
//                        String title = tmp.substring(0, len);
//
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));
//                    }

//                     playMp4Video();
                    playVideoByDefn();

                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String mDefn = "";
    private void playVideoByDefn() throws Exception{
        playVideoByDefn(mDefn);
    }

    private void playVideoByDefn(String defn) throws Exception{
        mDefn = defn;

        mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
        String video = QQUtils.fetchVideo(mIntentUrl, defn);

        if (TextUtils.isEmpty(video)) {
            fault("请重试");
            return;
        }

        Utils.setFile("qq", video);

        JSONObject obj = new JSONObject(video);

        if (obj.has("msg")) {
            fault(obj.getString("msg"));
            return;
        }

        JSONObject fl = obj.getJSONObject("fl");
        JSONArray fis = fl.getJSONArray("fi");

        mUrlList.clear();
        for (int i = 0; i < fis.length(); i++) {
            JSONObject fi = (JSONObject) fis.get(i);

            int stream = fi.getInt("id");
            String streamStr = fi.getString("name");
            String cname = fi.getString("cname");
            int size = fi.getInt("fs");

            mUrlList.add(new QQStream(stream, streamStr, cname, size));
        }

        for (QQStream v : mUrlList) {
            Log.i(TAG, v.toStr());
        }

        JSONObject vl = obj.getJSONObject("vl");
        JSONArray vis = vl.getJSONArray("vi");
        JSONObject vi = (JSONObject) vis.get(0);

        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, vi.getString("ti")));

        mSourceList.clear();
        JSONArray uis = vi.getJSONObject("ul").getJSONArray("ui");
        for (int i = 0; i < uis.length(); i++) {
            JSONObject ui = (JSONObject) uis.get(i);

            String pt = ui.getJSONObject("hls").getString("pt");
            String m3u8Url = ui.getString("url") + pt;

            mSourceList.add(new PlayVideo(QQUtils.formatSource(m3u8Url), m3u8Url));

            if(i == 0){
                for (QQStream v : mUrlList) {
                    if(pt.contains(String.valueOf(v.getStream()))){
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_FETCH_VIDEO, v.getCname()));
                        break;
                    }
                }
            }
        }

        Log.e(TAG, "SourceList=" + mSourceList.size() + "/" + uis.length());
        if (mSourceList.isEmpty()) {
            fault("无视频源地址");
        } else {
            for (PlayVideo v : mSourceList) {
                Log.i(TAG, v.toStr());
            }

            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, mSourceList.get(0)));
        }
    }

    /**
     * MP4 视频缓存服务器很卡
     */
    @Deprecated
    private void playMp4Video(){
        try{
            mVid = QQUtils.getPlayVid(mIntentUrl);

            mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
            String video = null;
            for (String platform : QQUtils.PLATFORMS) {
                String html = QQUtils.fetchMp4Video(platform, mVid);

                if (TextUtils.isEmpty(html)) {
                    fault("请重试");
                    return;
                }

                Utils.setFile("qq", html);

                video = QQUtils.formatJson(html);

                JSONObject obj = new JSONObject(video);
                if (obj.has("msg")) {
                    String msg = obj.getString("msg");
                    if (!"cannot play outside".equals(msg) || QQUtils.PLATFORMS[QQUtils.PLATFORMS.length - 1].equals(platform)) {
                        fault(msg);
                        return;
                    } else {
                        continue;
                    }
                }
            }


            JSONObject obj = new JSONObject(video);
            JSONObject vl = obj.getJSONObject("vl");
            JSONArray vis = vl.getJSONArray("vi");
            JSONObject vi = (JSONObject) vis.get(0);

            String title = vi.getString("ti");
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));

            String fvkey = vi.getString("fvkey");
            String lnk = vi.getString("lnk");

            JSONArray uis = vi.getJSONObject("ul").getJSONArray("ui");
//            for(int i=0; i<uis.length(); i++){
//                JSONObject ui = (JSONObject) uis.get(i);
//                Log.e(TAG,"host"+i+" "+ui.getString("url"));
//            }
            mHost = ((JSONObject) uis.get(0)).getString("url");


            JSONObject cl = vi.getJSONObject("cl");
            int fc_cnt = cl.getInt("fc");

            String filename = vi.getString("fn");

            String magic_str = "";
            String video_type = "";
            int seg_cnt = fc_cnt;
            if (seg_cnt == 0) {
                seg_cnt = 1;
            } else {
                String[] fns = filename.split("\\.");
                lnk = fns[0];
                magic_str = fns[1];
                video_type = fns[2];
            }

            mSectionList.clear();
            for (int part = 1; part < seg_cnt + 1; part++) {

                String part_format_id = null;
                if (fc_cnt == 0) {
                    String[] keyids = cl.getString("keyid").split("\\.");
                    part_format_id = keyids[keyids.length - 1];
                } else {
                    JSONArray cis = cl.getJSONArray("ci");
                    JSONObject ci = (JSONObject) cis.get(part - 1);
                    part_format_id = ci.getString("keyid").split("\\.")[1];
                    filename = String.format("%s.%s.%s.%s", lnk, magic_str, part, video_type);
                }

                mSectionList.add(new ListVideo(part_format_id, mVid, filename));
                if(part > 1){
                    continue;
                }

                mSectionIndex = 0;
                playSection(part_format_id, mVid, filename);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String mHost = "http://video.dispatch.tc.qq.com/";
    private void playSection(String formatId, String vid, String filename) throws Exception {
        mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
        String part_info = QQUtils.fetchMp4Token(formatId, vid, filename);
        Utils.setFile("qq", part_info);
        String urlInfo = QQUtils.formatJson(part_info);

        JSONObject obj = new JSONObject(urlInfo);

        if (obj.has("msg")) {
            if("not pay".equals(obj.getString("msg"))){
                fault("VIP 章节暂不支持试看");
            }else{
                fault(obj.getString("msg"));
            }
            return;
        }

        if (obj.has("key")) {
            String vkey = obj.getString("key");
            String url = String.format("%s%s?vkey=%s", mHost, filename, vkey);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, url));
        }else{
            fault("解析失败...");
        }

    }

    private void playNextSection(final int index) {
        if(mStateView.getText().length() == 0){
            mStateView.setText("缓存第"+(index+1)+"章节...");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ListVideo v = mSectionList.get(index);
                try {
                    playSection(v.getText(), v.getTitle(), v.getUrl());
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mSourceList.isEmpty()) {
            mSourceView.setVisibility(View.GONE);
        } else {
            mSourceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void completionToPlayNextVideo() {
        if (mSectionList.size() > 0) {
            mSectionIndex++;
            Log.d(TAG, "playNextSection " + mSectionIndex + "/" + mSectionList.size());
            if (mSectionIndex < mSectionList.size()) {
                playNextSection(mSectionIndex);
            }else{
                mSectionIndex = 0;
                super.completionToPlayNextVideo();
            }
        } else{
            super.completionToPlayNextVideo();
        }
    }

}
