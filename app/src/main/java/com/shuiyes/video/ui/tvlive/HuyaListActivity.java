package com.shuiyes.video.ui.tvlive;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.shuiye.video.util.ResourceUtils;
import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.PreferenceUtil;
import com.shuiyes.video.widget.NumberView;
import com.shuiyes.video.widget.Tips;
import com.zhy.view.flowlayout.TagView;

public class HuyaListActivity extends TVListActivity implements View.OnClickListener {

    private int mTitleWidth, mRedColor;
    private String mInvaildUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Rect rect = new Rect();
        getWindowManager().getDefaultDisplay().getRectSize(rect);
        mTitleWidth = rect.right - NumberView.WH - ResourceUtils.flowBtnPadding(this) - 2 * ResourceUtils.flowBtnMargin(this) - 2 * mResultView.getPaddingLeft();

        mRedColor = getResources().getColor(android.R.color.holo_red_dark);
        mInvaildUrl = PreferenceUtil.getHuyaInvaildUrl(getApplicationContext());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_huyalist;
    }

    @Override
    protected void addListVideo(String text) {
        if (!text.contains(",")) return;
        String[] tmp = text.split(",");
        String title = tmp[0];
        String url = tmp[1];

        if (!mVideos.isEmpty()) {
            mVideos.add(new ListVideo("", null, null));
        }
        // 虎牙电影
        mVideos.add(new ListVideo(title, null, null));
        mVideos.add(new ListVideo("测试", title, url.replace("https", "test").replace("http", "test")));

        String[] sources = {"aldirect",/* "jsdirect", "txdirect", "al", "js", "tx"*/};
        for (String source : sources) {
            String nurl = url.replace("aldirect", source);
            mVideos.add(new ListVideo("源." + source, title, nurl));
            mVideos.add(new ListVideo("源.1200", title, nurl.replace(".m3u8", "_1200.m3u8")));
            mVideos.add(new ListVideo("源.2000", title, nurl.replace(".m3u8", "_2000.m3u8")));
            mVideos.add(new ListVideo("源.2500", title, nurl.replace(".m3u8", "_2500.m3u8")));

            nurl = nurl.replace("huyalive", "backsrc");
            mVideos.add(new ListVideo("备源." + source, title, nurl));
            mVideos.add(new ListVideo("备源.1200", title, nurl.replace(".m3u8", "_1200.m3u8")));
            mVideos.add(new ListVideo("备源.2000", title, nurl.replace(".m3u8", "_2000.m3u8")));
            mVideos.add(new ListVideo("备源.2500", title, nurl.replace(".m3u8", "_2500.m3u8")));

//            nurl = nurl.replace("rtmp", "hls");
//            mVideos.add(new ListVideo("源." + source, title, nurl));
//            mVideos.add(new ListVideo("源." + source + "_1200", title, nurl.replace(".m3u8", "_1200.m3u8")));
//            mVideos.add(new ListVideo("源." + source + "_2000", title, nurl.replace(".m3u8", "_2000.m3u8")));
//            mVideos.add(new ListVideo("源." + source + "_2500", title, nurl.replace(".m3u8", "_2500.m3u8")));
        }
    }

    @Override
    protected TagView getTagView(int position, ListVideo o) {
        if (o.getUrl() == null) {
            // 标题
            TagView view = new TagView(mContext);
            int width = TextUtils.isEmpty(o.getText()) ? WindowManager.LayoutParams.MATCH_PARENT : mTitleWidth;
            view.setSize(width, WindowManager.LayoutParams.WRAP_CONTENT);
            view.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            view.setText(o.getText());
            view.setTextSize(30);
            return view;
        } else if (o.getUrl().startsWith("test://")) {
            NumberView view = new NumberView(getApplicationContext(), o);
            view.setTextColor(mRedColor);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    testHuyaUrl((NumberView) v);
                }
            });
            view.setSize(view.measureWidth(), 0);
            return view;
        } else {
            TagView view = super.getTagView(position, o);
            if (mInvaildUrl.contains(o.getUrl())) {
                view.setEnabled(false);
            }
            return view;
        }

    }

    private Thread mThread;

    static final int COUNT = 8;

    public void testHuyaUrl(NumberView v) {
        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
            mThread = null;
        }
        mThread = new Thread() {
            @Override
            public void run() {

                int id = v.getId();
                int flag = 0;
                for (int i = 1; i <= COUNT; i++) {
                    final NumberView view = mResultView.findViewById(id + i);
                    String html = HttpUtils.get(view.getUrl());

                    if (html.startsWith("Exception: thread interrupted")) {
                        return;
                    }

                    final boolean enable = html.startsWith("#EXTM3U");
                    Log.e(TAG, view.getTitle() + " " + view.getText() + ", " + (enable ? "有效" : "无效"));
                    if (enable) {
                        flag++;
                    } else {
                        String invaildUrl = PreferenceUtil.getHuyaInvaildUrl(getApplicationContext());
                        if (!invaildUrl.contains(view.getUrl())) {
                            PreferenceUtil.setHuyaInvaildUrl(getApplicationContext(), invaildUrl + view.getUrl() + ",");
                        }
                    }
                    if(enable != view.isEnabled()){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.setEnabled(enable);
                            }
                        });
                    }
                }

                if (flag == 0) {
                    tips(v.getTitle() + " 测试结束, 全部无效");
                } else if (flag == COUNT) {
                    tips(v.getTitle() + " 测试结束, 全部有效");
                } else {
                    tips(v.getTitle() + " 测试结束, 有效: " + flag + ", 无效: " + (COUNT - flag));
                }
            }
        };
        mThread.start();
    }

    private void tips(String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Tips.show(getApplicationContext(), text);
            }
        });
    }

}