package com.shuiyes.video.iqiyi;

import android.text.TextUtils;
import android.util.Log;

import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.MD5;
import com.shuiyes.video.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class IQiyiUtils {

    private final static String TAG = "IQiyiUtils";

    public static String fetchVideo(String tvid, String vid) {
        String url = String.format("https://cache.video.iqiyi.com/jp/vi/%s/%s/",tvid,vid);
        return HttpUtils.open(url);
    }

    public static String fetchAlbum(String aid, int page) {
        String url = String.format("http://cache.video.iqiyi.com/jp/avlist/%s/%s/50/",aid,page);
        return HttpUtils.open(url);
    }

    private static final String src = "76f90cbd92f94a2e925d83e8ccd22cb7";
    private static final String key = "d5fb4bd9d50c4be6948c97edd7254b0e";

    public static String getVMS(String tvid, String vid){
        String t = System.currentTimeMillis() / 1000 + "";
        String sc = MD5.encode(t + key  + vid);
        String url = String.format("http://cache.m.iqiyi.com/tmts/%s/%s/?t=%s&sc=%s&src=%s",tvid,vid,t,sc,src);
        return HttpUtils.open(url);
    }

    public static String search(String keyword) throws UnsupportedEncodingException {
        String url = "https://search.video.iqiyi.com/o?if=html5&pageNum=1&pageSize=30&video_allow_3rd=1";
        // 电影,1;电视剧,2;纪录片,3;动漫,4;音乐,5;综艺,6;娱乐,7;游戏,8;旅游,9;片花,10;
        // 公开课,11;教育,12;时尚,13;时尚综艺,14;少儿综艺,15;微电影,16;体育,17;奥运,18;直播,19;广告,20;
        // 生活,21;搞笑,22;奇葩,23",
        url += "&channel_name=";
        url += "&key="+ URLEncoder.encode(keyword,"utf-8");
        return HttpUtils.open(url);
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

            Utils.setFile("iqiyi.html", html);

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
        }
    }

}

/**
 视频详情：

 http://mixer.video.iqiyi.com/jp/mixin/videos/1178224700

 https://cache.video.iqiyi.com/jp/vi/1178224700/6c32b745086b7c8e76c89429debc7a37/
 http://cache.video.iqiyi.com/jp/avlist/207834001/2/50/
 http://cache.video.iqiyi.com/jp/othlist/205014501/4/desc/
 */