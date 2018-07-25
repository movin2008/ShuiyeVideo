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
        // TODO Auto-generated method stub
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
