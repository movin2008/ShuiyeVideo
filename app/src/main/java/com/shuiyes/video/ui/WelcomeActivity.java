package com.shuiyes.video.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.PreferenceUtil;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        final Context context = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String urlHistory = PreferenceUtil.getPlayUrl(context);
                if (TextUtils.isEmpty(urlHistory)) {
                    startActivity(new Intent(context, MainActivity.class));
                } else {
                    PlayUtils.play(context, urlHistory, "播放记录恢复中...");
                }
            }
        }, 2345);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}