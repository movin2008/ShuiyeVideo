package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.ui.base.BaseActivity;


public class FilmSourceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filmsource);
    }

    public void yinyuetai(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/音悦台.fm"));
    }

    public void film1(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/电影整理.fm"));
    }

    public void film2(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/经典电影.fm"));
    }

    public void laosj(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/开车老司机.fm"));
    }

    public void tokyo1(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/日本AV女优1.fm"));
    }

    public void tokyo2(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/日本AV女优2.fm"));
    }

    public void SKrBlue(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国三级.fm"));
    }

    public void SKrDance(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国热舞.fm"));
    }

    public void SKrDance2(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国骚舞.fm"));
    }

    public void SKrSilk(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国黑丝.fm"));
    }

    public void SKrSalonGirls(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国车模.fm"));
    }
}