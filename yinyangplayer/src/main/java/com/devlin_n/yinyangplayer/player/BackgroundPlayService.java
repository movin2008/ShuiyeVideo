package com.devlin_n.yinyangplayer.player;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

import com.devlin_n.yinyangplayer.R;
import com.devlin_n.yinyangplayer.controller.FloatController;
import com.devlin_n.yinyangplayer.util.Constants;
import com.devlin_n.yinyangplayer.util.KeyUtil;
import com.shuiyes.video.util.WindowUtil;
import com.devlin_n.yinyangplayer.widget.FloatView;

/**
 * 悬浮播放
 * Created by Devlin_n on 2017/4/14.
 */
public class BackgroundPlayService extends Service {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private YinYangPlayer videoView;
    private String url, title;
    private FloatView floatView;
    private int position;
    private boolean isCache, isFM;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra(KeyUtil.URL);
        title = intent.getStringExtra(KeyUtil.TITLE);
        position = intent.getIntExtra(KeyUtil.POSITION, 0);
        isCache = intent.getBooleanExtra(KeyUtil.ENABLE_CACHE, false);
        isFM = intent.getBooleanExtra(KeyUtil.IS_FM, false);

        if (Constants.IS_START_FLOAT_WINDOW) {
            ((FloatController) videoView.getVideoController()).setTitle(title);
            videoView.start(url);
        } else {
            initWindow();
            startPlay();
        }

        Constants.IS_START_FLOAT_WINDOW = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void startPlay() {
        if (isCache) videoView.enableCache();
        videoView.skipPositionWhenPlay(url, position).setVideoController(new FloatController(getApplicationContext()).setTitle(title).setCover(isFM ? R.drawable.fm : R.drawable.translucent)).start();
        wm.addView(floatView, wmParams);
    }

    private void initWindow() {
        wm = WindowUtil.getWindowManager(getApplicationContext());
        wmParams = new WindowManager.LayoutParams();
        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗口至右下角
        wmParams.gravity = Gravity.START | Gravity.TOP;
        // 设置悬浮窗口长宽数据
        if (isFM) {
            int width = WindowUtil.dp2px(getApplicationContext(), 128);
            wmParams.width = width;
            wmParams.height = width;
        } else {
            int width = WindowUtil.dp2px(getApplicationContext(), 250);
            wmParams.width = width;
            wmParams.height = width * 9 / 16;
        }
        wmParams.x = WindowUtil.getScreenWidth(getApplicationContext()) - wmParams.width;
        wmParams.y = WindowUtil.getScreenHeight(getApplicationContext(), false) / 2;
        floatView = new FloatView(getApplicationContext(), wm, wmParams);
        videoView = floatView.magicVideoView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.IS_START_FLOAT_WINDOW = false;
        if (floatView != null) wm.removeView(floatView);
        videoView.release();
    }
}
