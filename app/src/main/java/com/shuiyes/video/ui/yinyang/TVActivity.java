package com.shuiyes.video.ui.yinyang;

import android.text.TextUtils;

import com.devlin_n.yinyangplayer.controller.StandardVideoController;
import com.shuiyes.video.bean.PlayVideo;
import com.shuiyes.video.ui.tvlive.YinyangTVPlayActivity;
import com.shuiyes.video.util.PreferenceUtil;

import java.util.ArrayList;

public class TVActivity extends YinyangTVPlayActivity {

    // 暂先为父母播放CCTV3 综艺频道，后面改成网络API
    String[] cctv3Urls = {
            "上海百事通 1080p;http://112.25.48.68/live/program/live/cctv3hd/4000000/mnf.m3u8",
            "广西防城港 移动;http://39.135.38.165:6610/000000001000/1000000001000011218/1.m3u8?IASHttpSessionId=OTT16157620200202041417014267&fmt=ts2hls&u=45768392",
            "央视频;http://cctvalih5ca.v.myalicdn.com/live/cctv3_2/index.m3u8",
            "cctvtxyh5ca 480P;https://cctvtxyh5ca.liveplay.myqcloud.com/live/cctv3_2/index.m3u8",
            "北邮直播源;http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8",
    };

    @Override
    protected String[] getIntentStringExtras() {
        String urlHistory = PreferenceUtil.getTVUrl(this);
        if (TextUtils.isEmpty(urlHistory)) {
            urlHistory = cctv3Urls[0];
        }
        String[] tmp = urlHistory.split(";");
        return new String[]{tmp[1], tmp[0]};
    }

    @Override
    protected StandardVideoController initVideoController() {
        ShuiyeVideoController controller = new ShuiyeVideoController(this);
        controller.setLive(true);

        ArrayList<PlayVideo> list = new ArrayList<PlayVideo>();
        for (int i = 0; i < cctv3Urls.length; i++) {
            String[] tmp = cctv3Urls[i].split(";");
            list.add(new PlayVideo(tmp[0], tmp[1]));
        }
        controller.setSourceList(list);

        return controller;
    }
}