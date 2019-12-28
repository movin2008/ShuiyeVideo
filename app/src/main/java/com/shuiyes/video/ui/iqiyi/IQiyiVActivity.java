package com.shuiyes.video.ui.iqiyi;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IQiyiVActivity extends BasePlayActivity {

    private List<IQiyiVideo> mUrlList = new ArrayList<IQiyiVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBatName = "爱奇艺视频";
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
                        MiscView v = (MiscView) view;
                        mStream = v.getPlayVideo().getText();
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, v.getPlayVideo()));
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
                    String tvid = null;
                    String vid = null;
                    String albumUrl = null;

                    String key = "?tvid=";
                    if (mIntentUrl.contains(key)) {
                        int len = mIntentUrl.indexOf(key);
                        String tmp = mIntentUrl.substring(len + key.length());
                        len = tmp.indexOf("&");
                        tvid = tmp.substring(0, len);

                        key = "&vid=";
                        len = tmp.indexOf(key);
                        vid = tmp.substring(len + key.length());

                        Log.e(TAG, ":url 'tvid=" + tvid + ", vid=" + vid + "'");
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                    String html = HttpUtils.open(mIntentUrl);
                    Utils.setFile("iqiyi.html", html);

                    //&& tvid == null && vid == null
                    if(html.startsWith("Exception: ")){
                        fault(html);
                        return;
                    }

                    key = "\"albumUrl\":\"";
                    if (html.contains(key)) {
                        String tmp = html.substring(html.indexOf(key) + key.length());
                        albumUrl = tmp.substring(0, tmp.indexOf("\""));
                        Log.e(TAG, ":html albumUrl=" + albumUrl);
                    }

                    key = ":page-info='";
                    if (html.contains(key) && (albumUrl == null || tvid == null || vid == null)) {
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("'");
                        String pageInfo = tmp.substring(0, len);

                        try {
                            JSONObject obj = new JSONObject(pageInfo);

                            if (tvid == null || vid == null) {
                                tvid = obj.getString("tvId");
                                vid = obj.getString("vid");
                                Log.e(TAG, ":page-info 'tvid=" + tvid + ", vid=" + vid + "'");
                            }
                            if (albumUrl == null) {
                                albumUrl = obj.getString("albumUrl");
                                Log.e(TAG, ":page-info albumUrl=" + albumUrl);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    key = "param['tvid'] = \"";
                    if (tvid == null && html.contains(key)) {
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("\"");
                        tvid = tmp.substring(0, len);
                        Log.e(TAG, ":param['tvid'] = " + tvid);
                    }

                    key = "param['vid'] = \"";
                    if (vid == null && html.contains(key)) {
                        int len = html.indexOf(key);
                        String tmp = html.substring(len + key.length());
                        len = tmp.indexOf("\"");
                        vid = tmp.substring(0, len);
                        Log.e(TAG, ":param['vid'] = " + vid);
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String video = IQiyiUtils.fetchVideo(tvid, vid);
                    Utils.setFile("iqiyi", video);

                    if (TextUtils.isEmpty(video)) {
                        fault("空数据,请重试");
                        return;
                    }

                    key = "var tvInfoJs=";
                    String albumId;
                    int albumCount, showChannelId, sourceid, ty;
                    if (video.contains(key)) {
                        int len = video.indexOf(key);
                        video = video.substring(len + key.length());

                        JSONObject obj = new JSONObject(video);

                        albumId = obj.getString("aid");
                        sourceid = obj.getInt("sid");
                        albumCount = obj.getInt("es");
                        showChannelId = obj.getInt("showChannelId");
                        ty = obj.getInt("ty") / 10000;

                        String title;
                        if (showChannelId == IQiyiUtils.Channel.dianshiju) {
                            title = obj.getString("vn") + " - " + obj.getString("subt");
                        } else if (showChannelId == IQiyiUtils.Channel.zongyi) {
                            title = obj.getJSONObject("ppsInfo").getString("name");
                        } else {
                            title = obj.getString("vn");
                        }
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, title));
                    } else {
                        fault("数据tvInfoJs异常");
                        return;
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    video = IQiyiUtils.getVMS(tvid, vid);
                    Utils.setFile("iqiyi", video);
//                    Log.e(TAG, "video ="+video);

                    JSONObject obj = new JSONObject(video);
                    if (!"A00000".equals(obj.getString("code"))) {
//                        Log.e(TAG, info);

                        String msg = null;
                        if (obj.has("msg")) {
                            msg = obj.getString("msg");
                        } else if (obj.has("st")) {
                            msg = "Error code " + obj.getString("st");
                        } else {
                            JSONObject ctl = obj.getJSONObject("ctl");
                            if (ctl.has("msg")) {
                                msg = ctl.getString("msg");
                            } else if (ctl.has("area")) {
                                msg = "Error area " + ctl.getString("area");
                            }
                        }

                        if ("server return err-data.".equals(msg)) {
                            fault("VIP 视频暂不支持播放");
                        } else {
                            fault(msg);
                        }
                        return;
                    }

                    JSONArray vidl = obj.getJSONObject("data").getJSONArray("vidl");
                    int vidlLen = vidl.length();

                    mUrlList.clear();
                    for (int i = 0; i < vidlLen; i++) {
                        JSONObject stream = (JSONObject) vidl.get(i);

                        int vd = stream.getInt("vd");
                        String m3u8Url = stream.getString("m3u");

                        IQiyiVideo.VideoType type = IQiyiVideo.formateVideoType(vd);
                        if (type != null) {
                            mUrlList.add(new IQiyiVideo(type, m3u8Url));
                        }
                    }
                    Log.e(TAG, "UrlList=" + mUrlList.size() + "/" + vidlLen);

                    if (mUrlList.isEmpty()) {
                        fault("无视频地址");
                    } else {
                        Collections.sort(mUrlList, new Comparator<IQiyiVideo>() {
                            @Override
                            public int compare(IQiyiVideo v1, IQiyiVideo v2) {
                                return v2.getType().getScreenSize() - v1.getType().getScreenSize();
                            }
                        });

                        IQiyiVideo playVideo = null;
                        for (IQiyiVideo v : mUrlList) {
                            Log.i(TAG, v.toStr() + " mStream=" + mStream);

                            // TODO 4K 较卡，要改成可配置
                            if ((playVideo == null || v.getType().getProfile().equals(mStream)) && v.getType().getScreenSize() < IQiyiVideo.UHD_SZ) {
                                playVideo = v;
                            }
                        }

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));

                        Log.e(TAG, "Album showChannelId=" + showChannelId + ", count=" + albumCount + "/" + mVideoList.size() + ", albumId=" + albumId + ", sourceid=" + sourceid + ", time=" + ty);

                        if (!mVideoList.isEmpty()) {
                            return;
                        }

                        //  最后获取专辑列表信息

                        // showChannelId = QiyiUtils.Channel.dianshiju
                        fetchAlbumsOfAvlist(albumCount == 0 ? 1 : albumCount, albumId);

                        if (mVideoList.isEmpty() && sourceid != 0 && ty != 0) {
                            // showChannelId == IQiyiUtils.Channel.zongyi
                            //fetchAlbumsOfHtmlData(albumUrl, html);
                            fetchAlbumsOfSvlist(showChannelId, sourceid, String.valueOf(ty));
                        }
                        mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                    }
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取不到 播放 url
     *
     * @param albumUrl
     * @param albumHtml
     */
    @Deprecated
    private void fetchAlbumsOfHtmlData(String albumUrl, String albumHtml) {
        String key = ":initialized-data='";
        if (albumHtml.contains(key)) {
            int len = albumHtml.indexOf(key);
            String tmp = albumHtml.substring(len + key.length());
            len = tmp.indexOf("'");
            String data = tmp.substring(0, len);

            try {
                JSONArray arr = new JSONArray(data);
                int vlistLen = arr.length();

                for (int i = 0; i < vlistLen; i++) {
                    JSONObject obj = (JSONObject) arr.get(i);

                    String text = obj.getString("subtitle");
                    String title = obj.getString("name");
                    String url;
                    if (obj.has("url")) {
                        url = obj.getString("url");
                    } else if (obj.has("vid")) {
                        // TODO vid -> url
                        url = obj.getString("vid");
                    } else {
                        continue;
                    }

                    mVideoList.add(new ListVideo(text, title, url));
                }
                Log.e(TAG, "ZY VideoList " + mVideoList.size() + "/" + vlistLen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!mVideoList.isEmpty()) {
            return;
        }

        if (!TextUtils.isEmpty(albumUrl)) {
            if (!albumUrl.startsWith("http")) {
                albumUrl = "https:" + albumUrl;
            }
            String html = HttpUtils.open(albumUrl);
            if(html.startsWith("Exception: ")){
                fault(html);
                return;
            }
            Utils.setFile("iqiyi.html", html);
        }
    }

    private void fetchAlbumsOfSvlist(int cid, int sid, String time) throws Exception {
        String album = IQiyiUtils.fetchSvlist(cid, sid, time);

        JSONObject obj = new JSONObject(album);

        String code = obj.getString("code");
        if (!"A00000".equals(code)) {

            if (obj.has("msg")) {
                Log.e(TAG, obj.getString("msg"));
            } else if (obj.has("ctl")) {
                Log.e(TAG, obj.getJSONObject("ctl").getString("msg"));
            }else{
                Log.e(TAG, "fetchAlbumsOfSvlist error" + code);
            }
            return;
        }

        JSONObject data = obj.getJSONObject("data");
        if (!data.has(time)) {
            Log.e(TAG, "no such " + time + " data.");
            return;
        }
        JSONArray vlist = data.getJSONArray(time);
        int vlistLen = vlist.length();

        for (int i = 0; i < vlistLen; i++) {
            JSONObject stream = (JSONObject) vlist.get(i);

            String period = stream.getString("period");
            String shortTitle = stream.getString("shortTitle");
            int payMark = stream.getInt("payMark");
            String title = stream.getString("name");
            String url = stream.getString("playUrl") + "?tvid=" + stream.getString("tvId") + "&vid=" + stream.getString("vid");
            String text = period + " " + shortTitle + (payMark == 1 ? "(VIP)" : "");

            mVideoList.add(new ListVideo(text, title, url));
        }
        Log.e(TAG, "SV VideoList " + mVideoList.size() + "/" + vlistLen);
    }


    private void fetchAlbumsOfAvlist(int albumCount, String albumId) throws Exception {
        int pages = (int) Math.ceil((float) albumCount / 50);
        for (int page = 1; page <= pages; page++) {
            String album = IQiyiUtils.fetchAvlist(albumId, page);
            String key = "var tvInfoJs=";
            if (album.contains(key)) {
                int len = album.indexOf(key);
                album = album.substring(len + key.length());
                JSONObject obj = new JSONObject(album);


                String code = obj.getString("code");
                if (!"A00000".equals(code)) {

                    if (obj.has("msg")) {
                        Log.e(TAG, obj.getString("msg"));
                    } else if (obj.has("ctl")) {
                        Log.e(TAG, obj.getJSONObject("ctl").getString("msg"));
                    }else{
                        Log.e(TAG, "fetchAlbumsOfAvlist error" + code);
                    }
                    continue;
                }

                JSONArray vlist = obj.getJSONObject("data").getJSONArray("vlist");
                int vlistLen = vlist.length();

                for (int i = 0; i < vlistLen; i++) {
                    JSONObject stream = (JSONObject) vlist.get(i);

                    String pds = stream.getString("pds");
                    int payMark = stream.getInt("payMark");
                    String title = stream.getString("vn");
                    String url = stream.getString("vurl")
                            + "?tvid=" + stream.getString("id")
                            + "&vid=" + stream.getString("vid");
                    String text = pds + (payMark == 1 ? "(VIP)" : "");

                    mVideoList.add(new ListVideo(text, title, url));
                }
                Log.e(TAG, "TV VideoList " + mVideoList.size() + "/" + vlistLen);
            } else {
                Log.e(TAG, "数据tvInfoJs异常");
                continue;
            }
        }
    }

    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mUrlList.size() < 2) {
            mClarityView.setEnabled(false);
        } else {
            mClarityView.setEnabled(true);
        }

        mClarityView.setText(((IQiyiVideo) video).getType().getProfile());
    }

}