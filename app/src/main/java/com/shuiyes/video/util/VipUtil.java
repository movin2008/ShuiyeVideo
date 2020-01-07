package com.shuiyes.video.util;

import java.net.URLEncoder;

public class VipUtil {

    public static boolean isVipPojie(String url){
        return url.contains("vipvideo.github.io") || url.contains("administratorw.com/video.php");
    }

    public static String get(String url){
//        return "https://www.administratorw.com/video.php?url=" + URLEncoder.encode(url);
//        return "http://mt2t.com/lines?url=" + URLEncoder.encode(url);
//        return "http://vipjiexi.com/yun.php?url=" + URLEncoder.encode(url);
        return "http://vipvideo.github.io/lines?url=" + URLEncoder.encode(url);
    }

}