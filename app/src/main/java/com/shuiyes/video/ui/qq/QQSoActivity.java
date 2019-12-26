package com.shuiyes.video.ui.qq;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.base.BaseSearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class QQSoActivity extends BaseSearchActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("腾讯搜索");
        mSearch.setText("超级飞侠");
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

                String html = QQUtils.searchVideos(keyword);
                Utils.setFile("qqso.html", html);

                if (TextUtils.isEmpty(html)) {
                    notice("Search " + keyword + ", videos is empty.");
                    return false;
                }

                if (mCancelled) {
                    notice("Will list albums has Cancelled.");
                    return false;
                }


                int flag = 1;
                mAlbums.clear();

                String key = "result_item ";
                while (html.contains(key)) {
                    try {

                        html = html.substring(html.indexOf(key) + key.length());
                        String result = html;
                        if (html.contains(key)) {
                            result = html.substring(0, html.indexOf(key));
                        }else if (html.contains("result_relative")) {
                            result = html.substring(0, html.indexOf("result_relative"));
                        }

                        String tmp = "<a href=\"";
                        String albumUrl = result.substring(result.indexOf(tmp) + tmp.length());
                        albumUrl = albumUrl.substring(0, albumUrl.indexOf("\""));

                        tmp = "<img ";
                        String albumImg = result.substring(result.indexOf(tmp) + tmp.length());
                        tmp = "src=\"";
                        albumImg = albumImg.substring(albumImg.indexOf(tmp) + tmp.length());
                        if(!albumImg.startsWith("http")){
                            albumImg = "https:" + albumImg.substring(0, albumImg.indexOf("\""));
                        }

                        tmp = "alt=\"";
                        String albumTitle = result.substring(result.indexOf(tmp) + tmp.length());
                        albumTitle = albumTitle.substring(0, albumTitle.indexOf("\""));

                        if (result.contains("mark_2.png")) {
                            albumTitle += "(预告)";
                        } else if (result.contains("mark_5.png")) {
                            albumTitle += "(VIP)";
                        }

                        String albumSummary = "";
                        tmp = "<span class=\"label\">简　介：</span>";
                        if(result.indexOf(tmp) != -1){
                            albumSummary = result.substring(result.indexOf(tmp) + tmp.length());
                            tmp = ">";
                            albumSummary = albumSummary.substring(albumSummary.indexOf(tmp) + tmp.length());
                            albumSummary = albumSummary.substring(0, albumSummary.indexOf("<"));
                        }else {
                            tmp = "<span class=\"label\">主　题：</span>";
                            if(result.indexOf(tmp) != -1){
                                albumSummary = result.substring(result.indexOf(tmp) + tmp.length());
                                tmp = ">";
                                albumSummary = albumSummary.substring(albumSummary.indexOf(tmp) + tmp.length());
                                albumSummary = albumSummary.substring(0, albumSummary.indexOf("<"));
                            }
                        }

                        List<ListVideo> listVideos = new ArrayList<ListVideo>();

                        String listkey = "<div class=\"item\">";
                        String listHtml = result;
                        while (listHtml.contains(listkey)) {
                            listHtml = listHtml.substring(listHtml.indexOf(listkey) + listkey.length());
                            result = listHtml.substring(0, listHtml.indexOf("</div>"));

                            tmp = "<a href=\"";
                            String url = result.substring(result.indexOf(tmp) + tmp.length());
                            url = url.substring(0, url.indexOf("\""));

                            tmp = "\">";
                            String title = result.substring(result.indexOf(tmp) + tmp.length());
                            title = title.substring(0, title.indexOf("<"));

                            if (result.contains("mark_12.png")) {
                                title += "(预告)";
                            } else if (result.contains("mark_14.png")) {
                                title += "(VIP)";
                            }

                            ListVideo listVideo = new ListVideo(title, title, url);
                            listVideos.add(listVideo);
                        }

                        Album album = new Album(flag, albumTitle, albumSummary, albumImg, albumUrl, listVideos);
                        if (PlayUtils.isSurpportUrl(album.getPlayurl())) {
                            mAlbums.add(album);
                        } else {
                            Log.e(TAG, "暂不支持视频 《" + albumTitle + "》" + album.getPlayurl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "Albums1 ===== " + mAlbums.size());
                if (keyword.equals(mSearchText)) {
                    mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
                }

                return keyword.equals(mSearchText);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
        }
    }

}
