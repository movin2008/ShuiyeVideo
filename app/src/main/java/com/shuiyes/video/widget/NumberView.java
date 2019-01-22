package com.shuiyes.video.widget;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.ListVideo;
import com.zhy.view.flowlayout.TagView;

import android.content.Context;
import android.view.Gravity;
import android.widget.Checkable;

public class NumberView extends TagView implements Checkable {

    public static final int WH = 120;

    private String url;
    private String title;
    private boolean isChecked;

    private static final int[] CHECK_STATE = new int[]{android.R.attr.state_checked};

    public NumberView(Context context, ListVideo video) {
        super(context);

        this.url = video.getUrl();
        this.title = video.getTitle();

        this.setBackgroundResource(R.drawable.btn_rect);
        this.setText(video.getText());
        this.setGravity(Gravity.CENTER);
        this.setPadding(0, 0, 0, 0);
        this.setTextSize(25);
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public int measureWidth() {
        int measureWidth = Math.round(getPaint().measureText(getText().toString())) + 20;
        return measureWidth > WH ? measureWidth : WH;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }

    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    @Override
    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            refreshDrawableState();
        }
    }

    /**
     * @return The current checked state of the view
     */
    @Override
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
