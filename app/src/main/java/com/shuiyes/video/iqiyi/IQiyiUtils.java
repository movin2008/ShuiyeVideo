package com.shuiyes.video.iqiyi;

import android.text.Html;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.MD5;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

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
}

/**
 视频详情：

 http://mixer.video.iqiyi.com/jp/mixin/videos/1178224700

 https://cache.video.iqiyi.com/jp/vi/1178224700/6c32b745086b7c8e76c89429debc7a37/
 http://cache.video.iqiyi.com/jp/avlist/207834001/2/50/
 http://cache.video.iqiyi.com/jp/othlist/205014501/4/desc/
 */