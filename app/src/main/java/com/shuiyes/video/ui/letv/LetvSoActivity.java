package com.shuiyes.video.ui.letv;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.ui.base.BaseSearchActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.Constants;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LetvSoActivity extends BaseSearchActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearch.setHint("乐看搜索");
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

                String html = LetvUtils.searchVideos(keyword);
//					notice(result);

                if (html.startsWith("Exception: ")) {
                    notice(html);
                    return false;
                }

                if (mCancelled) {
                    notice("Will list albums has Cancelled.");
                    return false;
                }


                if (html.contains("play-terminal active j-tui-terminal")) {
                    html = html.substring(html.indexOf("play-terminal active j-tui-terminal"));
                } else {
                    notice("解析异常.");

                    Utils.setFile("letv.html", html);
                    return false;
                }

                int flag = 1;
                mAlbums.clear();
                String result = html;

                String start = "<div class=\"So-detail ";
                while (html.contains(start)) {

                    if (mCancelled) {
                        notice("Listing albums has Cancelled.");
                        return false;
                    }

                    int startIndex = html.indexOf(start);
                    int endIndex = html.indexOf(start, startIndex + start.length());
                    String data = null;
                    if (endIndex != -1) {
                        data = html.substring(startIndex, endIndex);
                    } else {
                        data = html.substring(startIndex);
                    }
//                    notice("data"+flag+" ===== "+data);

                    if (data.startsWith("<div class=\"So-detail Star-so\"")) {
                        html = html.substring(startIndex + start.length());

                        // 跳过明星
                        continue;
                    }


                    // 专辑播放地址
                    String key = "<a href=\"";
                    int len = data.indexOf(key);
                    String tmp = data.substring(len + key.length());
                    len = tmp.indexOf("\"");
                    String albumUrl = tmp.substring(0, len);

                    // 专辑图片
                    key = "src=\"";
                    len = data.indexOf(key);
                    tmp = data.substring(len + key.length());
                    len = tmp.indexOf("\"");
                    String albumImg = tmp.substring(0, len);

                    // 专辑名称
                    key = "<div class=\"info-tit\">";
                    len = data.indexOf(key);
                    tmp = data.substring(len + key.length());
                    key = "\">";
                    len = tmp.indexOf(key);
                    tmp = tmp.substring(len + key.length());
                    len = tmp.indexOf("</a>");
                    String albumTitle = tmp.substring(0, len).replaceAll("<u>", "").replaceAll("</u>", "");
                    boolean ico_vip = data.contains("<em class=ico_vip>会员</em>");

                    // 专辑简介
                    key = "<div class=\"info-cnt\" data-statectn=\"typeResult\" data-itemhldr=\"a\">";
                    len = data.indexOf(key);
                    if (len == -1) {
                        key = "<div class=\"info-con\">";
                        len = data.indexOf(key);
                    }
                    String albumSummary = "暂无简介.";
                    if (len != -1) {
                        tmp = data.substring(len + key.length());
                        len = tmp.indexOf("<a");
                        if (len != -1) {
                            albumSummary = tmp.substring(0, len).replaceAll("<p>", "").replaceAll("</p>", "").trim();
                        }
                    }

                    // 专辑列表
                    List<ListVideo> listVideos = new ArrayList<ListVideo>();
                    key = "data-info=\"";
                    len = data.indexOf(key);
                    if (len != -1) {
                        tmp = data.substring(len + key.length());
                        len = tmp.indexOf("\"");
                        String dataInfo = tmp.substring(0, len);

                        try {
                            JSONObject obj = new JSONObject(dataInfo);

                            String payEpisode = obj.getString("payEpisode");
//                            notice("data"+flag+" ===== "+payEpisode);

                            List<String> payEpisodes = new ArrayList<String>();
                            if (!TextUtils.isEmpty(payEpisode)) {

                                String[] vidEpisodes = payEpisode.split(",");
                                for (int i = 0; i < vidEpisodes.length; i++) {
                                    String[] vids = vidEpisodes[i].split("-");
                                    payEpisodes.add(vids[1]);
                                }
                            }

                            String vidEpisode = obj.getString("vidEpisode");
//                            notice("data"+flag+" ===== "+vidEpisode);

                            if (!TextUtils.isEmpty(vidEpisode)) {

                                String[] vidEpisodes = vidEpisode.split(",");
                                for (int i = 0; i < vidEpisodes.length; i++) {
                                    String[] vids = vidEpisodes[i].split("-");
                                    String text = payEpisodes.contains(vids[1]) ? vids[0] + "(VIP)" : vids[0];
                                    ListVideo listVideo = new ListVideo(text, vids[1], LetvUtils.getVideoPlayUrlFromVid(vids[1]));
                                    listVideos.add(listVideo);
                                }
                            }
                        } catch (Exception e) {
//                            notice("data"+flag+" ===== "+dataInfo);
                        }
                    }

                    if (listVideos.size() == 0) {
                        // 如果没有取到专辑列表信息，则可能是 音乐/综艺 等
                        listMusicOrZongyi(listVideos, data);
                    }

                    if (listVideos.size() == 0) {
                        // 如果没有取到专辑列表信息，则可能是 **

                        String titleKey = "<dl class=\"dl_temp";
                        while (data.contains(titleKey)) {
                            len = data.indexOf(titleKey);
                            data = data.substring(len + titleKey.length());

                            len = data.indexOf("</dl>");
                            String dl = data.substring(0, len);

                            key = "href=\"";
                            len = dl.indexOf(key);
                            tmp = dl.substring(len + key.length());
                            len = tmp.indexOf("\"");
                            String listUrl = tmp.substring(0, len);

                            key = "title=\"";
                            len = dl.indexOf(key);
                            tmp = dl.substring(len + key.length());
                            len = tmp.indexOf("\"");
                            String listTitle = tmp.substring(0, len);

                            ListVideo listVideo = new ListVideo(listTitle, listTitle, listUrl);
                            listVideos.add(listVideo);

//                            notice("++++++++++++++++ "+listVideo);
                        }
                    }

                    if ((listVideos == null || listVideos.size() < 1) && ico_vip) {
                        // 没有专辑列表，要考虑是否是 VIP
                        albumTitle += " (VIP)";
                    }

                    if (PlayUtils.isSurpportUrl(albumUrl)) {
                        Album album = new Album(flag, albumTitle, albumSummary, albumImg, albumUrl, listVideos);
                        if (PlayUtils.isSurpportUrl(album.getPlayurl())) {
                            //notice(album.toString());

                            album.setOrder(++flag);
                            mAlbums.add(album);
                        } else {
                            Log.e(TAG, "暂不支持视频 《" + albumTitle + "》" + album.getPlayurl());
                        }
                    } else {
                        Log.e(TAG, "暂不支持播放 《" + albumTitle + "》" + albumUrl);
                    }

                    html = html.substring(startIndex + start.length());
                }

                Log.e(TAG, "Albums So ===== " + mAlbums.size());
                if (keyword.equals(mSearchText)) {
                    mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
                }

                String starKey = "<div class=\"So-detail Star-so";
                // 现获取明星的个人作品
                if (result.contains(starKey)) {
                    try {
                        String star = result.substring(result.indexOf(starKey) + starKey.length());
                        //notice(star);

                        starKey = "data-info=\"";
                        star = star.substring(star.indexOf(starKey) + starKey.length());
                        //notice(star);

                        String dataInfo = star.substring(0, star.indexOf("\""));
                        //notice(dataInfo);

                        JSONObject obj = new JSONObject(dataInfo);
                        String leId = obj.getString("leId");

                        if (!TextUtils.isEmpty(leId)) {
                            flag = listStarVideos(leId, flag);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "Albums Star ===== " + mAlbums.size());
                if (keyword.equals(mSearchText)) {
                    mHandler.sendEmptyMessage(Constants.MSG_LIST_ALBUM);
                }

                // 查询用户上传的视频
                flag = listUploadVideos(keyword, flag);

                Log.e(TAG, "Albums Upload ===== " + mAlbums.size());
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

    private int listVideos(String json, int flag) throws Exception {
        JSONObject obj = new JSONObject(json);

        if (!obj.has("data_list")) {
            Utils.setFile("letv", json);
            notice("No data list.");
            return flag;
        }

        JSONArray dataList = obj.getJSONArray("data_list");
        int dataListLen = dataList.length();
        //notice("dataListLen ===== " + dataListLen);

        for (int i = 0; i < dataListLen; i++) {
            JSONObject data = (JSONObject) dataList.get(i);

            String albumTitle = data.getString("name");
            String albumSummary = "暂无简介.";
            if (data.has("description")) {
                albumSummary = data.getString("description");
            }
            JSONObject images = data.getJSONObject("images");
            String albumImg = "";
            if (images.has("400*300")) {
                albumImg = images.getString("400*300");
            } else {
                Iterator<String> keys = images.keys();
                while (keys.hasNext()) {
                    albumImg = images.getString(keys.next());
                    break;
                }
            }

            String albumUrl = data.getString("url");
            if (TextUtils.isEmpty(albumUrl)) {
                if (data.has("vid")) {
                    String vid = data.getString("vid");
                    if (!TextUtils.isEmpty(vid)) {
                        albumUrl = LetvUtils.getVideoPlayUrlFromVid(vid);
                    }
                }
            }

            List<ListVideo> listVideos = new ArrayList<ListVideo>();
            if (data.has("vids")) {
                /**
                 * 1 电影，2 电视剧，11 综艺
                 */
                String category = data.getString("category");

                if ("11".equals(category)) {
                    JSONArray videoList = data.getJSONArray("videoList");
                    for (int j = 0; j < videoList.length(); j++) {

                        JSONObject video = videoList.getJSONObject(j);

                        String url = video.getString("url");//.replaceAll("letv.com", "le.com")

                        ListVideo listVideo = new ListVideo(video.getString("episodes"), video.getString("name"), url);
                        listVideos.add(listVideo);
                    }
                } else if ("2".equals(category)) {
                    String vids = data.getString("vids");

                    if (TextUtils.isEmpty(vids)) {
                        // 非乐视视频资源，走这里
                        vids = data.getString("videoPlayUrls");
                        String episodes = data.getString("episodes");
                        String[] videoPlayUrls = vids.split(";");

                        int count = Integer.parseInt(episodes);
                        int min = (count < videoPlayUrls.length && count != 0) ? count : videoPlayUrls.length;

                        for (int j = 0; j < min; j++) {
                            String[] videoPlayUrl = videoPlayUrls[j].split("\\|");
                            ListVideo listVideo = new ListVideo(j + 1, albumTitle + videoPlayUrl[0], videoPlayUrl[1]);
                            listVideos.add(listVideo);
                        }
                    } else {
                        if (TextUtils.isEmpty(albumUrl)) {
                            albumUrl = LetvUtils.getAlbumUrlFromVid(data.getString("aid"));
                        }

                        String episodes = data.getString("episodes");
                        String[] vidsArr = vids.split(",");

                        int count = Integer.parseInt(episodes);
                        int min = (count < vidsArr.length && count != 0) ? count : vidsArr.length;

                        for (int j = 0; j < min; j++) {
                            ListVideo listVideo = new ListVideo(j + 1, albumTitle + vidsArr[j], LetvUtils.getVideoPlayUrlFromVid(vidsArr[j]));
                            listVideos.add(listVideo);
                        }
                    }
                } else if ("1".equals(category)) {
                    JSONArray videoList = data.getJSONArray("videoList");
                    for (int j = 0; j < videoList.length(); j++) {

                        JSONObject video = videoList.getJSONObject(j);

                        String url = video.getString("url");//.replaceAll("letv.com", "le.com")

                        String name = video.getString("name");
                        if (video.has("areaAllow") && !video.getBoolean("areaAllow")) {
                            name += " (VIP)";
                        }


                        ListVideo listVideo = new ListVideo(name, name, url);
                        listVideos.add(listVideo);
                    }

                    String vids = data.getString("vids");
                    if (TextUtils.isEmpty(albumUrl) && !TextUtils.isEmpty(vids)) {
                        albumUrl = LetvUtils.getVideoPlayUrlFromVid(vids.split(",")[0]);
                    }
                }
            }


            if (TextUtils.isEmpty(albumUrl) && listVideos.size() == 0) {
                throw new Exception("No album url.");
            }

            Album album = new Album(flag, albumTitle, albumSummary, albumImg, albumUrl, listVideos);
            if (PlayUtils.isSurpportUrl(album.getPlayurl())) {
                //notice(album.toString());

                album.setOrder(++flag);
                mAlbums.add(album);
            } else {
                Log.e(TAG, "暂不支持视频 《" + albumTitle + "》" + album.getPlayurl());
            }
        }
        return flag;
    }

    private final int LIST_MAX = 4;

    private int listStarVideos(String keyword, int flag) {
        try {
            int count = 1;
            String json = null;

            // 尝试获取
            while (count < LIST_MAX) {
                json = LetvUtils.searchStarVideos(keyword);
                //notice(json);

                if (TextUtils.isEmpty(json)) {
                    notice(count + ", Search " + keyword + ", star videos is empty.");
                    if (count++ < LIST_MAX) {
                        Thread.sleep(500 * count);
                    }
                } else {
                    break;
                }
            }

            if (!TextUtils.isEmpty(json)) {
                return listVideos(json, flag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private int listUploadVideos(String keyword, int flag) {
        try {
            int count = 1;
            String json = null;

            // 尝试获取
            while (count <= LIST_MAX) {
                json = LetvUtils.searchUploadVideos(keyword);
                //notice(json);

                if (TextUtils.isEmpty(json)) {
                    notice(count + ", Search " + keyword + ", upload videos is empty.");
                    if (count++ < LIST_MAX) {
                        Thread.sleep(500 * count);
                    }
                } else {
                    break;
                }
            }

            if (!TextUtils.isEmpty(json)) {
                return listVideos(json, flag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private void listMusicOrZongyi(List<ListVideo> listVideos, String data) {
        String titleKey = "<ul class=\"music_ul";
        if (!data.contains(titleKey)) {
            titleKey = "<ul class=\"zongyi_ul";
            if (!data.contains(titleKey)) {
                return;
            }
        }

        int len = data.indexOf(titleKey);
        String tmp = data.substring(len + titleKey.length());
        String key = "</ul>";
        len = tmp.indexOf(key);
        String ul = tmp.substring(0, len + key.length());

        titleKey = "<li>";
        while (ul.contains("<li>")) {
            len = ul.indexOf(titleKey);
            ul = ul.substring(len + titleKey.length());

            len = ul.indexOf("</li>");
            String li = ul.substring(0, len);

            key = "<a href=\"";
            len = li.indexOf(key);
            tmp = li.substring(len + key.length());
            len = tmp.indexOf("\"");
            String listUrl = tmp.substring(0, len).replaceAll("letv.com", "le.com");

            key = ">";
            len = tmp.indexOf(key);
            tmp = tmp.substring(len + key.length());
            len = tmp.indexOf("</a>");
            String listTitle = tmp.substring(0, len).replaceAll("<span>", "").replaceAll("</span>", "").split("\\(")[0].trim();

            ListVideo listVideo = new ListVideo(listTitle, listTitle, listUrl);
            listVideos.add(listVideo);

            //notice("++++++++++++++++ "+listVideo);
        }

    }


}
