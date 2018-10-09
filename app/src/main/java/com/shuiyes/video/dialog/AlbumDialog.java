package com.shuiyes.video.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.shuiye.video.util.ResourceDef;
import com.shuiyes.video.bean.ListVideo;
import com.shuiyes.video.widget.NumberView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagView;

import java.util.List;

public class AlbumDialog extends FlowlayoutDialog {

    private final String TAG = this.getClass().getSimpleName();

    private List<ListVideo> mVideoList;

    public AlbumDialog(Context context, List<ListVideo> videos) {
        super(context);
        mVideoList = videos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");

        mView.setAdapter(new TagAdapter<ListVideo>(mVideoList) {
            @Override
            public TagView getView(FlowLayout parent, int position, ListVideo t) {
                NumberView view = new NumberView(getContext(), t);
                view.setTextColor(Color.WHITE);
                view.setOnClickListener(mListener);
                view.setSize(view.measureWidth(), NumberView.WH);
                return view;
            }
        });
    }

    public void show(final int index) {
        super.show();
        final NumberView view = (NumberView) this.findViewById(ResourceDef.ID_TAG_BTN + index);
        if(view != null){
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            Log.e(TAG, "show("+index+") "+view+" focused:"+view.isFocused());
        }
    }

    @Override
    public void show() {
        super.show();
    }
}
