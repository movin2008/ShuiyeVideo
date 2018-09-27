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

public class IQiyiVActivity extends BasePlayActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSourceView.setOnClickListener(this);
        mClarityView.setOnClickListener(this);
        mClarityView.setEnabled(false);
        mSelectView.setOnClickListener(this);
        mNextView.setOnClickListener(this);

        playVideo();
    }

    private List<IQiyiVideo> mUrlList = new ArrayList<IQiyiVideo>();

    @Override
    protected void playVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String tvid = null;
                    String vid = null;

                    String key = "?tvid=";
                    if(mUrl.contains(key)){
                        int len = mUrl.indexOf(key);
                        String tmp = mUrl.substring(len + key.length());
                        len = tmp.indexOf("&");
                        tvid = tmp.substring(0, len);

                        key = "&vid=";
                        len = tmp.indexOf(key);
                        vid = tmp.substring(len + key.length());

                        Log.e(TAG, ":url 'tvid="+tvid+", vid="+vid+"'");
                    }

                    if(tvid == null && vid == null){
                        mHandler.sendEmptyMessage(MSG_FETCH_TOKEN);
                        String html = HttpUtils.open(mUrl);//.replaceAll("http://m.iqiyi.com", "https://www.iqiyi.com")

                        if(html == null){
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
                    Utils.setFile("/sdcard/iqiyi", video);

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
                    Utils.setFile("/sdcard/iqiyi", video);
//                    Log.e(TAG, "video ="+video);

                    JSONObject obj = new JSONObject(video);
                    if (!"A00000".equals(obj.getString("code"))) {
//                        Log.e(TAG, info);

                        if(obj.has("msg")){
                            fault(obj.getString("msg"));
                        }else{
                            fault(obj.getJSONObject("ctl").getString("msg"));
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
                            Log.i(TAG, v.toStr(mContext));

                            // TODO 4K 较卡，要改成可配置
                            if(playVideo == null && v.getType().getScreenSize() < IQiyiVideo.UHD_SZ){
                                playVideo = v;
                            }
                        }

                        mVid = vid;
                        mCurrentPosition = 0;
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CACHE_VIDEO, playVideo));

                        if(albumCount > 1){
                            int pages = (int) Math.ceil((float)albumCount/50);
                            int flag = 1;
                            List<ListVideo> videoList = new ArrayList<ListVideo>();
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

                                        videoList.add(new ListVideo(flag++, title, url));
                                    }
                                    Log.e(TAG, "videoList "+videoList.size()+"/"+vlistLen);
                                }else{
                                    fault("数据tvInfoJs异常");
                                    return;
                                }

                            }

                            if(mVideoList.isEmpty() || videoList.size() > mVideoList.size()) {
                                mVideoList.clear();
                                mVideoList.addAll(videoList);
                                mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
                            }

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
                        mUrl = v.getUrl();

                        mVideoView.stopPlayback();

                        mStateView.setText("初始化...");
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

        mClarityView.setText(((IQiyiVideo)video).getType().getProfile());
    }

    /**
     * 获取不全，最多50集
     * @param albumUrl
     */
    @Deprecated
    private void listAlbumUrl(String albumUrl){
        if(TextUtils.isEmpty(albumUrl)){
            Log.e(TAG, "list album is empty.");
            return;
        }
        if(!albumUrl.contains("iqiyi.com/a_")){
            Log.e(TAG, albumUrl+" is illegally.");
            return;
        }

        String html = HttpUtils.open(albumUrl);

        if (TextUtils.isEmpty(html)) {
            Log.e(TAG, "Seach album is empty.");
            return;
        }

        String key = "<ul class=\"site-piclist";
        if(html.contains(key)){
            int len = html.indexOf(key);
            html = html.substring(len + key.length());
            html = html.substring(0, html.indexOf("</ul>"));

            Utils.setFile("/sdcard/iqiyi.html", html);

            int flag = 1;
            List<ListVideo> videoList = new ArrayList<ListVideo>();
            String start = "<li data-albumlist-elem=\"playItem\">";
            while (html.contains(start)) {

                int startIndex = html.indexOf(start);
                int endIndex = html.indexOf(start, startIndex + start.length());
                String data = null;
                if (endIndex != -1) {
                    data = html.substring(startIndex, endIndex);
                } else {
                    data = html.substring(startIndex);
                }

                key = "href=\"";
                data = data.substring(data.indexOf(key) + key.length());
                String url = data.substring(0, data.indexOf("\""));

                key = "<p class=\"site-piclist_info_title\">";
                data = data.substring(data.indexOf(key) + key.length());
                key = "\">";
                data = data.substring(data.indexOf(key) + key.length());
                String title = data.substring(0, data.indexOf("</a>")).trim();

                videoList.add(new ListVideo(flag++, title, url));

                html = html.substring(html.indexOf(start) + start.length());
            }

            if(mVideoList.isEmpty() || videoList.size() > mVideoList.size()){
                mVideoList.clear();
                mVideoList.addAll(videoList);
                mHandler.sendEmptyMessage(MSG_UPDATE_SELECT);
            }
        }
    }

}