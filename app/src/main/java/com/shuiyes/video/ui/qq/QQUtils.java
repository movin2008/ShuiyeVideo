package com.shuiyes.video.ui.qq;

import android.util.Log;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;

import java.net.URLEncoder;

public class QQUtils {

    private final static String TAG = "QQUtils";

    public final static String HOST = "https://v.qq.com";

    /**
     * 10203 标清 270p
     * 10212 高清 480p
     * 10201 超清 720p
     * 10209 蓝光 1080p
     * <p>
     * 4100201
     */
    public static final String[] PLATFORMS = {"10201", "10203", "10212", "10209",
            "4100201", "11", "10901"};

    public static String fetchVideo(String playUrl, String defn) throws Exception {
        /**
         charge=0&defaultfmt=auto&otype=ojson&guid=7bc7120ffc74caf9985f7f3a7a829312&flowid=e539d2024e3b2411dd4f872a255a1ab2_10901&platform=10901&sdtfrom=v1010&defnpayver=1&appVer=3.6.1&host=v.qq.com&ehost=https%3A%2F%2Fv.qq.com%2Fx%2Fcover%2Flcpwn26degwm7t3%2Fz002760mclr.html&refer=v.qq.com&sphttps=1&timestamp=1538408946&spwm=4&unid=0098ade3f51311e79d19a0429186d00a&vid=z0027injhcq&defn=&fhdswitch=0&show1080p=1&isHLS=1&dtype=3&sphls=1&spgzip=&dlver=&drm=32&spau=1&spaudio=15&defsrc=1&encryptVer=7.1&cKey=c0149f3c5eba754514cf853615ac36eb&fp2p=1
         */
        String url = "https://vv.video.qq.com/getinfo?";
        url += "vid=" + QQUtils.getPlayVid(playUrl);
        url += "&defn=" + defn;

        // https://vd.l.qq.com/proxyhttp
//        url += "&appVer=3.5.57";
//        url += "&platform=10201";

        // 10901/11 视频很卡，4100201/10201 视频都只有3分钟,参数暂未明白
        // 两个合在一起完美
        // 20191225 失效
        url += "&platform=4100201";
        url += "&platform=11";
        url += "&appVer=3.6.1";

        url += "&dtype=3&spwm=4";
        url += "&otype=ojson";
        url += "&spgzip=&dlver=";
        url += "&ehost=" + URLEncoder.encode(playUrl, "utf-8");
        url += "&host=v.qq.com&refer=v.qq.com";
        url += "&timestamp=" + Utils.timestamp();
        url += "&charge=0&defaultfmt=auto&sdtfrom=v1010&defnpayver=1&sphttps=1&fhdswitch=0&show1080p=1&isHLS=1&sphls=1&drm=32&spau=1&spaudio=15&defsrc=1&encryptVer=7.1&fp2p=1";

        return HttpUtils.get(url);
    }

    /**
     * 只能获取 MP4 格式视频
     *
     * @param platform
     * @param defn
     * @param vid
     * @return
     */
    public static String fetchMp4Video(String platform, String defn, String vid) {
        String url = String.format("https://vv.video.qq.com/getinfo?otype=ojson&appver=3.2.19.333&platform=%s&defnpayver=1&defn=%s&vid=%s", platform, defn, vid);
        return HttpUtils.get(url);
    }

    /**
     * 只能获取 MP4 格式视频
     *
     * @param formatId
     * @param vid
     * @param filename
     * @return
     */
    public static String fetchMp4Token(String formatId, String vid, String filename) {
        String url = String.format("https://vv.video.qq.com/getkey?otype=ojson&appver=3.2.19.333&platform=11&format=%s&vid=%s&filename=%s", formatId, vid, filename);
        return HttpUtils.get(url);
    }

    public static boolean hasPlayVid(String url) {
        String key = "/x/cover/";
        String tmp = url.substring(url.indexOf(key) + key.length());
        boolean vid = tmp.contains("/");
        Log.e(TAG, "hasPlayVid(" + url + ")=" + vid);
        return vid;
    }

    public static String getPlayVid(String url) {
        String vid = "unkown";
        String key = "/";
        int index = url.lastIndexOf(key);
        if (url.indexOf(".html") != -1) {
            vid = url.substring(index + key.length(), url.indexOf(".html"));
        } else {
            vid = url.substring(index + key.length());
        }
        Log.e(TAG, "getPlayVid(" + url + ")=" + vid);
        return vid;
    }

    public static String getVideoPlayUrlFromVid(String url, String vid) {
        return url.substring(0, url.lastIndexOf("/") + 1) + vid + ".html";
    }

    public static String formatJson(String html) {
        String key = "QZOutputJson=";
        return html.substring(html.indexOf(key) + key.length());
    }

    public static String formatSource(String url) {
        String source = "未知源";
        try {
            String key = "://";
            url = url.substring(url.indexOf(key) + key.length());
            source = url.substring(0, url.indexOf("/"));
        } catch (Exception e) {
            Log.e(TAG, "formatSource(" + url + ") " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return source;
    }

    public static String searchVideos(String keyword) throws Exception {
        return HttpUtils.get("https://v.qq.com/x/search/?q=" + URLEncoder.encode(keyword, "utf-8"));
    }

}

/**
 * https://vv.video.qq.com/getinfo?callback=jQuery1579077075036&defn=shd&vid=e0017ah5b20&charge=0&defaultfmt=auto&otype=json&guid=972ab6a0a7fe41f0bb3ce56cf3da479f&flowid=2f71d5cfde5f6a70e3f6d03c2fe8df52_10901&platform=10901&sdtfrom=v1010&defnpayver=1&appVer=3.4.31&host=v.qq.com&ehost=https%3A%2F%2Fv.qq.com%2Fx%2Fpage%2Fe0017ah5b20.html&sphttps=1&tm=1579077074&spwm=4
 * <p>
 * 电视剧列表
 * https://s.video.qq.com/get_playsource?id=lcpwn26degwm7t3&plat=2&type=4&range=1-100&data_type=2&video_type=2&plname=qq&otype=json&_t=1542551072453
 */