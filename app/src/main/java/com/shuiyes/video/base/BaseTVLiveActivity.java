package com.shuiyes.video.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.tvlive.TVPlayActivity;
import com.shuiyes.video.util.OkHttpManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BaseTVLiveActivity extends BaseTVListActivity implements Callback {

    private OkHttpClient Client = OkHttpManager.getNormalClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Request request = new Request.Builder().url(getApi()).build();
        Client.newCall(request).enqueue(this);
    }

    @Override
    public int getContentViewId(){
        return R.layout.activity_tvlive;
    }

    public abstract String getApi();

    public abstract String getPlayUrl(String tv);

    public abstract void refreshVideos(String result) throws Exception;

    public void cctv1(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV1")
                .putExtra("url", getPlayUrl("1")));
    }

    public void cctv2(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV12")
                .putExtra("url", getPlayUrl("2")));
    }

    public void cctv3(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV3")
                .putExtra("url", getPlayUrl("3")));
    }

    public void cctv4(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV4")
                .putExtra("url", getPlayUrl("4")));
    }

    public void cctv5(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV5")
                .putExtra("url", getPlayUrl("5")));
    }

    public void cctv6(View view) {
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", "CCTV6")
                .putExtra("url", getPlayUrl("6")));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure("更多源请求失败: " + e.getLocalizedMessage());
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String action = call.request().url().url().getPath();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                Log.e(TAG, "onResponse(null): " + action);
                onFailure("更多源请求失败.");
            }

            String result = responseBody.string();
            if (TextUtils.isEmpty(result)) {
                onFailure("更多源加载失败.");
                return;
            } else {
                refreshVideos(result);
            }

            if (mVideos.isEmpty()) {
                onFailure("更多源加载为空.");
                return;
            }

            mVideos.add(0, new ListVideo(getTitle().toString(), null, null));
            onSuccess();
        } catch (Exception e) {
            onFailure("更多源请求失败：" + e.getLocalizedMessage());
        }
    }
}