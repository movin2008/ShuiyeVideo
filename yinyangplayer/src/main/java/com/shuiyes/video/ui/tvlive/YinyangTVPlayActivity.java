package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.devlin_n.yinyangplayer.R;
import com.devlin_n.yinyangplayer.controller.StandardVideoController;
import com.devlin_n.yinyangplayer.player.YinYangPlayer;

/**
 * Created by wang on 2017/6/22.
 */
public class YinyangTVPlayActivity extends AppCompatActivity {

    protected boolean isHLS;
    protected YinYangPlayer mYinYangPlayer;

    protected String[] getIntentStringExtras() {
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        return new String[]{url, title};
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tvplay_yinyang);
        mYinYangPlayer = (YinYangPlayer) findViewById(R.id.player);

        String[] values = getIntentStringExtras();
        String url = values[0];
        String title = values[1];
        isHLS = getIntent().getBooleanExtra("hls", false);

//        com.bumptech.glide.Glide.with(this).load("https://i.loli.net/2020/02/02/ChFgVjiAbeD25cQ.jpg").asBitmap().animate(R.anim.anim_alpha_in).placeholder(android.R.color.black).into(((StandardVideoController)mYinYangPlayer.getVideoController()).getThumb());
        mYinYangPlayer.alwaysFullScreen().setUrl(url).setTitle(title).setVideoController(initVideoController()).start();
    }

    protected StandardVideoController initVideoController() {
        StandardVideoController controller = new StandardVideoController(this);
        controller.setLive(isHLS);
        return controller;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mYinYangPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mYinYangPlayer.resume();
        mYinYangPlayer.stopFloatWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mYinYangPlayer.release();
    }

    @Override
    public void onBackPressed() {
        if (!mYinYangPlayer.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FloatWindowManager.PERMISSION_REQUEST_CODE) {
            if (FloatWindowManager.getInstance().checkPermission(this)) {
                mYinYangPlayer.startFloatWindow();
            } else {
                Toast.makeText(YinyangTVPlayActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}