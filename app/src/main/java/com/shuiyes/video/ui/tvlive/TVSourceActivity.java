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

    public void huya(View view) {
        startActivity(new Intent(this, HuyaListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/轮播.虎牙.tv"));
    }

    public void iqiyi(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/轮播.爱奇艺.tv"));
    }

    public void douyu(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/轮播.斗鱼.tv"));
    }

    public void wasu(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/轮播.华数.tv"));
    }

    public void shanxGD(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/广电.陕西.tv"));
    }

    public void cdnin(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/广电.四川蜀小果1.tv"));
    }

    public void cdnout(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/广电.四川蜀小果2.tv"));
    }

    public void bjCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.北京.tv"));
    }

    public void njCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.南京.tv"));
    }

    public void nnCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.南宁.tv"));
    }

    public void ncCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.南昌.tv"));
    }

    public void jygCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.嘉峪关.tv"));
    }

    public void herb1Cmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.哈尔滨1.tv"));
    }

    public void syCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.沈阳.tv"));
    }

    public void hz1Cmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.杭州1.tv"));
    }

    public void whCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.武汉.tv"));
    }

    public void zzCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.郑州.tv"));
    }

    public void fzCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.福州.tv"));
    }

    public void dlCmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "tvlive/移动.魔百盒.代理.tv"));
    }

    public void szCmcc(View view) {
        // 苏州移动源
        startActivity(new Intent(this, SuzhouCMCCActivity.class));
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