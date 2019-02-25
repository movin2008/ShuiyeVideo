package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BaseTVListActivity;
import com.shuiyes.video.bean.ListVideo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TVListActivity extends BaseTVListActivity implements View.OnClickListener {

    public final static String EXTRA = "filename";
    private String FileName = "default.list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA)) {
            FileName = intent.getStringExtra(EXTRA);
        }
        refreshTvlist();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_tvlist;
    }

    public void refreshTvlist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = mContext.getAssets().open("tvlist/" + FileName);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    if (FileName.endsWith(".list")) {

                        String text = null;
                        while ((text = br.readLine()) != null) {
                            if(text.startsWith("##")){
                                // 注释
                                continue;
                            }
                            if (text.contains(",")) {
                                String[] tmp = text.split(",");
                                mVideos.add(new ListVideo(tmp[0], tmp[0], tmp[1]));
                            } else {
                                mVideos.add(new ListVideo(text, null, null));
                            }
                        }
                    } else if (FileName.endsWith(".dpl")) {
                        String text = null;
                        String url = null;
                        while ((text = br.readLine()) != null) {
                            if(text.startsWith("##")){
                                // 注释
                                continue;
                            }

                            if (text.contains("*")) {
                                String[] tmp = text.split("\\*");
                                if ("file".equals(tmp[1])) {
                                    url = tmp[2];
                                } else if ("title".equals(tmp[1])) {
                                    mVideos.add(new ListVideo(tmp[2], tmp[2], url));
                                }
                            }
                        }
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure("更多源加载失败：" + e.getLocalizedMessage());
                    return;
                } finally {
                }

                if (mVideos.isEmpty()) {
                    onFailure("更多源加载为空.");
                    return;
                }

                onSuccess();
            }
        }).start();
    }
}