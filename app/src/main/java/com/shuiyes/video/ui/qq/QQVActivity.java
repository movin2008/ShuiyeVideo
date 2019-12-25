package com.shuiyes.video.ui.qq;

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

                    if (!QQUtils.hasPlayVid(mIntentUrl) || mVideoList.isEmpty()) {
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

                        key = "<title>";
                        if (html.contains(key)) {
                            int len = html.indexOf(key);
                            String tmp = html.substring(len + key.length());
                            len = tmp.indexOf("</title>");
                            String title = tmp.substring(0, len);

                            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));
                        }

                        if (mVideoList.isEmpty()) {
                            //listHtmlAlbums(html);
                            listJsonAlbums(html);
                            Log.e(TAG, "VideoList=" + mVideoList.size());
                            mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                        }
                    }

                    playMp4Video();
                    //playVideoByDefn();

                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String mDefn = "";

    private void playVideoByDefn() throws Exception {
        playVideoByDefn(mDefn);
    }

    /**
     * {
     * "dltype": 3,
     * "exem": 0,
     * "fl": {
     * "cnt": 4,
     * "fi": [{
     * "id": 321001,
     * "name": "sd",
     * "lmt": 0,
     * "sb": 1,
     * "cname": "标清;(270P)",
     * "br": 81,
     * "profile": 4,
     * "drm": 0,
     * "video": 1,
     * "audio": 1,
     * "fs": 139889484,
     * "super": 0,
     * "hdr10enh": 0,
     * "sname": "标清",
     * "resolution": "270P",
     * "sl": 0
     * }, {
     * "id": 321002,
     * "name": "hd",
     * "lmt": 0,
     * "sb": 1,
     * "cname": "高清;(480P)",
     * "br": 106,
     * "profile": 4,
     * "drm": 0,
     * "video": 1,
     * "audio": 1,
     * "fs": 307286000,
     * "super": 0,
     * "hdr10enh": 0,
     * "sname": "高清",
     * "resolution": "480P",
     * "sl": 1
     * }, {
     * "id": 321003,
     * "name": "shd",
     * "lmt": 0,
     * "sb": 1,
     * "cname": "超清;(720P)",
     * "br": 111,
     * "profile": 4,
     * "drm": 0,
     * "video": 1,
     * "audio": 1,
     * "fs": 614334180,
     * "super": 0,
     * "hdr10enh": 0,
     * "sname": "超清",
     * "resolution": "720P",
     * "sl": 0
     * }, {
     * "id": 321004,
     * "name": "fhd",
     * "lmt": 1,
     * "sb": 1,
     * "cname": "蓝光;(1080P)",
     * "br": 116,
     * "profile": 4,
     * "drm": 0,
     * "video": 1,
     * "audio": 1,
     * "fs": 953924408,
     * "super": 0,
     * "hdr10enh": 0,
     * "sname": "蓝光",
     * "resolution": "1080P",
     * "sl": 0
     * }]* 	},
     * "fp2p": 2,
     * "hs": 0,
     * "ip": "114.221.11.109",
     * "ls": 0,
     * "preview": 2730,
     * "s": "o",
     * "sfl": {
     * "cnt": 0
     * }    ,
     * "tm": 1577264153,
     * "vl": {
     * "cnt": 1,
     * "vi": [{
     * "br": 106,
     * "ch": 0,
     * "ct": 21600,
     * "drm": 0,
     * "dsb": 0,
     * "fc": 9,
     * "fmd5": "64a0721fb3474de1801f35d2fd7e27a4",
     * "fn": "v0028tb6gk1.321002.ts",
     * "fs": 307286000,
     * "fst": 5,
     * "head": 0,
     * "hevc": 0,
     * "hfs": "353378900",
     * "iflag": 1,
     * "keyid": "v0028tb6gk1.321002",
     * "lnk": "v0028tb6gk1",
     * "logo": 0,
     * "mst": 8,
     * "pl": [{
     * "cnt": 3,
     * "pd": [{
     * "cd": 10,
     * "h": 45,
     * "w": 80,
     * "r": 10,
     * "c": 10,
     * "fmt": 40001,
     * "fn": "q1",
     * "url": "https://puui.qpic.cn/video_caps/0/"
     * }, {
     * "cd": 10,
     * "h": 90,
     * "w": 160,
     * "r": 5,
     * "c": 5,
     * "fmt": 40002,
     * "fn": "q2",
     * "url": "https://puui.qpic.cn/video_caps/0/"
     * }, {
     * "cd": 10,
     * "h": 135,
     * "w": 240,
     * "r": 5,
     * "c": 5,
     * "fmt": 40003,
     * "fn": "q3",
     * "url": "https://puui.qpic.cn/video_caps/0/"
     * }]
     * }],
     * "share": 1,
     * "st": 2,
     * "tail": 149,
     * "td": "2730.04",
     * "ti": "你和我的倾城时光_01",
     * "tie": 0,
     * "type": 1036,
     * "ul": {
     * "ui": [{
     * "url": "http://ltsydzd.qq.com/uwMROfz2r5zAoaQXGdGnC2df644E7D3uP8M8pmtgwsRK9nEL/hnC4fbjTxThtl3Y5kDOqM8MuSwWf0osJjoBnXHAkgJpSBiaoCkIpA94ZXX3vIk8gWmWSPpEZ_kPaSge7qISsMarX8qykia2tNTdfs6BWUTcjwzL0SMcY9YChlN8ogfpCz5nj1q_62c2bOIhj16T9gQ/",
     * "vt": 2640,
     * "dtc": 0,
     * "dt": 2,
     * "hls": {
     * "et": 0,
     * "fbw": 64,
     * "ftype": "mp4",
     * "hk": "empty",
     * "hvl": null,
     * "pnl": {
     * "pi": [{
     * "bw": 106,
     * "fc": 9,
     * "fn": "321002"
     * }]
     * },
     * "st": 0,
     * "stype": "mp4",
     * "pname": "v0028tb6gk1.321002.ts",
     * "pt": "v0028tb6gk1.321002.ts.m3u8?ver=4"
     * }
     * }, {
     * "url": "http://58.222.51.26/moviets.tc.qq.com/AHBHoSFQq_Vtz_Jdbt11WzgNdNoaQdPfnK4aGJz6b2q4/uwMROfz2r5zAoaQXGdGnS2df6473yd25ojFHoYCTfeL-Gr55/hnC4fbjTxThtl3Y5kDOqM8MuSwWf0osJjoBnXHAkgJpSBiaoCkIpA94ZXX3vIk8gWmWSPpEZ_kPaSge7qISsMarX8qykia2tNTdfs6BWUTcjwzL0SMcY9YChlN8ogfpCz5nj1q_62c2bOIhj16T9gQ/",
     * "vt": 2803,
     * "dtc": 0,
     * "dt": 2,
     * "hls": {
     * "et": 0,
     * "fbw": 64,
     * "ftype": "mp4",
     * "hk": "empty",
     * "hvl": null,
     * "pnl": {
     * "pi": [{
     * "bw": 106,
     * "fc": 9,
     * "fn": "321002"
     * }]
     * },
     * "st": 0,
     * "stype": "mp4",
     * "pname": "v0028tb6gk1.321002.ts",
     * "pt": "v0028tb6gk1.321002.ts.m3u8?ver=4"
     * }
     * }, {
     * "url": "http://ltsws.qq.com/uwMROfz2r5zAoaQXGdGnT2df647Au4O82-5gEf4sRhCVVHKO/hnC4fbjTxThtl3Y5kDOqM8MuSwWf0osJjoBnXHAkgJpSBiaoCkIpA94ZXX3vIk8gWmWSPpEZ_kPaSge7qISsMarX8qykia2tNTdfs6BWUTcjwzL0SMcY9YChlN8ogfpCz5nj1q_62c2bOIhj16T9gQ/",
     * "vt": 2600,
     * "dtc": 0,
     * "dt": 2,
     * "hls": {
     * "et": 0,
     * "fbw": 64,
     * "ftype": "mp4",
     * "hk": "empty",
     * "hvl": null,
     * "pnl": {
     * "pi": [{
     * "bw": 106,
     * "fc": 9,
     * "fn": "321002"
     * }]
     * },
     * "st": 0,
     * "stype": "mp4",
     * "pname": "v0028tb6gk1.321002.ts",
     * "pt": "v0028tb6gk1.321002.ts.m3u8?ver=4"
     * }
     * }, {
     * "url": "http://defaultts.tc.qq.com/defaultts.tc.qq.com/uwMROfz2r5zAoaQXGdGlumdf646Jj-BCnEZ4OPOwuxrp13Mi/hnC4fbjTxThtl3Y5kDOqM8MuSwWf0osJjoBnXHAkgJpSBiaoCkIpA94ZXX3vIk8gWmWSPpEZ_kPaSge7qISsMarX8qykia2tNTdfs6BWUTcjwzL0SMcY9YChlN8ogfpCz5nj1q_62c2bOIhj16T9gQ/",
     * "vt": 2800,
     * "dtc": 0,
     * "dt": 2,
     * "hls": {
     * "et": 0,
     * "fbw": 64,
     * "ftype": "mp4",
     * "hk": "empty",
     * "hvl": null,
     * "pnl": {
     * "pi": [{
     * "bw": 106,
     * "fc": 9,
     * "fn": "321002"
     * }]
     * },
     * "st": 0,
     * "stype": "mp4",
     * "pname": "v0028tb6gk1.321002.ts",
     * "pt": "v0028tb6gk1.321002.ts.m3u8?ver=4"
     * }
     * }]
     * },
     * "vh": 486,
     * "vid": "q0028uvicmy",
     * "videotype": 2,
     * "vr": 0,
     * "vst": 2,
     * "vw": 864,
     * "wh": 1.7777778,
     * "wl": {
     * "wi": [{
     * "id": 19,
     * "x": 24,
     * "y": 24,
     * "w": 151,
     * "h": 49,
     * "a": 100,
     * "md5": "dcc9dc5c478c4100ea2817c5e6020f26",
     * "url": "http://puui.qpic.cn/vcolumn_pic/0/logo_qing_xi_color_336_108.png/0",
     * "surl": "https://puui.qpic.cn/vcolumn_pic/0/logo_qing_xi_color_336_108.png/0",
     * "rw": 0
     * }]
     * },
     * "uptime": 1542073246,
     * "fvideo": 0,
     * "cached": 1,
     * "fvpint": 0,
     * "swhdcp": 0
     * }]
     * }
     * }
     *
     * @param defn
     * @throws Exception
     */
    private void playVideoByDefn(String defn) throws Exception {
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

//            int stream = fi.getInt("id");
            int stream = fi.getInt("br");
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
                    if (br == v.getStreams()) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_FETCH_VIDEO, v.getCname()));
                        break;
                    }
                }
            }

            mSourceList.add(new PlayVideo(QQUtils.formatSource(m3u8Url), m3u8Url));
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

    private String mTitle = "";

    /**
     * MP4 视频
     */
    private void playMp4Video() {
        try {
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
                        if ("not pay".equals(msg)) {
                            fault("VIP 章节暂不支持试看");
                        } else {
                            fault(msg);
                        }
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

            mTitle = vi.getString("ti");
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, mTitle));

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

                mSectionList.add(new ListVideo(part_format_id, mVid, filename));
            }

            if (mSectionList.size() == 0) {
                fault("未获取到章节信息");
                return;
            }

            mSectionIndex = 0;
            playNextSection(mSectionIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String mHost = "http://video.dispatch.tc.qq.com/";

    private void playSection(String formatId, String vid, String filename) throws Exception {

        // 标题上添加章节显示
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, mTitle + " 章节(" + (mSectionIndex + 1) + "/" + mSectionList.size() + ")"));
        mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
        String part_info = QQUtils.fetchMp4Token(formatId, vid, filename);
        Utils.setFile("qq", part_info);
        String urlInfo = QQUtils.formatJson(part_info);

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
            String vkey = obj.getString("key");
            String url = String.format("%s%s?vkey=%s", mHost, filename, vkey);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_URL, url));
        } else {
            fault("解析失败...");
        }

    }

    private int mSectionIndex = 0;

    private List<ListVideo> mSectionList = new ArrayList<ListVideo>();

    private void playNextSection(final int index) {
        if (mStateView.getText().length() == 0) {
            mStateView.setText("缓存第" + (index + 1) + "章节...");
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
            Log.d(TAG, "playNextSection " + mSectionIndex + "/" + mSectionList.size());
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
