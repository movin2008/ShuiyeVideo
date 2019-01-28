package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlin_n.floatWindowPermission.FloatWindowManager;
import com.devlin_n.yinyangplayer.controller.StandardVideoController;
import com.devlin_n.yinyangplayer.player.YinYangPlayer;
import com.shuiyes.video.R;

/**
 * Created by wang on 2017/6/22.
 */
public class TVPlayActivity extends AppCompatActivity {

    private YinYangPlayer mYinYangPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tvplay);
        mYinYangPlayer = (YinYangPlayer) findViewById(R.id.player);

        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        StandardVideoController controller = new StandardVideoController(this);
        controller.setLive(true);

        Glide.with(this).load("http://7xqblc.com1.z0.glb.clouddn.com/tvlive.jpg")
                .asBitmap()
                .animate(R.anim.anim_alpha_in)
                .placeholder(android.R.color.black)
                .into(controller.getThumb());

        mYinYangPlayer.alwaysFullScreen()
//                .useAndroidMediaPlayer()
                .setUrl(url)
                .setTitle(title)
                .setVideoController(controller)
                .start();
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
                Toast.makeText(TVPlayActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
