package com.shuiyes.video.ui.base;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.ui.WebActivity;
import com.shuiyes.video.util.CbchotUtil;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTVListActivity extends BaseActivity implements View.OnClickListener {

    public final static String EXTRA = "file";

    protected TextView mTextView;
    protected TagFlowLayout mResultView;
    protected Handler mHandler = new Handler();
    protected List<ListVideo> mVideos = new ArrayList<ListVideo>();

    protected boolean isHLS = true;
    protected boolean isFM = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewId());
        mTextView = (TextView) this.findViewById(R.id.tv_result);
        mResultView = (TagFlowLayout) this.findViewById(R.id.lv_result);
    }

    public abstract int getContentViewId();

    protected void onFailure(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(error);
            }
        });
    }

    protected void onSuccess() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setVisibility(View.GONE);
                mResultView.setVisibility(View.VISIBLE);
                mResultView.setAdapter(new TagAdapter<ListVideo>(mVideos) {
                    @Override
                    public TagView getView(FlowLayout parent, int position, ListVideo o) {
                        return getTagView(position, o);
                    }
                });
            }
        });
    }

    protected TagView getTagView(int position, ListVideo o){
        if(o.getUrl() == null){
            // 标题
            TagView view = new TagView(mContext);
            view.setSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            view.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            view.setText(o.getText());
            view.setTextSize(30);
            return view;
        }else{
            NumberView view = new NumberView(getApplicationContext(), o);
            view.setSize(view.measureWidth(), 0);
            view.setTextColor(Color.BLACK);
            view.setOnClickListener(this);
            return view;
        }
    }

    @Override
    public void onClick(View v) {
        NumberView view = (NumberView) v;

        String url = view.getUrl();
        if(url.contains("cbn-live.cbchot.com")){
            // 中广热点云加了 auth 验证
            url = CbchotUtil.getAuthUrl(url);
        }else if(url.contains("player.cntv.cn/standard/live")){
            WebActivity.launch(this, url);
            return;
        }

        PlayUtils.playLive(this, url, view.getTitle(), isHLS, isFM);
    }

}