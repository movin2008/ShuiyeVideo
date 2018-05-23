package com.shuiyes.video.util;

public class HttpUtils {

    public static String FormateUrl(String url){
        if(url.startsWith("//")){
            url = "http:"+url;
        }
        return url;
    }

}
