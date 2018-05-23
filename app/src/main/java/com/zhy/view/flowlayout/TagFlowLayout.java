package com.zhy.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;

import com.shuiyes.video.util.ResourceUtils;

public class TagFlowLayout extends FlowLayout
        implements TagAdapter.OnDataChangedListener {

    private static final String TAG = "TagFlowLayout";

    private TagAdapter mTagAdapter;

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public void setAdapter(TagAdapter adapter) {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        updateAdapter();
    }

    private void updateAdapter() {
        removeAllViews();
        final TagAdapter adapter = mTagAdapter;
        for (int i = 0; i < adapter.getCount(); i++) {
            int id = i+1;
            TagView tagView = adapter.getView(this, i, adapter.getItem(i));
            tagView.setId(id);
            int width = tagView.getSetWidth();
            int height = tagView.getSetHeight();
            if(width == 0){
                width = LayoutParams.WRAP_CONTENT;
            }
            if(height == 0){
                height = LayoutParams.WRAP_CONTENT;
            }
            int margin = ResourceUtils.dip2px(getContext(), 15);

            MarginLayoutParams lp = new MarginLayoutParams(width, height);
            lp.setMargins(0, 0, margin, margin);
            tagView.setLayoutParams(lp);

            tagView.setFocusable(true);
            tagView.setNextFocusDownId(id+1);
            tagView.setNextFocusRightId(id+1);
            tagView.setNextFocusUpId(id-1);
            tagView.setNextFocusLeftId(id-1);

            addView(tagView);
        }
    }

    @Override
    public void onChanged() {
        updateAdapter();
    }

}
