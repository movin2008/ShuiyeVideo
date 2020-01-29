package com.shuiyes.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class DLVideoView extends VideoView {

    public DLVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int dlWidth = getDefaultSize(0, widthMeasureSpec);
        int dlHeight = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(dlWidth, dlHeight);

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();

//        android.util.Log.e("SHUIYES", "DLVideoView " + width + "x" + height + " -> " + dlWidth + "x" + dlHeight);
    }
}