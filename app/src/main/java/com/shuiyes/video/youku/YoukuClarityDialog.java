package com.shuiyes.video.youku;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.shuiyes.video.youku.YoukuVideo;
import com.shuiyes.video.widget.ClarityView;
import com.shuiyes.video.widget.FullScreenDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagView;

import java.util.List;

public class YoukuClarityDialog extends FullScreenDialog {

    private List<YoukuVideo> mUrlList;

    public YoukuClarityDialog(Context context, List<YoukuVideo> urls) {
        super(context);
        mUrlList = urls;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView.setAdapter(new TagAdapter<YoukuVideo>(mUrlList) {
            @Override
            public TagView getView(FlowLayout parent, int position, YoukuVideo t) {
                ClarityView view = new YoukuClarityView(getContext(), t);
                view.setTextColor(Color.WHITE);
                view.setOnClickListener(mListener);
                view.setSize(0, 120);
                return view;
            }
        });
    }
}
