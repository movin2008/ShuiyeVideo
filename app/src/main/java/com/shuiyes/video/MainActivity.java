package com.shuiyes.video;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shuiyes.video.base.BaseActivity;
import com.shuiyes.video.letv.LetvVActivity;
import com.shuiyes.video.youku.YoukuSoActivity;

public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate ========================= ");

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

    public void test(View view){
        this.startActivity(new Intent(this, LetvVActivity.class).putExtra("url", "http://www.le.com/ptv/vplay/26101788.html"));
    }

    public void soyouku(View view){
        this.startActivity(new Intent(this, YoukuSoActivity.class));
    }

    public void soletv(View view){
        this.startActivity(new Intent(this, LetvVActivity.class).putExtra("url", "http://www.le.com/ptv/vplay/26101788.html"));
    }

}
