package com.shuiyes.video.widget;

import android.content.Context;
import android.text.format.Formatter;

import com.shuiyes.video.R;
import com.shuiyes.video.bean.PlayVideo;
import com.zhy.view.flowlayout.TagView;

public class ClarityView extends TagView {


    private PlayVideo playVideo;
    public ClarityView(Context context, PlayVideo video)
    {
        super(context);
        this.playVideo = video;

        this.setText(video.getType().getProfile()+"("+ Formatter.formatFileSize(context, video.getSize())+")");
        this.setPadding(20, 0, 20, 0);
        this.setBackgroundResource(R.drawable.btn_rect);
    }

    public PlayVideo getPlayVideo() {
		return playVideo;
	}

}
