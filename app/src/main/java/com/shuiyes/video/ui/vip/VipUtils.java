package com.shuiyes.video.ui.vip;

import android.util.Base64;

import com.shuiyes.video.util.HttpUtils;
import com.shuiyes.video.util.Utils;

import java.net.URLEncoder;

public class VipUtils {

    private static final String URL8090 = "https://www.8090g.cn/?url=";
    private static final String API8090 = "https://8090.ylybz.cn/jiexi2019/api.php";
    private static final String REFER8090 = "https://8090.ylybz.cn/jiexi2019/?url=";

    public static String getVipUrl8090(String url) {
        String data = "url=" + URLEncoder.encode(url)
                + "&referer=" + URLEncoder.encode(Base64.encodeToString((URL8090 + url).getBytes(), Base64.DEFAULT))
                + "&time=" + Utils.timestamp()
                + "other=" + URLEncoder.encode(Base64.encodeToString((url).getBytes(), Base64.DEFAULT))
                + "&ref=1&type=&&ios=0";
        String refer = "Refer: " + REFER8090 + URLEncoder.encode(url);
        return HttpUtils.post(API8090, data, refer);
    }

}
