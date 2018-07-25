package com.shuiyes.video;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.widget.ClarityView;
import com.shuiyes.video.widget.FullScreenDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagView;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

public class ClarityDialog extends FullScreenDialog {

    private List<PlayVideo> mUrlList;

    public ClarityDialog(Context context, List<PlayVideo> urls) {
        super(context);
        mUrlList = urls;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView.setAdapter(new TagAdapter<PlayVideo>(mUrlList) {
            @Override
            public TagView getView(FlowLayout parent, int position, PlayVideo t) {
                ClarityView view = new ClarityView(getContext(), t);
                view.setTextColor(Color.WHITE);
                view.setOnClickListener(mListener);
                view.setSize(0, 120);
                return view;
            }
        });
    }
}
