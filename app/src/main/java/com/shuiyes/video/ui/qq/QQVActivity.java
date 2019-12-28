package com.shuiyes.video.ui.qq;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BasePlayActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.MiscDialog;
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
                        PlayVideo playVideo = ((MiscView) view).getPlayVideo();
                        if(playVideo.getUrl().endsWith("/")){
                            cacheSection(playVideo);
                        }else{
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));

                        }
                    }
                });
                mSourceDialog.show();
                break;
            case R.id.btn_section:
                if (mSectionDialog != null && mSectionDialog.isShowing()) {
                    mSectionDialog.dismiss();
                }
                mSectionDialog = new MiscDialog(this, mSectionList);
                mSectionDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSectionDialog != null && mSectionDialog.isShowing()) {
                            mSectionDialog.dismiss();
                        }

                        MiscView v = (MiscView) view;
                        QQSection section = (QQSection) v.getPlayVideo();
                        playNextSection(section.getIndex());
                    }
                });
                mSectionDialog.show();
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
                                    //playVideoByDefn(mDefn);
                                    playMp4Video(mDefn);
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

                    if (!QQUtils.hasPlayVid(mIntentUrl) || mVideoList.isEmpty()) {
                        mHandler.sendEmptyMessage(MSG_FETCH_VIDEOID);
                        String html = HttpUtils.open(mIntentUrl);

                        if(html.startsWith("Exception: ")){
                            fault(html);
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

                        key = "<title>";
                        if (html.contains(key)) {
                            int len = html.indexOf(key);
                            String tmp = html.substring(len + key.length());
                            len = tmp.indexOf("</title>");
                            String title = tmp.substring(0, len);

                            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));
                        }

                        //listHtmlAlbums(html);
                        listJsonAlbums(html);
                    }

                    playMp4Video(mDefn);
                    //playVideoByDefn(mDefn);

                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 默认超清 720p
    private String mDefn = "shd";

    /**
     * @param defn
     * @throws Exception
     */
    private void playVideoByDefn(String defn) throws Exception {
        mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
        String video = QQUtils.fetchVideo(mIntentUrl, defn);

        if (TextUtils.isEmpty(video)) {
            fault("请重试");
            return;
        }

        Utils.setFile("qq", video);

        JSONObject obj = new JSONObject(video);

        if (obj.has("msg")) {
            if ("not pay".equals(obj.getString("msg"))) {
                fault("暂不支持VIP视频");
            } else {
                fault(obj.getString("msg"));
            }
            return;
        }

        JSONObject fl = obj.getJSONObject("fl");
        JSONArray fis = fl.getJSONArray("fi");

        mUrlList.clear();
        for (int i = 0; i < fis.length(); i++) {
            JSONObject fi = (JSONObject) fis.get(i);

            int stream = fi.getInt("id");
            int br = fi.getInt("br");
            String streamStr = fi.getString("name");
            String cname = fi.getString("cname");
            int size = fi.getInt("fs");

            mUrlList.add(new QQStream(stream, br, streamStr, cname, size));
        }

        for (QQStream v : mUrlList) {
            Log.i(TAG, v.toStr());
        }

        JSONObject vl = obj.getJSONObject("vl");
        JSONArray vis = vl.getJSONArray("vi");
        JSONObject vi = (JSONObject) vis.get(0);

        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, vi.getString("ti")));

        int drm = vi.getInt("drm");
        // TODO
        if (drm == 1) {
            fault("暂不支持VIP视频");
            return;
        }

        mSourceList.clear();
        JSONArray uis = vi.getJSONObject("ul").getJSONArray("ui");
        int br = vi.getInt("br");
        String fn = null;
        String fvkey = null;
        for (int i = 0; i < uis.length(); i++) {
            JSONObject ui = (JSONObject) uis.get(i);

            String m3u8Url = ui.getString("url");
            if (ui.has("hls")) {
                String pt = ui.getJSONObject("hls").getString("pt");
                m3u8Url += pt;
            } else {
                if (fn == null) {
                    fn = vi.getString("fn");
                }
                if (fvkey == null) {
                    fvkey = vi.getString("fvkey");
                }
                m3u8Url = String.format("%s%s?vkey=%s", m3u8Url, fn, fvkey);
            }

            if (i == 0) {
                for (QQStream v : mUrlList) {
                    if (br == v.getBr()) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_FETCH_VIDEO, v.getCname()));
                        break;
                    }
                }
            }

            mSourceList.add(new PlayVideo(QQUtils.formatSource(m3u8Url), m3u8Url));
        }

        Log.e(TAG, "SourceList=" + mSourceList.size());
        if (mSourceList.isEmpty()) {
            fault("无视频源地址");
        } else {
            for (PlayVideo v : mSourceList) {
                Log.i(TAG, v.toStr());
            }

            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, mSourceList.get(0)));
        }
    }

    private void listJsonAlbums(String html) {
        String key = "var COVER_INFO = ";
        if (html.contains(key)) {
            String tmp = html.substring(html.indexOf(key) + key.length());
            key = "var COLUMN_INFO = ";
            if (tmp.contains(key)) {
                tmp = tmp.substring(0, tmp.indexOf(key));
            }

            Utils.setFile("album.qq", tmp);

            try {
                JSONObject obj = new JSONObject(tmp);
                if (!obj.has("nomal_ids")) {
                    return;
                }

                mVideoList.clear();
                JSONArray arr = (JSONArray) obj.get("nomal_ids");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject video = arr.getJSONObject(i);
                    int index = video.getInt("E");
                    int type = video.getInt("F");
                    String vid = video.getString("V");

                    String text = "" + index;
                    if (type == 7) {
                        text += "(VIP)";
                    } else if (type == 0 || type == 4) {
                        text += "(预告)";
                    }

                    if ("电影".equals(obj.getString("type_name"))) {
                        text = obj.getString("title") + text;
                    }

                    mVideoList.add(new ListVideo(text, text, QQUtils.getVideoPlayUrlFromVid(mIntentUrl, vid)));
                }

                Log.e(TAG, "VideoList=" + mVideoList.size());
                mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取不全，最多30集数据
     *
     * @param html
     */
    @Deprecated
    private void listHtmlAlbums(String html) {
        String span = "<span __wind";
        while (html.contains(span)) {
            html = html.substring(html.indexOf(span) + span.length());

            int len = html.indexOf("/x/cover/");
            String altStr = html.substring(0, len);
            html = html.substring(len);
            String href = html.substring(0, html.indexOf("\""));


            String key = "alt=\"";
            String alt = "";
            if (altStr.contains(key)) {
                altStr = altStr.substring(html.indexOf(key) + key.length());
                alt = altStr.substring(0, altStr.indexOf("\""));
                if (TextUtils.isEmpty(alt) || "更新".equals(alt)) {
                    alt = "";
                } else {
                    alt = "(" + alt + ")";
                }
            }


            key = "\">";
            html = html.substring(html.indexOf(key) + key.length());
            String index = html.substring(0, html.indexOf("</a>")).trim();

            mVideoList.add(new ListVideo(index + alt, index + alt, QQUtils.HOST + href));
        }
    }

    /**
     * MP4 视频
     */
    private void playMp4Video(String defn) {
        try {
            mVid = QQUtils.getPlayVid(mIntentUrl);

            mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);

            String video = null;
            int len = QQUtils.PLATFORMS.length;
            for (int i = 0; i < len; i++) {
                String html = QQUtils.fetchMp4Video(QQUtils.PLATFORMS[i], defn, mVid);

                if(html.startsWith("Exception: ")){
                    fault(html);
                    return;
                }

                Utils.setFile("qq", html);

                JSONObject obj = new JSONObject(html);
                if (obj.has("msg")) {
                    String msg = obj.getString("msg");
                    if (!"cannot play outside".equals(msg) || i == len - 1) {
                        if ("not pay".equals(msg)) {
                            fault("VIP 章节暂不支持试看");
                        } else if ("ip-copy limit".equals(msg)) {
                            fault("VIP付费 章节暂不支持试看");
                        } else {
                            fault(msg);
                        }
                        return;
                    } else {
                        continue;
                    }
                } else {
                    video = html;
                    break;
                }
            }


            if (TextUtils.isEmpty(video)) {
                fault("请重试");
                return;
            }

            JSONObject obj = new JSONObject(video);
            JSONObject fl = obj.getJSONObject("fl");
            JSONArray fis = fl.getJSONArray("fi");

            mUrlList.clear();
            for (int i = 0; i < fis.length(); i++) {
                JSONObject fi = (JSONObject) fis.get(i);

                int stream = fi.getInt("id");
                int br = fi.getInt("br");
                String streamStr = fi.getString("name");
                String cname = fi.getString("cname");
                int size = fi.getInt("fs");

                mUrlList.add(new QQStream(stream, br, streamStr, cname, size));
            }

            for (QQStream v : mUrlList) {
                Log.i(TAG, v.toStr());
            }

            JSONObject vl = obj.getJSONObject("vl");
            JSONArray vis = vl.getJSONArray("vi");
            JSONObject vi = (JSONObject) vis.get(0);

            BR = vi.getInt("br");
            String title = vi.getString("ti");
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));

            //String fvkey = vi.getString("fvkey");
            String lnk = vi.getString("lnk");

            mSourceList.clear();
            JSONArray uis = vi.getJSONObject("ul").getJSONArray("ui");
            for (int i = 0; i < uis.length(); i++) {
                JSONObject ui = (JSONObject) uis.get(i);
                String url = ui.getString("url");
                mSourceList.add(new PlayVideo(QQUtils.formatSource(url), url));
            }

            Log.e(TAG, "SourceList=" + mSourceList.size());
            if (mSourceList.isEmpty()) {
                fault("无视频源地址");
                return;
            }

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

                String part_format_id;
                if (fc_cnt == 0) {
                    String[] keyids = cl.getString("keyid").split("\\.");
                    part_format_id = keyids[keyids.length - 1];
                } else {
                    JSONArray cis = cl.getJSONArray("ci");
                    JSONObject ci = (JSONObject) cis.get(part - 1);
                    part_format_id = ci.getString("keyid").split("\\.")[1];
                    filename = String.format("%s.%s.%s.%s", lnk, magic_str, part, video_type);
                }

                mSectionList.add(new QQSection(part, part_format_id, mVid, filename));
            }

            if (mSectionList.size() == 0) {
                fault("未获取到章节信息");
                return;
            }

            for (PlayVideo playVideo:mSectionList){
                QQSection section = (QQSection) playVideo;
                Log.e(TAG, section.toStr());
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSectionIndex = 0;
                    playNextSection(0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSection(QQSection section) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    playSection(section.getFormatid(), section.getVid(), section.getUrl());
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int BR;

    private void playSection(String formatId, String vid, String filename) throws Exception {
        for (QQStream v : mUrlList) {
            if (BR == v.getBr()) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_FETCH_VIDEO, v.getCname()));
                break;
            }
        }

        String part_info = QQUtils.fetchMp4Token(formatId, vid, filename);
        Utils.setFile("qq", part_info);
        String urlInfo = part_info;//QQUtils.formatJson(part_info);

        JSONObject obj = new JSONObject(urlInfo);

        if (obj.has("msg")) {
            if ("not pay".equals(obj.getString("msg"))) {
                fault("VIP 章节暂不支持试看");
            } else {
                fault(obj.getString("msg"));
            }
            return;
        }

        if (obj.has("key")) {
            mSectionName = filename;
            mVKey = obj.getString("key");
            cacheSection(mSourceList.get(0));
        } else {
            fault("解析失败...");
        }

    }

    private String mVKey = "nokey";
    private String mSectionName = "noname";
    private void cacheSection(PlayVideo video){
        PlayVideo playVideo = video.clone();
        playVideo.setUrl(String.format("%s%s?vkey=%s", playVideo.getUrl(), mSectionName, mVKey));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));
    }


    private int mSectionIndex = 0;

    private List<QQSection> mSectionList = new ArrayList<QQSection>();

    private void playNextSection(final int index) {
        mCurrentPosition = 0;
        mSectionIndex = index;
        String sectionStr = (index + 1) + "/" + mSectionList.size();
        if (mSectionList.size() > 1) {
            mSectionView.setVisibility(View.VISIBLE);
            mSectionView.setText("章节" + "[" + sectionStr + "]");
            mStateView.setText(mStateView.getText() + "\n获取第 " + sectionStr + " 章节...");
        } else {
            mSectionView.setVisibility(View.GONE);
        }

        playSection(mSectionList.get(index));
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
    protected void completionToPlayNextVideo() {
        if (mSectionList.size() > 0) {
            mSectionIndex++;
            Log.d(TAG, "playNextSection " + (mSectionIndex+1) + "/" + mSectionList.size());
            if (mSectionIndex < mSectionList.size()) {
                playNextSection(mSectionIndex);
            } else {
                mSectionIndex = 0;
                super.completionToPlayNextVideo();
            }
        } else {
            super.completionToPlayNextVideo();
        }
    }

}
