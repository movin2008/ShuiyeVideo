package com.shuiyes.video.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shuiyes.video.ui.mdd.MDDVActivity;
import com.shuiyes.video.ui.WebActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.ui.iqiyi.IQiyiVActivity;
import com.shuiyes.video.ui.letv.LetvVActivity;
import com.shuiyes.video.ui.qq.QQVActivity;
import com.shuiyes.video.widget.Tips;
import com.shuiyes.video.ui.youku.YoukuVActivity;

public class PlayUtils {

    private static final String TAG = "PlayUtils";

    public static boolean isSurpportUrl(String url){
        return url.contains("youku.com")
                || url.contains("qq.com")
                || url.contains("le.com")
                || url.contains("letv.com")
                || url.contains("iqiyi.com");
    }

    public static String formateUrlSource(String url){
        String text = url;
        if(url.contains("mgtv.com")){
            text = "芒果视频";
        }
        else if(url.contains("qq.com")){
            text = "腾讯视频";
        }
        else if(url.contains("sohu.com")){
            text = "搜狐视频";
        }
        else if(url.contains("youku.com")){
            text = "优酷视频";
        }
        else if(url.contains("le.com") || url.contains("letv.com")){
            text = "乐视视频";
        }
        else if(url.contains("iqiyi.com")){
            text = "爱奇艺视频";
        }
        return text;
    }

    public static void play(Context context, Album album) {
        PlayUtils.play(context, album.getPlayurl(), album.getTitle());
    }

    public static void play(Context context, String url, String title) {
        if (url.contains("youku.com")) {
            if(url.contains("youku.com/v_show/")){
                context.startActivity(new Intent(context, YoukuVActivity.class).putExtra("url", url).putExtra("title", title));
            }else{
                playFail(context, url);
            }
        }else if (url.contains("le.com") || url.contains("letv.com")) {
            if(url.contains("/vplay/")){
                context.startActivity(new Intent(context, LetvVActivity.class).putExtra("url", url).putExtra("title", title));
            }else{
                playFail(context, url);
            }
        }else if (url.contains("iqiyi.com")) {
            if(url.contains("iqiyi.com/v_") || url.contains("iqiyi.com/w_")){
                context.startActivity(new Intent(context, IQiyiVActivity.class).putExtra("url", url).putExtra("title", title));
            }else{
                playFail(context, url);
            }
        }else if (url.contains("qq.com")) {
            if(url.contains("qq.com/x/")){
                context.startActivity(new Intent(context, QQVActivity.class).putExtra("url", url).putExtra("title", title));
            }else{
                playFail(context, url);
            }
        } else if(url.contains("mdd.com")){
            context.startActivity(new Intent(context, MDDVActivity.class).putExtra("url", url).putExtra("title", title));
        } else {
            playFail(context, url);
        }
    }

    private static void playFail(Context context, String url){
//        Tips.show(context, "暂不支持播放 " + PlayUtils.formateUrlSource(url), 0);

        String text = "暂不支持 " + url+"\n 请浏览至播放网页重试";
        Tips.show(context, text,1);
        Log.e(TAG, text);
        if(!Constants.WEB_FOEGROUND){
            context.startActivity(new Intent(context, WebActivity.class).putExtra("url", url));
        }
    }

}
