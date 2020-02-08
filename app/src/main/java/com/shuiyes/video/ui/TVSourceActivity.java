package com.shuiyes.video.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.SettingsActivity;
import com.shuiyes.video.ui.base.BaseActivity;
import com.shuiyes.video.ui.tvlive.BuptIVIActivity;
import com.shuiyes.video.ui.tvlive.HuyaListActivity;
import com.shuiyes.video.ui.tvlive.SopPlusActivity;
import com.shuiyes.video.ui.tvlive.SuzhouCMCCActivity;
import com.shuiyes.video.ui.tvlive.TVListActivity;


public class TVSourceActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tvsource);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 虎牙轮播源
    public void huya(View view) {
        startActivity(new Intent(this, HuyaListActivity.class).putExtra(TVListActivity.EXTRA, "虎牙轮播源.tv"));
    }

    // 南京移动源
    public void njCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.南京.tv"));
    }

    // 南宁移动源
    public void nnCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.南宁.tv"));
    }

    // 嘉峪关移动源
    public void jygCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.嘉峪关.tv"));
    }

    // 哈尔滨移动源
    public void herbCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.哈尔滨.tv"));
    }

    // 沈阳移动源
    public void syCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.沈阳.tv"));
    }

    // 杭州移动源
    public void hzCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.杭州.tv"));
    }

    // 苏州移动源
    public void szCmcc(View view) {
        startActivity(new Intent(this, SuzhouCMCCActivity.class));
    }

    // 其他移动源
    public void othCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "移动.其他.tv"));
    }

    // 南宁联通源
    public void nnCucc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "联通.南宁.tv"));
    }

    // 苏州联通源
    public void szCucc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "联通.苏州.tv"));
    }

    // 电信其他源
    public void othCtc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "电信.其他.tv"));
    }

    // soplus
    @Deprecated
    public void soplus(View view) {
        startActivity(new Intent(this, SopPlusActivity.class));
    }

    // 北邮测试源
    public void iviBupt(View view) {
        startActivity(new Intent(this, BuptIVIActivity.class));
    }

    // 国际台整理
    public void foreign(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "国际台.tv"));
    }

    // 地方台整理
    public void local(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "地方台.tv"));
    }
}