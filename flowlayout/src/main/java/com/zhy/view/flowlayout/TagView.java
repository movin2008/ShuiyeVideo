package com.zhy.view.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class TagView extends TextView {

    private int width;
    private int height;

    public int getSetWidth() {
        return width;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getSetHeight() {
        return height;
    }

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
