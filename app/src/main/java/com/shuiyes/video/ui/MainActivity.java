package com.shuiyes.video.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BaseActivity;
import com.shuiyes.video.iqiyi.IQIyiSoActivity;
import com.shuiyes.video.letv.LetvSoActivity;
import com.shuiyes.video.qq.QQVActivity;
import com.shuiyes.video.widget.Tips;
import com.shuiyes.video.youku.YoukuSoActivity;

public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    private EditText mInputUrl;

    private RadioGroup mRadioGroup;
    private RadioButton mIQiyi;
    private RadioButton mLetv;
    private RadioButton mYouku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate ========================= ");

        this.findViewById(R.id.text).requestFocus();
        mInputUrl = (EditText) this.findViewById(R.id.et_input_url);

        mRadioGroup = (RadioGroup) this.findViewById(R.id.rg_video);
        mIQiyi = (RadioButton) this.findViewById(R.id.rb_iqiyi);
        mLetv = (RadioButton) this.findViewById(R.id.rb_letv);
        mYouku = (RadioButton) this.findViewById(R.id.rb_youku);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

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
        mIQiyi.setChecked(true);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpUtils.testPost("https://vi.l.qq.com/proxyhttp");
//            }
//        }).start();

        //		Tips.show(this, Build.VERSION.SDK_INT+"/"+Build.MANUFACTURER, 1);
    }

    String mVideoUrl = null;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void soyouku(View view){
        this.startActivity(new Intent(this, YoukuSoActivity.class));
    }

    public void soletv(View view){
        this.startActivity(new Intent(this, LetvSoActivity.class));
    }

    public void soiqiyi(View view){
        this.startActivity(new Intent(this, IQIyiSoActivity.class));
    }

    public void soqq(View view){
        Tips.show(this, "腾讯视频待完善");
    }

    public void somgtv(View view){
        Tips.show(this, "芒果视频待完善");
    }

    public void sohu(View view){
        Tips.show(this, "搜狐视频待完善");
    }

    public void testUrl(View view){
        this.startActivity(new Intent(this, QQVActivity.class).putExtra("url", "https://v.qq.com/x/cover/6983f15b7g5xch7.html"));
    }

    public void testWeb(View view){
        this.startActivity(new Intent(this, WebActivity.class).putExtra("url", mVideoUrl));
    }

}
