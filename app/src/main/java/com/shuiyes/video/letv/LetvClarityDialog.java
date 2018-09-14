package com.shuiyes.video.letv;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.shuiyes.video.widget.ClarityView;
import com.shuiyes.video.widget.FullScreenDialog;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagView;

import java.util.List;

public class LetvClarityDialog extends FullScreenDialog {

    private List<LetvSource> mUrlList;

    public LetvClarityDialog(Context context, List<LetvSource> urls) {
        super(context);
        mUrlList = urls;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView.setAdapter(new TagAdapter<LetvSource>(mUrlList) {
            @Override
            public TagView getView(FlowLayout parent, int position, LetvSource t) {
                ClarityView view = new LetvClarityView(getContext(), t);
                view.setTextColor(Color.WHITE);
                view.setOnClickListener(mListener);
                view.setSize(0, 120);
                return view;
            }
        });
    }
}
