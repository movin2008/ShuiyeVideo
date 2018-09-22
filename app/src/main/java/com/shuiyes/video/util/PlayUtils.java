package com.shuiyes.video.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shuiyes.video.bean.Album;
import com.shuiyes.video.letv.LetvVActivity;
import com.shuiyes.video.widget.Tips;
import com.shuiyes.video.youku.YoukuVActivity;

public class PlayUtils {

    private static final String TAG = "PlayUtils";

    public static boolean isYoukuListAlbum(String url){
        return url.contains("list.youku.com");
    }

    public static boolean isSurpportUrl(String url){
        return url.contains("youku.com") || url.contains("le.com");
    }

    public static String formateUrlSource(String url){
        String text = url;
        if(url.contains("mgtv.com")){
            text = "芒果视频";
        }
        if(url.contains("iqiyi.com")){
            text = "爱奇艺视频";
        }
        return text;
    }

    public static void play(Context context, Album album) {
        String url = album.getPlayurl();
        if(PlayUtils.isYoukuListAlbum(url)){
            Tips.show(context, "暂不支持" + PlayUtils.formateUrlSource(url)+"的优酷专辑", 0);
        }else{
            PlayUtils.play(context, url, album.getTitle());
        }
    }

    public static void play(Context context, String url, String title) {
        if (url.contains("youku.com")) {
            if(url.contains("youku.com/v_show/")){
                context.startActivity(new Intent(context, YoukuVActivity.class).putExtra("url", url).putExtra("title", title));
            }else{
                Tips.show(context, "暂不支持播放" + url, 0);
            }
        }if (url.contains("le.com")) {
            if(url.contains("le.com/ptv/vplay/")){
                context.startActivity(new Intent(context, LetvVActivity.class).putExtra("url", url).putExtra("title", title));
            }else{
                Tips.show(context, "暂不支持播放" + url, 0);
            }
        } else {
            Tips.show(context, "暂不支持播放 " + PlayUtils.formateUrlSource(url), 0);
        }
    }

}
