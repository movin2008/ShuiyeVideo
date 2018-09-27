package com.shuiyes.video.widget;

import android.content.Context;
import android.view.Gravity;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.PlayVideo;
import com.zhy.view.flowlayout.TagView;

public class MiscView extends TagView {

    public static final int WH = 100;

    public MiscView(Context context) {
        super(context);

        this.setBackgroundResource(R.drawable.btn_rect);
        this.setGravity(Gravity.CENTER);
        this.setPadding(0, 0, 0, 0);
        this.setTextSize(25);
    }

    private PlayVideo playVideo;

    public MiscView(Context context, PlayVideo video) {
        this(context);

        this.playVideo = video;
        this.setText(video.getText());
    }

    public PlayVideo getPlayVideo() {
        return playVideo;
    }

    public int measureWidth(){
        int measureWidth = Math.round(getPaint().measureText(getText().toString()))+40;
        return measureWidth>WH?measureWidth:WH;
    }

}
