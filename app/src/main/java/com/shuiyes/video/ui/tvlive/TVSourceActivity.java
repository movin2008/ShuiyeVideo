package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.base.BaseTVListActivity;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.TagView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TVSourceActivity extends BaseTVListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listFiles(getIntent().getStringExtra(EXTRA));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure("加载失败：" + e.getLocalizedMessage());
                    return;
                }

                if (mVideos.isEmpty()) {
                    onFailure("加载为空.");
                    return;
                }

                onSuccess();
            }
        }).start();

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_tvlist;
    }

    @Override
    protected TagView getTagView(int position, ListVideo o) {
        return super.getTagView(position, o);
    }

    @Override
    public void onClick(View v) {
        NumberView view = (NumberView) v;
        startActivity(new Intent(this, TVListActivity.class).putExtra(TVListActivity.EXTRA, view.getUrl()));
    }

    public void szCmcc(View view) {
        // 苏州移动源
        startActivity(new Intent(this, SuzhouCMCCActivity.class));
    }

    @Deprecated
    public void soplus(View view) {
        startActivity(new Intent(this, SopPlusActivity.class));
    }

    // 北邮测试源
    public void iviBupt(View view) {
        startActivity(new Intent(this, BuptIVIActivity.class));
    }

    private void listFiles(String path) throws Exception {
        Log.e(TAG, "listFiles " + path);
        String[] list = mContext.getAssets().list(path);

        Map<String, String> maps = new HashMap<>();

        for (String s : list) {
            String subPath = path + s;
            if (mContext.getAssets().list(subPath).length > 1) {
                maps.put(subPath + "/", s);
                continue;
            }

            String text = s.replace(".tv", "").replace(".fm", "").replace(".list", "");
            mVideos.add(new ListVideo(text, s, subPath));
        }

        Set<String> sets = maps.keySet();
        Iterator<String> iterator = sets.iterator();
        while (iterator.hasNext()) {
            String subPath = iterator.next();
            String s = maps.get(subPath);

            // title
            mVideos.add(new ListVideo("", "", null));
            mVideos.add(new ListVideo(s, s, null));

            listFiles(subPath);
        }

    }

}