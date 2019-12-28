package com.shuiyes.video.ui.base;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.util.PlayUtils;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTVListActivity extends BaseActivity implements View.OnClickListener {

    protected TextView mTextView;
    protected TagFlowLayout mResultView;
    protected Handler mHandler = new Handler();
    protected List<ListVideo> mVideos = new ArrayList<ListVideo>();

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
                        return getTagView(o);
                    }
                });
            }
        });
    }

    protected TagView getTagView(ListVideo o){
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
            view.setTextColor(Color.BLACK);
            view.setOnClickListener(this);
            view.setSize(view.measureWidth(), NumberView.WH);
            return view;
        }
    }

    @Override
    public void onClick(View v) {
        NumberView view = (NumberView) v;
        PlayUtils.play(this, view.getUrl(), view.getTitle(), true);
    }
}