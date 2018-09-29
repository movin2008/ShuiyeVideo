package com.shuiyes.video.qq;

import com.shuiyes.video.util.HttpUtils;

public class QQUtils {

    private final static String TAG = "QQUtils";

    public static final String[] PLATFORMS = {"4100201", "11"};
    public static final String appver = "3.2.19.333";

    public static String fetchVideo(String platform, String vid) {
        String url = String.format("http://vv.video.qq.com/getinfo?otype=json&appver=%s&platform=%s&defnpayver=1&defn=shd&vid=%s", appver, platform, vid);
        return HttpUtils.open(url);
    }

    public static String fetchUrl(String formatId, String vid, String filename) {
        String url = String.format("http://vv.video.qq.com/getkey?otype=json&appver=%s&platform=11&format=%s&vid=%s&filename=%s", appver, formatId, vid, filename);
        return HttpUtils.open(url);
    }

    public static String formatJson(String html) {
        String key = "QZOutputJson=";
        return html.substring(html.indexOf(key)+key.length());
    }

}