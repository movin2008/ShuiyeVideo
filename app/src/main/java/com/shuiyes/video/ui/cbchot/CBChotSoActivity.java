package com.shuiyes.video.ui.cbchot;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.base.BaseSearchActivity;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;

import java.util.ArrayList;


public class CBChotSoActivity extends BaseSearchActivity {

    private static final boolean DEBUG = false;// true false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("中广热点云搜索");
        mSearch.setText("暗战");
    }

    @Override
    protected void searchVideos(String keyword) {
        mSearchAsyncTask = new SearchAsyncTask();
        mSearchAsyncTask.execute(keyword);
    }

    @Override
    protected void playVideo(int position) {
        PlayUtils.play(mContext, mAlbums.get(position));
    }

    private class SearchAsyncTask extends AsyncTask<String, Integer, Boolean> {

        private boolean mCancelled = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCancelled = false;
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            Log.e(TAG, "onCancelled.");
            mCancelled = true;
        }

        @Override
        protected Boolean doInBackground(String... args) {
            String keyword = args[0];
            try {
                if (mCancelled) {
                    notice("doInBackground has Cancelled.");
                    return false;
                }

                String html = HttpUtils.open(CBChotUtils.SEARCH_URL + mSearch.getText(), CBChotUtils.XCLIENT, false);
                if (DEBUG) {
                    Log.e(TAG, html);
                }

                if (mCancelled) {
                    notice("Will list albums has Cancelled.");
                    return false;
                }

                Utils.setFile("cbchot.html", html);

                int flag = 1;
                mAlbums.clear();
                String start = "<div class=\"img-mod \">";
                while (html.contains(start)) {

                    if (mCancelled) {
                        notice("Listing albums has Cancelled.");
                        return false;
                    }

                    int startIndex = html.indexOf(start);
                    int endIndex = html.indexOf(start, startIndex + start.length());
                    String data;
                    if (endIndex != -1) {
                        data = html.substring(startIndex, endIndex);
                    } else {
                        data = html.substring(startIndex);
                    }
                    if (DEBUG) {
                        Log.e(TAG, "data ===== " + data);
                    }

                    String key = "<div class=\"name\">";
                    int len = data.indexOf(key);
                    String tmp = data.substring(len + key.length());
                    String albumTitle = tmp.substring(0, tmp.indexOf("</div>"));
                    if (DEBUG) {
                        Log.e(TAG, flag + " albumTitle ===================== " + albumTitle);
                    }

                    key = "data-src=\"";
                    len = data.indexOf(key);
                    tmp = data.substring(len + key.length());
                    String albumImg = HttpUtils.FormateUrl(tmp.substring(0, tmp.indexOf("\""))).trim();
                    if (DEBUG) {
                        Log.e(TAG, flag + " albumImg ===================== " + albumImg);
                    }

                    key = "data-typeId=\"\" href=\"cbchot:";
                    len = data.indexOf(key);
                    tmp = data.substring(len + key.length());
                    String albumUrl = HttpUtils.FormateUrl(tmp.substring(0, tmp.indexOf("\""))).trim();

                    if (albumUrl.startsWith("wasuVideo|") || albumUrl.startsWith("videoDetails|")) {
                        String series = "/android/detail/series/";
                        String transcoded = "/android/detail/transcoded/";
                        boolean resCP = albumUrl.indexOf("|") != albumUrl.lastIndexOf("|");
                        if (albumUrl.indexOf(series) > 0) {
                            if(resCP){
                                albumUrl = "http://and.cbchot.com/api/video_detail/pay/" + albumUrl.substring(albumUrl.indexOf(series) + series.length(), albumUrl.lastIndexOf("|"));
                            }else{
                                albumUrl = "http://and.cbchot.com/api/video_detail/pay/" + albumUrl.substring(albumUrl.indexOf(series) + series.length());
                            }
                        } else if (albumUrl.indexOf(transcoded) > 0) {
                            if(resCP){
                                albumUrl = "http://and.cbchot.com/api/video_detail/pay/" + albumUrl.substring(albumUrl.indexOf(transcoded) + transcoded.length(), albumUrl.lastIndexOf("|"));
                            }else{
                                albumUrl = "http://and.cbchot.com/api/video_detail/pay/" + albumUrl.substring(albumUrl.indexOf(transcoded) + transcoded.length());
                            }                        }
                    }

                    if (DEBUG) {
                        Log.e(TAG, flag + " albumUrl ===================== " + albumUrl);
                    }

                    String albumSummary = "暂无简介";
                    html = html.substring(html.indexOf(start) + start.length());

                    Log.e(TAG, flag + ", <" + albumTitle + "> " + albumUrl);
                    if (PlayUtils.isSurpportUrl(albumUrl)) {
                        Album album = new Album(flag++, albumTitle, albumSummary, albumImg, albumUrl, new ArrayList<ListVideo>());
                        if (PlayUtils.isSurpportUrl(album.getPlayurl())) {
                            mAlbums.add(album);
                        } else {
                            Log.e(TAG, "暂不支持视频 《" + albumTitle + "》" + album.getPlayurl());
                        }
                    } else {
                        Log.e(TAG, "暂不支持播放 《" + albumTitle + "》" + albumUrl);
                    }
                }

                Log.e(TAG, "mAlbums ===== " + mAlbums.size());
                return keyword.equals(mSearchText);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
            }
        }
    }
}
