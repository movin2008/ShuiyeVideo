package com.shuiyes.video.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.shuiyes.video.ui.cbchot.CBChotVActivity;
import com.shuiyes.video.ui.mdd.MDDVActivity;
import com.shuiyes.video.ui.WebActivity;
import com.shuiyes.video.bean.Album;
import com.shuiyes.video.ui.iqiyi.IQiyiVActivity;
import com.shuiyes.video.ui.letv.LetvVActivity;
import com.shuiyes.video.ui.qq.QQVActivity;
import com.shuiyes.video.ui.tvlive.TVBusActivity;
import com.shuiyes.video.ui.tvlive.TVPlayActivity;
import com.shuiyes.video.widget.Tips;
import com.shuiyes.video.ui.youku.YoukuVActivity;

public class PlayUtils {

    private static final String TAG = "PlayUtils";

    public static boolean isSurpportUrl(String url) {
        return url.contains("youku.com")
                || url.contains("qq.com")
                || url.contains("le.com")
                || url.contains("letv.com")
                || url.contains("cbchot.com")
                || url.contains("iqiyi.com");
    }

    public static void play(Context context, Album album) {
        PlayUtils.play(context, album.getPlayurl(), album.getTitle());
    }

    public static void play(Context context, String url, String title) {
        PlayUtils.play(context, url, title, false);
    }

    public static void play(final Context context, String url, String title, boolean isHLS) {
        if (isHLS) {
            if (url.startsWith("tvbus://")) {
                try {
                    context.createPackageContext("io.binstream.github.demo", 0);
                    context.startActivity(new Intent(context, TVBusActivity.class)
                            .putExtra("title", title)
                            .putExtra("url", url));
                } catch (PackageManager.NameNotFoundException e) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("TVBus 播放插件尚未安装");
                    dialog.setPositiveButton("安装", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utils.installTVBus(context);
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            } else {
                context.startActivity(new Intent(context, TVPlayActivity.class)
                        .putExtra("title", title)
                        .putExtra("url", url));

//                context.startActivity(new Intent(context, PlayerActivity.class)
//                        .setAction(PlayerActivity.ACTION_VIEW)
//                        .setData(Uri.parse(url)));
            }

            return;
        }

        if (url.contains("youku.com")) {
            if (url.contains("youku.com/v_show/")) {
                context.startActivity(new Intent(context, YoukuVActivity.class).putExtra("url", url).putExtra("title", title));
            } else {
                playFail(context, url);
            }
        } else if (url.contains("le.com") || url.contains("letv.com")) {
            if (url.contains("/vplay/")) {
                context.startActivity(new Intent(context, LetvVActivity.class).putExtra("url", url).putExtra("title", title));
            } else {
                playFail(context, url);
            }
        } else if (url.contains("iqiyi.com")) {
            if (url.contains("iqiyi.com/v_") || url.contains("iqiyi.com/w_")) {
                context.startActivity(new Intent(context, IQiyiVActivity.class).putExtra("url", url).putExtra("title", title));
            } else {
                playFail(context, url);
            }
        } else if (url.contains("qq.com")) {
            if (url.contains("qq.com/x/")) {
                context.startActivity(new Intent(context, QQVActivity.class).putExtra("url", url).putExtra("title", title));
            } else {
                playFail(context, url);
            }
        } else if (url.contains("mdd.com")) {
            context.startActivity(new Intent(context, MDDVActivity.class).putExtra("url", url).putExtra("title", title));
        } else if (url.contains("and.cbchot.com")) {
            context.startActivity(new Intent(context, CBChotVActivity.class).putExtra("url", url).putExtra("title", title));
        } else {
            playFail(context, url);
        }
    }

    private static void playFail(Context context, String url) {
//        Tips.show(context, "暂不支持播放 " + PlayUtils.formateUrlSource(url), 0);

        String text = "暂不支持 " + url + "\n 请浏览至播放网页重试";
        Tips.show(context, text, 1);
        Log.e(TAG, text);

        WebActivity.launch(context, url);
    }

}
