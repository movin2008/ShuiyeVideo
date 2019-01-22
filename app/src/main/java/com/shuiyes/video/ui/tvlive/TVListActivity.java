package com.shuiyes.video.ui.tvlive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.base.BaseActivity;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TVListActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    public final static String EXTRA = "filename";
    private String FileName = "newtv.list";

    private TextView mTextView;
    private TagFlowLayout mResultView;
    private Handler mHandler = new Handler();
    protected List<ListVideo> mVideos = new ArrayList<ListVideo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tvlist);
        mTextView = (TextView) this.findViewById(R.id.tv_result);
        mResultView = (TagFlowLayout) this.findViewById(R.id.lv_result);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA)){
            FileName = intent.getStringExtra(EXTRA);
        }

        refreshVideos();
    }

    public void refreshVideos(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(FileName.endsWith(".list")){
                        InputStream in = mContext.getAssets().open("tvlist/"+FileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String text = null;
                        while ((text = br.readLine()) != null){
                            if(text.contains(",")){
                                String[] tmp = text.split(",");
                                mVideos.add(new ListVideo(tmp[0], tmp[0], tmp[1]));
                            }
                        }
                        br.close();
                    }else if(FileName.endsWith(".dpl")){
                        InputStream in = mContext.getAssets().open("tvdpl/"+FileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String text = null;
                        String url = null;
                        while ((text = br.readLine()) != null){
                            if(text.contains("*")){
                                String[] tmp = text.split("\\*");
                                if("file".equals(tmp[1])){
                                    url = tmp[2];
                                }else if("title".equals(tmp[1])){
                                    mVideos.add(new ListVideo(tmp[2], tmp[2], url));
                                }
                            }
                        }
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
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
                                view.setOnClickListener(TVListActivity.this);
                                view.setSize(view.measureWidth(), NumberView.WH);
                                return view;
                            }
                        });
                    }
                });
            }
        }).start();
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
    public void onClick(View v) {
        NumberView view = (NumberView) v;
        startActivity(new Intent(this, TVPlayActivity.class)
                .putExtra("name", view.getTitle())
                .putExtra("url", view.getUrl()));
    }
}