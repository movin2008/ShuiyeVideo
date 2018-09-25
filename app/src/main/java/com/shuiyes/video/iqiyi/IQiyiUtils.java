package com.shuiyes.video.iqiyi;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.MD5;

public class IQiyiUtils {

    private final static String TAG = "IQiyiUtils";

    public static String fetchVideo(String tvid) {
        return HttpUtils.open(IQiyiUtils.getVideoInfoUrl(tvid));
    }

    public static String getVideoInfoUrl(String tvid) {
        return "http://mixer.video.iqiyi.com/jp/mixin/videos/"+tvid;
    }

    private static final String src = "76f90cbd92f94a2e925d83e8ccd22cb7";
    private static final String key = "d5fb4bd9d50c4be6948c97edd7254b0e";

    public static String getVMS(String tvid, String vid){
        String t = System.currentTimeMillis() / 1000 + "";
        String sc = MD5.encode(t + key  + vid);
        String url = String.format("http://cache.m.iqiyi.com/tmts/%s/%s/?t=%s&sc=%s&src=%s",tvid,vid,t,sc,src);
        return HttpUtils.open(url);
    }

    public static String search(String keyword) {
        String url = "https://search.video.iqiyi.com/o?if=html5&pageNum=1&pageSize=30&video_allow_3rd=1";
        // 电影,1;电视剧,2;纪录片,3;动漫,4;音乐,5;综艺,6;娱乐,7;游戏,8;旅游,9;片花,10;
        // 公开课,11;教育,12;时尚,13;时尚综艺,14;少儿综艺,15;微电影,16;体育,17;奥运,18;直播,19;广告,20;
        // 生活,21;搞笑,22;奇葩,23",
        url += "&channel_name=";
        url += "&key="+keyword;
        return HttpUtils.open(url);
    }
}
