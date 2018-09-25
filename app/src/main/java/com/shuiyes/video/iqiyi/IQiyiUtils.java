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

}
