package com.shuiyes.video.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.shuiyes.video.R;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.util.PreferenceUtil;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final Context context = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String urlHistory = PreferenceUtil.getPlayUrl(context);
                if(TextUtils.isEmpty(urlHistory)){
                    startActivity(new Intent(context, MainActivity.class));
                }else{
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