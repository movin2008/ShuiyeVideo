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
                    if (mIntentUrl.contains("?tvid=") && mIntentUrl.contains("&vid=")) {
                        String key = "?tvid=";
                        int len = mIntentUrl.indexOf(key);
                        String tmp = mIntentUrl.substring(len + key.length());
                        len = tmp.indexOf("&");
                        tvid = tmp.substring(0, len);

                        key = "&vid=";
                        len = tmp.indexOf(key);
                        vid = tmp.substring(len + key.length());

                        Log.e(TAG, ":url 'tvid=" + tvid + ", vid=" + vid + "'");
                    } else {
                        mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                        String html = HttpUtils.get(mIntentUrl);
                        Utils.setFile("iqiyi.html", html);

                        if (!checkHtmlValid(html)) {
                            return;
                        }

                        String albumUrl = null;
                        String key = "\"albumUrl\":\"";
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
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String html = IQiyiUtils.fetchVideo(tvid, vid);
                    if (!checkHtmlValid(html)) {
                        return;
                    }
                    Utils.setFile("tvInfoJs", html);

                    String albumId, key = "var tvInfoJs=";
                    int albumCount, showChannelId, sourceid, ty;
                    if (html.contains(key)) {
                        int len = html.indexOf(key);
                        html = html.substring(len + key.length());

                        JSONObject obj = new JSONObject(html);

                        albumId = obj.getString("aid");
                        sourceid = obj.getInt("sid");
                        albumCount = obj.getInt("es");
                        showChannelId = obj.getInt("showChannelId");
                        ty = obj.getInt("ty") / 10000;

                        String title;
                        if (showChannelId == IQiyiUtils.Channel.dianshiju) {
                            title = obj.getString("vn");
                            String subt = obj.getString("subt");
                            if(!TextUtils.isEmpty(subt)){
                                title += " - " + subt;
                            }
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

                    fetchAlbums(albumId, albumCount, sourceid, ty, showChannelId);

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEO);
                    html = IQiyiUtils.getVMS(tvid, vid);
                    if (!checkHtmlValid(html)) {
                        return;
                    }
                    Utils.setFile("iqiyi.vi", html);

                    JSONObject obj = new JSONObject(html);
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

                        fault(msg, "server return err-data.".equals(msg));
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
                        }else{
                            Log.e(TAG, "Unkown vd: " + vd);
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
                    }
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int albumCount;
    private void fetchAlbums(final String albumId, final int albumCount, final int sourceid, final int ty, final int cid) {
        if (!mVideoList.isEmpty() && this.albumCount == albumCount) return;
        this.albumCount = albumCount;

        Log.e(TAG, "Album albumId=" + albumId + ", count=" + albumCount + ", showChannelId=" + cid + ", sourceid=" + sourceid + ", time=" + ty);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mVideoList.clear();

                    int showChannelId = IQiyiUtils.Channel.dianshiju;
                    fetchAlbumsOfAvlist(albumCount == 0 ? 1 : albumCount, albumId);

                    if (mVideoList.isEmpty() && sourceid != 0 && ty != 0) {
                        showChannelId = IQiyiUtils.Channel.zongyi;
                        fetchAlbumsOfSvlist(cid, sourceid, String.valueOf(ty));
                    }
                    mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
            } else {
                Log.e(TAG, "fetchAlbumsOfSvlist error: " + code);
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
                    } else {
                        Log.e(TAG, "fetchAlbumsOfAvlist error: " + code);
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
                    String url = stream.getString("vurl") + "?tvid=" + stream.getString("id") + "&vid=" + stream.getString("vid");
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
        mClarityView.setText(((IQiyiVideo) video).getType().getProfile());
        mClarityView.setVisibility(View.VISIBLE);
        if (mUrlList.size() < 2) {
            mClarityView.setEnabled(false);
        } else {
            mClarityView.setEnabled(true);
        }
    }

}