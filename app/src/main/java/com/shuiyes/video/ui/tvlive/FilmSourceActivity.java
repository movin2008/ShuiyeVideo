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
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/音悦台.list"));
    }

    public void film1(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/电影整理.list"));
    }

    public void film2(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/经典电影.list"));
    }

    public void laosj(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/开车老司机.list"));
    }

    public void tokyo1(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/日本AV女优1.list"));
    }

    public void tokyo2(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/日本AV女优2.list"));
    }

    public void SKrBlue(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国三级.list"));
    }

    public void SKrDance(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国热舞.list"));
    }

    public void SKrDance2(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国骚舞.list"));
    }

    public void SKrSilk(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国黑丝.list"));
    }

    public void SKrSalonGirls(View view) {
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, "film/韩国车模.list"));
    }
}