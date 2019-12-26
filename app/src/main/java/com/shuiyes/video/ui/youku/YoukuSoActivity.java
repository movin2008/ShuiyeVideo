package com.shuiyes.video.ui.youku;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.base.BaseSearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.PlayUtils;

import java.util.ArrayList;
import java.util.List;

public class YoukuSoActivity extends BaseSearchActivity {

    private static final boolean DEBUG = false;// true false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("优酷搜索");
        mSearch.setText("汪汪队立大功");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

                String ctoken = YoukuUtils.fetchCToken();
                if (TextUtils.isEmpty(ctoken)) {
                    notice("Fetch ctoken error, please try again.");
                }
                Log.e(TAG, ctoken);

                String cna = YoukuUtils.fetchCna();
                if (TextUtils.isEmpty(ctoken)) {
                    notice("Fetch cna error, please try again.");
                }
                Log.e(TAG, "cna=" + cna);

                String html = YoukuUtils.search(keyword, ctoken + " cna=" + cna + ";");
                if (DEBUG) {
                    Log.e(TAG, html);
                }

                if (TextUtils.isEmpty(html)) {
                    notice("Seach " + keyword + " is empty.");
                    return false;
                }

                if (mCancelled) {
                    notice("Will list albums has Cancelled.");
                    return false;
                }

                mAlbums.clear();

                int flag = 1;
                String start = "<div class=\\\"sk-mod\\\">";
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

                    String key = "data-spm=\\\"dtitle\\\"";
                    int len = data.indexOf(key);
                    String tmp = data.substring(len + key.length());

                    key = "\\\">";
                    len = tmp.indexOf(key);
                    tmp = tmp.substring(len + key.length());

                    len = tmp.indexOf("</a>");
                    String albumTitle = tmp.substring(0, len).replaceAll("<em class=\\\\\"hl\\\\\">", "").replaceAll("</em>", "");
                    if (DEBUG) {
                        Log.e(TAG, "albumTitle ==================== " + albumTitle);
                    }

                    if (TextUtils.isEmpty(albumTitle)) {
                        if (DEBUG) {
                            Log.e(TAG, "albumTitle ?????????????? " + tmp);
                        }

                        len = tmp.indexOf(">");
                        albumTitle = Html.fromHtml(tmp.substring(len + 1, tmp.indexOf("</a>"))).toString();
//				        albumTitle = tmp.substring(len+1, tmp.indexOf("</a>"));
                    }

                    key = "href=\\\"";
                    len = data.indexOf(key);
                    data = data.substring(len + key.length());

                    len = data.indexOf("\\\"");
                    String albumUrl = HttpUtils.FormateUrl(data.substring(0, len));
                    if (DEBUG) {
                        Log.e(TAG, flag + " albumUrl ===================== " + albumUrl);
                    }

                    String albumSummary = "暂无简介";
                    key = "<label>简介:</label>";
                    len = data.indexOf(key);
                    if (len != -1) {
                        tmp = data.substring(len + key.length());

                        len = tmp.indexOf("</span>\\n\\t");
                        albumSummary = tmp.substring(0, len);
                        if (DEBUG) {
                            Log.e(TAG, "albumSummary ===================== " + albumSummary);
                        }
                    }

                    key = "\\n\\t\\t<img";
                    len = data.indexOf(key);
                    tmp = data.substring(len + key.length());

                    key = "src=\\\"";
                    if (tmp.indexOf("alt=\\\"") != -1 && tmp.indexOf("alt=\\\"") < 10) {
                        key = "alt=\\\"";
                    }
                    len = tmp.indexOf(key);
                    tmp = tmp.substring(len + key.length());

                    len = tmp.indexOf("\\\"");
                    String albumImg = HttpUtils.FormateUrl(tmp.substring(0, len)).trim();
                    if (!albumImg.startsWith("http")) {
                        albumImg = "https:" + albumImg;
                    }
                    if (DEBUG) {
                        Log.e(TAG, flag + " albumImg ===================== " + albumImg);
                    }

                    int prev = 0;
                    List<ListVideo> listVideos = new ArrayList<ListVideo>();

                    String titleKey = "<a title=\\\"";
                    while (data.contains(titleKey)) {

                        if (mCancelled) {
                            notice("Listing videos has Cancelled.");
                            return false;
                        }

                        len = data.indexOf(titleKey);
                        data = data.substring(len + titleKey.length());

                        len = data.indexOf("\\\"");
                        String name = data.substring(0, len);
                        if ("查看更多".equals(name)) {
                            continue;
                        }

                        key = "href=\\\"";
                        len = data.indexOf(key);
                        data = data.substring(len + key.length());

                        len = data.indexOf("\\\"");
                        String url = data.substring(0, len);

                        key = ">";
                        len = data.indexOf(key);
                        data = data.substring(len + key.length());

                        len = data.indexOf("</a>");
                        String index = data.substring(0, len);

                        int id = 0;
                        try {
                            id = Integer.parseInt(index);
                        } catch (NumberFormatException e) {
                        }
                        if (id != 0 && id - prev != 1) {
                            break;
                        } else if (id == 0) {
//                         notice("4 ?????????????? " + name);
//                         notice("5 ??????????????  " + url);
//                         notice("6 ?????????????? " + index);
                            break;
                        }
                        prev = id;

                        ListVideo listVideo = new ListVideo(id, name, url);
                        listVideos.add(listVideo);

//				       notice("++++++++++++++++ "+listVideo);
                    }
                    html = html.substring(html.indexOf(start) + start.length());

                    if (PlayUtils.isSurpportUrl(albumUrl)) {
                        Album album = new Album(flag++, albumTitle, albumSummary, albumImg, albumUrl, listVideos);
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
