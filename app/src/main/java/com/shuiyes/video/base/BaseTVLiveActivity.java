package com.shuiyes.video.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.tvlive.TVPlayActivity;
import com.shuiyes.video.util.OkHttpManager;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class BaseTVLiveActivity extends BaseActivity implements Callback, View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private TextView mTextView;
    private TagFlowLayout mResultView;
    private Handler mHandler = new Handler();
    protected List<ListVideo> mVideos = new ArrayList<ListVideo>();
    private OkHttpClient Client = OkHttpManager.getNormalClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tvlive);
        mTextView = (TextView) this.findViewById(R.id.tv_result);
        mResultView = (TagFlowLayout) this.findViewById(R.id.lv_result);

        Request request = new Request.Builder().url(getApi()).build();
        Client.newCall(request).enqueue(this);
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
        fail("更多源加载失败: " + e.getLocalizedMessage());
    }

    private void fail(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(error);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String action = call.request().url().url().getPath();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                Log.e(TAG, "onResponse(null): " + action);
                fail("更多源请求失败.");
            }

            String result = responseBody.string();
            if (TextUtils.isEmpty(result)) {
                fail("更多源加载失败.");
                return;
            } else {
                refreshVideos(result);
            }

            if (mVideos.isEmpty()) {
                fail("更多源加载为空.");
                return;
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setVisibility(View.GONE);
                    mResultView.setVisibility(View.VISIBLE);
                    mResultView.setAdapter(new TagAdapter<ListVideo>(mVideos) {
                        @Override
                        public TagView getView(FlowLayout parent, int position, ListVideo o) {
                            NumberView view = new NumberView(getApplicationContext(), o);
                            view.setTextColor(Color.BLACK);
                            view.setOnClickListener(BaseTVLiveActivity.this);
                            view.setSize(view.measureWidth(), NumberView.WH);
                            return view;
                        }
                    });
                }
            });
        } catch (Exception e) {
            fail("更多源请求失败：" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        NumberView view = (NumberView) v;
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", view.getTitle())
                .putExtra("url", view.getUrl()));
    }
}