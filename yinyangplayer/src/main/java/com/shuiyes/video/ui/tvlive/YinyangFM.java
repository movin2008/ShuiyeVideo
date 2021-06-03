package com.shuiyes.video.ui.tvlive;

import android.content.Context;
import android.content.Intent;

import com.android.permission.FloatWindowManager;
import com.devlin_n.yinyangplayer.player.BackgroundPlayService;
import com.devlin_n.yinyangplayer.util.KeyUtil;

public class YinyangFM {

    public static void start(Context context, String url, String title) {

        if (FloatWindowManager.getInstance().checkPermission(context)) {
            /**
             * 启动画中画播放的后台服务
             */
            Intent intent = new Intent(context, BackgroundPlayService.class);
            intent.putExtra(KeyUtil.URL, url);
            intent.putExtra(KeyUtil.TITLE, title);
            intent.putExtra(KeyUtil.IS_FM, true);
            context.startService(intent);
        } else {
            FloatWindowManager.getInstance().applyPermission(context);
        }
    }

}
