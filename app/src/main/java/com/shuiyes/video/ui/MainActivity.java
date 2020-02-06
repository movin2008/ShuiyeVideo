package com.shuiyes.video.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseActivity;
import com.shuiyes.video.ui.cbchot.CBChotSoActivity;
import com.shuiyes.video.ui.iqiyi.IQIyiSoActivity;
import com.shuiyes.video.ui.letv.LetvSoActivity;
import com.shuiyes.video.ui.mdd.MDDSoActivity;
import com.shuiyes.video.ui.qq.QQSoActivity;
import com.shuiyes.video.ui.tvlive.BuptIVIActivity;
import com.shuiyes.video.ui.tvlive.SopPlusActivity;
import com.shuiyes.video.ui.tvlive.SuzhouCMCCActivity;
import com.shuiyes.video.ui.tvlive.TVListActivity;
import com.shuiyes.video.ui.vip.VipActivity;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.widget.Tips;
import com.shuiyes.video.ui.youku.YoukuSoActivity;


public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate ========================= ");

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

        this.findViewById(R.id.text).requestFocus();
        mRadioGroup = (RadioGroup) this.findViewById(R.id.rg_video);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_iqiyi:
                        mVideoUrl = "https://www.iqiyi.com/";
                        break;
                    case R.id.rb_letv:
                        mVideoUrl = "http://www.le.com/";
                        break;
                    case R.id.rb_youku:
                        mVideoUrl = "http://www.youku.com/";
                        break;
                    case R.id.rb_qq:
                        mVideoUrl = "https://v.qq.com/";
                        break;
                    case R.id.rb_mgtv:
                        mVideoUrl = "https://www.mgtv.com/";
                        break;
                    case R.id.rb_sohu:
                        mVideoUrl = "https://tv.sohu.com/";
                        break;
                    case R.id.rb_test:
                        mVideoUrl = "http://www.shuiyes.com/misc/UA/";
                        break;
                }
                Log.e(TAG, "mVideoUrl = " + mVideoUrl);
            }
        });
        mRadioGroup.check(R.id.rb_iqiyi);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                HttpUtils.testPost("https://vi.l.qq.com/proxyhttp");
//            }
//        }).start();

        //		Tips.show(this, Build.VERSION.SDK_INT+"/"+Build.MANUFACTURER, 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    String mVideoUrl = null;

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

    public void soyouku(View view) {
        this.startActivity(new Intent(this, YoukuSoActivity.class));
    }

    public void soletv(View view) {
        this.startActivity(new Intent(this, LetvSoActivity.class));
    }

    public void soiqiyi(View view) {
        this.startActivity(new Intent(this, IQIyiSoActivity.class));
    }

    public void soqq(View view) {
        this.startActivity(new Intent(this, QQSoActivity.class));
    }

    public void socbchot(View view) {
        this.startActivity(new Intent(this, CBChotSoActivity.class));
    }

    public void mdd(View view) {
        this.startActivity(new Intent(this, MDDSoActivity.class));
    }

    public void huya(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "huya.tv"));
    }

    public void cuiuc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "CUIUC.tv"));
    }

    public void jscmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "cmcc.nanjing.tv"));
    }
    public void othcmcc(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "cmcc.other.tv"));
    }

    public void szcmcc(View view) {
        startActivity(new Intent(this, SuzhouCMCCActivity.class));
    }

    public void fulibus(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "fulibus.tv"));
    }

    public void fmlist(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "fm.tv"));
    }

    public void other(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "other.tv"));
    }

    public void soplus(View view) {
        startActivity(new Intent(this, SopPlusActivity.class));
    }

    public void iviBupt(View view) {
        startActivity(new Intent(this, BuptIVIActivity.class));
    }

    public void somgtv(View view) {
        Tips.show(this, "芒果视频待完善");
    }

    public void sohu(View view) {
        Tips.show(this, "搜狐视频待完善");
    }

    public void testUrl(View view) {
//        startActivity(new Intent(this, MgtvVActivity.class).putExtra("title","test").putExtra("url", "https://www.mgtv.com/l/100028247/7370524.html?fpa=9349&fpos=3"));
        // https://m.iqiyi.com/v_19ruzj8gv0.html
        // https://v.qq.com/x/cover/4zhgrc6vcikqw0p/e0017ah5b20.html
//        VipActivity.launch(this, "http://www.le.com/ptv/vplay/31625706.html");

        //        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "cctv.misc.dpl"));
//        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "甘肃嘉峪关移动.list"));
//        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "meinvzhubo.m3u"));
//        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "虎牙电影频道.m3u"));
//        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "国外福利台.m3u"));
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "test.tv"));

        String url = "http://live.redtraffic.xyz/bigdick.m3u8";
//        PlayUtils.play(this, url, "", true);
    }

    public void testWeb(View view) {
        this.startActivity(new Intent(this, WebActivity.class).putExtra("url", mVideoUrl));
    }

}
