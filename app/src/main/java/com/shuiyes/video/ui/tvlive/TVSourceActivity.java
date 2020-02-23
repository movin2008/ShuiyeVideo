package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseActivity;

public class TVSourceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tvsource);
    }

    // 虎牙轮播源
    public void huya(View view) {
        startActivity(new Intent(this, HuyaListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/虎牙轮播源.tv"));
    }

    // 南京移动源
    public void njCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.南京.tv"));
    }

    // 南宁移动源
    public void nnCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.南宁.tv"));
    }

    // 嘉峪关移动源
    public void jygCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.嘉峪关.tv"));
    }

    // 哈尔滨移动源
    public void herbCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.哈尔滨.tv"));
    }

    // 沈阳移动源
    public void syCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.沈阳.tv"));
    }

    // 杭州移动源
    public void hzCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.杭州.tv"));
    }

    // 苏州移动源
    public void szCmcc(View view) {
        startActivity(new Intent(this, SuzhouCMCCActivity.class));
    }

    // 武汉移动源
    public void whCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.武汉.tv"));
    }

    // 武汉联通源
    public void whCucc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/联通.武汉.tv"));
    }

    // 南宁联通源
    public void nnCucc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/联通.南宁.tv"));
    }

    // 苏州联通源
    public void szCucc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/联通.苏州.tv"));
    }

    // 大连电信源
    public void dlCtc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/电信.大连.tv"));
    }

    // 成都电信源
    public void cdCtc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/电信.成都.tv"));
    }

    // 上海电信源
    public void shhCtc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/电信.上海.tv"));
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
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/国际台.tv"));
    }

    // 地方台整理
    public void local(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/地方台.tv"));
    }

    // 其他
    public void other(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/其他.tv"));
    }
}