package com.shuiyes.video.letv;

import android.content.Context;

import com.shuiyes.video.widget.ClarityView;

public class LetvClarityView extends ClarityView {

    private LetvSource playVideo;

    public LetvClarityView(Context context, LetvSource video) {
        super(context);

        this.playVideo = video;
        this.setText(video.getSize() + "P");
    }

    public LetvSource getPlayVideo() {
        return playVideo;
    }

}
