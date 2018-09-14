package com.shuiyes.video.widget;

import android.content.Context;
import android.view.Gravity;

import com.shuiyes.video.R;
import com.zhy.view.flowlayout.TagView;

public class ClarityView extends TagView {
    public ClarityView(Context context) {
        super(context);

        this.setBackgroundResource(R.drawable.btn_rect);
        this.setGravity(Gravity.CENTER);
        this.setPadding(20, 0, 20, 0);
        this.setTextSize(25);
    }
}
