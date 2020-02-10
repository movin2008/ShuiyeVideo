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

            // 广西南宁 移动 1920x1080
            "广西南宁 移动 1080p;http://39.135.34.151:18890/000000001000/1000000001000011218/1.m3u8?channel-id=ystenlive&Contentid=1000000001000011218&livemode=1&stbId=005203FF000360100001001A34C0CD33&userToken=4ef1f6fdd53988bdf19472c73151206f21vv&usergroup=g21077200000&version=1.0&owaccmark=1000000001000011218&owchid=ystenlive&owsid=1106497909461779572&AuthInfo=yOLXJswzZFfV3FvB8MhHuElKGJKLbU5H0jB3qAhfSE7AORAoVDZDWbFnJ0sXJEaRrAyJU4rR1Dadw9ISEYm5oBbA9lSzNfT0W7kMLAWHUBTbBAjpiIN0Pdi%2fTRm3zPoh",

            // 湖北武汉 联通 1920x1080
            "湖北武汉 联通 1080p;http://httpdvb.slave.homed.hrtn.net/playurl?playtype=live&protocol=hls&accesstoken=R5D22D2B7U309E0093K7735BBEDIAC2DC601PBM3187915V10453Z6B7EDWE3620470C71&&playtoken=&programid=4200000197.m3u8",

            // 四川成都 联通 1920x1080
            "四川成都 联通 1080p;http://60.255.149.208/tslive/c213_scc_20190618_0apbps2b_original_r10/c213_scc_20190618_0apbps2b_original_r10.m3u8",

            // 上海电信 1920x1080
            "上海电信 1080p;http://httpdvb.slave.ttcatv.tv:13164/playurl?playtype=live&protocol=hls&accesstoken=R5CA2B7CAU3090C010K77540044IFB84556FPBM3220A5DV1044EZ33519WE22942B42A1&playtoken=&programid=4200000134.m3u8",

            // 广西防城港 移动 1920x1080
            "广西防城港 移动 1080p;http://39.135.38.165:6610/000000001000/1000000001000011218/1.m3u8?IASHttpSessionId=OTT16157620200202041417014267&fmt=ts2hls&u=45768392",

            // 河北石家庄移动 1920x1080
            "河北石家庄 移动 1080p;http://111.11.123.47:6610/030000001000/CCTV-3/CCTV-3.m3u8",

            // 上海电信 1920x1080
            "上海电信 1080p;http://61.151.179.192/tlivecloud-cdn.ysp.cctv.cn/2000203803.m3u8",

            // 哈尔滨 移动 1920x1080
            "哈尔滨 移动 1080p;http://ottrrs.hl.chinamobile.com/PLTV/88888888/224/3221225606/index.m3u8",

            // 南京移动 1920x1080
            "南京移动183 PLTV 1080p;http://183.207.249.14/PLTV/3/224/3221225588/index.m3u8",
            "南京移动183 cntv 1080p;http://183.207.248.71/cntv/live1/HD-2500k-1080P-ccdtv3/HD-2500k-1080P-cctv3",
            "南京移动183 3221225588 1080p;http://183.207.249.14/PLTV/3/224/3221225588/index.m3u8",
            "南京移动223 3221225588 1080p;http://223.110.245.165/ott.js.chinamobile.com/PLTV/3/224/3221225588/index.m3u8",
            "南京移动223 3221227295 1080p;http://223.110.245.159/ott.js.chinamobile.com/PLTV/3/224/3221227295/index.m3u8",

            // 广西南宁 联通 1280x720
            "广西南宁 联通 720p;http://121.31.30.90:8085/ysten-business/live/cctv-3/yst.m3u8",
            // 广西南宁 联通 1280x720
            "苏州联通 720p;http://221.6.85.150:9000/live/cctv3_800/cctv3_800.m3u8",
            // 广东深圳 联通 960x540
            "广东深圳 联通 540p;https://116.77.72.195/streams/d/cctv3/playlist.m3u8",
            // 甘肃嘉峪关 移动 720x544
            "甘肃嘉峪关 移动 540p;http://117.156.28.21/PLTV/88888888/224/3221225615/index.m3u8",

            // 南京移动 720x576
            "南京移动223 540p;http://223.110.245.167/ott.js.chinamobile.com/PLTV/3/224/3221226360/index.m3u8",
            // 南京移动 640x480
            "南京移动223 480p;http://223.110.245.147/ott.js.chinamobile.com/PLTV/3/224/3221226992/index.m3u8",

            // 北京联通 640x360
            "北京联通1 360p;https://cctvtxyh5ca.liveplay.myqcloud.com/live/cctv3_2_hd.m3u8",
            "北京联通2 360p;https://cctvtxyh5ca.liveplay.myqcloud.com/live/cctv3_2/index.m3u8",

            // 济南联通 640x360
            "济南联通 360p;http://cctvalih5ca.v.myalicdn.com/live/cctv3_2/index.m3u8",

            // 北京教育网
            "北京教育网;http://60.13.128.12:5010/nn_live.ts?id=CCTV3&nn_m3u8=1&nn_file_index=316143772&nn_file_name=20200203T090500Z&nn_file_index_time=1580720735&nn_file_start=5683804&nn_file_end=6495776&nn_ak=0174bf22b417673b2c32e8691719e25f45&ndt=phone&nal=0158e2375e06079b8ee57746c4bf400e05c6ec6598725a&ndi=e21e2900bf112056&ngs=5e37e258000115d8d8c01ca94c08571b&ncmsid=10011",
            // 北邮测试源 1920x1080
            "北邮测试源 1080p;http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8",
            "北邮测试源(rtmp) 1080p;rtmp://ivi.bupt.edu.cn:1935/livetv/cctv3hd",

            // CCTV 官网采集 花屏
//            "CCTV官网;https://cctvbsh5c.v.live.baishancdnx.cn/live/cdrmcctv3_1_1800.m3u8",
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