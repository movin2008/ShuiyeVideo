package com.zhy.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;

import com.shuiye.video.util.ResourceDef;
import com.shuiye.video.util.ResourceUtils;

public class TagFlowLayout extends FlowLayout
        implements TagAdapter.OnDataChangedListener {

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
        for (int i = 0; i < mTagAdapter.getCount(); i++) {
            int id = i + ResourceDef.ID_TAG_BTN;
            TagView tagView = mTagAdapter.getView(this, i, mTagAdapter.getItem(i));
            tagView.setFocusable(true);
            tagView.setId(id);

            int width = tagView.getSetWidth();
            int height = tagView.getSetHeight();
            if (width == 0) {
                width = LayoutParams.WRAP_CONTENT;
            }
            if (height == 0) {
                height = LayoutParams.WRAP_CONTENT;
            }
            int margin = ResourceUtils.dip2px(getContext(), 15);

            MarginLayoutParams lp = new MarginLayoutParams(width, height);
            lp.setMargins(0, 0, margin, margin);
            tagView.setLayoutParams(lp);

            addView(tagView);
        }
    }

    @Override
    public void onChanged() {
        updateAdapter();
    }

}
