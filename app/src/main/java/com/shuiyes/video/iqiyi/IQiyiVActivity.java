package com.shuiyes.video.iqiyi;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BasePlayActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.dialog.AlbumDialog;
import com.shuiyes.video.dialog.MiscDialog;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;
import com.shuiyes.video.widget.MiscView;
import com.shuiyes.video.widget.NumberView;

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

        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mClarityView.setEnabled(false);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

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
                        playNextVideo(v.getTitle(), v.getUrl());
                    }
                });
                mAlbumDialog.show();
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

                    String key = "?tvid=";
                    if(mIntentUrl.contains(key)){
                        int len = mIntentUrl.indexOf(key);
                        String tmp = mIntentUrl.substring(len + key.length());
                        len = tmp.indexOf("&");
                        tvid = tmp.substring(0, len);

                        key = "&vid=";
                        len = tmp.indexOf(key);
                        vid = tmp.substring(len + key.length());

                        Log.e(TAG, ":url 'tvid="+tvid+", vid="+vid+"'");
                    }

                    if(tvid == null && vid == null){
                        mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                        String html = HttpUtils.open(mIntentUrl);//.replaceAll("http://m.iqiyi.com", "https://www.iqiyi.com")

                        if(TextUtils.isEmpty(html)){
                            fault("请重试");
                            return;
                        }

                        // page-info 见附录1
                        key = ":page-info='";
                        if(html.contains(key)){
                            int len = html.indexOf(key);
                            String tmp = html.substring(len + key.length());
                            len = tmp.indexOf("'");
                            String pageInfo = tmp.substring(0, len);

                            try{
                                JSONObject obj = new JSONObject(pageInfo);

                                tvid = obj.getString("tvId");
                                vid = obj.getString("vid");

                                Log.e(TAG, ":page-info 'tvid="+tvid+", vid="+vid+"'");
                                mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, obj.getString("tvName")));
                            }catch (Exception e){
                            }
                        }

                        key = "param['tvid'] = \"";
                        if(tvid == null && html.contains(key)){
                            int len = html.indexOf(key);
                            String tmp = html.substring(len + key.length());
                            len = tmp.indexOf("\"");
                            tvid = tmp.substring(0, len);
                            Log.e(TAG, "param['tvid'] = "+tvid);
                        }

                        key = "param['vid'] = \"";
                        if(vid == null && html.contains(key)){
                            int len = html.indexOf(key);
                            String tmp = html.substring(len + key.length());
                            len = tmp.indexOf("\"");
                            vid = tmp.substring(0, len);
                            Log.e(TAG, "param['vid'] = "+vid);
                        }
                    }

                    mHandler.sendEmptyMessage(MSG_FETCH_VIDEOINFO);
                    String video = IQiyiUtils.fetchVideo(tvid, vid);
                    Utils.setFile("iqiyi", video);

                    if (TextUtils.isEmpty(video)) {
                        fault("空数据,请重试");
                        return;
                    }

                    key = "var tvInfoJs=";
                    String albumUrl = null;
                    String albumId = null;
                    int albumCount = 0;
                    if(video.contains(key)){
                        int len = video.indexOf(key);
                        video = video.substring(len + key.length());

                        JSONObject obj = new JSONObject(video);
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TITLE, obj.getString("vn")));
                        albumUrl = obj.getString("au");

                        albumId = obj.getString("aid");
                        albumCount = obj.getInt("es");
                    }else{
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
                        if(obj.has("msg")){
                            msg = obj.getString("msg");
                        }else{
                            msg = obj.getJSONObject("ctl").getString("msg");
                        }

                        if("server return err-data.".equals(msg)){
                            fault("VIP 视频暂不支持播放");
                        }else{
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
                        if(type != null){
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
                            Log.i(TAG, v.toStr()+" mStream="+mStream);

                            // TODO 4K 较卡，要改成可配置
                            if((playVideo == null || v.getType().getProfile().equals(mStream)) && v.getType().getScreenSize() < IQiyiVideo.UHD_SZ){
                                playVideo = v;
                            }
                        }

                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));

                        if(mVideoList.isEmpty() && albumCount > 1){
                            int flag = 1;
                            int pages = (int) Math.ceil((float)albumCount/50);
                            for (int page=1; page<=pages; page++){
                                //  最后获取专辑信息
                                String album = IQiyiUtils.fetchAlbum(albumId, page);

                                key = "var tvInfoJs=";
                                if(album.contains(key)){
                                    int len = album.indexOf(key);
                                    album = album.substring(len + key.length());
                                    obj = new JSONObject(album);

                                    if (!"A00000".equals(obj.getString("code"))) {

                                        if(obj.has("msg")){
                                            Log.e(TAG, obj.getString("msg"));
                                        }else{
                                            Log.e(TAG, obj.getJSONObject("ctl").getString("msg"));
                                        }
                                        continue;
                                    }

                                    JSONArray vlist = obj.getJSONObject("data").getJSONArray("vlist");
                                    int vlistLen = vlist.length();

                                    for (int i = 0; i < vlistLen; i++) {
                                        JSONObject stream = (JSONObject) vlist.get(i);

                                        String title = stream.getString ("vn");
                                        String url = stream.getString("vurl")
                                                +"?tvid="+stream.getString("id")
                                                +"&vid="+stream.getString("vid");

                                        mVideoList.add(new ListVideo(flag++, title, url));
                                    }
                                    Log.e(TAG, "videoList "+mVideoList.size()+"/"+vlistLen);
                                }else{
                                    Log.e(TAG, "数据tvInfoJs异常");
                                    continue;
                                }
                            }
                            mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                        }
                    }
                } catch (Exception e) {
                    fault(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void cacheVideo(PlayVideo video) {
        if (mUrlList.size() < 2) {
            mClarityView.setEnabled(false);
        }else{
            mClarityView.setEnabled(true);
        }

        mClarityView.setText(((IQiyiVideo)video).getType().getProfile());
    }

    @Override
    protected void playNextSection(int index) {
    }

    @Override
    protected void playNextVideo(String title, String url) {
        super.playNextVideo(title, url);
        playVideo();
    }

}